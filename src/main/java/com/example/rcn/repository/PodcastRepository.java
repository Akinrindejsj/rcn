package com.example.rcn.repository;

import com.example.rcn.model.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PodcastRepository extends JpaRepository<Podcast, Long> {

    List<Podcast> findAllByOrderByPublishedAtDesc();

    List<Podcast> findByIdIn(List<Long> ids);
}
