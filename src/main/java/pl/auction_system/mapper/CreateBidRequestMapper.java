package pl.auction_system.mapper;

import org.mapstruct.Mapper;
import pl.auction_system.dto.CreateBidRequest;
import pl.auction_system.model.Bid;

@Mapper(componentModel = "spring")
public interface CreateBidRequestMapper {
    Bid toEntity(CreateBidRequest createBidRequest);
}
