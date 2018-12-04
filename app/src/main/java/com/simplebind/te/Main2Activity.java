package com.simplebind.te;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.simpleannotation.BindOnClick;
import com.simpleannotation.BindView;
import com.simplebind.R;
import com.simplebind.SimpleBind;

public class Main2Activity extends AppCompatActivity {
    @BindView(R.id.test3)
    TextView aaa;
    @BindView(R.id.test4)
    TextView test4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SimpleBind.bind(this);

        aaa.setText("绑定成功");
        test4.setText("绑定成功");
    }

    @BindOnClick({R.id.test3, R.id.test4})
    public void onc(View view) {
        finish();
    }
}
