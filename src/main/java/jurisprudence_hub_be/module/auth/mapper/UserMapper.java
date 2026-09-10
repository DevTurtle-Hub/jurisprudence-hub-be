package jurisprudence_hub_be.module.auth.mapper;

import jurisprudence_hub_be.common.mapper.CentralMapperConfig;
import jurisprudence_hub_be.module.auth.dto.request.RegisterRequest;
import jurisprudence_hub_be.module.auth.dto.response.UserProfileResponse;
import jurisprudence_hub_be.module.auth.dto.response.UserResponse;
import jurisprudence_hub_be.module.auth.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface UserMapper {

    @Mapping(target = "loggedInAt", expression = "java(java.time.Instant.now())")
    UserResponse toUserResponse(User user);

    UserProfileResponse toProfileResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "avatarUrl", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toEntity(RegisterRequest request);
}
