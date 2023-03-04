package edu.duke.risc.ui.view;

import static android.view.MotionEvent.INVALID_POINTER_ID;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.core.view.MotionEventCompat;

import edu.duke.risc.display.draw.MapTiles;
import edu.duke.shared.GameMap;

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

public class GameView extends SurfaceView implements Runnable {

    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId;
    private float mPosX;
    private float mPosY;

    private enum MapUpdateType {
        NONE,
        REFRESH,
        MOVE,
        ZOOM
    }

    private MapUpdateType mUpdateType = MapUpdateType.REFRESH;
    private boolean mRunning;
    private Thread mGameThread = null;

    private Context mContext;

    private MapTiles mMapTiles;

    private Paint mPaint;

    private int mViewWidth;
    private int mViewHeight;
    private final SurfaceHolder mSurfaceHolder;

    private GameMap mGameMap = null;
    private int offsetX = 0;
    private int offsetY = 0;
    private float scale = 1.0f;
    private ScaleGestureDetector mScaleGestureDetector;
    private Handler mHandler;
    private boolean pinchToZoom;


    public GameView(Context context) {
        this(context, null);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mSurfaceHolder = getHolder();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setFilterBitmap(true);
        mPaint.setStrokeWidth(1);
        mHandler = new Handler();
        mScaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
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
        mViewWidth = w;
        mViewHeight = h;
        mMapTiles = new MapTiles(mViewWidth, mViewHeight);
    }

    /**
     * Runs in a separate thread.
     * All drawing happens here.
     */
    public void run() {

        Canvas canvas;

        while (mRunning) {
            // If we can obtain a valid drawing surface...
            if (mSurfaceHolder.getSurface().isValid() && mUpdateType != MapUpdateType.NONE) {

                // Lock the canvas. Note that in a more complex app, with
                // more threads, you need to put this into a try/catch block
                // to make sure only one thread is drawing to the surface.
                // Starting with O, you can request a hardware surface with
                //    lockHardwareCanvas().
                // See https://developer.android.com/reference/android/view/
                //    SurfaceHolder.html#lockHardwareCanvas()
                canvas = mSurfaceHolder.lockCanvas();

                canvas.drawColor(Color.WHITE);
                canvas.scale(scale, scale);
                switch (mUpdateType) {
                    case REFRESH:
                    case ZOOM:
                        mMapTiles.update(canvas, this.mPaint, this.mGameMap);
                        break;
                    case MOVE:
                        mMapTiles.move(canvas, this.mPaint, this.offsetX, this.offsetY);
                        break;
                }

                // Restore the previously saved (default) clip and matrix state.
                // Release the lock on the canvas and show the surface's
                // contents on the screen.
                mSurfaceHolder.unlockCanvasAndPost(canvas);
                mUpdateType = MapUpdateType.NONE;
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
        mUpdateType = MapUpdateType.REFRESH;
    }

    /**
     * Move the map to a new position.
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
     * @param scale the canvas
     */
    private void zoomMap(float scale) {
        this.scale = scale;
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
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let the ScaleGestureDetector read possible gestures and
        // if so, block the move event below.
        mScaleGestureDetector.onTouchEvent(event);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
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
                mActivePointerId = INVALID_POINTER_ID;
                mHandler.removeCallbacksAndMessages(null);
                pinchToZoom = false;
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
            resetFlag();
            super.onScaleEnd(detector);
        }
    }

    private void resetFlag() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 500);
    }


}