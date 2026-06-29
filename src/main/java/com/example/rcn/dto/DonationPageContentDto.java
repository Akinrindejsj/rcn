package com.example.rcn.dto;

public class DonationPageContentDto {

    private Long id;
    private String pageHeading;
    private String introText;
    private String footerQuote;
    private String footnote;
    private String printingPressImageUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPageHeading() {
        return pageHeading;
    }

    public void setPageHeading(String pageHeading) {
        this.pageHeading = pageHeading;
    }

    public String getIntroText() {
        return introText;
    }

    public void setIntroText(String introText) {
        this.introText = introText;
    }

    public String getFooterQuote() {
        return footerQuote;
    }

    public void setFooterQuote(String footerQuote) {
        this.footerQuote = footerQuote;
    }

    public String getFootnote() {
        return footnote;
    }

    public void setFootnote(String footnote) {
        this.footnote = footnote;
    }

    public String getPrintingPressImageUrl() {
        return printingPressImageUrl;
    }

    public void setPrintingPressImageUrl(String printingPressImageUrl) {
        this.printingPressImageUrl = printingPressImageUrl;
    }
}
