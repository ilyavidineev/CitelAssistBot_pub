package ru.vidineev.entities;


import java.util.ArrayList;
import java.util.List;

public class RecordString {

    private String number;
    private String method;
    private String text;
    private List<String> exitNums;

    public RecordString(String number, String method, String text, List<String> exitNums) {
        this.number = number;
        this.method = method;
        this.text = text;
        this.exitNums = exitNums;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getExitNums() {
        return exitNums;
    }

    public void setExitNums(ArrayList<String> exitNums) {
        this.exitNums = exitNums;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
