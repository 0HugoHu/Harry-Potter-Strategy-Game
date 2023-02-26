package edu.duke.shared;

import java.util.Random;

public class Dice {


    // Dice's max point
    private final int maxPoint;
    /*
     * Initialize Dice by max_point
     * @param max_point Dice's max point
     */
    public Dice(int max_point){
        this.maxPoint = max_point;
    }
    /*
     * Get result of Dice
     * @return an int number between 1(inclusive) and max_point(inclusive)
     */
    public int getDice(){
        Random random = new Random();
        return random.nextInt(this.maxPoint) + 1;
    }
    public int getMaxPoint() {
        return maxPoint;
    }
}
