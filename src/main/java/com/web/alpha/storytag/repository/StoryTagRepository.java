package com.web.alpha.storytag.repository;

import com.web.alpha.storytag.entity.StoryTag;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryTagRepository extends JpaRepository<StoryTag, Long> {

    Optional<StoryTag> findByIdAndIsDeleted(Long id, Integer isDeleted);

    List<StoryTag> findAllByIsDeletedOrderByIdDesc(Integer isDeleted);

    boolean existsByNameAndIsDeleted(String name, Integer isDeleted);
}