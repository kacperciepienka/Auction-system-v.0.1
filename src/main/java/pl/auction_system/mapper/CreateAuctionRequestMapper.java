package pl.auction_system.mapper;

import org.mapstruct.Mapper;
import pl.auction_system.dto.CreateAuctionRequest;
import pl.auction_system.model.Auction;

@Mapper(componentModel = "spring")
public interface CreateAuctionRequestMapper {
    Auction toEntity(CreateAuctionRequest createAuctionRequest);
}
