package com.sm.message.order;

public class OrderAllStatusCntInfo {
    private int waitPayCnt;
    private int waitSentCnt;
    private int waitReceiveCnt;
    private int waitCommentCnt;
    private int drawbackCnt;

    public int getWaitPayCnt() {
        return waitPayCnt;
    }

    public void setWaitPayCnt(int waitPayCnt) {
        this.waitPayCnt = waitPayCnt;
    }

    public int getWaitSentCnt() {
        return waitSentCnt;
    }

    public void setWaitSentCnt(int waitSentCnt) {
        this.waitSentCnt = waitSentCnt;
    }

    public int getWaitReceiveCnt() {
        return waitReceiveCnt;
    }

    public void setWaitReceiveCnt(int waitReceiveCnt) {
        this.waitReceiveCnt = waitReceiveCnt;
    }

    public int getWaitCommentCnt() {
        return waitCommentCnt;
    }

    public void setWaitCommentCnt(int waitCommentCnt) {
        this.waitCommentCnt = waitCommentCnt;
    }

    public int getDrawbackCnt() {
        return drawbackCnt;
    }

    public void setDrawbackCnt(int drawbackCnt) {
        this.drawbackCnt = drawbackCnt;
    }
}
