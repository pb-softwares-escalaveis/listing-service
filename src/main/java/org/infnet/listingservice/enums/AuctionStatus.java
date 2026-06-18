package org.infnet.listingservice.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.Arrays;

public enum AuctionStatus {
    PENDING_REVIEW, //status inicial
    ACTIVE, //aprovado na analise
    EXPIRED, // expirationDate acabou sem lances
    SOLD, // expirationDate acabou com lances ou valor de arremate batido
    REMOVED, //anunciante removeu o anúncio
    CANCELED, // anúncio cancelado por penalidade
    REJECTED ;//rejeitado na analise

    @JsonCreator
    public static AuctionStatus fromString(String value) {
        if (value == null) return null;

        return Arrays.stream(values())
                .filter(status -> status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Status desconhecido: " + value));
    }
}