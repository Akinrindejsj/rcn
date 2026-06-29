package com.example.rcn.service;

import com.example.rcn.model.Activity;
import com.example.rcn.model.EventStatus;
import com.example.rcn.repository.ActivityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ActivityService {

    private final ActivityRepository repository;

    public ActivityService(ActivityRepository repository) {
        this.repository = repository;
    }

    public List<Activity> findAll() {
        List<Activity> all = repository.findAllByOrderByActivityDateDesc();
        all.forEach(this::syncStatus);
        return all;
    }

    public Optional<Activity> findById(Long id) {
        return repository.findById(id).map(this::syncStatus);
    }

    public Activity create(Activity activity) {
        syncStatus(activity);
        return repository.save(activity);
    }

    public Activity update(Activity activity) {
        syncStatus(activity);
        return repository.save(activity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Activity> findByIdsIn(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        List<Activity> result = repository.findByIdIn(ids);
        result.forEach(this::syncStatus);
        return result;
    }

    /**
     * Keeps the stored {@code eventStatus} consistent with {@code activityDate}
     * so a report never shows "upcoming" after its date has passed. The result
     * is not persisted here — the caller does that via create/update.
     */
    private Activity syncStatus(Activity activity) {
        if (activity.getActivityDate() == null) {
            return activity;
        }
        LocalDate today = LocalDate.now();
        if (activity.getActivityDate().isAfter(today)) {
            activity.setEventStatus(EventStatus.UPCOMING);
        } else if (activity.getActivityDate().isEqual(today)) {
            activity.setEventStatus(EventStatus.ONGOING);
        } else {
            activity.setEventStatus(EventStatus.PAST);
        }
        return activity;
    }
}
