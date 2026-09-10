package jurisprudence_hub_be.module.interaction.service;

import jurisprudence_hub_be.module.interaction.dto.request.LessonProgressRequest;
import jurisprudence_hub_be.module.interaction.dto.response.LessonProgressResponse;

public interface LessonProgressService {

    LessonProgressResponse updateProgress(String lessonId, LessonProgressRequest request);
}
