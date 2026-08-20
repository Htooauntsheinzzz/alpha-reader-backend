package com.web.alpha.storytag.controller;

import com.web.alpha.storytag.dto.StoryTagCreateRequest;
import com.web.alpha.storytag.dto.StoryTagResponse;
import com.web.alpha.storytag.dto.StoryTagUpdateRequest;
import com.web.alpha.storytag.service.StoryTagService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/content/story-tags")
public class StoryTagController {

    private final StoryTagService storyTagService;

    public StoryTagController(StoryTagService storyTagService) {
        this.storyTagService = storyTagService;
    }

    @PostMapping
    public ResponseEntity<StoryTagResponse> create(@Valid @RequestBody StoryTagCreateRequest request) {
        StoryTagResponse response = storyTagService.create(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/content/story-tags/" + response.id())).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryTagResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody StoryTagUpdateRequest request
    ) {
        return ResponseEntity.ok(storyTagService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        storyTagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryTagResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(storyTagService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<StoryTagResponse>> getAll() {
        return ResponseEntity.ok(storyTagService.getAll());
    }
}