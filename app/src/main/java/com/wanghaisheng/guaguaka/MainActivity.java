package com.wanghaisheng.guaguaka;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.wanghaisheng.view.guaguaka.GuaGuaKa;

public class MainActivity extends AppCompatActivity {

    GuaGuaKa mGuaGuaKa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mGuaGuaKa = (GuaGuaKa) findViewById(R.id.guaguaka);

        mGuaGuaKa.setCompleteListener(new GuaGuaKa.OnGuaGuaKaCompleteListener() {
            @Override
            public void onComplete() {
                Toast.makeText(getApplicationContext(),"刮奖完成了",Toast.LENGTH_SHORT).show();
            }
        });

        mGuaGuaKa.setText("￥500,000,000");
    }
}
