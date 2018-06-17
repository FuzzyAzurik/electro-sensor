package dk.wortmann.electro.sensor.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Blink {
    private int lightValue;
    private double lightRatio;
    private final LocalDateTime insertedTime;
    private final double kwhValue;
    private final int meterId;

    public Blink(LocalDateTime timestamp, int meterId) {
        this.lightValue = 0;
        this.lightRatio = 0.0;
        this.insertedTime = timestamp;
        this.kwhValue = 0.0001;
        this.meterId = meterId;
    }


    public int getLightValue() {
        return lightValue;
    }

    public void setLightValue(int lightValue) {
        this.lightValue = lightValue;
    }

    public double getLightRatio() {
        return lightRatio;
    }

    public void setLightRatio(double lightRatio) {
        this.lightRatio = lightRatio;
    }

    public LocalDateTime getInsertedTime() {
        return insertedTime;
    }

    public double getKwhValue() {
        return kwhValue;
    }

    public int getMeterId() {
        return meterId;
    }

    @Override
    public String toString() {
        return "Blink{" +
                "lightValue=" + lightValue +
                ", lightRatio=" + lightRatio +
                ", insertedTime=" + insertedTime +
                ", kwhValue=" + kwhValue +
                ", meterId=" + meterId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blink blink = (Blink) o;
        return lightValue == blink.lightValue &&
                Double.compare(blink.lightRatio, lightRatio) == 0 &&
                Double.compare(blink.kwhValue, kwhValue) == 0 &&
                meterId == blink.meterId &&
                Objects.equals(insertedTime, blink.insertedTime);
    }

    @Override
    public int hashCode() {

        return Objects.hash(lightValue, lightRatio, insertedTime, kwhValue, meterId);
    }
}
