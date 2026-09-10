package jurisprudence_hub_be.module.interaction.mapper;

import jurisprudence_hub_be.common.mapper.CentralMapperConfig;
import jurisprudence_hub_be.module.interaction.dto.request.AnnotationCreateRequest;
import jurisprudence_hub_be.module.interaction.dto.response.AnnotationResponse;
import jurisprudence_hub_be.module.interaction.entity.UserAnnotation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface AnnotationMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    AnnotationResponse toResponse(UserAnnotation annotation);

    List<AnnotationResponse> toResponseList(List<UserAnnotation> annotations);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    UserAnnotation toEntity(AnnotationCreateRequest request);
}
