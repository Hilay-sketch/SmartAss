package com.example.smartass;

public class Game {
    private String score;
    private String subject;

    public Game()
    {
    }
    public Game(String score, String subject) {
        this.score = score;
        this.subject = subject;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "Game{" +
                "score='" + score + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}
