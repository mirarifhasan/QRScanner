package com.example.asus.qrcodescanner;

import android.Manifest;
import android.content.ClipData;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import github.nisrulz.qreader.QRDataListener;
import github.nisrulz.qreader.QREader;

import android.content.ClipboardManager;

public class ScannerActivity extends AppCompatActivity {

    private TextView txt_result;
    private SurfaceView surfaceView;
    private QREader qrEader;
    private Button copyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        //Request Permission
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        setupCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ScannerActivity.this, "You need permission", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        //Copy button function
        copyBtn = (Button)findViewById(R.id.copy);

        copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", txt_result.getText());
                clipboard.setPrimaryClip(clip);

                Toast.makeText(ScannerActivity.this, "Copied to clipboard", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void setupCamera() {

        txt_result = (TextView)findViewById(R.id.qrText);
        final ToggleButton btn_on_off = (ToggleButton)findViewById(R.id.btn_enable_disable);

        btn_on_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(qrEader.isCameraRunning()){
                    btn_on_off.setChecked(false);
                    qrEader.stop();
                }else{
                    btn_on_off.setChecked(true);
                    qrEader.start();
                }
            }
        });

        surfaceView = (SurfaceView)findViewById(R.id.camera_view);
        setupQREader();

    }

    private void setupQREader() {
        qrEader = new QREader.Builder(this, surfaceView, new QRDataListener() {
            @Override
            public void onDetected(final String data) {
                txt_result.post(new Runnable() {
                    @Override
                    public void run() {
                        txt_result.setText(data);
                    }
                });
            }
        }).facing(QREader.BACK_CAM)
                .enableAutofocus(true)
                .height(surfaceView.getHeight())
                .width(surfaceView.getWidth())
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(qrEader != null)
                            qrEader.initAndStart(surfaceView);
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ScannerActivity.this, "You need permission", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        if(qrEader != null)
                            qrEader.releaseAndCleanup();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(ScannerActivity.this, "You need permission", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }
}
