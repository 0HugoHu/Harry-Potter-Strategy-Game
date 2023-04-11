package edu.duke.risc.ui.draw;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.method.Touch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.duke.risc.ui.action.TouchEventMapping;
import edu.duke.risc.ui.state.TouchEvent;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.map.Territory;
import edu.duke.shared.player.Player;

public class MapUI {
    // Map view width and height
    private final int mapViewWidth;
    private final int mapViewHeight;
    // Padding
    private int paddingTop;
    private int paddingBottom;
    private int paddingLeft = 64;
    private int paddingRight = 64;

    // Map movement offset
    private float offsetX = 0.0f;
    private float offsetY = 0.0f;

    // Territory mapping
    private GameMap mGameMap = null;
    // Territory selected
    private String territorySelected = null;
    // Second time territory selected
    private String territorySelectedDouble = null;
    // Selection bubble bitmap
    private final Bitmap selectionBubbleBitmap;

    /**
     * Constructor
     *
     * @param viewWidth  width of the view
     * @param viewHeight height of the view
     */
    public MapUI(int viewWidth, int viewHeight, Bitmap selectionBubbleBitmap) {
        this.paddingLeft = Math.max(viewWidth / 10, this.paddingLeft);
        this.paddingTop = Math.max(viewHeight / 10, this.paddingRight);
        this.paddingRight = this.paddingLeft;
        this.paddingBottom = this.paddingTop;
        this.mapViewWidth = viewWidth - this.paddingLeft - this.paddingRight;
        this.mapViewHeight = viewHeight - this.paddingTop - this.paddingBottom;
        this.selectionBubbleBitmap = selectionBubbleBitmap;
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

        if (territorySelected != null) {
            Territory t;
            if (territorySelectedDouble == null) {
                t = map.getTerritory(territorySelected);
            } else if (territorySelectedDouble.equals(TouchEvent.OUTSIDE.name())){
                return;
            } else {
                t = map.getTerritory(territorySelectedDouble);
            }
            if (t != null) {
                String territoryName = t.getName();

                if (territoryName.equals(this.territorySelectedDouble) || territoryName.equals(this.territorySelected)) {
                    int[] centerPoint = touchEventMapping.getCenterPoint(t);
                    int baseX = (int) (centerPoint[1] * size + offsetX + paddingLeft);
                    int baseY = (int) (centerPoint[0] * size + offsetY + paddingTop);
                    Rect rectangle = new Rect(baseX - 4 * size, baseY - 9 * size, baseX + 4 * size, baseY);
                    // Update the selection panel center point with its radius
                    assert(touchEventMapping.updateSelectionPanelMapping(TouchEvent.ORDER.name(), new int[] {baseY - 5 * size, baseX + 2 * size}, 2 * size));
                    assert(touchEventMapping.updateSelectionPanelMapping(TouchEvent.UNIT.name(), new int[] {baseY - 5 * size, baseX - 2 * size}, 2 * size));
                    assert(touchEventMapping.updateSelectionPanelMapping(TouchEvent.PROP.name(), new int[] {baseY - 2 * size, baseX}, 2 * size));
                    canvas.drawBitmap(this.selectionBubbleBitmap, null, rectangle, null);
                }
            }

        }


    }

}
