package com.university.project.repo;

import com.university.project.models.FlashCardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryRepo extends JpaRepository<FlashCardHistory, Long> {
    List<FlashCardHistory> findAllByUserId(long userId);

}
