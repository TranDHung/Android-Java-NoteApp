package com.example.noteappfinal;

public class Label {
    private String labelId, labelName, labelContent;

    public Label(String labelId, String labelName, String labelContent) {
        this.labelId = labelId;
        this.labelName = labelName;
        this.labelContent = labelContent;
    }

    public String getLabelId() {
        return labelId;
    }

    public void setLabelId(String labelId) {
        this.labelId = labelId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public String getLabelContent() {
        return labelContent;
    }

    public void setLabelContent(String labelContent) {
        this.labelContent = labelContent;
    }
}
