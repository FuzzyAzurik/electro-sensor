package dk.wortmann.electro.sensor;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BlinkRingBuffer extends CircularFifoQueue<Integer> {
    private static final Logger LOG = LogManager.getLogger(BlinkRingBuffer.class);
    private int readingsSum;

    public BlinkRingBuffer(int arraySize, int initialValue) {
        super(arraySize);
        this.readingsSum = 0;

        // initialize the array with default values
        for (int i = 0; i < this.maxSize(); i++) {
            super.add(initialValue);
            this.readingsSum += initialValue;
        }
    }

    @Override
    public boolean add(Integer element) {
        int previousElement = this.peek();
        boolean wasChanged = super.add(element);

        if (wasChanged) {
            this.readingsSum += (element - previousElement);
        } else {
            LOG.error("Unable to add element {} to ring buffer", element);
        }

        return wasChanged;
    }

    public int getSum() {
        return this.readingsSum;
    }
}
