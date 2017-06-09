package com.acmenxd.frescoview;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/1/20 11:34
 * @detail ImageView2 说明文档
 * 支持图片格式:PNG / GIF / WebP / JPEG
 * * 注意 : 此控件虽继承自ImageView,但是不要适用所有ImageView的属性和方法
 * * 注意 : 此空间不允许指定:android:layout_height="wrap_content",除非你指定了特定的宽高比:Image.setAspectRatio(4:3)
 * * 封装注意: 与原生相比,此封装使用GenericDraweeHierarchy的功能,无法支持GenericDraweeHierarchyBuilder的功能
 * * 优先级 : 加载最先可用图片 > 低分辨率&高分辨率 > 缩略图式加载 > 渐进式加载
 * * 优先级 : setFirstAvailableImageURIs > setLowImageURI > setHighImageURI > setImageURI
 * * 后处理器 : setPostprocessor() 图片加载完成后对图片进行特殊处理 参考-> https://www.fresco-cn.org/docs/modifying-image.html
 * * 自定义View : 单图 | 多图 参考-> https://www.fresco-cn.org/docs/writing-custom-views.html
 * * 缩放模式 :
 * -----1.Scaling 画布操作,通常是由硬件加速的.图片实际大小保持不变，只是在绘制时被放大或缩小.(Android 4.0 及以后,在配置 GPU 的设备上会启用硬件加速)
 * -------适用: 图片的像素数 < 视图量级(长 x 宽) x 2
 * -------用法: layout_width 和 layout_height 并制定缩放类型ScalingUtils.ScaleType
 * -----2.Resizing 不改变原始图片，只是在解码前修改内存中的图片大小.(目前只支持JPEG格式,由软件执行的,相比硬件加速的scale操作较慢)
 * -------适用: 图片的像素数 > 视图量级(长 x 宽) x 2
 * -------用法:setResizeOptions(width,height)
 * -----3.Downsampling (向下采样)如果开启该选项,会向下采样你的图片,代替Resizing操作,但仍然需要在每个图片请求中调用setResizeOptions(width,height)
 * -------在大部分情况下比Resizing更快.除了支持JPEG图片,它还支持PNG和WebP(除动画外)图片
 * -------目前存在一个缺陷: 在Android 4.4上会在解码时造成更多的内存开销(相比于Resizing).这在同时解码许多大图时会非常显著
 * -------适用: 图片的像素数 > 视图量级(长 x 宽) x 2 的 非JPEG格式图片
 * -------用法: 在ImageUtils的config中配置setDownsampleEnabled(true) 并 在每个图片请求中调用setResizeOptions(width,height)
 */
