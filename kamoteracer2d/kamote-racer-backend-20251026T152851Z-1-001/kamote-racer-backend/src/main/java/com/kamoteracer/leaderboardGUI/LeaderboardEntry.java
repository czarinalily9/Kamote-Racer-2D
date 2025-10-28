package com.kamoteracer.leaderboardGUI;

public class LeaderboardEntry {
    private int rank;
    private String initial;
    private int score;

    public LeaderboardEntry(int rank, String initial, int score) {
        this.rank = rank;
        this.initial = initial;
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public String getInitial() {
        return initial;
    }

    public int getScore() {
        return score;
    }
}
