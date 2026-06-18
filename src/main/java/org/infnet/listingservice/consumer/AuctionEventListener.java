package org.infnet.listingservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.infnet.listingservice.dto.events.*;
import org.infnet.listingservice.model.ListingLotDocument;
import org.infnet.listingservice.repository.ListingLotRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionEventListener {
    private final ListingLotRepository lotRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka-topics.connect-topic}")
    public void consume(DebeziumEvent event) {
        try {

            ListingLotDto data = event.after();

            if (data != null) {
                ListingLotDocument lotDoc = new ListingLotDocument();
                lotDoc.setId(data.id());
                lotDoc.setTitle(data.title());
                lotDoc.setDescription(data.description());
                lotDoc.setInitialBidPrice(data.initialBidPrice());
                lotDoc.setBuyNowPrice(data.buyNowPrice());
                lotDoc.setCurrentBidPrice(data.currentBidPrice());
                lotDoc.setMainImageUrl(data.mainImageUrl());
                lotDoc.setStatus(data.status());
                lotDoc.setCategory(data.category());
                lotDoc.setExpirationDate(data.expirationDate());
                lotDoc.setCreatedAt(data.createdAt());

                lotRepository.save(lotDoc);
                log.info("Documento {} processado com sucesso (Operação: {})", lotDoc.getId(), event.op());
            }

        } catch (Exception e) {
            log.error("Erro ao desserializar/processar evento: {}", e.getMessage());
        }
    }
}
