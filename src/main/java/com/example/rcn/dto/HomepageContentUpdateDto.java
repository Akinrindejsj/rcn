package com.example.rcn.dto;

/**
 * Plain POJO carrying the text-only fields of a homepage update. Images are
 * passed separately as {@code MultipartFile} parameters so this DTO can be
 * bound with {@code @ModelAttribute} without mixing in file parts.
 */
public class HomepageContentUpdateDto {

    // Hero
    private String heroHeadline;
    private String heroSubtext;
    private String heroPrimaryButtonText;
    private String heroPrimaryButtonUrl;
    private String heroSecondaryButtonText;
    private String heroSecondaryButtonUrl;

    // Ticker
    private String tickerLine1;
    private String tickerLine2;
    private String tickerLine3;
    private String tickerLine4;
    private String tickerLine5;

    // Featured article
    private String featuredArticleTitle;
    private String featuredArticleBody;
    private String featuredArticleDate;
    private String featuredArticleUrl;

    // Homepage selections
    private String featuredArticleId;
    private String homepageArticleIds;
    private String homepagePodcastIds;
    private String homepageActivityIds;

    // Per-section image URLs (populated by the service after upload; echoed back
    // so the CMS editor can re-render previews after a save).
    private String featuredArticleImageUrl;
    private String podcastSectionImageUrl;
    private String activitySectionImageUrl;
    private String buildPartyImageUrl;
    private String tickerBackgroundImageUrl;

    // Stats
    private String stat1Number;
    private String stat1Label;
    private String stat2Number;
    private String stat2Label;
    private String stat3Number;
    private String stat3Label;

    public String getHeroHeadline() {
        return heroHeadline;
    }

    public void setHeroHeadline(String heroHeadline) {
        this.heroHeadline = heroHeadline;
    }

    public String getHeroSubtext() {
        return heroSubtext;
    }

    public void setHeroSubtext(String heroSubtext) {
        this.heroSubtext = heroSubtext;
    }

    public String getHeroPrimaryButtonText() {
        return heroPrimaryButtonText;
    }

    public void setHeroPrimaryButtonText(String heroPrimaryButtonText) {
        this.heroPrimaryButtonText = heroPrimaryButtonText;
    }

    public String getHeroPrimaryButtonUrl() {
        return heroPrimaryButtonUrl;
    }

    public void setHeroPrimaryButtonUrl(String heroPrimaryButtonUrl) {
        this.heroPrimaryButtonUrl = heroPrimaryButtonUrl;
    }

    public String getHeroSecondaryButtonText() {
        return heroSecondaryButtonText;
    }

    public void setHeroSecondaryButtonText(String heroSecondaryButtonText) {
        this.heroSecondaryButtonText = heroSecondaryButtonText;
    }

    public String getHeroSecondaryButtonUrl() {
        return heroSecondaryButtonUrl;
    }

    public void setHeroSecondaryButtonUrl(String heroSecondaryButtonUrl) {
        this.heroSecondaryButtonUrl = heroSecondaryButtonUrl;
    }

    public String getTickerLine1() {
        return tickerLine1;
    }

    public void setTickerLine1(String tickerLine1) {
        this.tickerLine1 = tickerLine1;
    }

    public String getTickerLine2() {
        return tickerLine2;
    }

    public void setTickerLine2(String tickerLine2) {
        this.tickerLine2 = tickerLine2;
    }

    public String getTickerLine3() {
        return tickerLine3;
    }

    public void setTickerLine3(String tickerLine3) {
        this.tickerLine3 = tickerLine3;
    }

    public String getTickerLine4() {
        return tickerLine4;
    }

    public void setTickerLine4(String tickerLine4) {
        this.tickerLine4 = tickerLine4;
    }

    public String getTickerLine5() {
        return tickerLine5;
    }

    public void setTickerLine5(String tickerLine5) {
        this.tickerLine5 = tickerLine5;
    }

