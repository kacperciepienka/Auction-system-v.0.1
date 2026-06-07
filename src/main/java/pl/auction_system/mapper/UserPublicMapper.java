package pl.auction_system.mapper;

import org.mapstruct.Mapper;

import pl.auction_system.dto.UserPublicDto;
import pl.auction_system.model.User;

@Mapper(componentModel = "spring")
public interface UserPublicMapper {
    UserPublicDto toDto(User user);
}
