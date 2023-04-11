package edu.duke.risc.ui.view;

import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.view.MotionEventCompat;

import java.util.ArrayList;
import java.util.Random;

import edu.duke.risc.R;
import edu.duke.risc.ui.draw.MapTiles;
import edu.duke.risc.ui.draw.MapUI;
import edu.duke.risc.ui.action.TouchEventMapping;
import edu.duke.risc.ui.state.MapAnimationType;
import edu.duke.risc.ui.state.MapUpdateType;
import edu.duke.risc.ui.state.TouchEvent;
import edu.duke.shared.Game;
import edu.duke.shared.helper.State;
import edu.duke.shared.map.GameMap;
import edu.duke.shared.player.Player;

/**
 * This class demonstrates the following interactive game basics:
 * <p>
 * * Manages a rendering thread that draws to a SurfaceView.
 * * Basic game loop that sleeps to conserve resources.
 * * Processes user input to update game state.
 * * Uses clipping as a means of animation.
 * <p>
 * Note that these are basic versions of these techniques.
 * Non-fatal edge cases are not handled.
 * Error handling is minimal. No logging. App assumes and uses a single thread.
 * Additional thread management would otherwise be necessary. See code comments.
 */

@SuppressLint("ViewConstructor")
public class GameView extends SurfaceView implements Runnable {
    // Last touch position
    private float mLastTouchX;
    private float mLastTouchY;
    // Active pointer ID
    private int mActivePointerId;
    // Position of the image
    private float mPosX;
    private float mPosY;
    // Update type
    private MapUpdateType mUpdateType = MapUpdateType.REFRESH;
    // Animation type
    private MapAnimationType mAnimationType = MapAnimationType.NONE;
    // Game state
    private boolean mRunning;
    // Game thread
    private Thread mGameThread = null;

    // Map tiles Component
    private MapTiles mMapTiles;
    // Map UI Component
    private MapUI mMapUI;
    // Paint
    private final Paint mPaint;
    // Surface holder
    private final SurfaceHolder mSurfaceHolder;
    // Game map
    private GameMap mGameMap = null;
    // Map view offset
    private int offsetX = 0;
    private int offsetY = 0;
    // Map view scale
    private float scale = 1.0f;
    // Gesture detector
    private final ScaleGestureDetector mScaleGestureDetector;
    // Handler
    private final Handler mHandler;
    // Zoom in/out lock
    private boolean pinchToZoom;
    // Touch event mapping
    private TouchEventMapping touchEventMapping;
    // Touch event isClick
    private boolean isClick = true;
    // Animation timer
    private boolean animationTimer500 = true;
    // Territory selected
    private String territorySelected = null;
    // Second time territory selected
    private String territorySelectedDouble = null;
    // Store the previous clicked territory
    private String territorySelectedPrevious = null;
    // Selection bubble bitmap
    private final Bitmap selectionBubbleBitmap;
    // Background image
    private final Bitmap backgroundImageBitmap;
    private EventListener eventListener;
    // Context
    private final Context mContext;
    // Game
    private Game mGame;
    // Rectangle for wallpaper
    private final Rect mRect = new Rect();
    // Wallpaper bitmap
    private final Bitmap wallpaperBitmap;

    /**
     * Constructor
     *
     * @param context the context
     * @param game    the game
     */
    public GameView(Context context, Game game) {
        this(context, null, game);
    }

