package com.mizusoft.android.push4free;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Tim
 */
public class MessageTest {

    @Test
    public void message() {
        Message message = createMessageObject();
        assertNotNull(message);
    }

    @Test
    public void encoding() {
        Message message = createMessageObject();
        assertEquals(message.toString(), encodedMessageObject());
    }

    @Test
    public void decoding() {
        Message message = Message.fromString(encodedMessageObject());
        assertNotNull(message);
    }

    @Test
    public void compare() {
        Message original = createMessageObject();
        Message message = Message.fromString(encodedMessageObject());
        assertEquals(message.getId(), original.getId());
        assertEquals(message.getBody(), original.getBody());
        assertEquals(message.getTag(), original.getTag());
        assertEquals(message.getTitle(), original.getTitle());
    }

    private String encodedMessageObject() {
        return "MA==:dGVzdCBzdHJpbmc=:dGVzdCBzdHJpbmc=:dGVzdCBzdHJpbmc=";
    }

    private Message createMessageObject() {
        Message message = new Message();
        message.setId(0);
        message.setTag("test string");
        message.setTitle("test string");
        message.setBody("test string");
        return message;
    }
}
