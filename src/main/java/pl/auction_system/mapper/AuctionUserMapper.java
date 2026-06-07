package pl.auction_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.auction_system.dto.AuctionUserDto;
import pl.auction_system.model.Auction;

@Mapper(componentModel = "spring")
public interface AuctionUserMapper {

    @Mapping(target = "ownerUsername", source = "owner.username")
    @Mapping(target = "ownerEmail", source = "owner.email")

    AuctionUserDto toDto(Auction auction);
}
