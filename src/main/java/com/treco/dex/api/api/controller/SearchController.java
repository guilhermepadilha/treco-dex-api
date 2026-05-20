package com.treco.dex.api.api.controller;

import com.treco.dex.api.api.dto.ObjectSpeciesResponse;
import com.treco.dex.api.application.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/search")
@Slf4j
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<List<ObjectSpeciesResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String habitatId,
            @RequestParam(required = false) String environmentId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());

        List<ObjectSpeciesResponse> results;

        if (name != null && !name.isEmpty()) {
            results = searchService.searchObjectsByName(name, userId);
        } else if (habitatId != null && !habitatId.isEmpty()) {
            results = searchService.searchObjectsByHabitat(UUID.fromString(habitatId), userId);
        } else if (environmentId != null && !environmentId.isEmpty()) {
            results = searchService.searchObjectsByEnvironment(UUID.fromString(environmentId), userId);
        } else {
            results = List.of();
        }

        return ResponseEntity.ok(results);
    }
}
