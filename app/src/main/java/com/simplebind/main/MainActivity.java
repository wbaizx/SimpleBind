package com.simplebind.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.simpleannotation.BindOnClick;
import com.simpleannotation.BindView;
import com.simplebind.R;
import com.simplebind.SimpleBind;
import com.simplebind.main.f.BlankFragment;
import com.simplebind.te.Main2Activity;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.test1)
    TextView test1;
    @BindView(R.id.test2)
    TextView aaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SimpleBind.bind(this);

        test1.setText("绑定成功");
        aaa.setText("绑定成功");

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.layout, new BlankFragment()).commit();
    }

    @BindOnClick({R.id.test1, R.id.test2})
    public void onc(View view) {
        if (view.getId() == R.id.test2) {
            startActivity(new Intent(this, Main2Activity.class));
        } else {
            finish();
        }
    }
}
