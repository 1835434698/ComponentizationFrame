package com.tangzy.gradleplugin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.luojilab.component.componentlib.router.Router;
import com.luojilab.component.componentlib.router.ui.UIRouter;
import com.luojilab.router.facade.annotation.RouteNode;
import com.tangzy.gradleplugin.R;

@RouteNode(path = "/main", desc = "首页")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Router router = Router.getInstance();
                UIRouter.getInstance().openUri(MainActivity.this, "DDComp://share/login", null);
//                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });
    }
}