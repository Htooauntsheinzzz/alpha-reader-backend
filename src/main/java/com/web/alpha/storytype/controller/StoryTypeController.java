package com.web.alpha.storytype.controller;

import com.web.alpha.storytype.dto.StoryTypeCreateRequest;
import com.web.alpha.storytype.dto.StoryTypeResponse;
import com.web.alpha.storytype.dto.StoryTypeUpdateRequest;
import com.web.alpha.storytype.service.StoryTypeService;
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
@RequestMapping("/api/v1/admin/content/story-types")
public class StoryTypeController {

	private final StoryTypeService storyTypeService;

	public StoryTypeController(StoryTypeService storyTypeService) {
		this.storyTypeService = storyTypeService;
	}

	@PostMapping
	public ResponseEntity<StoryTypeResponse> create(@Valid @RequestBody StoryTypeCreateRequest request) {
		StoryTypeResponse response = storyTypeService.create(request);
		return ResponseEntity.created(URI.create("/api/v1/admin/content/story-types/" + response.id())).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<StoryTypeResponse> update(
			@PathVariable Long id,
			@Valid @RequestBody StoryTypeUpdateRequest request
	) {
		return ResponseEntity.ok(storyTypeService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		storyTypeService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<StoryTypeResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(storyTypeService.getById(id));
	}

	@GetMapping
	public ResponseEntity<List<StoryTypeResponse>> getAll() {
		return ResponseEntity.ok(storyTypeService.getAll());
	}
}
