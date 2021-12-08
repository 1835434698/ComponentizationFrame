package com.luojilab.router.rotues;

import com.alibaba.android.arouter.core.AutowiredServiceImpl;
import com.alibaba.android.arouter.core.InterceptorServiceImpl;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IRouteGroup;
import java.util.Map;

public class ARouter$$Group$$arouter implements IRouteGroup {
    public ARouter$$Group$$arouter() {
    }

    public void loadInto(Map<String, RouteMeta> atlas) {
        atlas.put("/arouter/service/autowired", RouteMeta.build(RouteType.PROVIDER, AutowiredServiceImpl.class, "/arouter/service/autowired", "arouter", (Map)null, -1, -2147483648));
        atlas.put("/arouter/service/interceptor", RouteMeta.build(RouteType.PROVIDER, InterceptorServiceImpl.class, "/arouter/service/interceptor", "arouter", (Map)null, -1, -2147483648));
    }
}
