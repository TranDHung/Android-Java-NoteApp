package com.example.noteappfinal.NoteModule;

public class Note{

    private String title, content, dateR, timeR, id, password, dateMoveToBin;
    private Boolean isPin, isRemind, isInBin, isPassOn;

    public Note() {}

    public Note(String id, String title, String content, Boolean isPin, Boolean isInBin, Boolean isPassOn, String dateMoveToBin) {
        this.title = title;
        this.content = content;
        this.id = id;
        this.isPin = isPin;
        this.isInBin = isInBin;
        this.isPassOn = isPassOn;
        this.dateMoveToBin = dateMoveToBin;
    }

    public Note(String id, String title, String content, Boolean isPin, Boolean isInBin, Boolean isPassOn, String password, String dateMoveToBin) {
        this.title = title;
        this.content = content;
        this.id = id;
        this.isPin = isPin;
        this.isInBin = isInBin;
        this.isPassOn = isPassOn;
        this.password = password;
        this.dateMoveToBin = dateMoveToBin;
    }

    public Note(String id, String title, String content, Boolean isPin, Boolean isRemind, Boolean isInBin, Boolean isPassOn, String password, String dateMoveToBin) {
        this.title = title;
        this.content = content;
        this.id = id;
        this.isPin = isPin;
        this.isRemind = isRemind;
        this.isInBin = isInBin;
        this.isPassOn = isPassOn;
        this.password = password;
        this.dateMoveToBin = dateMoveToBin;
    }

    public Note(String id, String title, String content, Boolean isPin, Boolean isRemind, Boolean isInBin, String dateR, String timeR, Boolean isPassOn, String password, String dateMoveToBin) {
        this.title = title;
        this.content = content;
        this.dateR = dateR;
        this.timeR = timeR;
        this.id = id;
        this.isPin = isPin;
        this.isRemind = isRemind;
        this.isInBin = isInBin;
        this.isPassOn = isPassOn;
        this.password = password;
        this.dateMoveToBin = dateMoveToBin;
    }

    public Note(String title, String content){
        this.title = title;
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDateR() {
        return dateR;
    }

    public void setDateR(String dateR) {
        this.dateR = dateR;
    }

    public String getTimeR() {
        return timeR;
    }

    public void setTimeR(String timeR) {
        this.timeR = timeR;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Boolean getPin() {
        return isPin;
    }

    public void setPin(Boolean pin) {
        isPin = pin;
    }

    public Boolean getRemind() {
        return isRemind;
    }

    public void setRemind(Boolean remind) {
        isRemind = remind;
    }

    public Boolean getInBin() {
        return isInBin;
    }

    public void setInBin(Boolean inBin) {
        isInBin = inBin;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getPassOn() {
        return isPassOn;
    }

    public void setPassOn(Boolean passOn) {
        isPassOn = passOn;
    }

    public String getDateMoveToBin() {
        return dateMoveToBin;
    }

    public void setDateMoveToBin(String dateMoveToBin) {
        this.dateMoveToBin = dateMoveToBin;
    }
}
