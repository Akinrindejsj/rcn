package com.example.rcn.controller;

import com.example.rcn.dto.AboutPageContentDto;
import com.example.rcn.dto.ArticleDto;
import com.example.rcn.dto.ContributionTierDto;
import com.example.rcn.dto.DonationPageContentDto;
import com.example.rcn.dto.FaqDto;
import com.example.rcn.dto.HomepageContentUpdateDto;
import com.example.rcn.dto.PaymentDetailsDto;
import com.example.rcn.dto.PodcastDto;
import com.example.rcn.dto.SiteSettingsDto;
import com.example.rcn.dto.TeamMemberDto;
import com.example.rcn.exception.CloudinaryUploadException;
import com.example.rcn.model.*;
import com.example.rcn.service.AboutPageContentService;
import com.example.rcn.service.ActivityPageContentService;
import com.example.rcn.service.ArticleService;
import com.example.rcn.service.CloudinaryService;
import com.example.rcn.service.ContributionTierService;
import com.example.rcn.service.DonationPageContentService;
import com.example.rcn.service.FaqService;
import com.example.rcn.service.HomepageContentService;
import com.example.rcn.service.MediaService;
import com.example.rcn.service.PaymentDetailsService;
import com.example.rcn.service.PodcastService;
import com.example.rcn.service.SiteSettingsService;
import com.example.rcn.service.TeamMemberService;
import com.example.rcn.service.MembersVoiceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CMS admin console. Every GET renders a management page; every POST mutates
 * state and redirects back with a flash success/error message.
 *
 * The singleton-page services (About, Donations, Payment, SiteSettings) only
 * expose {@code getSingleton()} / {@code save(entity)}, so the controller maps
 * the incoming DTO onto the loaded entity, handles any image upload, then saves.
 */
@Controller
@RequestMapping("/admin/cms")
public class CmsController {

    private final HomepageContentService homepageContentService;
    private final ArticleService articleService;
    private final PodcastService podcastService;
    private final TeamMemberService teamMemberService;
    private final MembersVoiceService membersVoiceService;
    private final FaqService faqService;
    private final AboutPageContentService aboutPageContentService;
    private final ActivityPageContentService activityPageContentService;
    private final DonationPageContentService donationPageContentService;
    private final ContributionTierService contributionTierService;
    private final PaymentDetailsService paymentDetailsService;
    private final SiteSettingsService siteSettingsService;
    private final MediaService mediaService;
    private final CloudinaryService cloudinaryService;
    private final com.example.rcn.service.ActivityService activityService;

    public CmsController(HomepageContentService homepageContentService,
                         ArticleService articleService,
                         PodcastService podcastService,
                          TeamMemberService teamMemberService,
                          MembersVoiceService membersVoiceService,
                         FaqService faqService,
                         AboutPageContentService aboutPageContentService,
                         ActivityPageContentService activityPageContentService,
                         DonationPageContentService donationPageContentService,
                         ContributionTierService contributionTierService,
                         PaymentDetailsService paymentDetailsService,
                         SiteSettingsService siteSettingsService,
                         MediaService mediaService,
                         CloudinaryService cloudinaryService,
                         com.example.rcn.service.ActivityService activityService) {
        this.homepageContentService = homepageContentService;
        this.articleService = articleService;
        this.podcastService = podcastService;
        this.teamMemberService = teamMemberService;
        this.membersVoiceService = membersVoiceService;
        this.faqService = faqService;
        this.aboutPageContentService = aboutPageContentService;
        this.activityPageContentService = activityPageContentService;
        this.donationPageContentService = donationPageContentService;
        this.contributionTierService = contributionTierService;
        this.paymentDetailsService = paymentDetailsService;
        this.siteSettingsService = siteSettingsService;
        this.mediaService = mediaService;
        this.cloudinaryService = cloudinaryService;
        this.activityService = activityService;
    }

    /**
     * Exposes the current request URI to every CMS view as ${currentUrl}.
     * Used by the shared sidebar to highlight the active page.
     */
    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // ---------------------------------------------------------------------
    // Dashboard
    // ---------------------------------------------------------------------

    @GetMapping
    public String index() {
        return "redirect:/admin/cms/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("articleCount", articleService.countByStatus(ArticleStatus.PUBLISHED));
        model.addAttribute("pendingCount", articleService.countByStatus(ArticleStatus.PENDING_APPROVAL));
        model.addAttribute("podcastCount", podcastService.findAll().size());
        model.addAttribute("activityCount", activityService.findAll().size());
        return "cms/cms_dashboard";
    }

    // ---------------------------------------------------------------------
    // Homepage
    // ---------------------------------------------------------------------

    @GetMapping("/homepage")
    public String homepage(Model model) {
        if (!model.containsAttribute("content")) {
            model.addAttribute("content", homepageContentService.getSingleton());
        }
        // Selection widgets need the full lists to render title + thumbnail.
        model.addAttribute("articles", articleService.findPublished());
        model.addAttribute("podcasts", podcastService.findAll());
        model.addAttribute("activities", activityService.findAll());
        return "cms/cms_homepage";
    }

    @PostMapping("/homepage")
    public String saveHomepage(@ModelAttribute HomepageContentUpdateDto content,
                               @RequestParam Map<String, MultipartFile> images,
                               RedirectAttributes redirectAttributes) {
        try {
            homepageContentService.update(content, images);
            redirectAttributes.addFlashAttribute("successMessage", "Homepage saved.");
            return "redirect:/admin/cms/homepage";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/homepage";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/homepage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/homepage";
        }
    }

    // ---------------------------------------------------------------------
    // Approval queue (pending submissions)
    // ---------------------------------------------------------------------

    @GetMapping("/approval")
    public String approvalQueue(Model model) {
        model.addAttribute("pendingArticles", articleService.findByStatus(ArticleStatus.PENDING_APPROVAL));
        return "cms/cms_article_approval";
    }

