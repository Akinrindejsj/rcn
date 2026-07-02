package com.example.rcn.controller;

import com.example.rcn.dto.SearchResponseDto;
import com.example.rcn.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SearchController {

    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/search")
    public String search(@RequestParam(value = "q", required = false) String query,
                         @RequestParam(value = "page", defaultValue = "0") int page,
                         @RequestParam(value = "size", defaultValue = "10") int size,
                         Model model) {
        SearchResponseDto search = searchService.search(query, page, size);
        model.addAttribute("search", search);
        model.addAttribute("query", search.query());
        return "pages/search";
    }
}
