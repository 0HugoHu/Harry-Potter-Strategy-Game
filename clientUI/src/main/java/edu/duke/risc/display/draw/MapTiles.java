package edu.duke.risc.display.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import edu.duke.shared.GameMap;

public class MapTiles {
    // Map view width and height
    private final int mapViewWidth;
    private final int mapViewHeight;
    // Padding
    private int paddingTop = 32;
    private int paddingBottom = 32;
    private int paddingLeft = 32;
    private int paddingRight = 32;

    /**
     * Constructor
     * @param viewWidth width of the view
     * @param viewHeight height of the view
     */
    public MapTiles(int viewWidth, int viewHeight) {
        this.mapViewWidth = viewWidth;
        this.mapViewHeight = viewHeight;
        this.paddingLeft = viewWidth / 10;
        this.paddingTop = viewHeight / 10;
    }

    /**
     * Update the map tiles
     * @param canvas canvas
     * @param mPaint paint
     * @param map map
     */
    public void update(Canvas canvas, Paint mPaint, GameMap map) {
        if (map == null) {
            return;
        }

        int size = 40;
        int borderSize = 2;
        Set<String> owners = new HashSet<>();
        Map<String, Integer> ownerColor = new HashMap<>();
        ownerColor.put("Player0", 0xFF1ABC9C);
        ownerColor.put("Player1", 0xFF3498DB);
        ownerColor.put("Player2", 0xFFE74C3C);
        ownerColor.put("Player3", 0xFFF1C40F);


        int height = map.getHeight();
        int width = map.getWidth();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String owner = map.getOwnerByCoord(y, x);
                mPaint.setColor(ownerColor.getOrDefault(owner, 0xFF000000));
                byte pattern = map.isBorderPoint(y, x);

                canvas.drawRect(paddingLeft + x * size, paddingTop + y * size, paddingLeft + x * size + size, paddingTop + y * size + size, mPaint);
                if (pattern != 0) {
                    mPaint.setColor(0xFF000000);
                    // Draw top border
                    if ((pattern & 1) != 0) {
                        canvas.drawRect(paddingLeft + x * size, paddingTop + y * size, paddingLeft + x * size + size, paddingTop + y * size + borderSize, mPaint);
                    }
                    // Draw right border
                    if ((pattern & 2) != 0) {
                        canvas.drawRect(paddingLeft + x * size + size - borderSize, paddingTop + y * size, paddingLeft + x * size + size, paddingTop + y * size + size, mPaint);
                    }
                    // Draw bottom border
                    if ((pattern & 4) != 0) {
                        canvas.drawRect(paddingLeft + x * size, paddingTop + y * size + size - borderSize, paddingLeft + x * size + size, paddingTop + y * size + size, mPaint);
                    }
                    // Draw left border
                    if ((pattern & 8) != 0) {
                        canvas.drawRect(paddingLeft + x * size, paddingTop + y * size, paddingLeft + x * size + borderSize, paddingTop + y * size + size, mPaint);
                    }
                }
            }
        }
    }
}
