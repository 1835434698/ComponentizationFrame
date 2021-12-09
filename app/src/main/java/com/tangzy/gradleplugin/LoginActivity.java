package com.tangzy.gradleplugin;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.tangzy.bindannotation.BindView;
import com.tangzy.bindlibrary.BindViewTools;
import com.tangzy.servicelib.ArouterConst;

//import com.luojilab.router.facade.annotation.RouteNode;

//@RouteNode(path = "/login", desc = "登录")
@Route(path = ArouterConst.APP_LOGIN_ACTIVITY)
public class LoginActivity  extends AppCompatActivity {

    @BindView(R.id.login)
    TextView textView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        BindViewTools.bind(this);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArouterConst.goDemoActivity(LoginActivity.this);
            }
        });

    }
}
