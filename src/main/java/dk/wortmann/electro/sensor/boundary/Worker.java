package dk.wortmann.electro.sensor.boundary;

import com.google.gson.GsonBuilder;
import dk.wortmann.electro.sensor.model.Blink;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;

public class Worker implements Runnable {
    private static final Logger LOG = LogManager.getLogger(Worker.class);
    private final LinkedBlockingQueue<Blink> queue;
    private final String endpoint;
    private final HttpClient client;


    public Worker(LinkedBlockingQueue<Blink> queue, String name, XMLConfiguration config) {
        this.queue = queue;
        this.endpoint = config.getString("endpoint.url");
        this.client = HttpClientBuilder.create().build();

        Thread worker = new Thread(this, name);
        worker.start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            deQueue().ifPresent((blink) -> {
                LOG.info("Processing blink: {}", blink);
                sendBlink(blink);
            });
        }
    }

    private void sendBlink(Blink blink) {
        String json = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeConverter())
                .create()
                .toJson(blink);

        HttpUriRequest request = RequestBuilder
                .post(this.endpoint.replace("{meterId}", String.valueOf(blink.getMeterId())))
                .addHeader(new BasicHeader("Content-Type", "application/json"))
                .setEntity(new StringEntity(json, Charset.forName("utf8")))
                .build();

        try {
            LOG.debug("Sending blink: {} to server", request.toString());
            final HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                LOG.info("Blink was successfully created");
            } else {
                LOG.error("Blink was not created reason: {}, status: {}", response.getStatusLine().getReasonPhrase(), response.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Blink> deQueue() {
        try {
            Blink blink = this.queue.take();
            return Optional.of(blink);
        } catch (InterruptedException e) {
            return Optional.empty();
        }
    }
}
