package com.mizusoft.android.push4free;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import lombok.extern.java.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

/**
 *
 * @author tim
 */
@Log
@Controller
public class BaseController {

    @Value("${push4free.password}")
    private String password;
    @Value("${push4free.tag}")
    private String tag;
    @Value("${push4free.timeout}")
    private Long timeout;
    //
    private int lastId = 0;
    private final Queue<DeferredResult<String>> pushRequests = new ConcurrentLinkedQueue<DeferredResult<String>>();
    private final Queue<Reader> readRequests = new ConcurrentLinkedQueue<Reader>();

    @RequestMapping("/follow")
    @ResponseBody
    public String follow(@RequestParam String url) {
        for (Reader reader : readRequests) {
            if (reader.getUrl().equals(url)) {
                readRequests.remove(reader);
                return "REMOVED";
            }
        }
        readRequests.add(new Reader(url));
        return "ADDED";
    }

    @RequestMapping("/read")
    @ResponseBody
    public DeferredResult<String> read() {
        final DeferredResult<String> deferredResult = new DeferredResult<String>(timeout, "PING");
        this.pushRequests.add(deferredResult);
        return deferredResult;
    }

    @RequestMapping("/push")
    @ResponseBody
    public String push(@RequestParam(required = false) Integer id,
            @RequestParam(required = true) String pwd,
            @RequestParam(required = true) String title,
            @RequestParam(required = true) String body) {
        if (pwd.equals(this.password)) {
            Message message = new Message();
            if (id != null) {
                message.setId(id);
            } else {
                lastId++;
                message.setId(lastId);
            }
            message.setTag(tag);
            message.setTitle(title);
            message.setBody(body);
            pushToDeferred(message.toString());
            return "OK SEND";
        } else {
            return "NOK AUTH";
        }
    }

    private void pushToDeferred(String msg) {
        for (DeferredResult<String> deferredResult : pushRequests) {
            deferredResult.setResult(msg);
            pushRequests.remove(deferredResult);
        }
    }

    private void update() {
        for (Reader reader : readRequests) {
            if (reader.getStatus() != Reader.STATUS.RUNNING) {
                background(reader);
            }
        }
    }

    @Async
    public void background(Reader r) {
        r.setStatus(Reader.STATUS.RUNNING);
        log.log(Level.INFO, "started listening {0}", r.getUrl());
        while (readRequests.contains(r)) {
            try {
                HttpClient httpClient = HttpClientBuilder.create().build();
                HttpGet httpGet = new HttpGet(r.getUrl());
                HttpResponse httpResponse = httpClient.execute(httpGet);
                String responseString = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
                if (responseString != null && !responseString.contains("PING")) {
                    pushToDeferred(responseString);
                }
                log.log(Level.INFO, "received message: {0} from {1}", new Object[]{responseString, r.getUrl()});
            } catch (IOException ex) {
                log.severe(ex.getMessage());
            }
        }
    }

    @Scheduled(fixedDelay = 10000)
    public void autoStart() {
        update();
    }
}
