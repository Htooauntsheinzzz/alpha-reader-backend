package com.web.alpha.appgenre.service;

import com.web.alpha.appgenre.dto.AppGenreCreateRequest;
import com.web.alpha.appgenre.dto.AppGenreResponse;
import com.web.alpha.appgenre.dto.AppGenreUpdateRequest;
import java.util.List;

public interface AppGenreService {

	AppGenreResponse create(AppGenreCreateRequest request);

	AppGenreResponse update(Long id, AppGenreUpdateRequest request);

	void delete(Long id);

	AppGenreResponse getById(Long id);

	List<AppGenreResponse> getAll();
}
