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
        boolean locked = false;
        Canvas canvas;
        while(isRunning) {
            canvas = null;

            if(!locked) {
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    screen.update();
                    screen.draw(canvas);
                }
                locked = true;
            }

            if(locked) {
                surfaceHolder.unlockCanvasAndPost(canvas);
                locked = false;
            }
        }
    }
}
