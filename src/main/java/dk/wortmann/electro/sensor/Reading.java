package dk.wortmann.electro.sensor;

public class Reading {
    private final double readingRatio;
    private final int reading;

    public Reading(int reading, double readingRatio) {
        this.reading = reading;
        this.readingRatio = readingRatio;
    }

    public double getReadingRatio() {
        return readingRatio;
    }

    public int getReading() {
        return reading;
    }
}
