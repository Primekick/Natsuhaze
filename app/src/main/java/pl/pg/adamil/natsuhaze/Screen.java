package pl.pg.adamil.natsuhaze;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

public class Screen extends SurfaceView implements SurfaceHolder.Callback {

    private ScreenThread screenThread;
    private Paint[] palette;
    private byte[] data;
    private int screenMultiplier;
    private String text;

    public Screen(Context context) {
        super(context);
        data = new byte[160*144];
        text = "test";
        getHolder().addCallback(this);
        screenThread = new ScreenThread(getHolder(), this);
        setFocusable(true);
        setVisibility(View.VISIBLE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("Screen", "Starting thread!");
        screenThread.setRunning(true);
        screenThread.start();
        screenMultiplier = 5;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean trying = true;
        while(trying) {
            try {
                screenThread.setRunning(false);
                screenThread.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
            trying = false;
        }
    }

    public void update() {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        int width = Resources.getSystem().getDisplayMetrics().widthPixels;
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        if(canvas != null) {
            canvas.save();
            for(int y = 0; y < 144; y++) {
                for(int x = 0; x < 160; x++) {
                    canvas.drawRect(screenMultiplier * x,
                            screenMultiplier * y,
                            screenMultiplier * (x + 1),
                            screenMultiplier * (y + 1),
                            palette[0]);
                }
            }
            canvas.restore();
            canvas.drawText(text, width/2, height/2, palette[1]);
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public void init() {
        palette = new Paint[4];
        palette[0] = new Paint();
        palette[0].setColor(Color.RED);

        palette[1] = new Paint();
        palette[1].setStyle(Paint.Style.FILL);
        palette[1].setColor(Color.WHITE);
        palette[1].setTextSize(24 * getResources().getDisplayMetrics().density);
        System.out.println("Initializing Screen");
    }

    public void setData(byte[] data) {
        synchronized (data) {
            this.data = data;
        }
    }
}
