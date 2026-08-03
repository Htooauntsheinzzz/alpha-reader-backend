package com.web.alpha.storytag.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.storytag.dto.deserializer.StoryTagUpdateRequestDeserializer;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@JsonDeserialize(using = StoryTagUpdateRequestDeserializer.class)
public record StoryTagUpdateRequest(
        @NotBlank
        @Size(max = 100)
        String name,

        LocalDate createdDate,

        @Size(max = 500)
        String description,

        @NotNull
        @Min(0)
        @Max(1)
        Integer isActive

) implements Serializable {
}