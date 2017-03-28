# FrescoView
基于facebook出品的fresco( 强大的图片加载组件 https://www.fresco-cn.org )进行二次开发的 更易于开发者使用的库.
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
	 compile 'com.github.AcmenXD:FrescoView:1.0'
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
 * 设置包名 -> 用于存取资源图片的路径拼接
 * * 默认为FrescoView
 */
FrescoManager.setPkgName(getPackageName());
/**
 * 初始化
 * context必须设置
 */
FrescoManager.setContext(this);
/**
 * 设置Log开关 & 等级
 * * 默认为 开 & Log.VERBOSE
 */
FrescoManager.setOpen(true, Log.VERBOSE);
/**
 * 设置缓存图片的存放路径
 *
 * @param cachePath     路径:默认为SD卡根目录Image下 (此路径非直接存储图片的路径,还需要以下目录设置)
 * @param mainCacheDir  大图片存放目录:默认为MainCache目录
 * @param smallCacheDir 小图片存放目录:默认为SmallCache目录 (如不想区分大小图片,可设置为null或者"",表示大小图片都放在mainCacheDir目录下)
 */
FrescoManager.setCachePath(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Image/", "MainCache", "SmallCache");
/**
 * 设置缓存磁盘大小
 *
 * @param mainCacheSize  大图片磁盘大小(MB) 默认为50MB
 * @param smallCacheSize 小图片磁盘大小(MB) 默认为20MB
 */
FrescoManager.setCacheSize(50, 20);
/**
 * 初始化 -> 配置完成后必须调用此函数生效
 */
FrescoManager.init();
```
### 使用 -> 以下代码 注释很详细、很重要很重要很重要!!!
---
```java
/**
 * v可替换成d/i/w/e/a,对应不同等级的日志
 * test可替换成java任意类型
 */
Logger.v("test");
```
```java
// MainActivity.java:28 可点击跳转到对应代码行
V/com.acmenxd.logger.demo.MainActivity.java:╔═════════════════════════════════════════════════════════════════════════════════════════
V/com.acmenxd.logger.demo.MainActivity.java:║ * [ Logger -=(MainActivity.java:28)=- OnCreate ]
V/com.acmenxd.logger.demo.MainActivity.java:║ 	test
V/com.acmenxd.logger.demo.MainActivity.java:╚═════════════════════════════════════════════════════════════════════════════════════════
```
---
### 打个小广告^_^
**gitHub** : https://github.com/AcmenXD   如对您有帮助,欢迎点Star支持,谢谢~

**技术博客** : http://blog.csdn.net/wxd_beijing
# END