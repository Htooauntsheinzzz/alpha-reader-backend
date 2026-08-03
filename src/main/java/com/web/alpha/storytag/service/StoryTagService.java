package com.web.alpha.storytag.service;

import com.web.alpha.storytag.dto.StoryTagCreateRequest;
import com.web.alpha.storytag.dto.StoryTagResponse;
import com.web.alpha.storytag.dto.StoryTagUpdateRequest;
import java.util.List;

public interface StoryTagService {

    StoryTagResponse create(StoryTagCreateRequest request);

    StoryTagResponse update(Long id, StoryTagUpdateRequest request);

    void delete(Long id);

    StoryTagResponse getById(Long id);

    List<StoryTagResponse> getAll();
}