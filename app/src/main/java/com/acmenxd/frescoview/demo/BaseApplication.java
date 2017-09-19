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
    // 记录启动时间
    public long startTime = 0;

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
        super.onCreate();
        startTime = System.currentTimeMillis();
        /**
         * 配置FrescoView
         */
        FrescoManager.LOG_OPEN  = true;
        FrescoManager.LOG_LEVEL = Log.VERBOSE;
        FrescoManager.IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FrescoView/";
        FrescoManager.MAIN_CACHE_DIR = "MainCache";
        FrescoManager.SMALL_CACHE_DIR = "SmallCache";
        FrescoManager.MAX_DISK_CACHE_SIZE = 50;
        FrescoManager.MAX_SMALL_DISK_LOW_CACHE_SIZE = 20;
        FrescoManager.setContext(this);
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
