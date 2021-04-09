package com.taixue.xiaominglexicons.autoreply;

import java.util.ArrayList;
import java.util.List;

public class AutoReplyItem {
    public List<String> answers;
    public List<String> alias;

    public AutoReplyItem() {
    }

    public AutoReplyItem(String firstAnswer) {
        answers = new ArrayList<>();
        answers.add(firstAnswer);
    }
}
