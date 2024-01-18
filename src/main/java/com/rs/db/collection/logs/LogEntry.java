package com.rs.db.collection.logs;

import java.util.Date;

public class LogEntry {

    public enum LogType {
        ERROR,
		GE, PICKUP, GRAVE, COMMAND, REPORT, TRADE
    }

    private final Date date;
    private final LogType type;
    private final long hash;
    private final Object data;

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
