package com.web.alpha.storytag.entity;

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
@Table(name = "story_tag")
@Getter
@Setter
public class StoryTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "created_date")
    private LocalDate createdDate;

    @Column(name = "is_active", nullable = false)
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer isActive;

    @Column(length = 500)
    private String description;

    @Column(name = "is_deleted", nullable = false)
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer isDeleted;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public StoryTag() {
    }
}