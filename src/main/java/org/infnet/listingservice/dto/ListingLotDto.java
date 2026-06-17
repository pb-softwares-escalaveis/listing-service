package org.infnet.listingservice.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ListingLotDto(
        Long id,
        String title,
        String description,
        String mainImageUrl,
        BigDecimal currentBidPrice,
        String category,
        String status,
        Instant expirationDate
) {
}
