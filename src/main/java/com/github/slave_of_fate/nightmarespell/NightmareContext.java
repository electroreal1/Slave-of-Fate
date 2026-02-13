package com.github.slave_of_fate.nightmarespell;

public class NightmareContext {
    private int nightmareKills = 0;
    private long startTime;
    private boolean bossDefeated = false;

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public void addKill() {
        this.nightmareKills++;
    }

    public int calculateScore() {
        int score = Math.min(nightmareKills * 2, 50);
        if (bossDefeated) score += 40;
        return score;
    }
}
