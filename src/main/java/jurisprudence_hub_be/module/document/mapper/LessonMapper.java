package jurisprudence_hub_be.module.document.mapper;

import jurisprudence_hub_be.common.mapper.CentralMapperConfig;
import jurisprudence_hub_be.module.document.dto.request.LessonCreateRequest;
import jurisprudence_hub_be.module.document.dto.request.LessonContentRequest;
import jurisprudence_hub_be.module.document.dto.request.LessonUpdateRequest;
import jurisprudence_hub_be.module.document.dto.response.LessonContentResponse;
import jurisprudence_hub_be.module.document.dto.response.LessonSummaryResponse;
import jurisprudence_hub_be.module.document.entity.Lesson;
import jurisprudence_hub_be.module.document.entity.LessonContent;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class)
public interface LessonMapper {

    @Mapping(target = "chapterId", source = "chapter.id")
    LessonSummaryResponse toSummaryResponse(Lesson lesson);

    List<LessonSummaryResponse> toSummaryResponseList(List<Lesson> lessons);

    LessonContentResponse toContentResponse(LessonContent content);

    @Mapping(target = "lessonId", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    LessonContent toContentEntity(LessonContentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "lessonId", ignore = true)
    @Mapping(target = "lesson", ignore = true)
    void updateContentFromRequest(LessonContentRequest request, @MappingTarget LessonContent content);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Lesson toEntity(LessonCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "chapter", ignore = true)
    @Mapping(target = "content", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(LessonUpdateRequest request, @MappingTarget Lesson lesson);
}
