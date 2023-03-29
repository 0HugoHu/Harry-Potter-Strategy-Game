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
import edu.duke.shared.map.Territory;
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

    // Map movement offset
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;

    // Boarder size
    private int boarderSize = 2;

    // Territory mapping
    private GameMap mGameMap = null;
    // Territory selected
    private String territorySelected = null;
    // Second time territory selected
    private String territorySelectedDouble = null;

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
        int[] colors = new int[]{0xFFB2DFDB, 0xFFC5CAE9, 0xFFD7CCC8, 0xFFC5CAE9};
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

    /**
     * Select a territory
     *
     * @param canvas                  canvas
     * @param mPaint                  paint
     * @param touchEventMapping       touch event mapping
     * @param territoryName           territory name previously selected
     * @param territorySelectedDouble territory name that is selected
     */
    public void selected(Canvas canvas, Paint mPaint, TouchEventMapping touchEventMapping, String territoryName, String territorySelectedDouble) {
        this.territorySelected = territoryName;
        this.territorySelectedDouble = territorySelectedDouble;
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
        int size = (int) (Math.min(this.mapViewWidth / width, this.mapViewHeight / height));

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String owner = map.getOwnerByCoord(y, x);
                String selectedOwner = map.getOwnerByTerrName(territorySelected);
                String territoryName = map.getTerritoryNameByCoord(y, x);
                Territory t = map.getTerritory(territoryName);
                mPaint.setColor(ownerColor.getOrDefault(owner, 0xFF000000));
                byte pattern = map.isBorderPoint(y, x);

                // Draw new tile
                canvas.drawRect(this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + y * size, this.paddingLeft + offsetX + x * size + size, this.paddingTop + offsetY + y * size + size, mPaint);

                if (pattern != 0) {
                    /* *****************
                     Draw border of current selected territory
                    ****************** */
                    mPaint.setColor(0xFF000000);
                    // If this border is selected
                    if (territoryName.equals(this.territorySelectedDouble) || territorySelectedDouble == null && territoryName.equals(this.territorySelected)) {
                        this.boarderSize = 6;
                        mPaint.setColor(0xFFFFFFFF);
                    }

                    /* *****************
                     Draw border of adjacent territory if "move" is selected
                    ****************** */
                    if (territorySelected != null && this.territorySelected.equals(this.territorySelectedDouble)) {

                        if (t.isAdjacent(territorySelectedDouble)) {
                            this.boarderSize = 6;
                            // Self territory
                            if (owner.equals(selectedOwner)) {
                                mPaint.setColor(0xFF29B6F6);
                            }
                            // Other's territory
                            else {
                                mPaint.setColor(0xFFEF5350);
                            }
                        }
                    }
                    drawBorders(pattern, y, x, size, canvas, mPaint);
                }
            }
            // Traversal the line to find the center point, and draw the text
            // otherwise, the text will be covered by the adjacent tiles
            for (int x = 0; x < width; x++) {
                String owner = map.getOwnerByCoord(y, x);
                String territoryName = map.getTerritoryNameByCoord(y, x);
                mPaint.setColor(ownerColor.getOrDefault(owner, 0xFF000000));
                // Update center point of each territory
                if (touchEventMapping.isCenterPoint(y, x)) {
                    // show center point
                    mPaint.setColor(0xFF000000);
                    mPaint.setTextSize(size);
                    canvas.drawText(territoryName + ": " + map.getTerritory(territoryName).getNumUnits(), this.paddingLeft + offsetX + (int) ((x - 0.5) * size), this.paddingTop + offsetY + (int) ((y + 0.5) * size), mPaint);
                }
            }
        }
    }

    private void drawBorders(byte pattern, int y, int x, int size, Canvas canvas, Paint mPaint) {
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
