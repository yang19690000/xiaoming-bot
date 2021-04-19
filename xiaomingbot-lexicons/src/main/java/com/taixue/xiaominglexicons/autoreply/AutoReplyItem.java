package com.taixue.xiaominglexicons.autoreply;

import java.util.ArrayList;
import java.util.List;

public class AutoReplyItem {
    private List<String> answers;
    private List<String> alias;

    public AutoReplyItem() {
    }

    public List<String> getAnswers() {
        return answers;
    }

    public List<String> getAlias() {
        return alias;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }

    public void setAlias(List<String> alias) {
        this.alias = alias;
    }

    public AutoReplyItem(String firstAnswer) {
        answers = new ArrayList<>();
        answers.add(firstAnswer);
    }
}