    public String getFeaturedArticleTitle() {
        return featuredArticleTitle;
    }

    public void setFeaturedArticleTitle(String featuredArticleTitle) {
        this.featuredArticleTitle = featuredArticleTitle;
    }

    public String getFeaturedArticleBody() {
        return featuredArticleBody;
    }

    public void setFeaturedArticleBody(String featuredArticleBody) {
        this.featuredArticleBody = featuredArticleBody;
    }

    public String getFeaturedArticleDate() {
        return featuredArticleDate;
    }

    public void setFeaturedArticleDate(String featuredArticleDate) {
        this.featuredArticleDate = featuredArticleDate;
    }

    public String getFeaturedArticleUrl() {
        return featuredArticleUrl;
    }

    public void setFeaturedArticleUrl(String featuredArticleUrl) {
        this.featuredArticleUrl = featuredArticleUrl;
    }

    public String getStat1Number() {
        return stat1Number;
    }

    public void setStat1Number(String stat1Number) {
        this.stat1Number = stat1Number;
    }

    public String getStat1Label() {
        return stat1Label;
    }

    public void setStat1Label(String stat1Label) {
        this.stat1Label = stat1Label;
    }

    public String getStat2Number() {
        return stat2Number;
    }

    public void setStat2Number(String stat2Number) {
        this.stat2Number = stat2Number;
    }

    public String getStat2Label() {
        return stat2Label;
    }

    public void setStat2Label(String stat2Label) {
        this.stat2Label = stat2Label;
    }

    public String getStat3Number() {
        return stat3Number;
    }

    public void setStat3Number(String stat3Number) {
        this.stat3Number = stat3Number;
    }

    public String getStat3Label() {
        return stat3Label;
    }

    public void setStat3Label(String stat3Label) {
        this.stat3Label = stat3Label;
    }

    public String getFeaturedArticleId() {
        return featuredArticleId;
    }

    public void setFeaturedArticleId(String featuredArticleId) {
        this.featuredArticleId = featuredArticleId;
    }

    public String getHomepageArticleIds() {
        return homepageArticleIds;
    }

    public void setHomepageArticleIds(String homepageArticleIds) {
        this.homepageArticleIds = homepageArticleIds;
    }

    public String getHomepagePodcastIds() {
        return homepagePodcastIds;
    }

    public void setHomepagePodcastIds(String homepagePodcastIds) {
        this.homepagePodcastIds = homepagePodcastIds;
    }

    public String getHomepageActivityIds() {
        return homepageActivityIds;
    }

    public void setHomepageActivityIds(String homepageActivityIds) {
        this.homepageActivityIds = homepageActivityIds;
    }

    public String getFeaturedArticleImageUrl() {
        return featuredArticleImageUrl;
    }

    public void setFeaturedArticleImageUrl(String featuredArticleImageUrl) {
        this.featuredArticleImageUrl = featuredArticleImageUrl;
    }

    public String getPodcastSectionImageUrl() {
        return podcastSectionImageUrl;
    }

    public void setPodcastSectionImageUrl(String podcastSectionImageUrl) {
        this.podcastSectionImageUrl = podcastSectionImageUrl;
    }

    public String getActivitySectionImageUrl() {
        return activitySectionImageUrl;
    }

    public void setActivitySectionImageUrl(String activitySectionImageUrl) {
        this.activitySectionImageUrl = activitySectionImageUrl;
    }

    public String getBuildPartyImageUrl() {
        return buildPartyImageUrl;
    }

    public void setBuildPartyImageUrl(String buildPartyImageUrl) {
        this.buildPartyImageUrl = buildPartyImageUrl;
    }

    public String getTickerBackgroundImageUrl() {
        return tickerBackgroundImageUrl;
    }

    public void setTickerBackgroundImageUrl(String tickerBackgroundImageUrl) {
        this.tickerBackgroundImageUrl = tickerBackgroundImageUrl;
    }
}
