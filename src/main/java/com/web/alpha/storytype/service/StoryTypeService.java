package com.web.alpha.storytype.service;

import com.web.alpha.storytype.dto.StoryTypeCreateRequest;
import com.web.alpha.storytype.dto.StoryTypeResponse;
import com.web.alpha.storytype.dto.StoryTypeUpdateRequest;
import java.util.List;

public interface StoryTypeService {

	StoryTypeResponse create(StoryTypeCreateRequest request);

	StoryTypeResponse update(Long id, StoryTypeUpdateRequest request);

	void delete(Long id);

	StoryTypeResponse getById(Long id);

	List<StoryTypeResponse> getAll();
}
