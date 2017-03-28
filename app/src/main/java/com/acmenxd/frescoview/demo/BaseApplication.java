package com.acmenxd.frescoview.demo;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;

import com.acmenxd.frescoview.FrescoManager;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2016/11/22 14:36
 * @detail 顶级Application
 */
public final class BaseApplication extends Application {
    protected final String TAG = this.getClass().getSimpleName();

    private static BaseApplication sInstance = null;
    // 初始化状态 -> 默认false,初始化完成为true
    public boolean isInitFinish = false;

    public BaseApplication() {
        super();
        sInstance = this;
    }

    public static synchronized BaseApplication instance() {
        if (sInstance == null) {
            new RuntimeException("BaseApplication == null ?? you should extends BaseApplication in you app");
        }
        return sInstance;
    }

    @Override
    public void onCreate() {
        /**
         * 配置FrescoView
         */
        FrescoManager.setPkgName(getPackageName());
        FrescoManager.setContext(this);
        FrescoManager.setOpen(true, Log.VERBOSE);
        FrescoManager.setCachePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Image/", "MainCache", "SmallCache");
        FrescoManager.setCacheSize(50, 20);
        FrescoManager.init();
        // 初始化完毕
        isInitFinish = true;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        // 低内存的时候执行
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        // 程序在内存清理的时候执行
        super.onTrimMemory(level);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // 应用配置变更~
        super.onConfigurationChanged(newConfig);
    }
}