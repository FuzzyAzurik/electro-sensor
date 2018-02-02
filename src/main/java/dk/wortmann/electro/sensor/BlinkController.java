package dk.wortmann.electro.sensor;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlinkController {
    private static final Logger LOG = LogManager.getLogger(BlinkController.class);
    private static final int SUM_THRESHOLD = ElectroConfiguration.getInstance().getInt("");
    private final double threshold;
    private boolean isBlinking;

    public BlinkController(double threshold) {
        this.isBlinking = false;
        this.threshold = threshold;
    }

    private double calcReadingRatio(final int reading, final BlinkRingBuffer buffer) {
        double avgReading = buffer.getSum() != 0.0 ? buffer.getSum() / buffer.maxSize() : 1.0;
        return (reading - avgReading) / avgReading * 100;
    }

    public boolean isBlinking(final int reading, final BlinkRingBuffer buffer) {
        double valueRatio = this.calcReadingRatio(reading, buffer);
        LOG.debug("value ratio: {}, {}", valueRatio, this.threshold);
        if (valueRatio < this.threshold && buffer.getSum() > SUM_THRESHOLD) {
            if (!this.isBlinking) {
                LOG.info("Blink! value ratio: {}", valueRatio);
                return this.isBlinking = true;
            }
        } else {
            return this.isBlinking = false;
        }


        return false;
    }
}
