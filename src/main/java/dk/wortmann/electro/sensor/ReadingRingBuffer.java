package dk.wortmann.electro.sensor;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ReadingRingBuffer extends CircularFifoQueue<Integer> {
    private static final Logger LOG = LogManager.getLogger(ReadingRingBuffer.class);
    private int readingsSum;

    public ReadingRingBuffer(int arraySize, int initialValue) {
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
        this.readingsSum -= this.peek();
        boolean wasChanged = super.add(element);

        if (wasChanged) {
            this.readingsSum += element;
        } else {
            LOG.error("Unable to add element {} to ring buffer", element);
        }

        return wasChanged;
    }

    public int getSum() {
        return this.readingsSum;
    }
}
