package com.rs.db.collection;

import java.util.Date;

public class LogEntry {

    public enum LogType {
        ERROR,
        TRADE
    }

    private Date date;
    private LogType type;
    private long hash;
    private Object data;

    public LogEntry(LogType type, long hash, Object data) {
        this.date = new Date();
        this.type = type;
        this.hash = hash;
        this.data = data;
    }

    public Date getDate() {
        return date;
    }

    public LogType getType() {
        return type;
    }

    public long getHash() {
        return hash;
    }

    public Object getData() {
        return data;
    }
}
