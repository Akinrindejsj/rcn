package com.example.rcn.dto;

public record SearchResultDto(
        String type,
        String title,
        String excerpt,
        String url,
        String imageUrl,
        String dateLabel
) {
}
