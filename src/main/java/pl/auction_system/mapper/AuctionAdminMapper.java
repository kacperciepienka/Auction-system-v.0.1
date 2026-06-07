package pl.auction_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.auction_system.dto.AuctionAdminDto;
import pl.auction_system.model.Auction;

@Mapper(componentModel = "spring")
public interface AuctionAdminMapper {

    @Mapping(target = "ownerNumber", source = "owner.userNumber")
    @Mapping(target = "ownerUsername", source = "owner.username")
    @Mapping(target = "ownerEmail", source = "owner.email")

    AuctionAdminDto toDto(Auction auction);
}
