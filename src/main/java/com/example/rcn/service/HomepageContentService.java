package com.example.rcn.service;

import com.example.rcn.dto.HomepageContentUpdateDto;
import com.example.rcn.exception.CloudinaryUploadException;
import com.example.rcn.model.HomepageContent;
import com.example.rcn.repository.HomepageContentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class HomepageContentService {

    private static final long MAX_IMAGE_BYTES = 5L * 1024 * 1024; // 5 MB
    private static final long MAX_VIDEO_BYTES = 50L * 1024 * 1024; // 50 MB
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp"
    );
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4",
            "video/webm",
            "video/quicktime"
    );

    private final HomepageContentRepository repository;
    private final CloudinaryService cloudinaryService;

    public HomepageContentService(HomepageContentRepository repository,
                                  CloudinaryService cloudinaryService) {
        this.repository = repository;
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Loads the singleton homepage row, seeding it from {@link HomepageContent#defaults()}
     * the first time it is requested.
     */
    public HomepageContent getSingleton() {
        return repository.findById(HomepageContent.SINGLETON_ID)
                .orElseGet(() -> repository.save(HomepageContent.defaultsTransient()));
    }

    /**
     * Applies a text + image update to the singleton row.
     *
     * @param dto     bound form containing the text fields (null/blank values mean
     *                "no change")
     * @param images map of field name → uploaded file; empty/null entries mean
     *                "no change"
     * @return the updated, persisted entity
     * @throws IllegalArgumentException    if a validation rule is violated
     * @throws CloudinaryUploadException if a provided image cannot be uploaded
     */
    public HomepageContent update(HomepageContentUpdateDto dto,
                                  Map<String, MultipartFile> images)
            throws CloudinaryUploadException {
        HomepageContent entity = getSingleton();

        // ---- Text fields: only overwrite when the client actually sent a value. ----
        if (notBlank(dto.getHeroHeadline())) {
            entity.setHeroHeadline(dto.getHeroHeadline().trim());
        }
        if (notBlank(dto.getHeroSubtext())) {
            entity.setHeroSubtext(dto.getHeroSubtext().trim());
        }
        if (notBlank(dto.getHeroPrimaryButtonText())) {
            entity.setHeroPrimaryButtonText(dto.getHeroPrimaryButtonText().trim());
        }
        if (notBlank(dto.getHeroPrimaryButtonUrl())) {
            entity.setHeroPrimaryButtonUrl(dto.getHeroPrimaryButtonUrl().trim());
        }
        if (notBlank(dto.getHeroSecondaryButtonText())) {
            entity.setHeroSecondaryButtonText(dto.getHeroSecondaryButtonText().trim());
        }
        if (notBlank(dto.getHeroSecondaryButtonUrl())) {
            entity.setHeroSecondaryButtonUrl(dto.getHeroSecondaryButtonUrl().trim());
        }

        if (notBlank(dto.getTickerLine1())) {
            entity.setTickerLine1(dto.getTickerLine1().trim());
        }
        if (notBlank(dto.getTickerLine2())) {
            entity.setTickerLine2(dto.getTickerLine2().trim());
        }
        if (notBlank(dto.getTickerLine3())) {
            entity.setTickerLine3(dto.getTickerLine3().trim());
        }
        if (notBlank(dto.getTickerLine4())) {
            entity.setTickerLine4(dto.getTickerLine4().trim());
        }
        if (notBlank(dto.getTickerLine5())) {
            entity.setTickerLine5(dto.getTickerLine5().trim());
        }

        if (notBlank(dto.getFeaturedArticleTitle())) {
            entity.setFeaturedArticleTitle(dto.getFeaturedArticleTitle().trim());
        }
        if (notBlank(dto.getFeaturedArticleBody())) {
            entity.setFeaturedArticleBody(dto.getFeaturedArticleBody().trim());
        }
        if (notBlank(dto.getFeaturedArticleDate())) {
            entity.setFeaturedArticleDate(dto.getFeaturedArticleDate().trim());
        }
        if (notBlank(dto.getFeaturedArticleUrl())) {
            entity.setFeaturedArticleUrl(dto.getFeaturedArticleUrl().trim());
        }

        if (notBlank(dto.getStat1Number())) {
            entity.setStat1Number(dto.getStat1Number().trim());
        }
        if (notBlank(dto.getStat1Label())) {
            entity.setStat1Label(dto.getStat1Label().trim());
        }
        if (notBlank(dto.getStat2Number())) {
            entity.setStat2Number(dto.getStat2Number().trim());
        }
        if (notBlank(dto.getStat2Label())) {
            entity.setStat2Label(dto.getStat2Label().trim());
        }
        if (notBlank(dto.getStat3Number())) {
            entity.setStat3Number(dto.getStat3Number().trim());
        }
        if (notBlank(dto.getStat3Label())) {
            entity.setStat3Label(dto.getStat3Label().trim());
        }

        // ---- Selection fields: sanitize and enforce homepage limits. ----
        if (dto.getFeaturedArticleId() != null) {
            entity.setFeaturedArticleId(parseNullableLong(dto.getFeaturedArticleId()));
        }
        if (dto.getHomepageArticleIds() != null) {
            entity.setHomepageArticleIds(cleanIdCsv(dto.getHomepageArticleIds(), 6, "articles"));
        }
        if (dto.getHomepagePodcastIds() != null) {
            entity.setHomepagePodcastIds(cleanIdCsv(dto.getHomepagePodcastIds(), 4, "podcasts"));
        }
        if (dto.getHomepageActivityIds() != null) {
            entity.setHomepageActivityIds(cleanIdCsv(dto.getHomepageActivityIds(), 3, "activities"));
        }

        // ---- Validation: reject any currently-set field that has been blanked out. ----
        assertNoBlankedField(entity);

        // ---- Image fields: upload any newly provided file to Cloudinary. ----
        if (images != null) {
            uploadVideoIfPresent(images, "heroImage", "rcn/homepage", entity::setHeroImage);
            uploadIfPresent(images, "featuredArticleImage", "rcn/homepage",
                    entity::setFeaturedArticleImageUrl);
            uploadIfPresent(images, "podcastSectionImage", "rcn/homepage",
                    entity::setPodcastSectionImageUrl);
            uploadIfPresent(images, "activitySectionImage", "rcn/homepage",
                    entity::setActivitySectionImageUrl);
            uploadIfPresent(images, "buildPartyImage", "rcn/homepage",
                    entity::setBuildPartyImageUrl);
            uploadIfPresent(images, "tickerBackgroundImage", "rcn/homepage",
                    entity::setTickerBackgroundImageUrl);
        }

        return repository.save(entity);
    }

    private void uploadIfPresent(Map<String, MultipartFile> images, String key,
                                 String folder,
                                 java.util.function.Consumer<String> setter) throws CloudinaryUploadException {
        MultipartFile file = images.get(key);
        if (file != null && !file.isEmpty()) {
            validateImage(file);
            String url = cloudinaryService.uploadImage(file, folder);
            setter.accept(url);
        }
    }

    private void uploadVideoIfPresent(Map<String, MultipartFile> images, String key,
                                      String folder,
                                      java.util.function.Consumer<String> setter) throws CloudinaryUploadException {
        MultipartFile file = images.get(key);
        if (file != null && !file.isEmpty()) {
            validateVideo(file);
            String url = cloudinaryService.uploadVideo(file, folder);
            setter.accept(url);
        }
    }

    private static Long parseNullableLong(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return Long.parseLong(s.trim());
    }

    private static String cleanIdCsv(String csv, int max, String label) {
        if (csv == null || csv.isBlank()) {
            return "";
        }
        java.util.List<Long> ids;
        try {
            ids = java.util.Arrays.stream(csv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(Long::parseLong)
                    .distinct()
                    .toList();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Please select valid " + label + " from the list.");
        }
        if (ids.size() > max) {
            throw new IllegalArgumentException("You can select at most " + max + " " + label + ".");
        }
        return ids.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    private static boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private void assertNoBlankedField(HomepageContent entity) {
        if (entity.getHeroHeadline() != null && entity.getHeroHeadline().isBlank()) {
            throw new IllegalArgumentException("Hero headline cannot be blank.");
        }
        if (entity.getHeroSubtext() != null && entity.getHeroSubtext().isBlank()) {
            throw new IllegalArgumentException("Hero subtext cannot be blank.");
        }
        if (entity.getHeroPrimaryButtonText() != null && entity.getHeroPrimaryButtonText().isBlank()) {
            throw new IllegalArgumentException("Primary button text cannot be blank.");
        }
        if (entity.getHeroPrimaryButtonUrl() != null && entity.getHeroPrimaryButtonUrl().isBlank()) {
            throw new IllegalArgumentException("Primary button URL cannot be blank.");
        }
        if (entity.getHeroSecondaryButtonText() != null && entity.getHeroSecondaryButtonText().isBlank()) {
            throw new IllegalArgumentException("Secondary button text cannot be blank.");
        }
        if (entity.getHeroSecondaryButtonUrl() != null && entity.getHeroSecondaryButtonUrl().isBlank()) {
            throw new IllegalArgumentException("Secondary button URL cannot be blank.");
        }
        for (int i = 1; i <= 5; i++) {
            String line = switch (i) {
                case 1 -> entity.getTickerLine1();
                case 2 -> entity.getTickerLine2();
                case 3 -> entity.getTickerLine3();
                case 4 -> entity.getTickerLine4();
                case 5 -> entity.getTickerLine5();
                default -> null;
            };
            if (line != null && line.isBlank()) {
                throw new IllegalArgumentException("Ticker line " + i + " cannot be blank.");
            }
        }
        if (entity.getFeaturedArticleTitle() != null && entity.getFeaturedArticleTitle().isBlank()) {
            throw new IllegalArgumentException("Featured article title cannot be blank.");
        }
        if (entity.getFeaturedArticleBody() != null && entity.getFeaturedArticleBody().isBlank()) {
            throw new IllegalArgumentException("Featured article body cannot be blank.");
        }
        if (entity.getFeaturedArticleDate() != null && entity.getFeaturedArticleDate().isBlank()) {
            throw new IllegalArgumentException("Featured article date cannot be blank.");
        }
        if (entity.getFeaturedArticleUrl() != null && entity.getFeaturedArticleUrl().isBlank()) {
            throw new IllegalArgumentException("Featured article URL cannot be blank.");
        }
        if (entity.getStat1Number() != null && entity.getStat1Number().isBlank()) {
            throw new IllegalArgumentException("Stat 1 number cannot be blank.");
        }
        if (entity.getStat1Label() != null && entity.getStat1Label().isBlank()) {
            throw new IllegalArgumentException("Stat 1 label cannot be blank.");
        }
        if (entity.getStat2Number() != null && entity.getStat2Number().isBlank()) {
            throw new IllegalArgumentException("Stat 2 number cannot be blank.");
        }
        if (entity.getStat2Label() != null && entity.getStat2Label().isBlank()) {
            throw new IllegalArgumentException("Stat 2 label cannot be blank.");
        }
        if (entity.getStat3Number() != null && entity.getStat3Number().isBlank()) {
            throw new IllegalArgumentException("Stat 3 number cannot be blank.");
        }
        if (entity.getStat3Label() != null && entity.getStat3Label().isBlank()) {
            throw new IllegalArgumentException("Stat 3 label cannot be blank.");
        }
    }

    private void validateImage(MultipartFile file) {
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new IllegalArgumentException(
                    "Image file is too large (max 5 MB). Please choose a smaller image.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only JPG, PNG, and WebP images are allowed. Please choose a different file.");
        }
    }

    private void validateVideo(MultipartFile file) {
        if (file.getSize() > MAX_VIDEO_BYTES) {
            throw new IllegalArgumentException(
                    "Video file is too large (max 50 MB). Please choose a smaller video.");
        }
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_VIDEO_TYPES.contains(contentType)) {
            throw new IllegalArgumentException(
                    "Only MP4, WebM, and MOV videos are allowed. Please choose a different file.");
        }
    }
}
