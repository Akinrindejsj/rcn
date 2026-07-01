package com.example.rcn.repository;

import com.example.rcn.model.MembersVoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembersVoiceRepository extends JpaRepository<MembersVoice, Long> {

    List<MembersVoice> findAllByOrderBySortOrderAsc();
}

