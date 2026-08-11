package com.web.alpha.membership.entity;

import com.web.alpha.membership.enums.MembershipDurationUnit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "membership_plans")
@Getter
@Setter
public class MembershipPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_id", length = 100, unique = true)
    private String planId;

    @Column(name = "plan_name" , nullable = false, length = 300)
    private String name;

    @Column(name = "price" , nullable = false)
    private BigDecimal price;

    @Column(name = "duration")
    private Long duration;

    @Column(name = "description", length = 500 )
    private String description;

    @Convert(converter = MembershipDurationUnitConverter.class)
    @Column(name = "duration_unit")
    private MembershipDurationUnit durationUnit;

    @Column(name = "access_level", nullable = false)
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer accessLevel;

    @Column(name = "is_lifetime", nullable = false)
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer isLifetime;

    @Column(name = "is_active" , nullable = false)
    @JdbcTypeCode(SqlTypes.SMALLINT) // 1 or 0
    private Integer isActive;

    @Column(name = "is_deleted" , nullable = false)
    @JdbcTypeCode(SqlTypes.SMALLINT)
    private Integer isDeleted;

    @Column(name = "created_by" , nullable = false)
    private Long createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "update_by")
    private Long updatedBy;

    @Column(name = "update_at")
    private LocalDateTime updatedAt;
}
