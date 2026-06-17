package org.infnet.listingservice.controller;

import lombok.RequiredArgsConstructor;
import org.infnet.listingservice.dto.ListingLotDto;
import org.infnet.listingservice.dto.TitleAutocompleteDto;
import org.infnet.listingservice.enums.AuctionLotCategory;
import org.infnet.listingservice.service.ListingLotSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/listings/auctions")
@RequiredArgsConstructor
public class ListingLotController {
    private final ListingLotSearchService searchService;

    @GetMapping("/search/autocomplete")
    public ResponseEntity<List<TitleAutocompleteDto>> autocomplete(
            @RequestParam("q") String query) {
        return ResponseEntity.ok(searchService.getAutocomplete(query));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ListingLotDto>> search(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "category", required = false) AuctionLotCategory category,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sortBy", defaultValue = "_score") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "DESC") Sort.Direction sortDir) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortDir, sortBy));

        Page<ListingLotDto> results = searchService.search(
                query, category, minPrice, maxPrice, pageRequest
        );
        return ResponseEntity.ok(results);
    }

    @GetMapping("/latest")
    public ResponseEntity<Page<ListingLotDto>> getLatestListings(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "created"));

        Page<ListingLotDto> results = searchService.search(
                null, null, null, null, pageRequest
        );
        return ResponseEntity.ok(results);
    }
}