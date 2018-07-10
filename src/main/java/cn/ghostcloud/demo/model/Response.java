package cn.ghostcloud.demo.model;

import lombok.Data;

@Data
public class Response {
    private String row;
    private String family;
    private String qualifier;
    private String value;
    private long timestamp;
}
