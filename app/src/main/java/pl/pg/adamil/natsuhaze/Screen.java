package pl.pg.adamil.natsuhaze;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Screen extends SurfaceView implements SurfaceHolder.Callback {

    private ScreenThread screenThread;
    private Context context;
    private boolean started;
    private Paint[] palette;
    private byte[][] data;
    private int screenMultiplier;
    private String text;
    private HashMap<String, Register16Bit> regs;

    public Screen(Context context) {
        super(context);
        this.context = context;
        data = new byte[144][160];
        for(byte[] row: data){
            Arrays.fill(row, (byte) 0);
        }
        getHolder().addCallback(this);
        text = "test";
        started = false;
        regs = new HashMap<String, Register16Bit>();
        setFocusable(true);

        init();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        resume();
        screenMultiplier = 6;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    public void pause() {
        Log.i("Screen", "Killing thread...");
        boolean trying = true;
        while(trying) {
            try {
                screenThread.setRunning(false);
                screenThread.join();
            } catch (InterruptedException e) { e.printStackTrace(); }
            trying = false;
            started = false;
            Log.i("Screen", "Thread killed!");
        }
    }

    public void resume() {
        if(!started) {
            Log.i("Screen", "Starting thread!");
            screenThread = new ScreenThread(getHolder(), this);
            screenThread.setRunning(true);
            screenThread.start();
            started = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(started) {
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
                                palette[data[y][x]]);
                    }
                }
                canvas.restore();
                int y = height/2;
                canvas.drawText(text, 0, y, palette[4]);
                for(Map.Entry<String, Register16Bit> entry : regs.entrySet()) {
                    y += 64;
                    canvas.drawText(entry.getKey() + ":", 0, y, palette[4]);
                    canvas.drawText(Integer.toHexString(entry.getValue().intValue() & 0xFFFF), 128, y, palette[4]);
                }
            }
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setReg(String regId, Register16Bit reg) {
        if(regs.containsKey(regId)) {
            regs.get(regId).set(reg.shortValue());
        } else {
            regs.put(regId, reg);
        }
    }

    public void init() {
        palette = new Paint[5];
        palette[0] = new Paint();
        palette[0].setColor(Color.parseColor("#8be5ff"));

        palette[1] = new Paint();
        palette[1].setColor(Color.parseColor("#608fcf"));

        palette[2] = new Paint();
        palette[2].setColor(Color.parseColor("#7550e8"));

        palette[3] = new Paint();
        palette[3].setColor(Color.parseColor("#622e4c"));

        palette[4] = new Paint();
        palette[4].setStyle(Paint.Style.FILL);
        palette[4].setColor(Color.WHITE);
        palette[4].setTextSize(24 * getResources().getDisplayMetrics().density);
        System.out.println("Initializing Screen");
    }

    public void setData(byte[][] data) {
        synchronized (this.data) {
            this.data = data;
        }
    }
}
