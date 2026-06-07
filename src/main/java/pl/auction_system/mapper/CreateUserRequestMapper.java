package pl.auction_system.mapper;

import org.mapstruct.Mapper;
import pl.auction_system.dto.CreateUserRequest;
import pl.auction_system.model.User;

@Mapper(componentModel = "spring")
public interface CreateUserRequestMapper {
    User toEntity(CreateUserRequest createUserRequest);
}
