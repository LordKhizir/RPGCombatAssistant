package com.altekis.rpg.combatassistant.maneuver;

public class MovingResult {

    public static final String FUMBLE = "F";
    private boolean fumbled;
    private boolean numeric;
    private int numericResult;
    private String result;

    public MovingResult(String result) {
        this.result = result;
        this.fumbled = FUMBLE.equals(result);
        if (result != null && result.matches("\\d+")) {
            this.numeric = true;
            this.numericResult = Integer.parseInt(result);
        }
    }

    public boolean isFumbled() {
        return fumbled;
    }

    public void setFumbled(boolean fumbled) {
        this.fumbled = fumbled;
    }

    public boolean isNumeric() {
        return numeric;
    }

    public void setNumeric(boolean numeric) {
        this.numeric = numeric;
    }

    public int getNumericResult() {
        return numericResult;
    }

    public void setNumericResult(int numericResult) {
        this.numericResult = numericResult;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
