package com.luojilab.router.rotues;

import com.alibaba.android.arouter.facade.template.IRouteGroup;
import com.alibaba.android.arouter.facade.template.IRouteRoot;
import com.alibaba.android.arouter.routes.ARouter..Group..arouter;
import java.util.Map;

public class ARouter$$Root$$arouterapi implements IRouteRoot {
    public ARouter$$Root$$arouterapi() {
    }

    public void loadInto(Map<String, Class<? extends IRouteGroup>> routes) {
        routes.put("arouter", arouter.class);
    }
}
