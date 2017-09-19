# FrescoView
基于facebook出品的fresco( 强大的图片加载组件 https://www.fresco-cn.org )进行二次开发, 更易于开发者使用的库.

如要了解功能实现,请运行app程序查看控制台日志和源代码!
* 源代码 : <a href="https://github.com/AcmenXD/FrescoView">AcmenXD/FrescoView</a>
* apk下载路径 : <a href="https://github.com/AcmenXD/Resource/blob/master/apks/FrescoView.apk">FrescoView.apk</a>

![jpg](https://github.com/AcmenXD/FrescoView/blob/master/pic/1.jpg)

### 依赖
---
- AndroidStudio
```
	allprojects {
            repositories {
                ...
                maven { url 'https://jitpack.io' }
            }
	}
```
```
	 compile 'com.github.AcmenXD:FrescoView:1.5'
```
### 混淆
---
```
     #FrescoView默认使用okhttp3,所以要加入此配置
     -dontwarn okhttp3.**
     -dontwarn okio.**
     -dontwarn com.squareup.okhttp3.**

     #fresco的混淆配置
     -keep,allowobfuscation @interface com.facebook.common.internal.DoNotStrip
     -keep @com.facebook.common.internal.DoNotStrip class *
     -keepclassmembers class * {
         @com.facebook.common.internal.DoNotStrip *;
     }
     -keepclassmembers class * {
         native <methods>;
     }
     -dontwarn okio.**
     -dontwarn com.squareup.okhttp.**
     -dontwarn okhttp3.**
     -dontwarn javax.annotation.**
     -dontwarn com.android.volley.toolbox.**
```
### 功能
---
- 支持fresco所支持的大多数功能
- 支持JPEG / PNG / GIF / WebP 格式
- 支持加载网络 / res资源 / 本地资源
- 支持低分辨率&高分辨率加载 / 缩略图式加载 > 渐进式加载
- 支持监听加载进度,加载成功和失败回调
- 支持多层构图, 占位图 -> 背景图 -> 覆盖图 -> 加载失败图 -> 失败重试图 -> 进度条图
- 支持加载失败后,点击再次加载
- 支持加载过程中显示加载动画
- 支持圆角 / 圆形 / 边框 处理,并可设置颜色
- 支持图片渐显动画
- 支持控制gif动画的播放和停止
- 支持函数链式调用,方便使用
- 更多功能请查看项目根目录下<a href="https://github.com/AcmenXD/FrescoView/blob/master/frescoview/src/main/java/com/acmenxd/frescoview/FrescoViewDoc.java" target="_blank">frescoview/src/main/java/com/acmenxd/frescoview/FrescoViewDoc.java</a>文件,有xml或代码中每个函数的具体含义
### 配置
---
**在Application中配置**
```java
/**
 * 设置Log开关 & 等级
 * * 默认为 开 & Log.VERBOSE
 */
FrescoManager.LOG_OPEN  = true;
FrescoManager.LOG_LEVEL = Log.VERBOSE;
/**
 * 设置缓存图片的存放路径
 * Environment.getExternalStorageDirectory().getAbsolutePath() + "/FrescoView/"
 * * 路径:默认为SD卡根目录FrescoView下 (此路径非直接存储图片的路径,还需要以下目录设置)
 * * 大图片存放目录:默认为MainCache目录
 * * 小图片存放目录:默认为SmallCache目录 (如不想区分大小图片,可设置为null或者"",表示大小图片都放在mainCacheDir目录下)
 */
FrescoManager.IMAGE_CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FrescoView/";
FrescoManager.MAIN_CACHE_DIR = "MainCache";
FrescoManager.SMALL_CACHE_DIR = "SmallCache";
/**
 * 设置缓存磁盘大小
 * * mainCacheSize  大图片磁盘大小(MB) 默认为50MB
 * * smallCacheSize 小图片磁盘大小(MB) 默认为20MB
 */
FrescoManager.MAX_DISK_CACHE_SIZE = 50;
FrescoManager.MAX_SMALL_DISK_LOW_CACHE_SIZE = 20;
/**
 * 初始化
 * * context必须设置
 * * 配置完成后必须调用此函数生效
 */
FrescoManager.setContext(this);
```
### 使用 -> 以下代码 注释很详细、很重要很重要很重要!!!
---
```xml
<!-- xml布局中定义FrescoView, 请查看FrescoViewDoc.java,有详细的参数解释 -->
<com.acmenxd.frescoview.FrescoView
    android:id="@+id/imageView1"
    android:layout_width="match_parent"
    android:layout_height="200dp"
    fresco:actualImageScaleType="focusCrop"
    fresco:backgroundImage="@color/colorPrimaryDark"
    fresco:fadeDuration="1000"
    fresco:failureImage="@mipmap/ic_launcher"
    fresco:failureImageScaleType="centerInside"
    fresco:overlayImage="@mipmap/ic_launcher"
    fresco:placeholderImage="@color/colorPrimary"
    fresco:placeholderImageScaleType="fitCenter"
    fresco:pressedStateOverlayImage="@color/colorAccent"
    fresco:progressBarAutoRotateInterval="1000"
    fresco:progressBarImage="@mipmap/ic_launcher"
    fresco:progressBarImageScaleType="centerInside"
    fresco:retryImage="@color/colorAccent"
    fresco:retryImageScaleType="centerCrop"
    fresco:roundAsCircle="false"
    fresco:roundBottomLeft="false"
    fresco:roundBottomRight="true"
    fresco:roundTopLeft="true"
    fresco:roundTopRight="false"
    fresco:roundWithOverlayColor="@color/colorAccent"
    fresco:roundedCornerRadius="30dp"
    fresco:roundingBorderColor="@color/colorAccent"
    fresco:roundingBorderWidth="10dp"
    />
```
---
```java
// ** 请查看FrescoViewDoc.java,有详细的参数解释
FrescoView iv = (FrescoView) findViewById(R.id.imageView1);
/**
 * 加载回调
 */
FrescoCallback callback = new FrescoCallback() {
    @Override
    public void succeed(String id, ImageInfo imageInfo, Animatable animatable) {
        super.succeed(id, imageInfo, animatable);
        Log.e("AcmenXD", "onSuccess");
        if (imageInfo != null) {
            QualityInfo qualityInfo = imageInfo.getQualityInfo();
            Log.e("AcmenXD", appendStrs("Size: ", imageInfo.getWidth(), " x ", imageInfo.getHeight()));
            Log.e("AcmenXD", appendStrs("Quality level: ", qualityInfo.getQuality()));
            Log.e("AcmenXD", appendStrs("good enough: ", qualityInfo.isOfGoodEnoughQuality()));
            Log.e("AcmenXD", appendStrs("full quality: ", qualityInfo.isOfFullQuality()));
        }
        if (animatable != null) {
            animatable.start();
        }
    }
    @Override
    public void failed(String id, Throwable throwable) {
        super.failed(id, throwable);
        Log.e("AcmenXD", "onFailure");
    }
};
iv.image()
        .setImageURI(R.color.colorAccent)
        .setImageURI(R.mipmap.ic_launcher)
        .setImageURI("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1854928198,1677845423&fm=23&gp=0.jpg") //gif|webp格式
        .setBackgroundImage(getResources().getDrawable(R.color.colorPrimary))
        .setPlaceholderImage(R.color.colorAccent)
        .setProgressBarImage(R.mipmap.ic_launcher)
        .setFailureImage(R.color.colorAccent)
        .setRetryImage(R.mipmap.ic_launcher)
        .setProgressBarImage(R.color.colorAccent)
        .setLocalThumbnailPreviewsEnabled(true)
        .setProgressiveRenderingEnabled(true)
        .setAutoPlayAnimations(true)
        .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
        .setRoundingParams(30, Color.BLACK, 0, 0, false)
        .setFadeDuration(0)
        .setControllerListener(callback)
//                .setFirstAvailableImageURIs(
//                        "http://image52.360doc.com/DownloadImg/2012/06/0316/24581213_7.jpg",
//                        "http://image52.360doc.com/DownloadImg/2012/06/0316/24581213_6.jpg")
//                .setAspectRatio(10, 3)
//                .setPressedStateOverlay(getResources().getDrawable(R.mipmap.ic_launcher))
        .commit();
```
---
### 打个小广告^_^
**gitHub** : https://github.com/AcmenXD   如对您有帮助,欢迎点Star支持,谢谢~

**技术博客** : http://blog.csdn.net/wxd_beijing
# END