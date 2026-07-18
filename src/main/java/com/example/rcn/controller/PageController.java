package com.example.rcn.controller;

import com.example.rcn.model.Activity;
import com.example.rcn.model.Article;
import com.example.rcn.model.ArticleStatus;
import com.example.rcn.model.HomepageContent;
import com.example.rcn.model.Podcast;
import com.example.rcn.dto.JoinCmd;
import com.example.rcn.service.ActivityService;
import com.example.rcn.service.ArticleService;
import com.example.rcn.service.HomepageContentService;
import com.example.rcn.service.PodcastService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Comparator;
import java.util.List;

@Controller
public class PageController {

    private final HomepageContentService homepageContentService;
    private final ArticleService articleService;
    private final PodcastService podcastService;
    private final ActivityService activityService;

    public PageController(HomepageContentService homepageContentService,
                          ArticleService articleService,
                          PodcastService podcastService,
                          ActivityService activityService) {
        this.homepageContentService = homepageContentService;
        this.articleService = articleService;
        this.podcastService = podcastService;
        this.activityService = activityService;
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Model model) {
        HomepageContent content = homepageContentService.getSingleton();
        List<Article> homepageArticles = orderedArticles(content.getHomepageArticleIdList());
        List<Podcast> homepagePodcasts = orderedPodcasts(content.getHomepagePodcastIdList());
        List<Activity> homepageActivities = orderedActivities(content.getHomepageActivityIdList());

        model.addAttribute("content", content);
        model.addAttribute("homepageArticles", homepageArticles);
        model.addAttribute("featuredHomepageArticle", homepageArticles.isEmpty() ? null : homepageArticles.get(0));
        model.addAttribute("secondaryHomepageArticles",
                homepageArticles.size() <= 1 ? List.of() : homepageArticles.subList(1, homepageArticles.size()));
        model.addAttribute("homepagePodcasts", homepagePodcasts);
        model.addAttribute("homepageActivities", homepageActivities);
        model.addAttribute("cmd", new JoinCmd());
        return "pages/dashboard";
    }

    private List<Article> orderedArticles(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return articleService.findByIdsIn(ids).stream()
                .filter(article -> article.getStatus() == ArticleStatus.PUBLISHED)
                .sorted(Comparator.comparingInt(article -> ids.indexOf(article.getId())))
                .limit(6)
                .toList();
    }

    private List<Podcast> orderedPodcasts(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return podcastService.findByIdsIn(ids).stream()
                .sorted(Comparator.comparingInt(podcast -> ids.indexOf(podcast.getId())))
                .limit(4)
                .toList();
    }

    private List<Activity> orderedActivities(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        return activityService.findByIdsIn(ids).stream()
                .sorted(Comparator.comparingInt(activity -> ids.indexOf(activity.getId())))
                .limit(3)
                .toList();
    }

    @GetMapping("/theory")
    public String theory() {
        return "pages/theory";
    }

    @GetMapping("/404")
    public String notFound() {
        return "pages/404";
    }
}
