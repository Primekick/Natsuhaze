package pl.pg.adamil.natsuhaze;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        NatsuhazeCore core = new NatsuhazeCore(this);
        core.init();
        setContentView(core.getScreen());
        core.mainLoop(); //main emulator loop, everything happens here
        core.destroy();
    }
}