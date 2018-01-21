package dk.wortmann.electro.sensor;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ReadingsRingBufferTest {

    private final int arraySize = 5;
    private final int intialValue = 15000;
    private ReadingsRingBuffer buffer;

    @Before
    public void setUp() throws Exception {
        this.buffer = new ReadingsRingBuffer(arraySize, intialValue);
    }

    @Test
    public void initialConstruct() {
        assertEquals(this.arraySize, this.buffer.maxSize());
        assertEquals(this.arraySize * this.intialValue, this.buffer.getSum());
    }

    @Test
    public void add() {
        // given
        int reading = 100;

        // when
        this.buffer.add(reading);

        //then
        assertEquals(((this.arraySize - 1) * 15000) + reading, this.buffer.getSum());
    }

    @Test
    public void add_full() {
        // given
        int reading = 100;
        int lowReading = 50;

        // when
        for (int i = 0; i < this.buffer.maxSize(); i++) {
            this.buffer.add(reading);
        }
        this.buffer.add(lowReading);

        //then
        assertEquals(((this.arraySize - 1) * reading) + lowReading, this.buffer.getSum());
    }

    @Test
    public void sum() {
        int reading = 100;

        for (int i = 0; i < this.buffer.maxSize(); i++) {
            this.buffer.add(reading);
        }

        assertEquals(this.buffer.maxSize() * reading, this.buffer.getSum());
    }

    @Test
    public void getCurrent() {
        int startValue = 100;
        int intervals = 6;

        for (int i = 0; i < intervals; i++) {
            startValue -= 10;
            this.buffer.add(startValue);
        }

        assertEquals(new Integer(80), this.buffer.peek());
    }
}