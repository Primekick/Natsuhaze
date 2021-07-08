package pl.pg.adamil.natsuhaze;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;

public class ScreenThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private Screen screen;
    public static Canvas canvas;
    private boolean isRunning;

    public ScreenThread(SurfaceHolder surfaceHolder, Screen screen) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.screen = screen;
        isRunning = false;
    }

    public void setRunning(boolean set) {
        isRunning = set;
    }

    @Override
    public void run() {
        synchronized (this) {
            boolean locked = false;
            Canvas canvas;
            while(isRunning) {
                canvas = null;
                try {
                    canvas = surfaceHolder.lockCanvas();
                    if (canvas != null) {
                        screen.draw(canvas);
                    }
                }
                catch (IllegalStateException e) {
                    e.printStackTrace();
                }
                finally {
                    try {
                        if(canvas != null) {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        }
                    }
                    catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
}
