package com.example.rcn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.rcn.exception.CloudinaryUploadException;
import com.example.rcn.model.FileType;
import com.example.rcn.model.MediaFile;
import com.example.rcn.repository.MediaFileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Wraps every Cloudinary upload so a row is written into {@code media_files}.
 * Reuses the existing {@link CloudinaryService} for the actual upload — this
 * class only owns the DB record.
 */
@Service
@Transactional
public class MediaService {

    private final MediaFileRepository repository;
    private final CloudinaryService cloudinaryService;
    private final Cloudinary cloudinary;

    public MediaService(MediaFileRepository repository,
                        CloudinaryService cloudinaryService,
                        Cloudinary cloudinary) {
        this.repository = repository;
        this.cloudinaryService = cloudinaryService;
        this.cloudinary = cloudinary;
    }

    public List<MediaFile> findAll() {
        return repository.findAllByOrderByUploadedAtDesc();
    }

    public List<MediaFile> findImages() {
        return repository.findByFileTypeOrderByUploadedAtDesc(FileType.IMAGE);
    }

    public Optional<MediaFile> findByPublicId(String publicId) {
        return repository.findByPublicId(publicId);
    }

    /**
     * Uploads the file to Cloudinary and records it in the media library.
     *
     * @param file       the uploaded file
     * @param folder    Cloudinary folder (e.g. "rcn/articles")
     * @param uploadedBy display name of the CMS user who uploaded (for the log)
     * @return the saved MediaFile record
     */
    public MediaFile upload(MultipartFile file, String folder, String uploadedBy) throws CloudinaryUploadException {
        String url = cloudinaryService.uploadImage(file, folder);
        MediaFile record = new MediaFile();
        record.setFileName(file.getOriginalFilename());
        record.setCloudinaryUrl(url);
        record.setPublicId(publicIdFrom(url));
        record.setFileType(FileType.IMAGE);
        record.setBytes(file.getSize());
        record.setUploadedBy(uploadedBy == null || uploadedBy.isBlank() ? "cms" : uploadedBy);
        return repository.save(record);
    }

    public void delete(Long id) {
        repository.findById(id).ifPresent(record -> {
            try {
                cloudinary.uploader().destroy(record.getPublicId(),
                        ObjectUtils.asMap("resource_type", "image"));
            } catch (Exception ignored) {
                // The DB record is the source of truth for the library; a failed
                // remote delete should not stop us from removing the local entry.
            }
            repository.delete(record);
        });
    }

    /**
     * Cloudinary's public_id is the path portion of the secure_url after
     * "/image/upload/". We store it so we can destroy the asset on delete.
     */
    private static String publicIdFrom(String secureUrl) {
        if (secureUrl == null) {
            return "";
        }
        int marker = secureUrl.indexOf("/image/upload/");
        if (marker < 0) {
            return secureUrl;
        }
        String after = secureUrl.substring(marker + "/image/upload/".length());
        int dot = after.lastIndexOf('.');
        return dot < 0 ? after : after.substring(0, dot);
    }
}
