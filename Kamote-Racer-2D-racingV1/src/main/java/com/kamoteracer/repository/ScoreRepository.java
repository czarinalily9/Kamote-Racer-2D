package com.kamoteracer.repository;

import com.kamoteracer.model.ScoreEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreRepository extends JpaRepository<ScoreEntry, Long> {

    @Query("SELECT s FROM ScoreEntry s ORDER BY s.score DESC, s.createdAt ASC")
    List<ScoreEntry> findTop10ByScoreDesc();
}