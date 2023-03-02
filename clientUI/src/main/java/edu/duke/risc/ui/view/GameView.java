package edu.duke.risc.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import edu.duke.risc.R;
import edu.duke.risc.display.draw.MapTiles;

public class GameView extends SurfaceView implements Runnable{
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    private Path mPath;
    private Context mContext;
    private Thread mGameThread;
    private boolean mRunning;
    private int mViewWidth;
    private int mViewHeight;
    private int mBitmapX;
    private int mBitmapY;
    private RectF mWinnerRect;
    private Bitmap mBitmap;
    private MapTiles mMapTiles;



    public GameView(Context context) {
        super(context);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mPaint.setColor(Color.DKGRAY);
        mPath = new Path();
    }

    private void setUpBitmap() {
        mBitmapX = (int) Math.floor(
                Math.random() * (mViewWidth - mBitmap.getWidth()));
        mBitmapY = (int) Math.floor(
                Math.random() * (mViewHeight - mBitmap.getHeight()));
        mWinnerRect = new RectF(mBitmapX, mBitmapY,
                mBitmapX + mBitmap.getWidth(),
                mBitmapY + mBitmap.getHeight());
    }

    @Override
    public void run() {
        if (mSurfaceHolder.getSurface().isValid()) {

            Canvas canvas;
            while (mRunning) {
                if (mSurfaceHolder.getSurface().isValid()) {
                    int x = mMapTiles.getX();
                    int y = mMapTiles.getY();
                    int radius = mMapTiles.getRadius();
                    canvas = mSurfaceHolder.lockHardwareCanvas();
                    canvas.save();
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(mBitmap, mBitmapX, mBitmapY, mPaint);
                    mPath.addCircle(x, y, radius, Path.Direction.CCW);
                    canvas.clipPath(mPath, Region.Op.DIFFERENCE);
                    canvas.drawColor(Color.BLACK);
                    if (x > mWinnerRect.left && x < mWinnerRect.right
                            && y > mWinnerRect.top && y < mWinnerRect.bottom) {
                        canvas.drawColor(Color.WHITE);
                        canvas.drawBitmap(mBitmap, mBitmapX, mBitmapY, mPaint);
                        canvas.drawText(
                                "WIN!", mViewWidth / 3, mViewHeight / 2, mPaint);
                    }
                    mPath.rewind();
                    canvas.restore();
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }

    public void pause() {
        mRunning = false;
        try {
            // Stop the thread (rejoin the main thread)
            mGameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        mRunning = true;
        mGameThread = new Thread(this);
        mGameThread.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mViewWidth = w;
        mViewHeight = h;
        mMapTiles = new MapTiles(mViewWidth, mViewHeight);
        mPaint.setTextSize(mViewHeight / 5);
        mBitmap = BitmapFactory.decodeResource(
                mContext.getResources(), R.drawable.ic_launcher_background);
        setUpBitmap();
    }
}
