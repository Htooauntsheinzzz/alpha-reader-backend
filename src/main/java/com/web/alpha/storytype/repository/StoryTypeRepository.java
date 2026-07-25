package com.web.alpha.storytype.repository;

import com.web.alpha.storytype.entity.StoryType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryTypeRepository extends JpaRepository<StoryType, Long> {

	Optional<StoryType> findByIdAndIsDeleted(Long id, Integer isDeleted);

	List<StoryType> findAllByIsDeletedOrderByIdDesc(Integer isDeleted);

	boolean existsByNameAndIsDeleted(String name, Integer isDeleted);
}
