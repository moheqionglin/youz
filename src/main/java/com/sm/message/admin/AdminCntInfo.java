package com.sm.message.admin;

public class AdminCntInfo {
    private int orderManagerCnt;
    private int drawbackManagerCnt;
    private int feedbackManagerCnt;

    public int getOrderManagerCnt() {
        return orderManagerCnt;
    }

    public int getFeedbackManagerCnt() {
        return feedbackManagerCnt;
    }

    public void setFeedbackManagerCnt(int feedbackManagerCnt) {
        this.feedbackManagerCnt = feedbackManagerCnt;
    }

    public void setOrderManagerCnt(int orderManagerCnt) {
        this.orderManagerCnt = orderManagerCnt;
    }

    public int getDrawbackManagerCnt() {
        return drawbackManagerCnt;
    }

    public void setDrawbackManagerCnt(int drawbackManagerCnt) {
        this.drawbackManagerCnt = drawbackManagerCnt;
    }
}
