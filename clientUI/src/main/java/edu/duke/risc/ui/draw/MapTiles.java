package edu.duke.risc.ui.draw;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

import edu.duke.risc.R;
import edu.duke.risc.ui.action.TouchEventMapping;
import edu.duke.risc.ui.state.TouchEvent;
import edu.duke.shared.helper.Validation;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;

public class MapTiles {
    // Map view width and height
    private final int mapViewWidth;
    private final int mapViewHeight;
    // Padding
    private final int paddingTop;
    private int paddingLeft = 64;

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
    // Background image
    private final Bitmap backgroundImageBitmap;
    // Background image rect
    private Typeface plain;

    /**
     * Constructor
     *
     * @param viewWidth  width of the view
     * @param viewHeight height of the view
     */
    public MapTiles(Context mContext, int viewWidth, int viewHeight, Bitmap backgroundImageBitmap) {
        this.paddingLeft = Math.max(viewWidth / 10, this.paddingLeft);
        int paddingRight = 64;
        this.paddingTop = Math.max(viewHeight / 10, paddingRight);
        paddingRight = this.paddingLeft;
        this.mapViewWidth = viewWidth - this.paddingLeft - paddingRight;
        this.mapViewHeight = viewHeight - this.paddingTop - this.paddingTop;
        this.backgroundImageBitmap = backgroundImageBitmap;
        plain = mContext.getResources().getFont(R.font.harry_potter);
        plain = Typeface.create(plain, Typeface.ITALIC);
    }

