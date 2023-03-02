package edu.duke.risc.display.draw;

public class MapTiles {
    private int mX;
    private int mY;
    private int mRadius;

    public int getX() {
        return mX;
    }

    public int getY() {
        return mY;
    }

    public int getRadius() {
        return mRadius;
    }

    public MapTiles(int viewWidth, int viewHeight) {
        mX = viewWidth / 2;
        mY = viewHeight / 2;
        // Adjust the radius for the narrowest view dimension.
        mRadius = ((viewWidth <= viewHeight) ? mX / 3 : mY / 3);
    }

    public void update(int newX, int newY) {
        mX = newX;
        mY = newY;
    }
}
