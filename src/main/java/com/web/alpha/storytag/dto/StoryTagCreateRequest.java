package com.web.alpha.storytag.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.web.alpha.storytag.dto.deserializer.StoryTagCreateRequestDeserializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDate;

@JsonDeserialize(using = StoryTagCreateRequestDeserializer.class)
public record StoryTagCreateRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        LocalDate createdDate,

        @Size(max = 500)
        String description

) implements Serializable {
}