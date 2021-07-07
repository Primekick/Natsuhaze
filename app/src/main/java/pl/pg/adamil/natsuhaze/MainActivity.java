package pl.pg.adamil.natsuhaze;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {

    private NatsuhazeCore core;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        core.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case 101:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    core = new NatsuhazeCore(this);
                    core.init();
                    setContentView(core.getScreen());
                    new Thread(() ->core.mainLoop()).start(); //main emulator loop, everything happens here
                }
        }
    }
}