package org.infnet.listingservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum AuctionLotCategory {
    ELECTRONICS,
    VEHICLES,
    FASHION,
    COLLECTIBLES_AND_ART,
    SPORTS,
    HEALTH_AND_BEAUTY,
    BOOKS,
    MOVIE,
    INDUSTRIAL,
    JEWELRY,
    PETS,
    TOYS,
    HOME_AND_GARDEN,
    MUSIC,
    OTHER;

    @JsonCreator
    public static AuctionLotCategory fromString(String value) {
        if (value == null) return null;

        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Status desconhecido: " + value));
    }
}
