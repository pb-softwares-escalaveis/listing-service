package org.infnet.listingservice.dto.events;

import java.util.UUID;

public record AuctionStatusChangeEvent(
        Long auctionId,
        UUID correlationId
) {
}
