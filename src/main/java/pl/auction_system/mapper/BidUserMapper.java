package pl.auction_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.auction_system.dto.BidAdminDto;
import pl.auction_system.dto.BidUserDto;
import pl.auction_system.model.Bid;

@Mapper(componentModel = "spring")
public interface BidUserMapper {

    @Mapping(target = "bidderUsername", source = "bidder.username")
    @Mapping(target = "auctionReferenceNumber", source = "auction.referenceNumber")
    @Mapping(target = "auctionTitle", source = "auction.title")

    BidUserDto toDto(Bid bid);
}
