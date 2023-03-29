package edu.duke.risc.display.draw;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import edu.duke.risc.ui.action.TouchEventMapping;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.player.Player;

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

    // Boarder size
    private int boarderSize = 2;

    // Territory mapping
    private GameMap mGameMap = null;
    // Territory selected
    private String territorySelected = null;

    // Territory color mapping
    Map<String, Integer> ownerColor = new HashMap<>();


    /**
     * Constructor
     *
     * @param viewWidth  width of the view
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

    public void initColorMapping(ArrayList<Player> players) {
        int[] colors = new int[]{0xFF1ABC9C, 0xFF3498DB, 0xFFE74C3C, 0xFFF1C40F};
        for (Player player : players) {
            this.ownerColor.put(player.getPlayerName(), colors[player.getPlayerId()]);
        }
    }

    /**
     * Move the map
     *
     * @param canvas            canvas
     * @param mPaint            paint
     * @param touchEventMapping touch event mapping
     * @param dx                x offset
     * @param dy                y offset
     */
    public void move(Canvas canvas, Paint mPaint, TouchEventMapping touchEventMapping, float dx, float dy) {
        this.offsetX = dx;
        this.offsetY = dy;
        this.update(canvas, mPaint, this.mGameMap, touchEventMapping);
    }

    public void zoom(Canvas canvas, Paint mPaint, TouchEventMapping touchEventMapping, float scale) {
        this.scale = scale;
        this.update(canvas, mPaint, this.mGameMap, touchEventMapping);
    }

    /**
     * Select a territory
     *
     * @param canvas            canvas
     * @param mPaint            paint
     * @param touchEventMapping touch event mapping
     * @param territoryName     territory name
     */
    public void selected(Canvas canvas, Paint mPaint, TouchEventMapping touchEventMapping, String territoryName) {
        this.territorySelected = territoryName;
        this.update(canvas, mPaint, this.mGameMap, touchEventMapping);
    }

    /**
     * Update the map tiles
     *
     * @param canvas canvas
     * @param mPaint paint
     * @param map    map
     */
    public void update(Canvas canvas, Paint mPaint, GameMap map, TouchEventMapping touchEventMapping) {
        if (map == null) {
            return;
        }
        this.mGameMap = map;

        int height = map.getHeight();
        int width = map.getWidth();
        int size = (int) (Math.min(this.mapViewWidth / width, this.mapViewHeight / height) * scale);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String owner = map.getOwnerByCoord(y, x);
                String territoryName = map.getTerritoryNameByCoord(y, x);
                mPaint.setColor(ownerColor.getOrDefault(owner, 0xFF000000));
                byte pattern = map.isBorderPoint(y, x);

                // Update center point of each territory
                if (touchEventMapping.isCenterPoint(y, x)) {
                    assert (touchEventMapping.updateTerritoryMapping(map.getTerritoryNameByCoord(y, x), new int[]{(int) (this.paddingTop + offsetY + y * size + size / 2), (int) (this.paddingLeft + offsetX + x * size + size / 2)}));
                    mPaint.setColor(0xFF000000);
                    mPaint.setTextSize(size);
                    canvas.drawText(territoryName + ": " + map.getTerritory(territoryName).getNumUnits(), this.paddingLeft + offsetX + (int)((x - 0.5) * size), this.paddingTop + offsetY + (int)((y - 0.5) * size), mPaint);
                }

                // Draw new tile
                canvas.drawRect(this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + y * size, this.paddingLeft + offsetX + x * size + size, this.paddingTop + offsetY + y * size + size, mPaint);

                // Draw new border
                if (pattern != 0) {
                    mPaint.setColor(0xFF000000);
                    // If this border is selected
                    if (territoryName.equals(this.territorySelected)) {
                        this.boarderSize = 6;
                        mPaint.setColor(0xFFFFFFFF);
                    }

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
                    // Reset border size
                    this.boarderSize = 2;
                }


            }
        }
    }

}
