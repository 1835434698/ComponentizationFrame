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

4、新建com.tangzy.plugin.properties文件在GradlePlugin/myplugin/src/main/resources/META-INF/gradle-plugins目录下

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