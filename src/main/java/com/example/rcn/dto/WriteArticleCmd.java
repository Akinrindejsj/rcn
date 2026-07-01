package com.example.rcn.dto;

import org.springframework.web.multipart.MultipartFile;

/**
 * Visitor-facing "Write for the Revolution" submission. This is intentionally
 * separate from the editor DTO — a public visitor should never set status,
 * slug, or publishing metadata.
 */
public class WriteArticleCmd {
    private String title;
    private String category;
    private String body;
    private String authorName;
    private String emailAddress;
    private MultipartFile attachment;

    // Activity-specific fields
    private String activityLocation;
    private String activityType;

    // submissionType: "article" or "activity"
    private String submissionType;

    public WriteArticleCmd() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public MultipartFile getAttachment() {
        return attachment;
    }

    public void setAttachment(MultipartFile attachment) {
        this.attachment = attachment;
    }

    public String getActivityLocation() {
        return activityLocation;
    }

    public void setActivityLocation(String activityLocation) {
        this.activityLocation = activityLocation;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getSubmissionType() {
        return submissionType;
    }

    public void setSubmissionType(String submissionType) {
        this.submissionType = submissionType;
    }

}
