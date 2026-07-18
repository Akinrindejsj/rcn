package com.example.rcn.service;

import com.example.rcn.dto.JoinCmd;
import com.example.rcn.model.SiteSettings;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Sends the membership-application notification email when a visitor submits
 * a join form. The message is addressed to the configured join-notification
 * recipient (see {@link SiteSettings#getJoinNotificationEmail()}), defaults to
 * akinrindeakinkunmi2006@gmail.com, and uses the visitor's own email as the
 * reply-to so a comrade can respond directly to them.
 */
@Service
public class EmailService {

    private static final String DEFAULT_JOIN_RECIPIENT = "akinrindeakinkunmi2006@gmail.com";

    private final JavaMailSender mailSender;
    private final SiteSettingsService siteSettingsService;

    public EmailService(JavaMailSender mailSender, SiteSettingsService siteSettingsService) {
        this.mailSender = mailSender;
        this.siteSettingsService = siteSettingsService;
    }

    /**
     * Builds and sends the join notification. Throws {@link MailException} (or a
     * wrapped runtime exception) on failure so the caller can surface an error
     * and must not report success.
     */
    public void sendJoinNotification(JoinCmd cmd) {
        String recipient = resolveRecipient();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipient);
        message.setFrom(cmd.getEmail());
        message.setReplyTo(cmd.getEmail());
        message.setSubject(buildSubject(cmd));
        message.setText(buildBody(cmd));

        mailSender.send(message);
    }

    private String resolveRecipient() {
        SiteSettings settings = siteSettingsService.getSingleton();
        String recipient = settings.getJoinNotificationEmail();
        return (recipient == null || recipient.isBlank()) ? DEFAULT_JOIN_RECIPIENT : recipient.trim();
    }

    private String buildSubject(JoinCmd cmd) {
        return "New RCN membership application — " + cmd.getFirstName() + " " + cmd.getLastName()
                + " (" + cmd.getMembershipType() + ")";
    }

    private String buildBody(JoinCmd cmd) {
        StringBuilder body = new StringBuilder();
        body.append("A new membership application has been submitted via the RCN website.\n\n");
        body.append("Name:           ").append(cmd.getFirstName()).append(" ").append(cmd.getLastName()).append("\n");
        body.append("Email:          ").append(cmd.getEmail()).append("\n");
        body.append("Phone/WhatsApp: ").append(cmd.getPhone()).append("\n");
        body.append("City / State:   ").append(cmd.getCityState()).append("\n");
        body.append("Membership:     ").append(cmd.getMembershipType()).append("\n");
        body.append("Found us via:   ").append(cmd.getFoundUs()).append("\n");
        body.append("Anything else:  ")
                .append(cmd.getAnythingElse() == null || cmd.getAnythingElse().isBlank()
                        ? "(not provided)" : cmd.getAnythingElse())
                .append("\n");
        body.append("\n--\nReply directly to this applicant at ").append(cmd.getEmail()).append(".");
        return body.toString();
    }
}
