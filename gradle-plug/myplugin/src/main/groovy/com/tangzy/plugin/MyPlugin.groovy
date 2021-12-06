package com.tangzy.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

public class MyPlugin implements Plugin<Project> {

    void apply(Project project) {
        def log = project.logger
        log.error "========================";
        log.error "完整的MyPlugin，开始修改Class!";
        log.error "========================";
        System.out.println("========================")
        System.out.println( "完整的MyPlugin，开始修改Class!")

        String bootClass = project.android.bootClasspath

        System.out.println("bootClass = "+bootClass)
        System.out.println("========================")
    }
}