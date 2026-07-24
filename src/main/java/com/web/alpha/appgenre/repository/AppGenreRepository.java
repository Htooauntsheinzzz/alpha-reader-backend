package com.web.alpha.appgenre.repository;

import com.web.alpha.appgenre.entity.AppGenre;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppGenreRepository extends JpaRepository<AppGenre, Long> {

	Optional<AppGenre> findByIdAndIsDeleted(Long id, Integer isDeleted);

	List<AppGenre> findAllByIsDeletedOrderByIdAsc(Integer isDeleted);

	boolean existsByNameAndIsDeleted(String name, Integer isDeleted);

	boolean existsByNameIgnoreCaseAndIsDeleted(String name, Integer isDeleted);
}
