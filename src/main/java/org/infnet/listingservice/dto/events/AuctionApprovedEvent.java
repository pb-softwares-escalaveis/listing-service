package org.infnet.listingservice.dto.events;

import org.infnet.listingservice.enums.AuctionLotCategory;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record AuctionApprovedEvent(
        Long auctionId,
        UUID sellerId,
        String auctionTitle,
        String auctionThumb,
        AuctionLotCategory category,
        String description,
        Instant expirationDate,
        Instant createdAt,
        BigDecimal currentPrice,
        BigDecimal initialBidPrice,
        BigDecimal buyNowPrice,
        UUID correlationId
) {
}
