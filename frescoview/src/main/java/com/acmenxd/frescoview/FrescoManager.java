package com.acmenxd.frescoview;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.util.ByteConstants;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.backends.okhttp3.OkHttpImagePipelineConfigFactory;
import com.facebook.imagepipeline.cache.MemoryCacheParams;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import okhttp3.OkHttpClient;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/1/17 15:39
 * @detail 图片加载配置工具
 * * 混淆 参考-> https://www.fresco-cn.org/docs/proguard.html
 * * 打包 参考-> https://www.fresco-cn.org/docs/multiple-apks.html
 */
public final class FrescoManager {
    // 分配的可用内存
    private static final int MAX_HEAP_SIZE = (int) Runtime.getRuntime().maxMemory();
    // 使用的缓存数量
    private static final int MAX_MEMORY_CACHE_SIZE = MAX_HEAP_SIZE / 4;
    // 默认图磁盘缓存的最大值 单位:MB
    public static long MAX_DISK_CACHE_SIZE = 50;
    // 默认图低磁盘空间缓存的最大值
    private static final long MAX_DISK_CACHE_LOW_SIZE = 20 * ByteConstants.MB;
    // 默认图极低磁盘空间缓存的最大值
    private static final long MAX_DISK_CACHE_VERYLOW_SIZE = 8 * ByteConstants.MB;
    // 小图低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图） 单位:MB
    public static long MAX_SMALL_DISK_LOW_CACHE_SIZE = 20;
    // 小图极低磁盘空间缓存的最大值（特性：可将大量的小图放到额外放在另一个磁盘空间防止大图占用磁盘空间而删除了大量的小图）
    private static final long MAX_SMALL_DISK_VERYLOW_CACHE_SIZE = 8 * ByteConstants.MB;

    /**
     * 使用配置
     */
    protected static String APP_PKG_NAME = "FrescoView";  // 包名 -> 用于存取资源图片的路径拼接
    private static Context sContext; // 上下文对象
    public static boolean LOG_OPEN = true; // Log开关
    public static int LOG_LEVEL = Log.VERBOSE; // Log等级
    //设置缓存图片的存放路径 - 路径:默认为SD卡根目录FrescoView下 (此路径非直接存储图片的路径,还需要以下目录设置)
    public static String IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FrescoView/";
    //大图片存放目录:默认为MainCache目录
    public static String MAIN_CACHE_DIR = "MainCache";
    //小图片存放目录:默认为SmallCache目录 (如不想区分大小图片,可设置为null或者"",表示大小图片都放在mainCacheDir目录下)
    public static String SMALL_CACHE_DIR = "SmallCache";

    /**
     * 初始化
     * * context必须设置
     * * 配置完成后必须调用此函数生效
     */
    public static void init(@NonNull Context pContext) {
        sContext = pContext;
        APP_PKG_NAME = sContext.getPackageName();
        init();
    }

