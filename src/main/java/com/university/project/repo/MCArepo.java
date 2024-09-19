package com.university.project.repo;

import com.university.project.models.MultipleChoiceOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
@EnableJpaRepositories
@Repository
public interface MCArepo extends JpaRepository<MultipleChoiceOption, Long> {
}
