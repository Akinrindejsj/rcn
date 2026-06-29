package com.example.rcn.dto;

public class AboutPageContentDto {

    private Long id;
    private String heading;
    private String introText;
    private String introImageUrl;
    private String joinBannerText;
    private String joinBannerUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getIntroImageUrl() {
        return introImageUrl;
    }

    public void setIntroImageUrl(String introImageUrl) {
        this.introImageUrl = introImageUrl;
    }

    public String getJoinBannerText() {
        return joinBannerText;
    }

    public void setJoinBannerText(String joinBannerText) {
        this.joinBannerText = joinBannerText;
    }

    public String getJoinBannerUrl() {
        return joinBannerUrl;
    }

    public void setJoinBannerUrl(String joinBannerUrl) {
        this.joinBannerUrl = joinBannerUrl;
    }
}
