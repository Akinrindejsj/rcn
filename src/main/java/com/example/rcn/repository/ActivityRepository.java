package com.example.rcn.repository;

import com.example.rcn.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    List<Activity> findAllByOrderByActivityDateDesc();

    List<Activity> findByIdIn(List<Long> ids);

    List<Activity> findByTypeOrderByActivityDateDesc(String type);
}
