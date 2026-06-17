package org.infnet.listingservice.repository;

import org.infnet.listingservice.model.ListingLotDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListingLotRepository extends ElasticsearchRepository<ListingLotDocument, Long> {}