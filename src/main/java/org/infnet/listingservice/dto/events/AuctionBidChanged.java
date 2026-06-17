package org.infnet.listingservice.dto.events;

import java.math.BigDecimal;
import java.util.UUID;

public record AuctionBidChanged(
        Long auctionId,
        UUID correlationId,
        BigDecimal newPrice
) {
}
