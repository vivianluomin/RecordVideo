package com.example.asus1.videorecoder.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.widget.Toast;

import com.example.asus1.videorecoder.Camera.RecordActivity;
import com.example.asus1.videorecoder.R;
import com.example.asus1.videorecoder.UI.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        setPermission();
    }

    private void setPermission(){
        String[] permissions = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };
        List<String> pers = new ArrayList<>();

        for(String per :permissions){
            if(ContextCompat.checkSelfPermission(this,per)
                    != PackageManager.PERMISSION_GRANTED){
                pers.add(per);
            }
        }

        if(pers.size()>0){
            ActivityCompat.requestPermissions(this,pers.toArray(new String[pers.size()]),
                    100);
        }else {
            startActivity(new Intent(this, RecordActivity.class));
            finish();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        for(int i = 0;i<grantResults.length;i++){
            if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,getResources().
                        getText(R.string.requestPermission),Toast.LENGTH_SHORT).show();
                finish();
            }
        }

        startActivity(new Intent(this, RecordActivity.class));
        finish();
    }
}
