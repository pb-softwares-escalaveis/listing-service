package org.infnet.listingservice.dto.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DebeziumEvent(
        ListingLotDto after,
        String op
) {
}
