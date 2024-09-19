package com.university.project.repo;

import com.university.project.models.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@EnableJpaRepositories
@Repository
public interface QuizRepo extends JpaRepository<Quiz,Long> {

    @Query(value = "SELECT * FROM quiz WHERE user_id = ?1 ORDER BY id DESC LIMIT ?2", nativeQuery = true)
    List<Quiz> findLatestQuizzesForUser(long userId, int limit);

    Optional<Quiz> findByUserIdAndId(Long userId, Long quizId);

    @Query(value = "SELECT id, name FROM quiz WHERE user_id = ?1", nativeQuery = true)
    List<Object[]> findAllQuizInfoByUserId(long userId);





}
