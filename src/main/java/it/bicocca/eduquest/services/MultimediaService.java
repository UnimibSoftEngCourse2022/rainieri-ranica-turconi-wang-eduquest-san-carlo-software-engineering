package it.bicocca.eduquest.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.IOException;
import java.util.Map;

@Service
public class MultimediaService {

    private final Cloudinary cloudinary;

    @Autowired
    public MultimediaService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }
    public String uploadMedia(MultipartFile file, String folderName) {
        try {
            Map<String, Object> params = ObjectUtils.asMap(
                "folder", folderName,
                "resource_type", "auto"
            );

            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new MediaUploadException("Error while uploading the media file to Cloudinary", e);
        }
    }

    public static class MediaUploadException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public MediaUploadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}