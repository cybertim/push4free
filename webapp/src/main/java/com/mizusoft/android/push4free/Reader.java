package com.mizusoft.android.push4free;

/**
 *
 * @author tim
 */
public class Reader {

    public enum STATUS {

        IDLE,
        RUNNING
    }
    private STATUS status = STATUS.IDLE;
    private String url;

    public Reader(String url) {
        this.url = url;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

}
