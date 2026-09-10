package jurisprudence_hub_be.module.document.mapper;

import jurisprudence_hub_be.common.mapper.CentralMapperConfig;
import jurisprudence_hub_be.module.document.dto.request.ChapterCreateRequest;
import jurisprudence_hub_be.module.document.dto.request.ChapterUpdateRequest;
import jurisprudence_hub_be.module.document.dto.response.ChapterResponse;
import jurisprudence_hub_be.module.document.entity.Chapter;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", config = CentralMapperConfig.class, uses = {LessonMapper.class})
public interface ChapterMapper {

    ChapterResponse toResponse(Chapter chapter);

    List<ChapterResponse> toResponseList(List<Chapter> chapters);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Chapter toEntity(ChapterCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "lessons", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(ChapterUpdateRequest request, @MappingTarget Chapter chapter);
}
