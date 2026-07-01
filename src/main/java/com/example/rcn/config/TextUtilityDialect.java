package com.example.rcn.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class TextUtilityDialect {

    @ModelAttribute("textUtil")
    public TextUtilityFunctions textUtil() {
        return new TextUtilityFunctions();
    }

    public static class TextUtilityFunctions {
        /**
         * Strips HTML tags from text
         */
        public String stripHtml(String html) {
            if (html == null || html.isEmpty()) {
                return "";
            }
            return html.replaceAll("<[^>]*>", "");
        }

        /**
         * Truncates text to specified length and adds ellipsis if needed.
         * Strips HTML tags first.
         */
        public String truncate(String text, int maxLength) {
            if (text == null || text.isEmpty()) {
                return "";
            }
            String plain = stripHtml(text);
            if (plain.length() <= maxLength) {
                return plain;
            }
            return plain.substring(0, maxLength).trim() + "…";
        }

        /**
         * Strips HTML and truncates to 150 characters for article cards
         */
        public String cardText(String text) {
            return truncate(text, 150);
        }
    }
}







