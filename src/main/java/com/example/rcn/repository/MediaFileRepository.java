package com.example.rcn.repository;

import com.example.rcn.model.FileType;
import com.example.rcn.model.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MediaFileRepository extends JpaRepository<MediaFile, Long> {

    List<MediaFile> findAllByOrderByUploadedAtDesc();

    List<MediaFile> findByFileTypeOrderByUploadedAtDesc(FileType fileType);

    Optional<MediaFile> findByPublicId(String publicId);

    void deleteByPublicId(String publicId);
}
