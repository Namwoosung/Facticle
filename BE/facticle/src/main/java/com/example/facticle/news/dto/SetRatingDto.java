package com.example.facticle.news.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SetRatingDto {


    @NotNull(message = "rating must not be null")
    @DecimalMin(value = "1.0", inclusive = true, message = "rating must be at least 1.0")
    @DecimalMax(value = "5.0", inclusive = true, message = "rating must be at most 5.0")
    private BigDecimal rating;
}
