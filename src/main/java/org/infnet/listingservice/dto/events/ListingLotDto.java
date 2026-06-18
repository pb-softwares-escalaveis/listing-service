package org.infnet.listingservice.dto.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.infnet.listingservice.enums.AuctionLotCategory;
import org.infnet.listingservice.enums.AuctionStatus;

import java.math.BigDecimal;
import java.time.Instant;

public record ListingLotDto(
        Long id,
        @JsonProperty("buy_now_price")
        BigDecimal buyNowPrice,
        @JsonProperty("current_bid_price")
        BigDecimal currentBidPrice,
        @JsonProperty("initial_bid_price")
        BigDecimal initialBidPrice,
        String title,
        String description,
        AuctionLotCategory category,
        @JsonProperty("main_image_url")
        String mainImageUrl,
        AuctionStatus status,
        @JsonProperty("expiration_date")
        Instant expirationDate,
        @JsonProperty("created_at")
        Instant createdAt
) {
}
