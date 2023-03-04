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
    private int paddingTop = 64;
    private int paddingBottom = 64;
    private int paddingLeft = 64;
    private int paddingRight = 64;

    // Map scale and offset
    private float scale = 1.0f;
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;
    
    private int boarderSize = 2;

    private GameMap mGameMap = null;
    

    /**
     * Constructor
     * @param viewWidth width of the view
     * @param viewHeight height of the view
     */
    public MapTiles(int viewWidth, int viewHeight) {
        this.paddingLeft = Math.max(viewWidth / 10, this.paddingLeft);
        this.paddingTop = Math.max(viewHeight / 10, this.paddingRight);
        this.paddingRight = this.paddingLeft;
        this.paddingBottom = this.paddingTop;
        this.mapViewWidth = viewWidth - this.paddingLeft - this.paddingRight;
        this.mapViewHeight = viewHeight - this.paddingTop - this.paddingBottom;
    }

    public void move(Canvas canvas, Paint mPaint, float dx, float dy) {
        this.offsetX = dx;
        this.offsetY = dy;
        this.update(canvas, mPaint, this.mGameMap);
    }

    public void zoom(Canvas canvas, Paint mPaint, float scale) {
        this.scale = scale;
        this.update(canvas, mPaint, this.mGameMap);
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
        this.mGameMap = map;

        Set<String> owners = new HashSet<>();
        Map<String, Integer> ownerColor = new HashMap<>();
        ownerColor.put("Player0", 0xFF1ABC9C);
        ownerColor.put("Player1", 0xFF3498DB);
        ownerColor.put("Player2", 0xFFE74C3C);
        ownerColor.put("Player3", 0xFFF1C40F);


        int height = map.getHeight();
        int width = map.getWidth();
        int size = (int) (Math.min(this.mapViewWidth / width, this.mapViewHeight / height) * scale);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String owner = map.getOwnerByCoord(y, x);
                mPaint.setColor(ownerColor.getOrDefault(owner, 0xFF000000));
                byte pattern = map.isBorderPoint(y, x);

                canvas.drawRect(this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + y * size, this.paddingLeft + offsetX + x * size + size, this.paddingTop + offsetY + y * size + size, mPaint);
                if (pattern != 0) {
                    mPaint.setColor(0xFF000000);
                    // Draw top border
                    if ((pattern & 1) != 0) {
                        canvas.drawRect(this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + y * size, this.paddingLeft + offsetX + x * size + size, this.paddingTop + offsetY + y * size + this.boarderSize, mPaint);
                    }
                    // Draw right border
                    if ((pattern & 2) != 0) {
                        canvas.drawRect(this.paddingLeft + offsetX + x * size + size - this.boarderSize, this.paddingTop + offsetY + y * size, this.paddingLeft + offsetX + x * size + size, this.paddingTop + offsetY + y * size + size, mPaint);
                    }
                    // Draw bottom border
                    if ((pattern & 4) != 0) {
                        canvas.drawRect(this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + y * size + size - this.boarderSize, this.paddingLeft + offsetX + x * size + size, this.paddingTop + offsetY + y * size + size, mPaint);
                    }
                    // Draw left border
                    if ((pattern & 8) != 0) {
                        canvas.drawRect(this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + y * size, this.paddingLeft + offsetX + x * size + this.boarderSize, this.paddingTop + offsetY + y * size + size, mPaint);
                    }
                }
            }
        }
    }
}
