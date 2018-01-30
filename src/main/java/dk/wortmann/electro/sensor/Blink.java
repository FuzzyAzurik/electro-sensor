package dk.wortmann.electro.sensor;

import java.time.LocalDateTime;
import java.util.Objects;

public class Blink {
    private int lightValue;
    private double lightRatio;
    private final LocalDateTime readingTime;
    private final int meterId;

    public Blink(int lightValue, int meterId) {
        this.lightValue = lightValue;
        this.lightRatio = 0.0;
        this.readingTime = LocalDateTime.now();
        this.meterId = meterId;
    }

    public int getLightValue() {
        return lightValue;
    }

    @Override
    public String toString() {
        return "Blink{" +
                "lightValue=" + lightValue +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blink blink1 = (Blink) o;
        return lightValue == blink1.lightValue;
    }

    @Override
    public int hashCode() {

        return Objects.hash(lightValue);
    }
}
