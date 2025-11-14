package com.kamoteracer.controller;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kamoteracer.model.ScoreEntry;
import com.kamoteracer.repository.ScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/leaderboard")
public class LeaderboardController {

    @Autowired
    private ScoreRepository scoreRepository;

    private static final Pattern INITIALS_PATTERN = Pattern.compile("^[a-zA-Z]{1,3}$");

    @PostMapping("/submit")
    public ResponseEntity<String> submitScore(@RequestBody Map<String, Object> payload) {
        System.out.println(">>> Raw payload: " + payload);

        // extract initialsssssss
        String initials = (String) payload.get("initials");
        if (initials == null || initials.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Initials missing or empty.");
        }
        initials = initials.trim();
        if (!initials.matches("^[a-zA-Z]{1,3}$")) {
            return ResponseEntity.badRequest().body("Initials must be 1–3 letters (A-Z only).");
        }

        //extract scoreballs
        Object scoreObj = payload.get("score");
        if (scoreObj == null) {
            return ResponseEntity.badRequest().body("Score missing.");
        }
        int score;
        try {
            if (scoreObj instanceof Number) {
                score = ((Number) scoreObj).intValue();
            } else {
                score = Integer.parseInt(scoreObj.toString());
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Score must be a number.");
        }
        if (score <= 0) {
            return ResponseEntity.badRequest().body("Score must be positive.");
        }

        // save
        ScoreEntry entry = new ScoreEntry(initials, score);
        scoreRepository.save(entry);
        return ResponseEntity.ok("Score saved!");
    }

    @GetMapping("/top10")
    public ResponseEntity<List<ScoreEntry>> getTop10() {
        List<ScoreEntry> all = scoreRepository.findTop10ByScoreDesc();
        List<ScoreEntry> top10 = all.stream().limit(10).toList();
        return ResponseEntity.ok(top10);
    }

    // submit scorez
    public static class ScoreSubmissionRequest {
        private final String initials;
        private final Integer score;

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public ScoreSubmissionRequest(
                @JsonProperty("initials") String initials,
                @JsonProperty("score") Integer score) {
            this.initials = initials;
            this.score = score;
        }

        public String getInitials() { return initials; }
        public Integer getScore() { return score; }
    }
}