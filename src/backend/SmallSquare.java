package backend;

import java.util.HashSet;
import java.util.Set;


public class SmallSquare {

    private Integer value;
    private Set<Integer> guesses;
    private boolean isSolid;
    private int realValue = 0;

    public SmallSquare(int value, boolean isSolid) {
        this.value = value;
        this.isSolid = isSolid;

        this.guesses = new HashSet<Integer>();
    }

    @Override
    public String toString() {
        if(value == 0) {
            return " ";
        }
        return ""+value;
    }

    public Integer getValue() {
        return value;
    }

    public Set<Integer> getGuesses() {
        return guesses;
    }

    public boolean isSolid() {
        return isSolid;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public void addGuess(int guess) {
        guesses.add(guess);
    }

    public int getRealValue() {
        return realValue;
    }

    public void setRealValue(int realValue) {
        this.realValue = realValue;
    }
}
