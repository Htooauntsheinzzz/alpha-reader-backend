package com.web.alpha.appgenre.controller;

import com.web.alpha.appgenre.dto.AppGenreCreateRequest;
import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.appgenre.dto.AppGenreUpdateRequest;
import com.web.alpha.appgenre.service.AppGenreService;
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
@RequestMapping("/api/v1/admin/genres")
public class AppGenreController {

	private final AppGenreService appGenreService;

	public AppGenreController(AppGenreService appGenreService) {
		this.appGenreService = appGenreService;
	}

	@PostMapping
	public ResponseEntity<AppGenreResponse> create(@Valid @RequestBody AppGenreCreateRequest request) {
		AppGenreResponse response = appGenreService.create(request);
		return ResponseEntity.created(URI.create("/api/v1/admin/genres/" + response.id())).body(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<AppGenreResponse> update(
			@PathVariable Long id,
			@Valid @RequestBody AppGenreUpdateRequest request
	) {
		return ResponseEntity.ok(appGenreService.update(id, request));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		appGenreService.delete(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/{id}")
	public ResponseEntity<AppGenreResponse> getById(@PathVariable Long id) {
		return ResponseEntity.ok(appGenreService.getById(id));
	}

	@GetMapping
	public ResponseEntity<List<AppGenreResponse>> getAll() {
		return ResponseEntity.ok(appGenreService.getAll());
	}
}
