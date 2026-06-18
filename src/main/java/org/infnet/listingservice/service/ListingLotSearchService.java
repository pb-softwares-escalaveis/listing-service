package org.infnet.listingservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import org.infnet.listingservice.dto.ListingLotResponse;
import org.infnet.listingservice.dto.TitleAutocompleteDto;
import org.infnet.listingservice.enums.AuctionLotCategory;
import org.infnet.listingservice.enums.AuctionStatus;
import org.infnet.listingservice.model.ListingLotDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ListingLotSearchService {

    private final ElasticsearchClient esClient;
    private static final String INDEX = "listing-lots";

    private static final String STATUS_ACTIVE = AuctionStatus.ACTIVE.name();

    public ListingLotSearchService(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    public List<TitleAutocompleteDto> getAutocomplete(String query) {
        try {
            SearchResponse<ListingLotDocument> response = esClient.search(s -> s
                    .index(INDEX)
                    .query(q -> q.bool(b -> b
                            .must(m -> m.match(match -> match
                                    .field("title")
                                    .query(query)))
                            .filter(f -> f.term(t -> t
                                    .field("status")
                                    .value(STATUS_ACTIVE)))))
                    .size(5),
                    ListingLotDocument.class);

            return response.hits().hits().stream()
                    .map(Hit::source).filter(Objects::nonNull)
                    .map(doc -> new TitleAutocompleteDto(doc.getId(), doc.getTitle()))
                    .collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Erro executando busca por autocomplete", e);
        }
    }

    public Page<ListingLotResponse> search(
            String textQuery,
            AuctionLotCategory category,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            boolean endingSoon,
            PageRequest pageRequest) {

        try {
            SearchResponse<ListingLotDocument> response = esClient.search(s -> s
                            .index(INDEX)
                            .from((int) pageRequest.getOffset())
                            .size(pageRequest.getPageSize())
                            .sort(sort -> {
                                if (pageRequest.getSort().isSorted()) {
                                    var order = pageRequest.getSort().iterator().next();
                                    String esFieldName = convertSortField(order.getProperty());

                                    if ("_score".equals(esFieldName)) {
                                        return sort.score(score -> score.order(SortOrder.Desc));
                                    }

                                    SortOrder esOrder = order.isAscending() ? SortOrder.Asc : SortOrder.Desc;
                                    return sort.field(f -> f.field(esFieldName).order(esOrder));
                                }
                                return sort.score(score -> score.order(SortOrder.Desc));
                            })
                            .query(q -> q.bool(b -> {
                                if (textQuery != null && !textQuery.isBlank()) {
                                    b.must(m -> m.bool(inner -> inner
                                            .should(sm -> sm.match(mm -> mm
                                                    .field("title.pt")
                                                    .query(textQuery)
                                                    .boost(3.0f)
                                                    .fuzziness("AUTO")))
                                            .should(sm -> sm.match(mm -> mm
                                                    .field("title")
                                                    .query(textQuery)
                                                    .boost(2.0f)))
                                            .should(sm -> sm.match(mm -> mm
                                                    .field("description")
                                                    .query(textQuery)
                                                    .boost(1.0f))))

                                    );
                                }
                                b.filter(f -> f.term(t -> t.field("status").value(STATUS_ACTIVE)));

                                if (category != null) {
                                    b.filter(f -> f.term(t -> t.field("category").value(category.name())));
                                }

                                if (endingSoon) {
                                    b.filter(f -> f.range(r -> r
                                            .date(d -> d
                                                    .field("expirationDate")
                                                    .gte("now")
                                                    .lte("now+24h")
                                            )
                                    ));
                                }

                                if ((minPrice != null && minPrice.compareTo(BigDecimal.ZERO) > 0) ||
                                        (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) > 0)) {
                                    b.filter(f -> f.range(r -> r
                                            .number(n -> {
                                                n.field("currentBidPrice");
                                                if (minPrice != null) n.gte(minPrice.doubleValue());
                                                if (maxPrice != null) n.lte(maxPrice.doubleValue());
                                                return n;
                                            })
                                    ));
                                }
                                return b;
                            })),
                    ListingLotDocument.class);

            List<ListingLotResponse> dtos = mapResponseToDtoList(response);
            long totalHits = response.hits().total() != null ? response.hits().total().value() : 0;

            return new PageImpl<>(dtos, pageRequest, totalHits);

        } catch (IOException e) {
            throw new RuntimeException("Erro ao executar a busca:", e);
        }
    }

    private List<ListingLotResponse> mapResponseToDtoList(SearchResponse<ListingLotDocument> response) {
        return response.hits().hits().stream()
                .map(Hit::source)
                .filter(Objects::nonNull)
                .map(doc -> new ListingLotResponse(
                        doc.getId(),
                        doc.getTitle(),
                        doc.getDescription(),
                        doc.getMainImageUrl(),
                        doc.getCurrentBidPrice(),
                        doc.getCategory() != null ? doc.getCategory().name() : null,
                        doc.getStatus().name(),
                        doc.getExpirationDate()))
                .collect(Collectors.toList());
    }

    private String convertSortField(String frontendField) {
        if (frontendField == null || frontendField.isBlank()) {
            return "_score";
        }

        return switch (frontendField.toLowerCase()) {
            case "title" -> "title.keyword";
            case "price" -> "currentBidPrice";
            case "date" -> "expirationDate";
            case "created" -> "createdAt";
            default -> "_score";
        };
    }
}