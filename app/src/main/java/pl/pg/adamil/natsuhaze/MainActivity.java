package pl.pg.adamil.natsuhaze;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.app.ActivityCompat;

public class MainActivity extends Activity {

    private NatsuhazeCore core;
    private Screen screen;
    private Uri fileUri;
    private String[] permissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        ActivityCompat.requestPermissions(this, permissions, 101);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch(requestCode) {
            case 101:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initialFile();
                }
                break;
        }
    }

    public void initialFile() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Wybierz ROM");
        startActivityForResult(chooseFileIntent, 667);
    }

    public void chooseFile() {
        Intent chooseFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFileIntent.setType("*/*");
        chooseFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        chooseFileIntent = Intent.createChooser(chooseFileIntent, "Wybierz ROM");
        startActivityForResult(chooseFileIntent, 666);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 666:
                if(resultCode == Activity.RESULT_OK) {
                    if(data != null) {
                        fileUri = data.getData();
                    }
                }
                break;
            case 667:
                if(resultCode == Activity.RESULT_OK) {
                    if(data != null) {
                        fileUri = data.getData();
                        core = new NatsuhazeCore(this);
                        core.startScreen();
                        screen = core.getScreen();
                        setContentView(screen);
                        screen.setOnTouchListener((view, event) -> {
                            if(event.getAction() == MotionEvent.ACTION_UP)
                                chooseFile();
                            return true;
                        });
                        core.loadCart(fileUri);
                        new Thread(() ->core.mainLoop()).start();
                    }
                }
                break;
        }
    }
}