package org.infnet.listingservice.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infnet.listingservice.dto.events.*;
import org.infnet.listingservice.enums.AuctionStatus;
import org.infnet.listingservice.exception.ListingProjectionNotFound;
import org.infnet.listingservice.model.ListingLotDocument;
import org.infnet.listingservice.repository.ListingLotRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuctionEventListener {
    private final ListingLotRepository lotRepository;

    @KafkaListener(topics = "${app.kafka-topics.auction-approved}")
    public void consumeApproved(AuctionApprovedEvent event){
        log.info("Consumido evento de AuctionApproved. Anúncio: {}", event.auctionId());

        ListingLotDocument lot = new  ListingLotDocument();
        lot.setId(event.auctionId());
        lot.setTitle(event.auctionTitle());
        lot.setDescription(event.description());
        lot.setCurrentBidPrice(event.currentPrice());
        lot.setBuyNowPrice(event.buyNowPrice());
        lot.setMainImageUrl(event.auctionThumb());
        lot.setStatus(AuctionStatus.ACTIVE);
        lot.setInitialBidPrice(event.initialBidPrice());
        lot.setCategory(event.category());
        lot.setExpirationDate(event.expirationDate());
        lot.setCreatedAt(event.createdAt());
        lotRepository.save(lot);
    }

    @KafkaListener(topics = {"${app.kafka-topics.auction-ended-with-winner}"})
    public void consumeEndedWithWinner(AuctionStatusChangeEvent event){
        log.info("Consumido evento de AuctionEndedWinnerEvent. Anúncio: {}", event.auctionId());

        var lot = getLot(event.auctionId());

        lot.setStatus(AuctionStatus.SOLD);
        lotRepository.save(lot);
    }

    @KafkaListener(topics = {"${app.kafka-topics.auction-ended-without-winner}"})
    public void consumeEndedWithoutWinner(AuctionStatusChangeEvent event){
        log.info("Consumido evento de AuctionEndedWithoutWinnerEvent. Anúncio: {}", event.auctionId());

        var lot = getLot(event.auctionId());

        lot.setStatus(AuctionStatus.EXPIRED);
        lotRepository.save(lot);
    }

    @KafkaListener(topics = {"${app.kafka-topics.auction-removed}"})
    public void consumeRemoved(AuctionStatusChangeEvent event){
        log.info("Consumido evento de AuctionRemoved. Anúncio: {}", event.auctionId());

        var lot = getLot(event.auctionId());

        lot.setStatus(AuctionStatus.REMOVED);
        lotRepository.save(lot);
    }

    @KafkaListener(topics = {"${app.kafka-topics.auction-canceled}"})
    public void consumeCanceled(AuctionStatusChangeEvent event){
        log.info("Consumido evento de AuctionCanceled. Anúncio: {}", event.auctionId());

        var lot = getLot(event.auctionId());

        lot.setStatus(AuctionStatus.CANCELED);
        lotRepository.save(lot);
    }

    @KafkaListener(topics = {"${app.kafka-topics.auction-renewed}"})
    public void consumeRenewed(AuctionStatusChangeEvent event){
        log.info("Consumido evento de AuctionRenewed. Anúncio: {}", event.auctionId());

        var lot = getLot(event.auctionId());

        lot.setStatus(AuctionStatus.ACTIVE);
        lot.setCurrentBidPrice(lot.getInitialBidPrice());
        lotRepository.save(lot);
    }

    @KafkaListener(topics = {"${app.kafka-topics.bid-highest-invalidated}"})
    public void consumeBidInvalidated(AuctionBidChanged event){
        log.info("Consumido evento de NewHighestBidderAssigned. Anúncio: {}", event.auctionId());

        var lot = getLot(event.auctionId());

        lot.setCurrentBidPrice(event.newPrice());
        lotRepository.save(lot);
    }

    @KafkaListener(topics = {"${app.kafka-topics.auction-bid-reset}"})
    public void consumeBidReset(AuctionStatusChangeEvent event){
        log.info("Consumido evento de AuctionBidReset. Anúncio: {}", event.auctionId());

        var lot = getLot(event.auctionId());

        lot.setCurrentBidPrice(lot.getInitialBidPrice());
        lotRepository.save(lot);
    }

    @KafkaListener(topics = "${app.kafka-topics.bid-placed}")
    public void consumeBidPlaced(AuctionBidChanged event){
        log.info("Consumido evento de BidPlaced. Anúncio: {}", event.auctionId());

        var lot = getLot(event.auctionId());

        lot.setCurrentBidPrice(event.newPrice());
        lotRepository.save(lot);
    }

    private ListingLotDocument getLot(Long lotId){
        return lotRepository.findById(lotId)
                .orElseThrow(() -> new ListingProjectionNotFound(
                        "Anúncio não encontrado na projeção local: " + lotId));
    }
}
