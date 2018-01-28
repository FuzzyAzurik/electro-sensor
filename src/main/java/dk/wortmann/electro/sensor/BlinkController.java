package dk.wortmann.electro.sensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlinkController {
    private static final Logger LOG = LogManager.getLogger(BlinkController.class);
    private final double threshold;
    private boolean isBlinking;

    public BlinkController(double threshold) {
        this.isBlinking = false;
        this.threshold = threshold;
    }

    private double calcReadingRatio(final int reading, final ReadingRingBuffer buffer) {
        double avgReading = buffer.getSum() != 0.0 ? buffer.getSum() / buffer.maxSize() : 1.0;
        return (reading - avgReading) / avgReading * 100;
    }

    public boolean isBlinking(final int reading, final ReadingRingBuffer buffer) {
        double readingRatio = this.calcReadingRatio(reading, buffer);
        if (readingRatio < this.threshold) {
            if (!this.isBlinking) {
                LOG.info("Blink! readingRatio: {}", readingRatio);
                this.isBlinking = true;
            }
        } else {
            this.isBlinking = false;
        }

        return this.isBlinking;
    }
}
