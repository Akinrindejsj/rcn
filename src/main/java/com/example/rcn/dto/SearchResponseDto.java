package com.example.rcn.dto;

import java.util.List;

public record SearchResponseDto(
        String query,
        List<SearchResultDto> results,
        long totalResults,
        int currentPage,
        int pageSize,
        int totalPages
) {
}
