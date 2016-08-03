package com.app.proj.silverbars;

/**
 * Created by andre_000 on 6/6/2016.
 */
public class ProgressInfo {
    private String muscle;
    private int progress;

    public ProgressInfo(String muscle, int progress) {
        this.muscle = muscle;
        this.progress = progress;
    }

    public String getMuscle() { return muscle; }

    public int getProgress() { return progress; }
}