    /**
     * Initialize the color mapping
     *
     * @param players players
     */
    public void initColorMapping(ArrayList<Player> players) {
        int[] colors = new int[]{0x66003366, 0x55660000, 0x66003300, 0x55CC9900};
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
        int size = Math.min(this.mapViewWidth / width, this.mapViewHeight / height);

        // Draw background image
        Rect src = new Rect((int) (this.paddingLeft + offsetX), (int) (this.paddingTop + offsetY), (int) (this.paddingLeft + offsetX) + width * size, (int) (this.paddingTop + offsetY) + height * size);
        canvas.drawBitmap(this.backgroundImageBitmap, null, src, null);

        System.out.println(territorySelected + " " + territorySelectedDouble);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                String owner = map.getOwnerByCoord(y, x);
                String selectedOwner = map.getOwnerByTerrName(territorySelected);
                String territoryName = map.getTerritoryNameByCoord(y, x);
                Territory t = map.getTerritory(territoryName);
                mPaint.setColor(ownerColor.getOrDefault(owner, 0x44000000));
                byte pattern = map.isBorderPoint(y, x);

                // Draw new map tile
                canvas.drawRect(this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + y * size, this.paddingLeft + offsetX + x * size + size, this.paddingTop + offsetY + y * size + size, mPaint);

                if (pattern != 0) {
                    /* *****************
                     Draw border of current selected territory
                    ****************** */
                    mPaint.setColor(0x44000000);
                    // If this border is selected
                    if (showCurrentTerr(territoryName, territorySelected, territorySelectedDouble)) {
                        this.boarderSize = 6;
                        mPaint.setColor(0xCCFFFFFF);
                    }

                    /* *****************
                     Draw border of adjacent territory if "order" button is clicked
                    ****************** */
                    if (showAdjacentTerr(territorySelected, territorySelectedDouble)) {
                        if (t.isAdjacent(territorySelected)) {
                            this.boarderSize = 6;
                            // Self territory
                            if (owner.equals(selectedOwner)) {
                                mPaint.setColor(0xCC29B6F6);
                            }
                            // Other's territory
                            else {
                                mPaint.setColor(0xCCEF5350);
                            }
                        }
                        // Also add border of the territory that can be visited by path
                        if (showSelfTerr(territoryName, territorySelected)) {
                            this.boarderSize = 6;
                            mPaint.setColor(0xCC29B6F6);
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
                mPaint.setColor(ownerColor.getOrDefault(owner, 0x44000000));
                // Update center point of each territory
                if (touchEventMapping.isCenterPoint(y, x)) {
                    assert (touchEventMapping.updateTerritoryMapping(map.getTerritoryNameByCoord(y, x), new int[]{(int) (this.paddingTop + offsetY + y * size + size / 2), (int) (this.paddingLeft + offsetX + x * size + size / 2)}));
                    // show center point
                    mPaint.setColor(0xDD000000);
                    mPaint.setTextSize(size);
                    mPaint.setTypeface(plain);
                    int textWidth = (int) mPaint.measureText(territoryName);
                    canvas.drawText(territoryName, this.paddingLeft + offsetX + (int) ((x + 0.5) * size) - (textWidth / 2), this.paddingTop + offsetY + (y + 1) * size, mPaint);
                    // Draw distance text
                    if (showAdjacentTerr(territorySelected, territorySelectedDouble)) {
                        Territory t = map.getTerritory(territoryName);
                        String selectedOwner = map.getOwnerByTerrName(territorySelected);
                        boolean distanceFlag = false;
                        if (t.isAdjacent(territorySelected)) {
                            // Other's territory
                            if (!owner.equals(selectedOwner)) {
                                distanceFlag = true;
                            }
                        }
                        // Also add distance that can be visited by path
                        if (showSelfTerr(territoryName, territorySelected)) {
                            distanceFlag = true;
                        }

                        if (distanceFlag) {
                            // Show distance between two territories
                            mPaint.setColor(0xFFF57F17);
                            // If the territory is owned by the player, show the distance
                            if (owner.equals(selectedOwner)) {
                                canvas.drawText(String.valueOf(map.getShortestDistance(territoryName, territorySelected)), this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + (y) * size, mPaint);
                            } else {
                                canvas.drawText(String.valueOf(map.getDistance(territoryName, territorySelected)), this.paddingLeft + offsetX + x * size, this.paddingTop + offsetY + (y) * size, mPaint);
                            }
                        }
                    }
                }
            }
        }
        touchEventMapping.updateBoundary(new int[]{(int) (this.paddingTop + offsetY), (int) (this.paddingLeft + offsetX + width * size), (int) (this.paddingTop + offsetY + height * size), (int) (this.paddingLeft + offsetX)});
    }

    /**
     * Draw borders of the territory
     *
     * @param pattern 1: top, 2: right, 4: bottom, 8: left
     * @param y       y coordinate
     * @param x       x coordinate
     * @param size    size of the territory
     * @param canvas  canvas
     * @param mPaint  paint
     */
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

    /**
     * Check if the territory been selected should display its border
     *
     * @param territoryName the territory name been checked
     * @param selected1     the first selected event
     * @param selected2     the second selected event
     * @return true if the territory should display its border
     */
    private boolean showCurrentTerr(String territoryName, String selected1, String selected2) {
        // If the territory is selected
        if (territoryName.equals(selected2)) return true;
        // If the territory is selected from the first time
        if (selected2 == null && territoryName.equals(selected1)) return true;
        // If "order" action is selected
        return !TouchEventMapping.checkIsAction(selected1) && territoryName.equals(selected1) && selected2 != null && selected2.equals(TouchEvent.ORDER.name());
    }

    /**
     * Check if the territory been selected should display its adjacent territories
     *
     * @param selected1 the first selected event
     * @param selected2 the second selected event
     * @return true if the territory should display its adjacent territories
     */
    private boolean showAdjacentTerr(String selected1, String selected2) {
        // If "order" action is selected
        return selected1 != null && selected2 != null && selected2.equals(TouchEvent.ORDER.name());
    }

    /**
     * Check if the territory been selected should display its adjacent territories
     *
     * @param territoryName the territory name been checked
     * @param selected1     the first selected event
     * @return true if the territory should display its adjacent territories
     */
    private boolean showSelfTerr(String territoryName, String selected1) {
        // If "order" action is selected
        return !TouchEventMapping.checkIsAction(selected1) && !selected1.equals(territoryName) && Validation.checkPathExist(this.mGameMap, selected1, territoryName);
    }
}
