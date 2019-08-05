package inha.ac.kr.pdychoo.buslinker.Restful;

public class DealStatus {
    int All, Ready, Progress, Complete;

    public DealStatus(int all, int ready, int progress, int complete) {
        All = all;
        Ready = ready;
        Progress = progress;
        Complete = complete;
    }

    public int getAll() {
        return All;
    }

    public void setAll(int all) {
        All = all;
    }

    public int getReady() {
        return Ready;
    }

    public void setReady(int ready) {
        Ready = ready;
    }

    public int getProgress() {
        return Progress;
    }

    public void setProgress(int progress) {
        Progress = progress;
    }

    public int getComplete() {
        return Complete;
    }

    public void setComplete(int complete) {
        Complete = complete;
    }
}
