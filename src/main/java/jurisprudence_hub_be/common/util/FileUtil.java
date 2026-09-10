package jurisprudence_hub_be.common.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class FileUtil {

    private static final byte[] JPEG_MAGIC = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF};
    private static final byte[] PNG_MAGIC = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A};
    private static final byte[] GIF_MAGIC = new byte[]{0x47, 0x49, 0x46, 0x38}; // "GIF8"
    private static final byte[] PDF_MAGIC = new byte[]{0x25, 0x50, 0x44, 0x46}; // "%PDF"
    private static final byte[] ZIP_MAGIC = new byte[]{0x50, 0x4B, 0x03, 0x04}; // "PK.." (also docx, xlsx)

    private FileUtil() {
    }

    public static boolean isEmpty(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    public static String getExtension(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) {
            return "";
        }
        return getExtension(file.getOriginalFilename());
    }

    public static String getExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        String clean = sanitizeFilename(filename);
        int index = clean.lastIndexOf('.');

        if (index < 0 || index == clean.length() - 1) {
            return "";
        }

        return clean.substring(index + 1).toLowerCase(Locale.ROOT);
    }

    public static boolean hasExtension(MultipartFile file, String... extensions) {
        String extension = getExtension(file);
        List<String> allowedExtensions = Arrays.asList(extensions);
        return allowedExtensions.stream().anyMatch(extension::equalsIgnoreCase);
    }

    public static String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }

        // 1. Loại bỏ null byte và các ký tự điều khiển trước để tránh lỗi hệ điều hành
        String clean = filename.replace("\0", "").replaceAll("[\\p{Cntrl}]", "");

        // 2. Chống path traversal (loại bỏ chuỗi ..)
        while (clean.contains("..")) {
            clean = clean.replace("..", "");
        }

        // 3. Loại bỏ thư mục theo cả / và \ 
        int lastSlash = Math.max(clean.lastIndexOf('/'), clean.lastIndexOf('\\'));
        if (lastSlash >= 0) {
            clean = clean.substring(lastSlash + 1);
        }

        // 4. Giữ lại tên file an toàn (chữ, số, gạch nối, gạch dưới, dấu chấm)
        clean = clean.replaceAll("[^a-zA-Z0-9._-]", "_");

        return clean.trim();
    }

    public static String getCleanFilename(MultipartFile file) {
        if (file == null || file.getOriginalFilename() == null) {
            return "";
        }
        return sanitizeFilename(file.getOriginalFilename());
    }

    public static String generateUniqueFilename(MultipartFile file) {
        String extension = getExtension(file);
        String uuid = UUID.randomUUID().toString();
        return extension.isBlank() ? uuid : uuid + "." + extension;
    }

    public static String generateUniqueFilename(String originalFilename) {
        String extension = getExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return extension.isBlank() ? uuid : uuid + "." + extension;
    }

    public static boolean isValidMimeType(MultipartFile file, String... allowedMimeTypes) {
        if (file == null || file.getContentType() == null) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }
        String lowerContentType = contentType.toLowerCase(Locale.ROOT);
        return Arrays.stream(allowedMimeTypes)
                .map(type -> type.toLowerCase(Locale.ROOT))
                .anyMatch(allowed -> allowed.equals(lowerContentType) ||
                        (allowed.endsWith("/*") && lowerContentType.startsWith(allowed.substring(0, allowed.length() - 1))));
    }

    public static boolean validateMagicBytes(MultipartFile file, String expectedExtension) {
        if (file == null || file.isEmpty() || expectedExtension == null) {
            return false;
        }

        byte[] header = readHeader(file, 16);
        if (header.length < 4) {
            return false;
        }

        String ext = expectedExtension.toLowerCase(Locale.ROOT);
        return switch (ext) {
            case "jpg", "jpeg" -> startsWith(header, JPEG_MAGIC);
            case "png" -> startsWith(header, PNG_MAGIC);
            case "gif" -> startsWith(header, GIF_MAGIC);
            case "pdf" -> startsWith(header, PDF_MAGIC);
            case "zip", "docx", "xlsx" -> startsWith(header, ZIP_MAGIC);
            case "webp" -> isWebp(header);
            default -> true; // Định dạng không có magic byte cố định trong tập mẫu
        };
    }

    private static boolean isWebp(byte[] header) {
        if (header.length < 12) {
            return false;
        }
        // "RIFF" .... "WEBP"
        return header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F' &&
                header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
    }

    private static boolean startsWith(byte[] data, byte[] prefix) {
        if (data.length < prefix.length) {
            return false;
        }
        for (int i = 0; i < prefix.length; i++) {
            if (data[i] != prefix[i]) {
                return false;
            }
        }
        return true;
    }

    private static byte[] readHeader(MultipartFile file, int byteCount) {
        try (InputStream is = file.getInputStream()) {
            byte[] buffer = new byte[byteCount];
            int read = is.read(buffer);
            if (read <= 0) {
                return new byte[0];
            }
            if (read < byteCount) {
                return Arrays.copyOf(buffer, read);
            }
            return buffer;
        } catch (IOException e) {
            return new byte[0];
        }
    }

    public static String getOriginalFilename(MultipartFile file) {
        return getCleanFilename(file);
    }

    public static long getSizeInBytes(MultipartFile file) {
        return file == null ? 0 : file.getSize();
    }

    public static double getSizeInMB(MultipartFile file) {
        return getSizeInBytes(file) / (1024.0 * 1024.0);
    }
}
