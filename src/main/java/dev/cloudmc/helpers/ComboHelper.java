package dev.cloudmc.helpers;

import net.minecraft.entity.Entity;

public class ComboHelper {

    private int combo;
    public Entity potentialTarget;
    public Timer timer;

    public ComboHelper() {
        this.combo = 0;
        this.potentialTarget = null;
        this.timer = new Timer();
    }

    public void dealtHit(int damagedEntityID) {
        if (potentialTarget != null && damagedEntityID == potentialTarget.getEntityId()) {
            this.combo++;
            timer.reset();
        }
    }

    public void gotHit() {
        this.combo = 0;
        timer.reset();
    }

    public void reset() {
        this.combo = 0;
        timer.reset();
    }

    public int getCombo() {
        return combo;
    }
}
