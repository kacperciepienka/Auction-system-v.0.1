package pl.auction_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.auction_system.dto.UserUserDto;
import pl.auction_system.model.User;

@Mapper(componentModel = "spring")
public interface UserUserMapper {
    @Mapping(target = "name", expression = "java(user.getFirstName() + \" \" + user.getLastName())")

    UserUserDto toDto(User user);
}