    /**
     * 初始化 -> 配置完成后必须调用此函数生效
     */
    private static synchronized void init() {
        // 内存配置
        final MemoryCacheParams memoryCacheParams = new MemoryCacheParams(
                MAX_MEMORY_CACHE_SIZE,// 内存缓存中总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中图片的最大数量。
                MAX_MEMORY_CACHE_SIZE,// 内存缓存中准备清除但尚未被删除的总图片的最大大小,以字节为单位。
                Integer.MAX_VALUE,// 内存缓存中准备清除的总图片的最大数量。
                Integer.MAX_VALUE);// 内存缓存中单个图片的最大大小。
        // 修改内存图片缓存数量
        Supplier<MemoryCacheParams> bitmapMemoryCacheParamsSupplier = new Supplier<MemoryCacheParams>() {
            @Override
            public MemoryCacheParams get() {
                return memoryCacheParams;
            }
        };
        // 默认图片的磁盘配置
        DiskCacheConfig mainDiskCacheConfig = DiskCacheConfig.newBuilder(sContext)
                .setBaseDirectoryPath(new File(IMAGE_CACHE_PATH))
                .setBaseDirectoryName(MAIN_CACHE_DIR)
                .setMaxCacheSize(MAX_DISK_CACHE_SIZE * ByteConstants.MB)//默认缓存的最大大小。
                .setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)//缓存的最大大小,使用设备时低磁盘空间。
                .setMaxCacheSizeOnVeryLowDiskSpace(MAX_DISK_CACHE_VERYLOW_SIZE)//缓存的最大大小,当设备极低磁盘空间
                .build();
        // 小图片的磁盘配置
        DiskCacheConfig smallImageDiskCacheConfig = null;
        if (!TextUtils.isEmpty(SMALL_CACHE_DIR)) {
            smallImageDiskCacheConfig = DiskCacheConfig.newBuilder(sContext)
                    .setBaseDirectoryPath(new File(IMAGE_CACHE_PATH)) //存储路径
                    .setBaseDirectoryName(SMALL_CACHE_DIR) //文件名
                    .setMaxCacheSize(MAX_DISK_CACHE_SIZE * ByteConstants.MB) //默认缓存的最大大小
                    .setMaxCacheSizeOnLowDiskSpace(MAX_SMALL_DISK_LOW_CACHE_SIZE * ByteConstants.MB) //缓存的最大大小,使用设备时低磁盘空间
                    .setMaxCacheSizeOnVeryLowDiskSpace(MAX_SMALL_DISK_VERYLOW_CACHE_SIZE) //缓存的最大大小,当设备极低磁盘空间
                    .build();
        }
        // 设置Log & 显示等级
        Set<RequestListener> requestListeners = new HashSet<>();
        if (LOG_OPEN) {
            requestListeners.add(new RequestLoggingListener());
            FLog.setMinimumLoggingLevel(LOG_LEVEL);

        }
        // 配置 -> 使用OkHttp网络库加载图片
        ImagePipelineConfig.Builder builder = OkHttpImagePipelineConfigFactory
                .newBuilder(sContext, new OkHttpClient())
                .setBitmapMemoryCacheParamsSupplier(bitmapMemoryCacheParamsSupplier)
                .setMainDiskCacheConfig(mainDiskCacheConfig);
        if (smallImageDiskCacheConfig != null) {
            builder.setSmallImageDiskCacheConfig(smallImageDiskCacheConfig);
        }
        builder.setRequestListeners(requestListeners);
//                .setBitmapsConfig(Bitmap.Config.RGB_565)
//                .setDownsampleEnabled(true)
//                .setWebpSupportEnabled(true)
//                .setCacheKeyFactory(cacheKeyFactory)
//                .setEncodedMemoryCacheParamsSupplier(encodedCacheParamsSupplier)
//                .setExecutorSupplier(executorSupplier)
//                .setImageCacheStatsTracker(imageCacheStatsTracker)
//                .setMemoryTrimmableRegistry(memoryTrimmableRegistry)
//                .setNetworkFetchProducer(networkFetchProducer)
//                .setPoolFactory(poolFactory)
//                .setProgressiveJpegConfig(progressiveJpegConfig)
        ImagePipelineConfig config = builder.build();
        Fresco.initialize(sContext, config);
    }

    /**
     * 根据uri检查是否被缓存
     *
     * @param uri
     * @return
     */
    public static boolean checkMemoryCache(@NonNull String uri) {
        return Fresco.getImagePipeline().isInBitmapMemoryCache(Uri.parse(uri));
    }

    public static boolean checkMemoryCache(int resId) {
        return checkMemoryCache(FrescoUtils.appendStrs("res://", APP_PKG_NAME, "/", resId));
    }

    /**
     * 根据uri删除缓存
     */
    public static void deleteCache(@NonNull String uri) {
        Uri uri1 = Uri.parse(uri);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.evictFromMemoryCache(uri1);
        imagePipeline.evictFromDiskCache(uri1);
        imagePipeline.evictFromCache(uri1);
    }

    /**
     * 清空缓存
     */
    public static void clearCache() {
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        imagePipeline.clearMemoryCaches();
        imagePipeline.clearDiskCaches();
        imagePipeline.clearCaches();
    }
}
