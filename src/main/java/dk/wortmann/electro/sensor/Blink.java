package dk.wortmann.electro.sensor;

import java.util.Objects;

public class Blink {
    private final int value;

    public Blink(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Blink{" +
                "value=" + value +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Blink blink1 = (Blink) o;
        return value == blink1.value;
    }

    @Override
    public int hashCode() {

        return Objects.hash(value);
    }
}
