package jurisprudence_hub_be.module.exam.service;

import jurisprudence_hub_be.module.exam.dto.response.ParsedDocumentResponse;
import org.springframework.web.multipart.MultipartFile;

public interface DocumentParserService {

    ParsedDocumentResponse parseDocument(MultipartFile file, String targetType);
}
