package com.example.rcn.util;

/**
 * Utility class for string manipulation and formatting.
 */
public class StringUtils {

    /**
     * Generates initials from a full name or username.
     *
     * Examples:
     * - "Amaka Okonkwo" → "AO"
     * - "John Smith" → "JS"
     * - "james" → "J"
     * - "amaka.okonkwo@example.com" → "AO" (removes domain)
     *
     * @param name the full name or username
     * @return the initials in uppercase, max 2 characters
     */
    public static String getInitials(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "U";  // Default to "U" for unknown
        }

        // Remove email domain if present
        if (name.contains("@")) {
            name = name.substring(0, name.indexOf("@"));
        }

        // Replace common separators with spaces
        name = name.replaceAll("[._-]", " ").trim();

        // Split into parts
        String[] parts = name.split("\\s+");

        if (parts.length == 0) {
            return "U";
        }

        // Get initials from first two parts
        StringBuilder initials = new StringBuilder();

        for (int i = 0; i < Math.min(2, parts.length); i++) {
            if (!parts[i].isEmpty()) {
                initials.append(parts[i].charAt(0));
            }
        }

        return initials.toString().toUpperCase();
    }
}