    /**
     * Constructor
     *
     * @param context the context
     * @param attrs   the attributes
     * @param game    the game
     */
    public GameView(Context context, AttributeSet attrs, Game game) {
        super(context, attrs);
        this.mContext = context;
        this.mSurfaceHolder = getHolder();
        this.mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.mPaint.setFilterBitmap(true);
        this.mPaint.setStrokeWidth(1);
        this.mHandler = new Handler();
        this.mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        this.selectionBubbleBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.selection_bubble_bold);
        this.backgroundImageBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_map_opt);
        this.mGame = game;
        Random rand = new Random();
        int wallpaperId = rand.nextInt(8) + 1;
        switch (wallpaperId) {
            case 1:
                this.wallpaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_1);
                break;
            case 2:
                this.wallpaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_2);
                break;
            case 3:
                this.wallpaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_3);
                break;
            case 4:
                this.wallpaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_4);
                break;
            case 5:
                this.wallpaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_5);
                break;
            case 6:
                this.wallpaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_6);
                break;
            case 7:
                this.wallpaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_7);
                break;
            default:
                this.wallpaperBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.background_8);
                break;
        }
    }

    /**
     * This method is called every time the size of a view changes, including the first time
     * after it has been inflated.
     *
     * @param w    Current width of view.
     * @param h    Current height of view.
     * @param oldw Previous width of view.
     * @param oldh Previous height of view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mMapTiles = new MapTiles(mContext, w, h, backgroundImageBitmap);
        mMapUI = new MapUI(w, h, selectionBubbleBitmap);
        this.mRect.set(0, 0, w, h);
        mUpdateType = MapUpdateType.REFRESH;
    }

    /**
     * Runs in a separate thread.
     * All drawing happens here.
     */
    public void run() {

        Canvas canvas;

        while (mRunning) {
            // If we can obtain a valid drawing surface...
            if (mSurfaceHolder.getSurface().isValid()) {
                if (mUpdateType != MapUpdateType.NONE) {
                    canvas = mSurfaceHolder.lockCanvas();
                    // Draw background image
                    canvas.drawBitmap(wallpaperBitmap, null, mRect, null);
                    canvas.scale(scale, scale);
                    switch (mUpdateType) {
                        case REFRESH:
                        case ZOOM:
                            mMapTiles.update(canvas, this.mPaint, this.mGameMap, this.touchEventMapping);
                            mMapUI.update(canvas, this.mPaint, this.mGameMap, this.touchEventMapping);
                            break;
                        case MOVE:
                            mMapTiles.move(canvas, this.mPaint, this.touchEventMapping, this.offsetX, this.offsetY);
                            mMapUI.move(canvas, this.mPaint, this.touchEventMapping, this.offsetX, this.offsetY);
                            break;
                    }

                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                    mUpdateType = MapUpdateType.NONE;
                }
                if (mAnimationType != MapAnimationType.NONE) {
                    canvas = mSurfaceHolder.lockCanvas();
                    canvas.drawBitmap(wallpaperBitmap, null, mRect, null);
                    canvas.scale(scale, scale);
                    if (animationTimer500) {
                        switch (mAnimationType) {
                            case TERRITORY_SELECTED:
                                mMapTiles.selected(canvas, this.mPaint, this.touchEventMapping, this.territorySelected, this.territorySelectedDouble);
                                mMapUI.selected(canvas, this.mPaint, this.touchEventMapping, this.territorySelected, this.territorySelectedDouble);
                                break;
                            default:
                                break;
                        }
//                        this.animationTimer500 = false;
                        this.mAnimationType = MapAnimationType.NONE;
                    }
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    /**
     * Updates the game data.
     * Sets new coordinates for the flashlight cone.
     *
     * @param gameMap The map to be drawn.
     */
    public void updateMap(GameMap gameMap) {
        this.mGameMap = gameMap;
        this.touchEventMapping = new TouchEventMapping(mGameMap);
        mUpdateType = MapUpdateType.REFRESH;
    }

    /**
     * Updates the game data.
     * Sets new coordinates for the flashlight cone.
     *
     * @param game The game to be drawn.
     */
    public void updateGame(Game game) {
        this.mGame = game;
    }

    /**
     * Initializes the color mapping for the players.
     *
     * @param players The players to be drawn.
     */
    public void initColorMapping(ArrayList<Player> players) {
        mMapTiles.initColorMapping(players);
    }

    /**
     * Move the map to a new position.
     *
     * @param x offset in x direction
     * @param y offset in y direction
     */
    private void moveMap(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
        mUpdateType = MapUpdateType.MOVE;
    }

    /**
     * Zoom the map to a new scale.
     *
     * @param scale the canvas
     */
    private void zoomMap(float scale) {
        this.scale = scale;
        this.touchEventMapping.setScale(scale);
        mUpdateType = MapUpdateType.ZOOM;
    }


    /**
     * Called by MainActivity.onPause() to stop the thread.
     */
    public void pause() {
        mRunning = false;
        try {
            // Stop the thread == rejoin the main thread.
            mGameThread.join();
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * Called by MainActivity.onResume() to start a thread.
     */
    public void resume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    /**
     * Called by MainActivity.onTouchEvent() to handle touch screen input.
     *
     * @param event The MotionEvent object.
     * @return True if the event was handled, false otherwise.
     */
    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the ScaleGestureDetector read possible gestures and
        // if so, block the move event below.
        mScaleGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                this.isClick = true;

                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final float x = MotionEventCompat.getX(event, pointerIndex);
                final float y = MotionEventCompat.getY(event, pointerIndex);

                mLastTouchX = x;
                mLastTouchY = y;
                // Save the ID of this pointer (for dragging)
                mActivePointerId = MotionEventCompat.getPointerId(event, 0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                if (!pinchToZoom) {
                    // Find the index of the active pointer and fetch its position
                    final int pointerIndex =
                            MotionEventCompat.findPointerIndex(event, mActivePointerId);

                    final float x = MotionEventCompat.getX(event, pointerIndex);
                    final float y = MotionEventCompat.getY(event, pointerIndex);

                    // Calculate the distance moved
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    moveMap((int) mPosX, (int) mPosY);

                    invalidate();

                    // Remember this touch position for the next move event
                    mLastTouchX = x;
                    mLastTouchY = y;
                }
                break;
            }

            case MotionEvent.ACTION_UP: {
                if (mGame.getGameState() != State.READY_TO_INIT_UNITS) {
                    if (this.isClick) {
                        this.isClick = false;
                        if (this.touchEventMapping != null) {
                            mAnimationType = MapAnimationType.TERRITORY_SELECTED;
//                        animationTimer500();
                            // Record the last touch territory
                            if (territorySelected == null) {
                                territorySelected = this.touchEventMapping.getOnTouchObject((int) event.getY(), (int) event.getX(), territorySelected, mGame);
                            } else if (territorySelectedDouble == null) {
                                territorySelectedDouble = this.touchEventMapping.getOnTouchObject((int) event.getY(), (int) event.getX(), territorySelected, mGame);
                                // When the first touch is a territory, and the second touch is an
                                // action, the first territory will be recorded.
                                if (!TouchEventMapping.checkIsAction(territorySelected) && territorySelectedDouble.equals(TouchEvent.ORDER.name())) {
                                    territorySelectedPrevious = territorySelected;
                                }
                                actionCallback();
                            } else {
                                if (!TouchEventMapping.checkIsAction(territorySelected) && territorySelectedDouble.equals(TouchEvent.ORDER.name())) {
                                    territorySelectedPrevious = territorySelected;
                                }
                                territorySelected = territorySelectedDouble;
                                territorySelectedDouble = this.touchEventMapping.getOnTouchObject((int) event.getY(), (int) event.getX(), territorySelected, mGame);
                                actionCallback();
                            }
                        }
                    }
                }

                mActivePointerId = INVALID_POINTER_ID;
                mHandler.removeCallbacksAndMessages(null);
                pinchToZoom = false;
                testIfClick();
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {

                final int pointerIndex = MotionEventCompat.getActionIndex(event);
                final int pointerId = MotionEventCompat.getPointerId(event, pointerIndex);

                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = MotionEventCompat.getX(event, newPointerIndex);
                    mLastTouchY = MotionEventCompat.getY(event, newPointerIndex);
                    mActivePointerId = MotionEventCompat.getPointerId(event, newPointerIndex);
                }
                break;
            }
        }
        return true;
    }

    /**
     * Called by MainActivity.onTouchEvent() to handle touch screen input.
     *
     * @param eventListener the event listener
     */
    public void setEventListener(GameView.EventListener eventListener) {
        this.eventListener = eventListener;
    }

    /**
     * Called by MainActivity.onTouchEvent() to handle touch screen input.
     */
    private void actionCallback() {
        // Callback function in GameFragment
        TouchEvent touchEvent = TouchEventMapping.getAction(territorySelected, territorySelectedDouble, territorySelectedPrevious, mGameMap);
        if (touchEvent != null) {
            switch (touchEvent) {
                case UNIT:
                    System.out.println("UNIT ORDER");
                    eventListener.onUnitOrder(territorySelected);
                    break;
                case PROP:
                    System.out.println("PROP ORDER");
                    eventListener.onPropOrder(territorySelected);
                    break;
                case ATTACK:
                    System.out.println("ATTACK ORDER");
                    eventListener.onAttackOrder(territorySelectedPrevious, territorySelectedDouble);
                    break;
                case MOVE:
                    System.out.println("MOVE ORDER");
                    eventListener.onMoveOrder(territorySelectedPrevious, territorySelectedDouble);
                default:
                    break;
            }
        }
    }

    /**
     * ScaleListener is used to detect pinch to zoom gestures.
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            scale *= scaleGestureDetector.getScaleFactor();
            scale = Math.max(0.5f, Math.min(scale, 2.0f));
            zoomMap(scale);
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            pinchToZoom = true;
            return super.onScaleBegin(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            super.onScaleEnd(detector);
        }
    }

    /**
     * Test if the touch is a click.
     */
    private void testIfClick() {
        mHandler.postDelayed(() -> this.isClick = false, 500);
    }

    /**
     * Animation timer.
     */
    private void animationTimer500() {
        mHandler.postDelayed(() -> this.animationTimer500 = true, 500);
    }

    /**
     * Event listener.
     */
    public abstract static class EventListener {

        public abstract void onMoveOrder(String terrFrom, String terrTo);

        public abstract void onAttackOrder(String terrFrom, String terrTo);

        public abstract void onPropOrder(String territoryName);

        public abstract void onUnitOrder(String territoryName);
    }
}