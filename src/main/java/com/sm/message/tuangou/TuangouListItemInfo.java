package com.sm.message.tuangou;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class TuangouListItemInfo {

    private Integer tuangouId;
    List<TuangouOrderInfo> tuangouOrderInfoList = new ArrayList<>();
    private int threashold;
    private String tuangouDate;
    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
    public Integer getTuangouId() {
        return tuangouId;
    }

    public TuangouListItemInfo(Integer tuangouId, List<TuangouOrderInfo> tuangouOrderInfoList, int tuangouCount, Timestamp tuangouDate) {
        this.tuangouId = tuangouId;
        this.tuangouOrderInfoList = tuangouOrderInfoList;
        this.threashold = tuangouCount;
        this.tuangouDate = df.format(tuangouDate);
    }

    public void setTuangouId(Integer tuangouId) {
        this.tuangouId = tuangouId;
    }

    public int getThreashold() {
        return threashold;
    }

    public void setThreashold(int threashold) {
        this.threashold = threashold;
    }

    public String getTuangouDate() {
        return tuangouDate;
    }

    public void setTuangouDate(String tuangouDate) {
        this.tuangouDate = tuangouDate;
    }

    public List<TuangouOrderInfo> getTuangouOrderInfoList() {
        return tuangouOrderInfoList;
    }

    public void setTuangouOrderInfoList(List<TuangouOrderInfo> tuangouOrderInfoList) {
        this.tuangouOrderInfoList = tuangouOrderInfoList;
    }

    public TuangouListItemInfo() {
    }

}
