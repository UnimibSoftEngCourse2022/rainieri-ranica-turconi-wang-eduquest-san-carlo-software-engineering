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

    /**
     * Upload a file (image, video, or audio) to Cloudinary.
     * @param file The file received from the frontend (MultipartFile)
     * @param folderName The destination folder on Cloudinary
     * @return String The public URL of the uploaded resource
     */
    public String uploadMedia(MultipartFile file, String folderName) {
        try {
            Map params = ObjectUtils.asMap(
                "folder", folderName,
                "resource_type", "auto"
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            return (String) uploadResult.get("secure_url");

        } catch (IOException e) {
            throw new RuntimeException("Error while uploading the media file to Cloudinary", e);
        }
    }
}