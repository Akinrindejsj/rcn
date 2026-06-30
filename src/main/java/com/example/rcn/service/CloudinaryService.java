package com.example.rcn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.rcn.exception.CloudinaryUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    @Value("${cloudinary.upload-folder:rcn/homepage}")
    private String defaultFolder;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Uploads an image to Cloudinary under the configured folder.
     *
     * @param file   the image to upload; must not be null or empty
     * @param folder target folder, e.g. "rcn/homepage"; the Cloudinary SDK
     *               prepends it to the public_id
     * @return the secure_url of the uploaded asset
     * @throws CloudinaryUploadException on any upload failure, with a
     *                                  plain-English message
     */
    public String uploadImage(MultipartFile file, String folder) throws CloudinaryUploadException {
        if (file == null || file.isEmpty()) {
            throw new CloudinaryUploadException("No image file was provided.");
        }
        try {
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "image"
            );
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String url = (String) result.get("secure_url");
            if (url == null || url.isBlank()) {
                throw new CloudinaryUploadException(
                        "Cloudinary returned no URL. Please try again or use a different file.");
            }
            return url;
        } catch (CloudinaryUploadException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw new CloudinaryUploadException(
                    "Could not read the image file. Please try again or use a different file.", e);
        } catch (Exception e) {
            throw new CloudinaryUploadException(
                    "Image upload failed: " + e.getMessage()
                            + ". Please try again or use a different file.", e);
        }
    }

    /**
     * Convenience overload that uploads to the default folder (rcn/homepage).
     */
    public String uploadImage(MultipartFile file) throws CloudinaryUploadException {
        return uploadImage(file, defaultFolder);
    }

    public String uploadVideo(MultipartFile file, String folder) throws CloudinaryUploadException {
        if (file == null || file.isEmpty()) {
            throw new CloudinaryUploadException("No video file was provided.");
        }
        try {
            Map<String, Object> uploadOptions = ObjectUtils.asMap(
                    "folder", folder,
                    "resource_type", "video"
            );
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            String url = (String) result.get("secure_url");
            if (url == null || url.isBlank()) {
                throw new CloudinaryUploadException(
                        "Cloudinary returned no URL. Please try again or use a different file.");
            }
            return url;
        } catch (CloudinaryUploadException e) {
            throw e;
        } catch (java.io.IOException e) {
            throw new CloudinaryUploadException(
                    "Could not read the video file. Please try again or use a different file.", e);
        } catch (Exception e) {
            throw new CloudinaryUploadException(
                    "Video upload failed: " + e.getMessage()
                            + ". Please try again or use a different file.", e);
        }
    }
}
