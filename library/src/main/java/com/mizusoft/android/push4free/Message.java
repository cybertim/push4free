package com.mizusoft.android.push4free;

/**
 *
 * @author tim
 */
public class Message {

    private final static String SPLIT = ":";
    private int id = 0;
    private String tag = "";
    private String title = "";
    private String body = "";

    public static Message fromString(String s) {
        Message m = new Message();
        String i[] = s.split(SPLIT);
        m.setId(Integer.valueOf(new String(Base64.decode(i[0]))));
        m.setTag(new String(Base64.decode(i[1])));
        m.setTitle(new String(Base64.decode(i[2])));
        m.setBody(new String(Base64.decode(i[3])));
        return m;
    }

    @Override
    public String toString() {
        return Base64.encode(("" + id)) + SPLIT
                + Base64.encode(tag) + SPLIT
                + Base64.encode(title) + SPLIT
                + Base64.encode(body);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

}
