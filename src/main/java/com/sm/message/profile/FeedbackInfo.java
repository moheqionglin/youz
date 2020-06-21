package com.sm.message.profile;

import java.util.ArrayList;
import java.util.List;

public class FeedbackInfo {
    List<FeedBackItemInfo> items = new ArrayList<>();

    public List<FeedBackItemInfo> getItems() {
        return items;
    }

    public void setItems(List<FeedBackItemInfo> items) {
        this.items = items;
    }
}
