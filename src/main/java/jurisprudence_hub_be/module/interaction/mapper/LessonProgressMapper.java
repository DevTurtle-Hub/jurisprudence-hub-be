package jurisprudence_hub_be.module.interaction.mapper;

import jurisprudence_hub_be.common.mapper.CentralMapperConfig;
import jurisprudence_hub_be.module.interaction.dto.response.LessonProgressResponse;
import jurisprudence_hub_be.module.interaction.entity.UserLessonProgress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface LessonProgressMapper {

    @Mapping(target = "lessonId", source = "lesson.id")
    @Mapping(target = "isCompleted", source = "completed")
    LessonProgressResponse toResponse(UserLessonProgress progress);
}
