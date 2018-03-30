package dk.wortmann.electro.sensor.boundary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.wortmann.electro.adaptors.LocalDateTimeConverter;
import dk.wortmann.electro.sensor.model.Blink;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Worker implements Runnable {
    private static final Logger LOG = LogManager.getLogger(Worker.class);
    private final LinkedBlockingQueue<Blink> queue;
    private final String endpoint;
    private final HttpClient client;
    private final String username;
    private final String password;
    private final ExecutorService pool;

    public Worker(LinkedBlockingQueue<Blink> queue, String name, XMLConfiguration config) {
        this.queue = queue;
        this.endpoint = config.getString("endpoint.url");
        this.username = config.getString("endpoint.username");
        this.password = config.getString("endpoint.password");
        this.client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider()).build();
        this.pool = Executors.newFixedThreadPool(3);

        Thread worker = new Thread(this, name);
        worker.start();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            deQueue().ifPresent((Blink blink) -> {
                LOG.info("Processing blink: {}", blink);
                this.pool.execute(() -> sendBlink(blink));
            });
        }
        LOG.info("Worker has stopped");
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
                throw new Exception("Unable to create the blink");
            }
        } catch (Exception e) {
            LOG.debug("Putting blink: {} back on the queue", blink);
            if (!queue.offer(blink)) {
                LOG.error("Putting the blink back onto the queue failed, discarding the event.");
            }
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

    private CredentialsProvider provider() {
        final CredentialsProvider provider = new BasicCredentialsProvider();
        final UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.username, this.password);
        provider.setCredentials(AuthScope.ANY, credentials);
        return provider;
    }
}
