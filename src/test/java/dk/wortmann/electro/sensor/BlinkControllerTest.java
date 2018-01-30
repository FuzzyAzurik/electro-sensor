package dk.wortmann.electro.sensor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class BlinkControllerTest {
    private static final double THRESHOLD = -75;
    private BlinkRingBuffer buffer;
    private BlinkController blinkController;

    @Before
    public void setUp() throws Exception {
        this.blinkController = new BlinkController(THRESHOLD);
        this.buffer = new BlinkRingBuffer(4, 15000);
    }

    @Test
    public void calcReadingRatio_NonBlink() {
        for (int i = 0; i < this.buffer.maxSize(); i++) {
            this.buffer.add(0);
        }

        boolean isBlinking = this.blinkController.isBlinking(1300, this.buffer);

        assertFalse(isBlinking);
    }

    @Test
    @Ignore
    public void calcReadingRatio_NonBlink_UpAndDown() {
        for (int i = 0; i < this.buffer.maxSize(); i++) {
            this.buffer.add(15000);
        }

        assertTrue(this.blinkController.isBlinking(1500, this.buffer));
        this.buffer.add(1500);
        assertTrue(this.blinkController.isBlinking(1300, this.buffer));
        this.buffer.add(1300);
        assertTrue(this.blinkController.isBlinking(1000, this.buffer));
        this.buffer.add(1000);
        assertFalse(this.blinkController.isBlinking(12000, this.buffer));
        this.buffer.add(12000);
    }

    @Test
    public void calcReadingRatio_Blink() {
        for (int i = 0; i < this.buffer.maxSize(); i++) {
            this.buffer.add(15000);
        }

        boolean isBlinking = this.blinkController.isBlinking(1300, this.buffer);

        assertTrue(isBlinking);
    }

    @Test
    public void calcReadingRatio2_Blink() {
        for (int i = 0; i < this.buffer.maxSize(); i++) {
            this.buffer.add(15000);
        }

        boolean isBlinking = this.blinkController.isBlinking(0, this.buffer);

        assertTrue(isBlinking);
    }
}