package pl.auction_system.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.auction_system.dto.UserAdminDto;
import pl.auction_system.model.User;

@Mapper(componentModel = "spring")
public interface UserAdminMapper {
    @Mapping(target = "name", expression = "java(user.getFirstName() + \" \" + user.getLastName())")

    UserAdminDto toDto(User user);
}
