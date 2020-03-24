package io.rapidw.loader.master.response;

import lombok.Data;

@Data
public class LineResponse {

    public enum Type {TOTAL, SUCCESS, ERROR, TIMEOUT, MIN, MAX, AVG}
    private String time;
    private Type type;
    private double value;
}
