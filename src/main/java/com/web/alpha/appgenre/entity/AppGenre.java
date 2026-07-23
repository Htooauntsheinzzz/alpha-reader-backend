package com.web.alpha.appgenre.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "app_genre")
@Getter
@Setter
public class AppGenre {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 300)
	private String name;

	@Column(name = "created_date", nullable = false)
	private LocalDate createdDate;

	@Column(length = 500)
	private String description;

	@Column(name = "is_active", nullable = false)
	@JdbcTypeCode(SqlTypes.SMALLINT)
	private Integer isActive;

	@Column(name = "is_deleted", nullable = false)
	@JdbcTypeCode(SqlTypes.SMALLINT)
	private Integer isDeleted;

	@Column(name = "create_by", nullable = false)
	private Long createBy;

	@Column(name = "create_at", nullable = false)
	private LocalDateTime createAt;

	public AppGenre() {
	}
}
