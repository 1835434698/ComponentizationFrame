package com.luojilab.router.rotues;
import com.alibaba.android.arouter.core.AutowiredServiceImpl;
import com.alibaba.android.arouter.core.InterceptorServiceImpl;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IProviderGroup;
import java.util.Map;

public class ARouter$$Providers$$arouterapi implements IProviderGroup {
    public ARouter$$Providers$$arouterapi() {
    }

    public void loadInto(Map<String, RouteMeta> providers) {
        providers.put("com.alibaba.android.arouter.facade.service.AutowiredService", RouteMeta.build(RouteType.PROVIDER, AutowiredServiceImpl.class, "/arouter/service/autowired", "arouter", (Map)null, -1, -2147483648));
        providers.put("com.alibaba.android.arouter.facade.service.InterceptorService", RouteMeta.build(RouteType.PROVIDER, InterceptorServiceImpl.class, "/arouter/service/interceptor", "arouter", (Map)null, -1, -2147483648));
    }
}