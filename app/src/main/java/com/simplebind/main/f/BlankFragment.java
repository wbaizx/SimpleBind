package com.simplebind.main.f;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.simpleannotation.BindOnClick;
import com.simpleannotation.BindView;
import com.simplebind.R;
import com.simplebind.SimpleBind;

/**
 * A simple {@link Fragment} subclass.
 */
public class BlankFragment extends Fragment {
    @BindView(R.id.test4)
    TextView test4;
    @BindView(R.id.test5)
    TextView aaa;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        SimpleBind.bind(this, view);
        test4.setText("绑定成功");
        aaa.setText("绑定成功");
        return view;
    }

    @BindOnClick({R.id.test4, R.id.test5})
    public void onc(View view) {
        if (view.getId() == R.id.test4) {
            Toast.makeText(getContext(), "点击1", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "点击2", Toast.LENGTH_SHORT).show();
        }
    }
}
