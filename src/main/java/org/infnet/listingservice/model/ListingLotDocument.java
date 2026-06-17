package org.infnet.listingservice.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.infnet.listingservice.enums.AuctionLotCategory;
import org.infnet.listingservice.enums.AuctionStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Document(indexName = "listing-lots")
@Setting(settingPath = "elastic/index.json")
@Getter @Setter
@NoArgsConstructor
public class ListingLotDocument {

    @Id
    private Long id;

    @Field(type = FieldType.Keyword)
    private UUID sellerId;

    @MultiField(
            mainField = @Field(
                    type = FieldType.Text,
                    analyzer = "autocomplete_index",
                    searchAnalyzer = "autocomplete_search"
            ),
            otherFields = {
                    @InnerField(suffix = "pt", type = FieldType.Text, analyzer = "pt_search"),
                    @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 256)
            }
    )
    private String title;

    @Field(type = FieldType.Text, analyzer = "pt_search")
    private String description;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal initialBidPrice;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal currentBidPrice;

    @Field(type = FieldType.Scaled_Float, scalingFactor = 100)
    private BigDecimal buyNowPrice;

    @Field(type = FieldType.Keyword)
    private String mainImageUrl;

    @Field(type = FieldType.Keyword)
    private AuctionStatus status;

    @Field(type = FieldType.Keyword)
    private AuctionLotCategory category;

    @Field(type = FieldType.Date)
    private Instant expirationDate;

    @Field(type = FieldType.Date)
    private Instant createdAt;
}