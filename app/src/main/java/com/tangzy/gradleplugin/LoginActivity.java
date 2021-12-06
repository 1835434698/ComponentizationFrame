package com.tangzy.gradleplugin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.luojilab.router.facade.annotation.RouteNode;

@RouteNode(path = "/login", desc = "登录")
public class LoginActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

    }
}