    @PostMapping("/approval/{id}/publish")
    public String publishPendingArticle(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.publish(id);
            redirectAttributes.addFlashAttribute("successMessage", "Article approved and published.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not publish article: " + e.getMessage());
        }
        return "redirect:/admin/cms/approval";
    }

    @PostMapping("/approval/{id}/reject")
    public String rejectPendingArticle(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.reject(id);
            redirectAttributes.addFlashAttribute("successMessage", "Article rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Could not reject article: " + e.getMessage());
        }
        return "redirect:/admin/cms/approval";
    }

    // ---------------------------------------------------------------------
    // Articles
    // ---------------------------------------------------------------------

    @GetMapping("/articles")
    public String articles(Model model) {
        model.addAttribute("articles", articleService.findAll());
        return "cms/cms_articles";
    }

    @GetMapping("/articles/new")
    public String newArticle(Model model) {
        if (!model.containsAttribute("article")) {
            model.addAttribute("article", new ArticleDto());
        }
        model.addAttribute("statuses", ArticleStatus.values());
        return "cms/cms_article_form";
    }

    @PostMapping("/articles/new")
    public String createArticle(@ModelAttribute("article") ArticleDto dto,
                                @RequestParam(value = "featuredImage", required = false) MultipartFile featuredImage,
                                RedirectAttributes redirectAttributes) {
        try {
            Article article = new Article();
            applyArticleFields(article, dto);
            article.setStatus(ArticleStatus.DRAFT);
            if (featuredImage != null && !featuredImage.isEmpty()) {
                article.setFeaturedImageUrl(cloudinaryService.uploadImage(featuredImage, "rcn/articles"));
            }
            articleService.create(article);
            redirectAttributes.addFlashAttribute("successMessage", "Article created.");
            return "redirect:/admin/cms/articles";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/articles/new";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/articles/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/articles/new";
        }
    }

    @GetMapping("/articles/{id}/edit")
    public String editArticle(@PathVariable("id") Long id, Model model) {
        Article article = articleService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article not found (id=" + id + ")"));
        model.addAttribute("article", toDto(article));
        model.addAttribute("statuses", ArticleStatus.values());
        return "cms/cms_article_form";
    }

    @PostMapping("/articles/{id}/edit")
    public String updateArticle(@PathVariable("id") Long id,
                                @ModelAttribute("article") ArticleDto dto,
                                @RequestParam(value = "featuredImage", required = false) MultipartFile featuredImage,
                                RedirectAttributes redirectAttributes) {
        try {
            Article article = articleService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Article not found (id=" + id + ")"));
            applyArticleFields(article, dto);
            if (featuredImage != null && !featuredImage.isEmpty()) {
                article.setFeaturedImageUrl(cloudinaryService.uploadImage(featuredImage, "rcn/articles"));
            }
            articleService.update(article);
            redirectAttributes.addFlashAttribute("successMessage", "Article updated.");
            return "redirect:/admin/cms/articles";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/articles/" + id + "/edit";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/articles/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/articles/" + id + "/edit";
        }
    }

    @PostMapping("/articles/{id}/publish")
    public String publishArticle(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.publish(id);
            redirectAttributes.addFlashAttribute("successMessage", "Article published.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
        }
        return "redirect:/admin/cms/articles";
    }

    @PostMapping("/articles/{id}/delete")
    public String deleteArticle(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            articleService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Article deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
        }
        return "redirect:/admin/cms/articles";
    }

    // ---------------------------------------------------------------------
    // Podcasts
    // ---------------------------------------------------------------------

    @GetMapping("/podcasts")
    public String podcasts(Model model) {
        model.addAttribute("podcasts", podcastService.findAll());
        return "cms/cms_podcasts";
    }

    @GetMapping("/podcasts/new")
    public String newPodcast(Model model) {
        if (!model.containsAttribute("podcast")) {
            model.addAttribute("podcast", new PodcastDto());
        }
        return "cms/cms_podcast_form";
    }

    @PostMapping("/podcasts/new")
    public String createPodcast(@ModelAttribute("podcast") PodcastDto dto,
                                @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                                RedirectAttributes redirectAttributes) {
        try {
            Podcast podcast = new Podcast();
            applyPodcastFields(podcast, dto);
            if (coverImage != null && !coverImage.isEmpty()) {
                podcast.setCoverImageUrl(cloudinaryService.uploadImage(coverImage, "rcn/podcasts"));
            }
            podcastService.create(podcast);
            redirectAttributes.addFlashAttribute("successMessage", "Podcast episode created.");
            return "redirect:/admin/cms/podcasts";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/podcasts/new";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/podcasts/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/podcasts/new";
        }
    }

    @GetMapping("/podcasts/{id}/edit")
    public String editPodcast(@PathVariable("id") Long id, Model model) {
        Podcast podcast = podcastService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Podcast not found (id=" + id + ")"));
        model.addAttribute("podcast", toDto(podcast));
        return "cms/cms_podcast_form";
    }

    @PostMapping("/podcasts/{id}/edit")
    public String updatePodcast(@PathVariable("id") Long id,
                                @ModelAttribute("podcast") PodcastDto dto,
                                @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
                                RedirectAttributes redirectAttributes) {
        try {
            Podcast podcast = podcastService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Podcast not found (id=" + id + ")"));
            applyPodcastFields(podcast, dto);
            if (coverImage != null && !coverImage.isEmpty()) {
                podcast.setCoverImageUrl(cloudinaryService.uploadImage(coverImage, "rcn/podcasts"));
            }
            podcastService.update(podcast);
            redirectAttributes.addFlashAttribute("successMessage", "Podcast episode updated.");
            return "redirect:/admin/cms/podcasts";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/podcasts/" + id + "/edit";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/podcasts/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/podcasts/" + id + "/edit";
        }
    }

    @PostMapping("/podcasts/{id}/delete")
    public String deletePodcast(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            podcastService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Podcast episode deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
        }
        return "redirect:/admin/cms/podcasts";
    }

    // ---------------------------------------------------------------------
    // Activity / events
    // ---------------------------------------------------------------------

    /**
     * Activity Page CMS. Renders one editor that controls both the page-level
     * copy/settings (hero, stats, map, footer CTA) and the list of report cards.
     * Settings come from the singleton {@link ActivityPageContent}; reports are
     * the live {@link Activity} rows.
     */
    @GetMapping("/events")
    public String events(Model model) {
        if (!model.containsAttribute("activitySettings")) {
            model.addAttribute("activitySettings", activityPageContentService.getSingleton());
        }
        model.addAttribute("activities", activityService.findAll());
        return "cms/cms_events";
    }

    /** Persists the Activity-page settings (hero / stats / map / footer CTA). */
    @PostMapping("/events/save")
    public String saveActivityPageSettings(@ModelAttribute("activitySettings") ActivityPageContent activitySettings,
                                           @RequestParam(value = "backdrop", required = false) MultipartFile backdrop,
                                           @RequestParam(value = "backdropRemoved", required = false, defaultValue = "false") boolean backdropRemoved,
                                           @RequestParam(value = "map", required = false) MultipartFile map,
                                           @RequestParam(value = "mapRemoved", required = false, defaultValue = "false") boolean mapRemoved,
                                           RedirectAttributes redirectAttributes) {
        try {
            ActivityPageContent entity = activityPageContentService.getSingleton();

            if (activitySettings.getPageTitle() != null) {
                entity.setPageTitle(activitySettings.getPageTitle().trim());
            }
            if (activitySettings.getKicker() != null) {
                entity.setKicker(activitySettings.getKicker().trim());
            }
            if (activitySettings.getHeadline() != null) {
                entity.setHeadline(activitySettings.getHeadline().trim());
            }
            if (activitySettings.getIntroText() != null) {
                entity.setIntroText(activitySettings.getIntroText().trim());
            }
            if (activitySettings.getCta1Text() != null) {
                entity.setCta1Text(activitySettings.getCta1Text().trim());
            }
            if (activitySettings.getCta1Url() != null) {
                entity.setCta1Url(activitySettings.getCta1Url().trim());
            }
            if (activitySettings.getCta2Text() != null) {
                entity.setCta2Text(activitySettings.getCta2Text().trim());
            }
            if (activitySettings.getCta2Url() != null) {
                entity.setCta2Url(activitySettings.getCta2Url().trim());
            }
            if (activitySettings.getStat1Number() != null) {
                entity.setStat1Number(activitySettings.getStat1Number().trim());
            }
            if (activitySettings.getStat1Label() != null) {
                entity.setStat1Label(activitySettings.getStat1Label().trim());
            }
            if (activitySettings.getStat2Number() != null) {
                entity.setStat2Number(activitySettings.getStat2Number().trim());
            }
            if (activitySettings.getStat2Label() != null) {
                entity.setStat2Label(activitySettings.getStat2Label().trim());
            }
            if (activitySettings.getStat3Number() != null) {
                entity.setStat3Number(activitySettings.getStat3Number().trim());
            }
            if (activitySettings.getStat3Label() != null) {
                entity.setStat3Label(activitySettings.getStat3Label().trim());
            }
            if (activitySettings.getMapHeading() != null) {
                entity.setMapHeading(activitySettings.getMapHeading().trim());
            }
            if (activitySettings.getMapBodyText() != null) {
                entity.setMapBodyText(activitySettings.getMapBodyText().trim());
            }
            if (activitySettings.getFooterHeading() != null) {
                entity.setFooterHeading(activitySettings.getFooterHeading().trim());
            }
            if (activitySettings.getFooterBodyText() != null) {
                entity.setFooterBodyText(activitySettings.getFooterBodyText().trim());
            }
            if (activitySettings.getFooterCtaText() != null) {
                entity.setFooterCtaText(activitySettings.getFooterCtaText().trim());
            }
            if (activitySettings.getFooterCtaUrl() != null) {
                entity.setFooterCtaUrl(activitySettings.getFooterCtaUrl().trim());
            }

            // Hero backdrop image
            if (backdrop != null && !backdrop.isEmpty()) {
                entity.setBackdropImageUrl(cloudinaryService.uploadImage(backdrop, "rcn/activity"));
            } else if (backdropRemoved) {
                entity.setBackdropImageUrl("");
            }

            // Map image
            if (map != null && !map.isEmpty()) {
                entity.setMapImageUrl(cloudinaryService.uploadImage(map, "rcn/activity"));
            } else if (mapRemoved) {
                entity.setMapImageUrl("");
            }

            activityPageContentService.save(entity);
            redirectAttributes.addFlashAttribute("successMessage", "Activity page saved.");
            return "redirect:/admin/cms/events";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/events";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/events";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/events";
        }
    }

    @GetMapping("/events/new")
    public String newEvent(Model model) {
        model.addAttribute("activity", new com.example.rcn.dto.ActivityDto());
        model.addAttribute("edit", false);
        return "cms/cms_event_form";
    }

    @PostMapping("/events/new")
    public String createEvent(@ModelAttribute("activity") com.example.rcn.dto.ActivityDto dto,
                              @RequestParam(value = "image", required = false) MultipartFile image,
                              RedirectAttributes redirectAttributes) {
        try {
            Activity activity = new Activity();
            applyActivityFields(activity, dto);
            if (image != null && !image.isEmpty()) {
                activity.setImageUrl(cloudinaryService.uploadImage(image, "rcn/activities"));
            }
            activityService.create(activity);
            redirectAttributes.addFlashAttribute("successMessage", "Activity report added.");
            return "redirect:/admin/cms/events";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/events/new";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/events/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/events/new";
        }
    }

    @GetMapping("/events/{id}/edit")
    public String editEvent(@PathVariable("id") Long id, Model model) {
        Activity activity = activityService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Activity report not found (id=" + id + ")"));
        model.addAttribute("activity", toDto(activity));
        model.addAttribute("edit", true);
        return "cms/cms_event_form";
    }

    @PostMapping("/events/{id}/edit")
    public String updateEvent(@PathVariable("id") Long id,
                              @ModelAttribute("activity") com.example.rcn.dto.ActivityDto dto,
                              @RequestParam(value = "image", required = false) MultipartFile image,
                              RedirectAttributes redirectAttributes) {
        try {
            Activity activity = activityService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Activity report not found (id=" + id + ")"));
            applyActivityFields(activity, dto);
            if (image != null && !image.isEmpty()) {
                activity.setImageUrl(cloudinaryService.uploadImage(image, "rcn/activities"));
            }
            activityService.update(activity);
            redirectAttributes.addFlashAttribute("successMessage", "Activity report updated.");
            return "redirect:/admin/cms/events";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/events/" + id + "/edit";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/events/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/events/" + id + "/edit";
        }
    }

    @PostMapping("/events/{id}/delete")
    public String deleteEvent(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            activityService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Activity report deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
        }
        return "redirect:/admin/cms/events";
    }

    private void applyActivityFields(Activity activity, com.example.rcn.dto.ActivityDto dto) {
        if (dto.getTitle() != null) {
            activity.setTitle(dto.getTitle().trim());
        }
        if (dto.getBody() != null) {
            activity.setBody(dto.getBody().trim());
        }
        if (dto.getLocation() != null) {
            activity.setLocation(dto.getLocation().trim());
        }
        if (dto.getAuthorName() != null) {
            activity.setAuthorName(dto.getAuthorName().trim());
        }
        if (dto.getActivityDate() != null && !dto.getActivityDate().isBlank()) {
            activity.setActivityDate(LocalDate.parse(dto.getActivityDate()));
        }
        if (dto.getType() != null) {
            activity.setType(dto.getType().trim());
        }
    }

    private com.example.rcn.dto.ActivityDto toDto(Activity activity) {
        com.example.rcn.dto.ActivityDto dto = new com.example.rcn.dto.ActivityDto();
        dto.setId(activity.getId());
        dto.setTitle(activity.getTitle());
        dto.setBody(activity.getBody());
        dto.setLocation(activity.getLocation());
        dto.setAuthorName(activity.getAuthorName());
        dto.setActivityDate(activity.getActivityDate() == null ? null : activity.getActivityDate().toString());
        dto.setType(activity.getType());
        return dto;
    }

    // ---------------------------------------------------------------------
    // About page (programme points + FAQ + intro)
    // ---------------------------------------------------------------------

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("about", aboutPageContentService.getSingleton());
        model.addAttribute("programmePoints", teamMemberService.findAll());
        model.addAttribute("faqs", faqService.findAll());
        return "cms/cms_about";
    }

    @PostMapping("/about")
    public String saveAbout(@ModelAttribute AboutPageContentDto dto,
                            @RequestParam(value = "introImage", required = false) MultipartFile introImage,
                            @RequestParam(value = "imageRemoved", required = false, defaultValue = "false") boolean imageRemoved,
                            RedirectAttributes redirectAttributes) {
        try {
            AboutPageContent entity = aboutPageContentService.getSingleton();
            if (dto.getHeading() != null) {
                entity.setHeading(dto.getHeading().trim());
            }
            if (dto.getIntroText() != null) {
                entity.setIntroText(dto.getIntroText().trim());
            }
            if (dto.getJoinBannerText() != null) {
                entity.setJoinBannerText(dto.getJoinBannerText().trim());
            }
            if (dto.getJoinBannerUrl() != null) {
                entity.setJoinBannerUrl(dto.getJoinBannerUrl().trim());
            }
            if (introImage != null && !introImage.isEmpty()) {
                entity.setIntroImageUrl(cloudinaryService.uploadImage(introImage, "rcn/about"));
            } else if (imageRemoved) {
                entity.setIntroImageUrl("");
            }
            aboutPageContentService.save(entity);
            redirectAttributes.addFlashAttribute("successMessage", "About page saved.");
            return "redirect:/admin/cms/about";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/about";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/about";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/about";
        }
    }

    @GetMapping("/about/programme-points/new")
    public String newProgrammePoint(Model model) {
        if (!model.containsAttribute("point")) {
            model.addAttribute("point", new TeamMemberDto());
        }
        return "cms/cms_programme_point_form";
    }

    @PostMapping("/about/programme-points/new")
    public Object createProgrammePoint(@ModelAttribute("point") TeamMemberDto dto,
                                       @RequestParam(value = "image", required = false) MultipartFile image,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        try {
            validateTeamMember(dto);
            TeamMember point = new TeamMember();
            applyTeamMemberFields(point, dto);
            if (image != null && !image.isEmpty()) {
                point.setImageUrl(cloudinaryService.uploadImage(image, "rcn/about"));
            }
            teamMemberService.create(point);
            if (isXhr(request)) {
                return jsonOk(point);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Programme point added.");
            return "redirect:/admin/cms/about";
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/about/programme-points/new";
        } catch (CloudinaryUploadException e) {
            if (isXhr(request)) {
                return jsonError("Image upload failed: " + e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/about/programme-points/new";
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again. "
                        + "If the problem continues, contact your site administrator.");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/about/programme-points/new";
        }
    }

    @GetMapping("/about/programme-points/{id}/edit")
    public String editProgrammePoint(@PathVariable("id") Long id, Model model) {
        TeamMember point = teamMemberService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Programme point not found (id=" + id + ")"));
        model.addAttribute("point", toDto(point));
        return "cms/cms_programme_point_form";
    }

    @PostMapping("/about/programme-points/{id}/edit")
    public Object updateProgrammePoint(@PathVariable("id") Long id,
                                       @ModelAttribute("point") TeamMemberDto dto,
                                       @RequestParam(value = "image", required = false) MultipartFile image,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        try {
            validateTeamMember(dto);
            TeamMember point = teamMemberService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Programme point not found (id=" + id + ")"));
            applyTeamMemberFields(point, dto);
            if (image != null && !image.isEmpty()) {
                point.setImageUrl(cloudinaryService.uploadImage(image, "rcn/about"));
            }
            teamMemberService.update(point);
            if (isXhr(request)) {
                return jsonOk(point);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Programme point updated.");
            return "redirect:/admin/cms/about";
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/about/programme-points/" + id + "/edit";
        } catch (CloudinaryUploadException e) {
            if (isXhr(request)) {
                return jsonError("Image upload failed: " + e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/about/programme-points/" + id + "/edit";
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again. "
                        + "If the problem continues, contact your site administrator.");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/about/programme-points/" + id + "/edit";
        }
    }

    @PostMapping("/about/programme-points/{id}/delete")
    public Object deleteProgrammePoint(@PathVariable("id") Long id,
                                       RedirectAttributes redirectAttributes,
                                       HttpServletRequest request) {
        try {
            teamMemberService.delete(id);
            if (isXhr(request)) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                return ResponseEntity.ok(body);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Programme point deleted.");
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again.");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
        }
        return "redirect:/admin/cms/about";
    }

    @GetMapping("/about/faqs/new")
    public String newFaq(Model model) {
        if (!model.containsAttribute("faq")) {
            model.addAttribute("faq", new FaqDto());
        }
        return "cms/cms_faq_form";
    }

    @PostMapping("/about/faqs/new")
    public Object createFaq(@ModelAttribute("faq") FaqDto dto,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        try {
            validateFaq(dto);
            Faq faq = new Faq();
            faq.setQuestion(dto.getQuestion().trim());
            faq.setAnswer(dto.getAnswer().trim());
            faq.setSortOrder(parseIntOrNull(dto.getSortOrder()));
            faqService.create(faq);
            if (isXhr(request)) {
                return jsonOk(faq);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Question added.");
            return "redirect:/admin/cms/about";
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/about/faqs/new";
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again. "
                        + "If the problem continues, contact your site administrator.");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/about/faqs/new";
        }
    }

    @GetMapping("/about/faqs/{id}/edit")
    public String editFaq(@PathVariable("id") Long id, Model model) {
        Faq faq = faqService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Question not found (id=" + id + ")"));
        model.addAttribute("faq", toDto(faq));
        return "cms/cms_faq_form";
    }

    @PostMapping("/about/faqs/{id}/edit")
    public Object updateFaq(@PathVariable("id") Long id,
                            @ModelAttribute("faq") FaqDto dto,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        try {
            validateFaq(dto);
            Faq faq = faqService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Question not found (id=" + id + ")"));
            faq.setQuestion(dto.getQuestion().trim());
            faq.setAnswer(dto.getAnswer().trim());
            faq.setSortOrder(parseIntOrNull(dto.getSortOrder()));
            faqService.update(faq);
            if (isXhr(request)) {
                return jsonOk(faq);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Question updated.");
            return "redirect:/admin/cms/about";
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/about/faqs/" + id + "/edit";
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again. "
                        + "If the problem continues, contact your site administrator.");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/about/faqs/" + id + "/edit";
        }
    }

    @PostMapping("/about/faqs/{id}/delete")
    public Object deleteFaq(@PathVariable("id") Long id,
                            RedirectAttributes redirectAttributes,
                            HttpServletRequest request) {
        try {
            faqService.delete(id);
            if (isXhr(request)) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                return ResponseEntity.ok(body);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Question deleted.");
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again.");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
        }
        return "redirect:/admin/cms/about";
    }

    // ---------------------------------------------------------------------
    // Member Voices (join page)
    // ---------------------------------------------------------------------

    @GetMapping("/members")
    public String members(Model model) {
        model.addAttribute("members", membersVoiceService.findAll());
        return "cms/cms_members";
    }

    @GetMapping("/members/new")
    public String newMember(Model model) {
        if (!model.containsAttribute("member")) {
            model.addAttribute("member", new com.example.rcn.model.MembersVoice());
        }
        return "cms/cms_members_form";
    }

    @PostMapping("/members/new")
    public Object createMember(@ModelAttribute("member") com.example.rcn.model.MembersVoice member,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        try {
            validateMemberVoice(member);
            membersVoiceService.create(member);
            if (isXhr(request)) {
                return jsonOk(member);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Member voice added.");
            return "redirect:/admin/cms/members";
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/members/new";
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again. ");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. ");
            return "redirect:/admin/cms/members/new";
        }
    }

    @GetMapping("/members/{id}/edit")
    public String editMember(@PathVariable("id") Long id, Model model) {
        com.example.rcn.model.MembersVoice member = membersVoiceService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Member voice not found (id=" + id + ")"));
        model.addAttribute("member", member);
        return "cms/cms_members_form";
    }

    @PostMapping("/members/{id}/edit")
    public Object updateMember(@PathVariable("id") Long id,
                               @ModelAttribute("member") com.example.rcn.model.MembersVoice member,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        try {
            validateMemberVoice(member);
            com.example.rcn.model.MembersVoice existing = membersVoiceService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Member voice not found (id=" + id + ")"));
            // copy fields
            existing.setAuthorName(member.getAuthorName());
            existing.setLocation(member.getLocation());
            existing.setQuote(member.getQuote());
            existing.setSortOrder(member.getSortOrder());
            existing.setImageUrl(member.getImageUrl());
            membersVoiceService.update(existing);
            if (isXhr(request)) {
                return jsonOk(existing);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Member voice updated.");
            return "redirect:/admin/cms/members";
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/members/" + id + "/edit";
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again. ");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. ");
            return "redirect:/admin/cms/members/" + id + "/edit";
        }
    }

    @PostMapping("/members/{id}/delete")
    public Object deleteMember(@PathVariable("id") Long id,
                               RedirectAttributes redirectAttributes,
                               HttpServletRequest request) {
        try {
            membersVoiceService.delete(id);
            if (isXhr(request)) {
                Map<String, Object> body = new LinkedHashMap<>();
                body.put("success", true);
                return ResponseEntity.ok(body);
            }
            redirectAttributes.addFlashAttribute("successMessage", "Member voice deleted.");
        } catch (IllegalArgumentException e) {
            if (isXhr(request)) {
                return jsonError(e.getMessage());
            }
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            if (isXhr(request)) {
                return jsonError("Something went wrong on our end. Please try again.");
            }
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. ");
        }
        return "redirect:/admin/cms/members";
    }

    private void validateMemberVoice(com.example.rcn.model.MembersVoice dto) {
        if (dto.getAuthorName() == null || dto.getAuthorName().isBlank()) {
            throw new IllegalArgumentException("Please enter the member's name.");
        }
        if (dto.getQuote() == null || dto.getQuote().isBlank()) {
            throw new IllegalArgumentException("Please enter the member's quote.");
        }
    }

    // ---------------------------------------------------------------------
    // Donations
    // ---------------------------------------------------------------------

    @GetMapping("/donations")
    public String donations(Model model) {
        model.addAttribute("donation", donationPageContentService.getSingleton());
        model.addAttribute("tiers", contributionTierService.findAll());
        model.addAttribute("payment", paymentDetailsService.getSingleton());
        return "cms/cms_donations";
    }

    @PostMapping("/donations")
    public String saveDonations(@ModelAttribute DonationPageContentDto dto,
                                @RequestParam(value = "printingPressImage", required = false) MultipartFile printingPressImage,
                                RedirectAttributes redirectAttributes) {
        try {
            DonationPageContent entity = donationPageContentService.getSingleton();
            if (dto.getPageHeading() != null) {
                entity.setPageHeading(dto.getPageHeading().trim());
            }
            if (dto.getIntroText() != null) {
                entity.setIntroText(dto.getIntroText().trim());
            }
            if (dto.getFooterQuote() != null) {
                entity.setFooterQuote(dto.getFooterQuote().trim());
            }
            if (dto.getFootnote() != null) {
                entity.setFootnote(dto.getFootnote().trim());
            }
            if (printingPressImage != null && !printingPressImage.isEmpty()) {
                entity.setPrintingPressImageUrl(cloudinaryService.uploadImage(printingPressImage, "rcn/donations"));
            }
            donationPageContentService.save(entity);
            redirectAttributes.addFlashAttribute("successMessage", "Donation page saved.");
            return "redirect:/admin/cms/donations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/donations";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/donations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/donations";
        }
    }

    @GetMapping("/donations/tiers/new")
    public String newTier(Model model) {
        if (!model.containsAttribute("tier")) {
            model.addAttribute("tier", new ContributionTierDto());
        }
        return "cms/cms_tier_form";
    }

    @PostMapping("/donations/tiers/new")
    public String createTier(@ModelAttribute("tier") ContributionTierDto dto, RedirectAttributes redirectAttributes) {
        try {
            validateTier(dto);
            ContributionTier tier = new ContributionTier();
            applyTierFields(tier, dto);
            contributionTierService.create(tier);
            redirectAttributes.addFlashAttribute("successMessage", "Contribution tier added.");
            return "redirect:/admin/cms/donations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/donations/tiers/new";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/donations/tiers/new";
        }
    }

    @GetMapping("/donations/tiers/{id}/edit")
    public String editTier(@PathVariable("id") Long id, Model model) {
        ContributionTier tier = contributionTierService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tier not found (id=" + id + ")"));
        model.addAttribute("tier", toDto(tier));
        return "cms/cms_tier_form";
    }

    @PostMapping("/donations/tiers/{id}/edit")
    public String updateTier(@PathVariable("id") Long id,
                             @ModelAttribute("tier") ContributionTierDto dto,
                             RedirectAttributes redirectAttributes) {
        try {
            validateTier(dto);
            ContributionTier tier = contributionTierService.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Tier not found (id=" + id + ")"));
            applyTierFields(tier, dto);
            contributionTierService.update(tier);
            redirectAttributes.addFlashAttribute("successMessage", "Contribution tier updated.");
            return "redirect:/admin/cms/donations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/donations/tiers/" + id + "/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/donations/tiers/" + id + "/edit";
        }
    }

    @PostMapping("/donations/tiers/{id}/delete")
    public String deleteTier(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            contributionTierService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Contribution tier deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
        }
        return "redirect:/admin/cms/donations";
    }

    @PostMapping("/donations/payment")
    public String savePayment(@ModelAttribute PaymentDetailsDto dto, RedirectAttributes redirectAttributes) {
        try {
            PaymentDetails entity = paymentDetailsService.getSingleton();
            if (dto.getBankName() != null) {
                entity.setBankName(dto.getBankName().trim());
            }
            if (dto.getAccountName() != null) {
                entity.setAccountName(dto.getAccountName().trim());
            }
            if (dto.getAccountNumber() != null) {
                entity.setAccountNumber(dto.getAccountNumber().trim());
            }
            if (dto.getSortCode() != null) {
                entity.setSortCode(dto.getSortCode().trim());
            }
            paymentDetailsService.save(entity);
            redirectAttributes.addFlashAttribute("successMessage", "Payment details saved.");
            return "redirect:/admin/cms/donations";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/donations";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/donations";
        }
    }

    // ---------------------------------------------------------------------
    // Site settings
    // ---------------------------------------------------------------------

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("settings", siteSettingsService.getSingleton());
        return "cms/cms_settings";
    }

    @PostMapping("/settings")
    public String saveSettings(@ModelAttribute SiteSettingsDto dto,
                               @RequestParam(value = "footerImage", required = false) MultipartFile footerImage,
                               RedirectAttributes redirectAttributes) {
        try {
            SiteSettings entity = siteSettingsService.getSingleton();
            if (dto.getSiteName() != null) {
                entity.setSiteName(dto.getSiteName().trim());
            }
            if (dto.getTagline() != null) {
                entity.setTagline(dto.getTagline().trim());
            }
            if (dto.getContactEmail() != null) {
                entity.setContactEmail(dto.getContactEmail().trim());
            }
            if (dto.getWhatsApp() != null) {
                entity.setWhatsApp(dto.getWhatsApp().trim());
            }
            if (dto.getBannerText() != null) {
                entity.setBannerText(dto.getBannerText().trim());
            }
            if (dto.getSocialX() != null) {
                entity.setSocialX(dto.getSocialX().trim());
            }
            if (dto.getSocialInstagram() != null) {
                entity.setSocialInstagram(dto.getSocialInstagram().trim());
            }
            if (dto.getSocialYoutube() != null) {
                entity.setSocialYoutube(dto.getSocialYoutube().trim());
            }
            if (dto.getSocialTelegram() != null) {
                entity.setSocialTelegram(dto.getSocialTelegram().trim());
            }
            if (dto.getMarxistUrl() != null) {
                entity.setMarxistUrl(dto.getMarxistUrl().trim());
            }
            if (dto.getBooksUrl() != null) {
                entity.setBooksUrl(dto.getBooksUrl().trim());
            }
            if (dto.getFooterDescription() != null) {
                entity.setFooterDescription(dto.getFooterDescription().trim());
            }
            if (footerImage != null && !footerImage.isEmpty()) {
                entity.setFooterImageUrl(cloudinaryService.uploadImage(footerImage, "rcn/settings"));
            }
            if (dto.getCopyrightLine() != null) {
                entity.setCopyrightLine(dto.getCopyrightLine().trim());
            }
            if (dto.getClosingSlogan() != null) {
                entity.setClosingSlogan(dto.getClosingSlogan().trim());
            }
            siteSettingsService.save(entity);
            redirectAttributes.addFlashAttribute("successMessage", "Site settings saved.");
            return "redirect:/admin/cms/settings";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/settings";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Footer image upload failed: " + e.getMessage());
            return "redirect:/admin/cms/settings";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/settings";
        }
    }

    // ---------------------------------------------------------------------
    // Media library
    // ---------------------------------------------------------------------

    @GetMapping("/media")
    public String media(Model model,
                        @RequestParam(value = "type", required = false) String type) {
        if ("VIDEO".equalsIgnoreCase(type)) {
            model.addAttribute("files", mediaService.findImages()); // no video query yet; show images
        } else {
            model.addAttribute("files", mediaService.findImages());
        }
        model.addAttribute("filter", type);
        return "cms/cms_media";
    }

    @PostMapping("/media/upload")
    public String uploadMedia(@RequestParam("file") MultipartFile file,
                              @RequestParam(value = "uploadedBy", required = false) String uploadedBy,
                              RedirectAttributes redirectAttributes) {
        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException("Please choose a file to upload.");
            }
            mediaService.upload(file, "rcn/media", uploadedBy == null ? "cms" : uploadedBy);
            redirectAttributes.addFlashAttribute("successMessage", "File uploaded.");
            return "redirect:/admin/cms/media";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/cms/media";
        } catch (CloudinaryUploadException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Upload failed: " + e.getMessage());
            return "redirect:/admin/cms/media";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
            return "redirect:/admin/cms/media";
        }
    }

    @PostMapping("/media/{id}/delete")
    public String deleteMedia(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            mediaService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "File deleted.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Something went wrong on our end. Please try again. "
                            + "If the problem continues, contact your site administrator.");
        }
        return "redirect:/admin/cms/media";
    }

    // ---------------------------------------------------------------------
    // XHR helpers
    // ---------------------------------------------------------------------

    private boolean isXhr(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    private ResponseEntity<Map<String, Object>> jsonOk(Object entity) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", true);
        if (entity instanceof TeamMember point) {
            body.put("id", point.getId());
            body.put("orderNumber", point.getOrderNumber());
            body.put("pointTitle", point.getPointTitle());
            body.put("pointDescription", point.getPointDescription());
            body.put("sortOrder", point.getSortOrder());
            body.put("imageUrl", point.getImageUrl());
        } else if (entity instanceof Faq faq) {
            body.put("id", faq.getId());
            body.put("question", faq.getQuestion());
            body.put("answer", faq.getAnswer());
            body.put("sortOrder", faq.getSortOrder());
        } else if (entity instanceof com.example.rcn.model.MembersVoice mv) {
            body.put("id", mv.getId());
            body.put("authorName", mv.getAuthorName());
            body.put("location", mv.getLocation());
            body.put("quote", mv.getQuote());
            body.put("sortOrder", mv.getSortOrder());
            body.put("initials", mv.getInitials());
        }
        return ResponseEntity.ok(body);
    }

    private ResponseEntity<Map<String, Object>> jsonError(String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("success", false);
        body.put("message", message);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    // ---------------------------------------------------------------------
    // Field-mapping + validation helpers
    // ---------------------------------------------------------------------

    private void applyArticleFields(Article article, ArticleDto dto) {
        if (dto.getTitle() != null) {
            article.setTitle(dto.getTitle().trim());
        }
        if (dto.getSlug() != null) {
            article.setSlug(dto.getSlug().trim());
        }
        if (dto.getBody() != null) {
            article.setBody(dto.getBody().trim());
        }
        if (dto.getExcerpt() != null) {
            article.setExcerpt(dto.getExcerpt().trim());
        }
        if (dto.getCategory() != null) {
            article.setCategory(dto.getCategory().trim());
        }
        if (dto.getAuthorName() != null) {
            article.setAuthorName(dto.getAuthorName().trim());
        }
        if (dto.getPublishedAt() != null && !dto.getPublishedAt().isBlank()) {
            article.setPublishedAt(LocalDateTime.parse(dto.getPublishedAt()));
        }
    }

    private void applyPodcastFields(Podcast podcast, PodcastDto dto) {
        if (dto.getEpisodeNumber() != null) {
            podcast.setEpisodeNumber(dto.getEpisodeNumber().trim());
        }
        if (dto.getTitle() != null) {
            podcast.setTitle(dto.getTitle().trim());
        }
        if (dto.getDescription() != null) {
            podcast.setDescription(dto.getDescription().trim());
        }
        if (dto.getAudioUrl() != null) {
            podcast.setAudioUrl(dto.getAudioUrl().trim());
        }
        if (dto.getDuration() != null) {
            podcast.setDuration(dto.getDuration().trim());
        }
        if (dto.getSpotifyUrl() != null) {
            podcast.setSpotifyUrl(dto.getSpotifyUrl().trim());
        }
        if (dto.getApplePodcastsUrl() != null) {
            podcast.setApplePodcastsUrl(dto.getApplePodcastsUrl().trim());
        }
        if (dto.getYoutubeUrl() != null) {
            podcast.setYoutubeUrl(dto.getYoutubeUrl().trim());
        }
        if (dto.getRssUrl() != null) {
            podcast.setRssUrl(dto.getRssUrl().trim());
        }
        if (dto.getPodcastTitle() != null) {
            podcast.setPodcastTitle(dto.getPodcastTitle().trim());
        }
        if (dto.getPodcastDescription() != null) {
            podcast.setPodcastDescription(dto.getPodcastDescription().trim());
        }
        if (dto.getPublishedAt() != null && !dto.getPublishedAt().isBlank()) {
            podcast.setPublishedAt(LocalDateTime.parse(dto.getPublishedAt()));
        }
    }

    private void validateTeamMember(TeamMemberDto dto) {
        if (dto.getPointTitle() == null || dto.getPointTitle().isBlank()) {
            throw new IllegalArgumentException("Please give this programme point a title.");
        }
    }

    private void applyTeamMemberFields(TeamMember point, TeamMemberDto dto) {
        if (dto.getOrderNumber() != null) {
            point.setOrderNumber(dto.getOrderNumber().trim());
        }
        if (dto.getPointTitle() != null) {
            point.setPointTitle(dto.getPointTitle().trim());
        }
        if (dto.getPointDescription() != null) {
            point.setPointDescription(dto.getPointDescription().trim());
        }
        point.setSortOrder(parseIntOrNull(dto.getSortOrder()));
    }

    private void validateFaq(FaqDto dto) {
        if (dto.getQuestion() == null || dto.getQuestion().isBlank()) {
            throw new IllegalArgumentException("Please enter the question.");
        }
        if (dto.getAnswer() == null || dto.getAnswer().isBlank()) {
            throw new IllegalArgumentException("Please enter the answer.");
        }
    }

    private void validateTier(ContributionTierDto dto) {
        if (dto.getTierName() == null || dto.getTierName().isBlank()) {
            throw new IllegalArgumentException("Please give this tier a name.");
        }
        if (dto.getAmount() == null || dto.getAmount().isBlank()) {
            throw new IllegalArgumentException("Please enter the amount for this tier.");
        }
    }

    private void applyTierFields(ContributionTier tier, ContributionTierDto dto) {
        if (dto.getAmount() != null) {
            tier.setAmount(dto.getAmount().trim());
        }
        if (dto.getTierName() != null) {
            tier.setTierName(dto.getTierName().trim());
        }
        if (dto.getDescription() != null) {
            tier.setDescription(dto.getDescription().trim());
        }
        if (dto.getRecurring() != null && !dto.getRecurring().isBlank()) {
            tier.setRecurring(Boolean.parseBoolean(dto.getRecurring().trim()));
        }
        tier.setSortOrder(parseIntOrNull(dto.getSortOrder()));
    }

    private static Integer parseIntOrNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return Integer.parseInt(s.trim());
    }

    private ArticleDto toDto(Article article) {
        ArticleDto dto = new ArticleDto();
        dto.setId(article.getId());
        dto.setTitle(article.getTitle());
        dto.setSlug(article.getSlug());
        dto.setBody(article.getBody());
        dto.setExcerpt(article.getExcerpt());
        dto.setCategory(article.getCategory());
        dto.setAuthorName(article.getAuthorName());
        dto.setFeaturedImageUrl(article.getFeaturedImageUrl());
        dto.setStatus(article.getStatus() == null ? null : article.getStatus().name());
        dto.setArticleType(article.getArticleType() == null ? null : article.getArticleType().name());
        dto.setPublishedAt(article.getPublishedAt() == null ? null : article.getPublishedAt().toString());
        dto.setViewCount(article.getViewCount() == null ? null : article.getViewCount().toString());
        return dto;
    }

    private PodcastDto toDto(Podcast podcast) {
        PodcastDto dto = new PodcastDto();
        dto.setId(podcast.getId());
        dto.setEpisodeNumber(podcast.getEpisodeNumber());
        dto.setTitle(podcast.getTitle());
        dto.setDescription(podcast.getDescription());
        dto.setAudioUrl(podcast.getAudioUrl());
        dto.setDuration(podcast.getDuration());
        dto.setCoverImageUrl(podcast.getCoverImageUrl());
        dto.setSpotifyUrl(podcast.getSpotifyUrl());
        dto.setApplePodcastsUrl(podcast.getApplePodcastsUrl());
        dto.setYoutubeUrl(podcast.getYoutubeUrl());
        dto.setRssUrl(podcast.getRssUrl());
        dto.setPodcastTitle(podcast.getPodcastTitle());
        dto.setPodcastDescription(podcast.getPodcastDescription());
        dto.setPublishedAt(podcast.getPublishedAt() == null ? null : podcast.getPublishedAt().toString());
        return dto;
    }

    private TeamMemberDto toDto(TeamMember point) {
        TeamMemberDto dto = new TeamMemberDto();
        dto.setId(point.getId());
        dto.setOrderNumber(point.getOrderNumber());
        dto.setPointTitle(point.getPointTitle());
        dto.setPointDescription(point.getPointDescription());
        dto.setSortOrder(point.getSortOrder() == null ? null : point.getSortOrder().toString());
        dto.setImageUrl(point.getImageUrl());
        return dto;
    }

    private ContributionTierDto toDto(ContributionTier tier) {
        ContributionTierDto dto = new ContributionTierDto();
        dto.setId(tier.getId());
        dto.setAmount(tier.getAmount());
        dto.setTierName(tier.getTierName());
        dto.setDescription(tier.getDescription());
        dto.setRecurring(tier.getRecurring() == null ? null : tier.getRecurring().toString());
        dto.setSortOrder(tier.getSortOrder() == null ? null : tier.getSortOrder().toString());
        return dto;
    }

    private FaqDto toDto(Faq faq) {
        FaqDto dto = new FaqDto();
        dto.setId(faq.getId());
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        dto.setSortOrder(faq.getSortOrder() == null ? null : faq.getSortOrder().toString());
        return dto;
    }
}