public class FrescoViewDoc {
/*
    -----------------------------xml布局属性详解-------------------------------------
    fresco:fadeDuration="300"   //渐显的时长
    fresco:actualImageScaleType="focusCrop"   //设置图片缩放. 通常使用focusCrop,该属性值会通过算法把人头像放在中间
    fresco:placeholderImage="@color/wait_color"   //占位图,请求图片加载完成之前显示
    fresco:placeholderImageScaleType="fitCenter"  //占位图缩放类型
    fresco:failureImage="@drawable/error"  //加载失败的时候显示的图片
    fresco:failureImageScaleType="centerInside" //缩放类型
    fresco:retryImage="@drawable/retrying" //加载失败,提示用户点击重新加载的图片,会重试4次,都失败后会显示failureImage
    fresco:retryImageScaleType="centerCrop" //缩放类型
    fresco:progressBarImage="@drawable/progress_bar" //提示用户正在加载,和加载进度无关(下载过程中显示的图片)
    fresco:progressBarImageScaleType="centerInside"//缩放类型
    fresco:progressBarAutoRotateInterval="1000"
    fresco:backgroundImage="@color/blue"   //背景图片
    fresco:overlayImage="@drawable/watermark"    //覆盖图像
    fresco:pressedStateOverlayImage="@color/red"  //按压状态下的叠加图
    fresco:roundAsCircle="false"   //是否设置为圆形
    fresco:roundedCornerRadius="1dp" //圆角角度,180的时候会变成圆形图片
    fresco:roundTopLeft="true" //左上角是否改变角度
    fresco:roundTopRight="false" //右上角是否改变角度
    fresco:roundBottomLeft="false" //左下角是否改变角度
    fresco:roundBottomRight="true" //右下角是否改变角度
    fresco:roundWithOverlayColor="@color/corner_color" //覆盖色,圆角|圆形 之外的填充色
    fresco:roundingBorderWidth="2dp"  //边框宽度
    fresco:roundingBorderColor="@color/border_color" //边框颜色
*/
/*
    ----------------------------ScalingUtils.ScaleType : 缩放类型详解 -> https://www.fresco-cn.org/docs/scaling.html-------------------------------------
    center 	居中，无缩放。
    centerCrop 	保持宽高比缩小或放大，使得两边都大于或等于显示边界，且宽或高契合显示边界。居中显示。
    focusCrop 	同centerCrop, 但居中点不是中点，而是指定的某个点。
    centerInside 	缩放图片使两边都在显示边界内，居中显示。和 fitCenter 不同，不会对图片进行放大。
    如果图尺寸大于显示边界，则保持长宽比缩小图片。
    fitCenter 	保持宽高比，缩小或者放大，使得图片完全显示在显示边界内，且宽或高契合显示边界。居中显示。
    fitStart 	同上。但不居中，和显示边界左上对齐。
    fitEnd 	同fitCenter， 但不居中，和显示边界右下对齐。
    fitXY 	不保存宽高比，填充满显示边界。
    none 	如要使用tile mode(平铺)显示, 需要设置为none
*/
/*
        ----------------------------ImageView2.image 方法解释 -------------------------------------
    void reset(); //重置所有配置
    Image setImageURI(String pImageUri); //设置图片请求地址 *远程图片 http://  https:// *本地文件 file:// *res目录下的资源 res:// *asset目录下的资源 asset:// *Content provider content://
    Image setImageURI(int resId);
    Image setLowImageURI(String pLowImageUri); //先显示低分辨率的图,然后是高分辨率的图(优先级 > setHighImageURI > setImageURI)
    Image setHighImageURI(String pHighImageUri); //先显示低分辨率的图,然后是高分辨率的图(优先级 > setImageURI)
    Image setFirstAvailableImageURIs(String... pFirstAvailableImageUris) //加载最先可用的图像(同一个图片有多个Uri的情况下,设置多个Uri路径) * 显示规则:检查集合中任意uri是否存在,如存在则显示,如不存在,则寻找磁盘缓存,如不存在,则外部请求
    Image setPlaceholderImage(int resId) //修改占位图
    Image setPlaceholderImage(int resId, ScalingUtils.ScaleType pScaleType)
    Image setPlaceholderImage(Drawable pDrawable)
    setPlaceholderImage(Drawable pDrawable, ScalingUtils.ScaleType pScaleType)
    Image setFailureImage(int resId) //修改请求失败的图像
    Image setFailureImage(int resId, ScalingUtils.ScaleType pScaleType)
    Image setFailureImage(Drawable pDrawable)
    Image setFailureImage(Drawable pDrawable, ScalingUtils.ScaleType pScaleType)
    Image setRetryImage(int resId) //加载失败重试显示的图片,会触发点击重试四次,如果还是加载失败,则显示请求失败的图像
    Image setRetryImage(int resId, ScalingUtils.ScaleType pScaleType)
    Image setRetryImage(Drawable pDrawable)
    Image setRetryImage(Drawable pDrawable, ScalingUtils.ScaleType pScaleType)
    Image setProgressBarImage(int resId) //显示进度条图片 -> view底部会有一个深蓝色的矩形进度条 * 支持AnimationDrawable动画,xml自定义动画 * 如需精确显示进度 参考-> https://www.fresco-cn.org/docs/progress-bars.html
    Image setProgressBarImage(int resId, ScalingUtils.ScaleType pScaleType)
    Image setProgressBarImage(Drawable pDrawable)
    Image setProgressBarImage(Drawable pDrawable, ScalingUtils.ScaleType pScaleType)
    Image setBackgroundImage(Drawable pDrawable) //设置背景图像
    Image setOverlayImage(Drawable pDrawable) //设置一个新的覆盖图像 -> 在指定的索引位置。
    Image setOverlayImage(Drawable pDrawable) //设置一个新的覆盖图像 -> 在指定的索引位置。
    Image setOverlayImage(int index, Drawable pDrawable)
    Image setControllerListener(ControllerListener<? super ImageInfo> pListener) //设置控制器监听
    Image setAspectRatio(int widthRatio, int heightRatio) //指定ImageView2的宽高比
    Image setActualImageScaleType(ScalingUtils.ScaleType pScaleType) //修改"要显示的图片"的缩放类型
    Image setActualImageColorFilter(ColorFilter pColorFilter) //修改"要显示的图片"的color filter
    Image setActualImageFocusPoint(PointF pFocusPoint) //如果选择缩放类型为ScalingUtils.ScaleType.FOCUS_CROP 需要指定一个居中点:new PointF(0.5f,0.5f)
    Image setRoundingParams(float pRadius, int pOverlayColor, int pBorder, int pBorderColor, boolean isCircle) // 修改圆角|圆形参数
    Image setRoundingParams(RoundingParams pRoundingParams)
    // 设置圆角|圆形的角度 180度会变成圆形 // 设置覆盖的颜色 // 设置边框尺寸和颜色 // 是否设置为圆型
    Image setFadeDuration(int pFadeDuration) //渐显的时长 -> 毫秒
    Image setResizeOptions(int width, int height) //Resizing缩放模式所需宽高参数
    Image setResizeOptions(ResizeOptions pResizeOptions)
    Image setAutoRotateEnabled(boolean isEnabled) //自动旋转(支持JPEG格式) -> 不管图片方向是怎样的,显示时都会将图片的方向与设备屏幕的方向保持一致
    Image setPostprocessor(Postprocessor pPostprocessor) //设置后处理器 -> 图片加载完成后对图片进行特殊处理(不支持动图加载)
    Image setLocalThumbnailPreviewsEnabled(boolean isEnabled) //是否开启缩略图式加载
    Image setProgressiveRenderingEnabled(boolean isEnabled) //是否开启渐进式加载
    Image setAutoPlayAnimations(boolean isAuto) //动画图加载完是否自动播放
    void animatableStart() //图片为 GIF | WebP 格式时,设置播放
    void animatableStop() //图片为 GIF | WebP 格式时,设置暂停
    boolean animatableIsRunning() //图片为 GIF | WebP 格式时,检查是否在播放
    void commit() //提交生效
*/

}