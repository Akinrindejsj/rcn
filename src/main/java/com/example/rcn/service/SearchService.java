package com.example.rcn.service;

import com.example.rcn.dto.SearchResponseDto;
import com.example.rcn.dto.SearchResultDto;
import com.example.rcn.event.SearchIndexRefreshEvent;
import com.example.rcn.model.AboutPageContent;
import com.example.rcn.model.Activity;
import com.example.rcn.model.ActivityStatus;
import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.model.Faq;
import com.example.rcn.model.Podcast;
import com.example.rcn.model.TeamMember;
import com.example.rcn.repository.AboutPageContentRepository;
import com.example.rcn.repository.ActivityRepository;
import com.example.rcn.repository.ArticleRepository;
import com.example.rcn.repository.FaqRepository;
import com.example.rcn.repository.PodcastRepository;
import com.example.rcn.repository.TeamMemberRepository;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class SearchService {

    private static final DateTimeFormatter MONTH_YEAR = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
    private static final int MAX_RESULTS_WINDOW = 500;

    private final ArticleRepository articleRepository;
    private final PodcastRepository podcastRepository;
    private final ActivityRepository activityRepository;
    private final AboutPageContentRepository aboutPageContentRepository;
    private final FaqRepository faqRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final Path indexRoot;

    private volatile boolean indexReady;

    public SearchService(ArticleRepository articleRepository,
                         PodcastRepository podcastRepository,
                         ActivityRepository activityRepository,
                         AboutPageContentRepository aboutPageContentRepository,
                         FaqRepository faqRepository,
                         TeamMemberRepository teamMemberRepository,
                         @Value("${rcn.search.index-root:./data/search-index}") String indexRoot) {
        this.articleRepository = articleRepository;
        this.podcastRepository = podcastRepository;
        this.activityRepository = activityRepository;
        this.aboutPageContentRepository = aboutPageContentRepository;
        this.faqRepository = faqRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.indexRoot = Path.of(indexRoot);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void buildInitialIndex() {
        rebuildIndex();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void refreshIndexAfterContentChange(SearchIndexRefreshEvent event) {
        rebuildIndex();
    }

    public SearchResponseDto search(String rawQuery, int page, int size) {
        String queryText = rawQuery == null ? "" : rawQuery.trim();
        int safePage = Math.max(0, page);
        int safeSize = size < 1 || size > 50 ? 10 : size;

        if (queryText.length() < 2) {
            return new SearchResponseDto(queryText, List.of(), 0, safePage, safeSize, 0);
        }

        ensureIndex();

        try (Directory directory = FSDirectory.open(indexRoot)) {
            if (!DirectoryReader.indexExists(directory)) {
                return new SearchResponseDto(queryText, List.of(), 0, safePage, safeSize, 0);
            }

            Query query = buildQuery(queryText);
            try (DirectoryReader reader = DirectoryReader.open(directory)) {
                IndexSearcher searcher = new IndexSearcher(reader);
                int offset = safePage * safeSize;
                int limit = Math.min(MAX_RESULTS_WINDOW, offset + safeSize);
                TopDocs topDocs = searcher.search(query, limit);
                long totalHits = topDocs.totalHits.value;

                List<SearchResultDto> results = new ArrayList<>();
                ScoreDoc[] hits = topDocs.scoreDocs;
                for (int i = offset; i < hits.length; i++) {
                    Document doc = searcher.storedFields().document(hits[i].doc);
                    results.add(toResult(doc));
                }

                int totalPages = (int) Math.ceil((double) totalHits / safeSize);
                return new SearchResponseDto(queryText, results, totalHits, safePage, safeSize, totalPages);
            }
        } catch (IOException | ParseException ex) {
            return new SearchResponseDto(queryText, List.of(), 0, safePage, safeSize, 0);
        }
    }

    public synchronized void rebuildIndex() {
        try {
            Files.createDirectories(indexRoot);
            try (Directory directory = FSDirectory.open(indexRoot);
                 StandardAnalyzer analyzer = new StandardAnalyzer();
                 IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
                writer.deleteAll();

                indexArticles(writer);
                indexPodcasts(writer);
                indexActivities(writer);
                indexAboutPage(writer);
                indexFaqs(writer);
                indexProgrammePoints(writer);

                writer.commit();
                indexReady = true;
            }
        } catch (IOException ex) {
            indexReady = false;
        }
    }

    private void ensureIndex() {
        if (!indexReady) {
            rebuildIndex();
        }
    }

    private Query buildQuery(String queryText) throws ParseException {
        Map<String, Float> boosts = Map.of(
                "title", 4.0f,
                "summary", 2.0f,
                "body", 1.0f,
                "type", 1.5f
        );
        MultiFieldQueryParser parser = new MultiFieldQueryParser(
                new String[]{"title", "summary", "body", "type"},
                new StandardAnalyzer(),
                boosts
        );
        parser.setDefaultOperator(QueryParser.Operator.AND);
        return parser.parse(QueryParser.escape(queryText));
    }

    private void indexArticles(IndexWriter writer) throws IOException {
        for (Article article : articleRepository.findByStatusOrderByPublishedAtDesc(ArticleStatus.PUBLISHED)) {
            writer.addDocument(document(
                    "Article",
                    article.getTitle(),
                    firstNonBlank(article.getExcerpt(), article.getBody()),
                    article.getBody(),
                    "/article/" + article.getId(),
                    article.getFeaturedImageUrl(),
                    article.getPublishedAt() == null ? "" : MONTH_YEAR.format(article.getPublishedAt())
            ));
        }
    }

    private void indexPodcasts(IndexWriter writer) throws IOException {
        for (Podcast podcast : podcastRepository.findAllByOrderByPublishedAtDesc()) {
            writer.addDocument(document(
                    "Podcast",
                    podcast.getTitle(),
                    podcast.getDescription(),
                    joinText(podcast.getDescription(), podcast.getPodcastTitle(), podcast.getPodcastDescription()),
                    "/podcast",
                    podcast.getCoverImageUrl(),
                    podcast.getPublishedAt() == null ? "" : MONTH_YEAR.format(podcast.getPublishedAt())
            ));
        }
    }

    private void indexActivities(IndexWriter writer) throws IOException {
        for (Activity activity : activityRepository.findAllByOrderByActivityDateDesc()) {
            if (!isPublicActivity(activity)) {
                continue;
            }
            writer.addDocument(document(
                    "Activity",
                    activity.getTitle(),
                    firstNonBlank(activity.getBody(), activity.getLocation()),
                    joinText(activity.getBody(), activity.getLocation(), activity.getType(), activity.getAuthorName()),
                    "/activity",
                    activity.getImageUrl(),
                    activity.getActivityDate() == null ? "" : activity.getActivityDate().toString()
            ));
        }
    }

    private void indexAboutPage(IndexWriter writer) throws IOException {
        AboutPageContent about = aboutPageContentRepository.findById(AboutPageContent.SINGLETON_ID)
                .orElseGet(AboutPageContent::defaults);
        writer.addDocument(document(
                "Page",
                firstNonBlank(about.getHeading(), "About"),
                about.getIntroText(),
                joinText(about.getIntroText(), about.getJoinBannerText()),
                "/about",
                about.getIntroImageUrl(),
                ""
        ));
    }

    private void indexFaqs(IndexWriter writer) throws IOException {
        for (Faq faq : faqRepository.findAllByOrderBySortOrderAsc()) {
            writer.addDocument(document(
                    "FAQ",
                    faq.getQuestion(),
                    faq.getAnswer(),
                    faq.getAnswer(),
                    "/about#faq",
                    "",
                    ""
            ));
        }
    }

    private void indexProgrammePoints(IndexWriter writer) throws IOException {
        for (TeamMember point : teamMemberRepository.findAllByOrderBySortOrderAsc()) {
            writer.addDocument(document(
                    "Programme",
                    point.getPointTitle(),
                    point.getPointDescription(),
                    point.getPointDescription(),
                    "/about#programme",
                    point.getImageUrl(),
                    ""
            ));
        }
    }

    private Document document(String type,
                              String title,
                              String summary,
                              String body,
                              String url,
                              String imageUrl,
                              String dateLabel) {
        Document document = new Document();
        document.add(new StringField("type", safe(type), Field.Store.YES));
        document.add(new TextField("title", safe(title), Field.Store.YES));
        document.add(new TextField("summary", clip(summary, 220), Field.Store.YES));
        document.add(new TextField("body", stripHtml(safe(body)), Field.Store.NO));
        document.add(new StringField("url", safe(url), Field.Store.YES));
        document.add(new StringField("imageUrl", safe(imageUrl), Field.Store.YES));
        document.add(new StringField("dateLabel", safe(dateLabel), Field.Store.YES));
        return document;
    }

    private SearchResultDto toResult(Document doc) {
        return new SearchResultDto(
                doc.get("type"),
                doc.get("title"),
                doc.get("summary"),
                doc.get("url"),
                doc.get("imageUrl"),
                doc.get("dateLabel")
        );
    }

    private boolean isPublicActivity(Activity activity) {
        ActivityStatus status = activity.getApprovalStatus();
        return status == null || status == ActivityStatus.PUBLISHED || status == ActivityStatus.APPROVED;
    }

    private static String firstNonBlank(String first, String second) {
        return !safe(first).isBlank() ? first : second;
    }

    private static String joinText(String... values) {
        StringBuilder joined = new StringBuilder();
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                if (!joined.isEmpty()) {
                    joined.append(' ');
                }
                joined.append(value);
            }
        }
        return joined.toString();
    }

    private static String clip(String value, int maxLength) {
        String clean = stripHtml(safe(value)).replaceAll("\\s+", " ").trim();
        if (clean.length() <= maxLength) {
            return clean;
        }
        return clean.substring(0, maxLength).trim() + "...";
    }

    private static String stripHtml(String value) {
        return safe(value).replaceAll("<[^>]*>", " ");
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }
}
