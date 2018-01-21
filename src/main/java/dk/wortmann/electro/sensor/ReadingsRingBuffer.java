package dk.wortmann.electro.sensor;

import org.apache.commons.collections4.queue.CircularFifoQueue;

public class ReadingsRingBuffer extends CircularFifoQueue<Integer> {
    private int readingsSum;

    public ReadingsRingBuffer(int arraySize, int initialValue) {
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
        }
        return wasChanged;
    }

    public int getSum() {
        return this.readingsSum;
    }
}
