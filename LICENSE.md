# gradle插件

## 一、背景

gradle插件的集成方式有多种，

1、maven远程仓库（需要首先搭建maven仓库，并且本地调试较慢）

2、maven本地仓库（需要首先搭建maven仓库，但是团队开发不方便）

3、本地文件。（直接使用文件，开发调试方便。建议开发调试阶段使用）

## 二、本次讲解使用本地文件方式

1、首先创建一个app工程，然后创建一个plugin的module工程。

2、删除plugin中的所有文件，只留下build.gradle文件。

3、新建com.tangzy.plugin.MyPlugin.groovy 这个就是插件入口。

4、新建com.tangzy.plugin.properties文件在ComponentizationFrame/myplugin/src/main/resources/META-INF/gradle-plugins目录下

5、build.gradle文件使用

```java
apply plugin: 'groovy'
apply plugin: 'maven'

dependencies {
    implementation gradleApi() //gradle sdk
    implementation localGroovy() //groovy sdk
}
repositories {
    jcenter()
}

uploadArchives {
    repositories {
        mavenDeployer {
            repository(url: uri('../repo')) //仓库的路径，此处是项目根目录下的 repo 的文件夹
            pom.groupId = 'com.tangzy.plugin'  //groupId ，自行定义，一般是包名
            pom.artifactId = 'gradleplugin' //artifactId ，自行定义
            pom.version = '1.0.1' //version 版本号
        }
    }
}
```

6、使用uploadArchives命令打包。

7、在根目录的build.gradle使用

```java
buildscript {
    repositories {
       ...
        maven {
            url uri('repo')
        }

    }
    dependencies {
        ...
        classpath 'com.tangzy.plugin:gradleplugin:1.0.1'//uploadArchives里面的名字
    }
}
```

8、app工程目录的build.gradle使用

```java
apply plugin: 'com.tangzy.plugin'//resources/MEZTA-INF/gradle-plugins/里面的文件名字前缀
```

9、运行app即可。



ARouter原理
## 一、编译时插入代码。

```java
//每个module一个类。里面存放的是这个module中所有的group名。
public class ARouter$$Root$$TencentTRTC implements IRouteRoot {
  public void loadInto(Map<String, Class<? extends IRouteGroup>> paramMap) {
    paramMap.put("jumpApp", ARouter$$Group$$jumpApp.class);
  }
}
//每个group一个类。里面存放的是这个group中所有的注解的路径与对应的class关系。
public class ARouter$$Group$$jumpApp implements IRouteGroup {
  public void loadInto(Map<String, RouteMeta> paramMap) {
    paramMap.put("/jumpApp/democlient://mine/demo", RouteMeta.build(RouteType.ACTIVITY, DemoActivity.class, "/jumpapp/democlient://mine/demo", "jumpapp", null, -1, -2147483648));
    paramMap.put("/jumpApp/democlient://mine/trtc", RouteMeta.build(RouteType.ACTIVITY, RoomActivity.class, "/jumpapp/democlient://mine/trtc", "jumpapp", null, -1, -2147483648));
  }
}
```

## 二、初始化

```java
//初始化代码
ARouter.init(this);
// 内部实际调用
 _ARouter.init(application);
//内部调用了
 LogisticsCenter.init(mContext, executor);
//内部做初始化操作。初始化groupsIndex
//初始化一个handler，跳转时切换到主线程
 mHandler = new Handler(Looper.getMainLooper());


//初始化后调用了一个afterInit
_ARouter.afterInit();

static void afterInit() {
        // Trigger interceptor init, use byName. 添加了一个interceptorService
        interceptorService = (InterceptorService) ARouter.getInstance().build("/arouter/service/interceptor").navigation();
    }
```

三、调用跳转



```java
 ARouter.getInstance().build(ArouterConst.APP_LOGIN_ACTIVITY)
                .navigation(context);
//build内部调用了
_ARouter.getInstance().build(path);
protected Postcard build(String path) {
        if (TextUtils.isEmpty(path)) {
            throw new HandlerException(Consts.TAG + "Parameter is invalid!");
        } else {
            PathReplaceService pService = ARouter.getInstance().navigation(PathReplaceService.class);
            if (null != pService) {
                path = pService.forString(path);
            }
            return build(path, extractGroup(path));//获取到group调用了build
        }
    }
protected Postcard build(String path, String group) {
        if (TextUtils.isEmpty(path) || TextUtils.isEmpty(group)) {
            throw new HandlerException(Consts.TAG + "Parameter is invalid!");
        } else {
            PathReplaceService pService = ARouter.getInstance().navigation(PathReplaceService.class);
            if (null != pService) {
                path = pService.forString(path);
            }
            return new Postcard(path, group);//调用了Postcard的构造方法构造了对象
        }
    }
//设置了路径、group、uri、bundle等。就是一个RouteMeta对象，即路由map中存放的对象。
    public Postcard(String path, String group, Uri uri, Bundle bundle) {
        setPath(path);
        setGroup(group);
        setUri(uri);
        this.mBundle = (null == bundle ? new Bundle() : bundle);
    }
//
//然后调用了
.navigation(context);
//内部调用了Postcard的navigation
navigation(context, null);
//内部调用了ARouter的navigation
ARouter.getInstance().navigation(context, this, -1, callback);
//内部调用了_ARouter的navigation
_ARouter.getInstance().navigation(mContext, postcard, requestCode, callback);
//内部调用了navigation的下面方法（递归的完成路由添加）
LogisticsCenter.completion(postcard);
//然后调用完成跳转
 _navigation(context, postcard, requestCode, callback);

```





ARouter :

代理模式 代理 _ARouter



_ARouter:

真正的处理层，处理初始化、跳转等事件。



LogisticsCenter：

读取插入的代码到map中，维护map



Warehouse：

存放map的地方。groupsIndex 的map与class的map

Postcard：

每一个路径的载体，存放了路径以及对于的class信息。
