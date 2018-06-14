package com.acmenxd.frescoview;

import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.AnyRes;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.facebook.imagepipeline.request.Postprocessor;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/1/18 14:30
 * @detail 支持图片格式:PNG / GIF / WebP / JPEG
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
public final class FrescoView extends SimpleDraweeView {
    private Image mImage;
    private Context mContext;

    public FrescoView(Context context) {
        super(context);
        mContext = context;
    }

    public FrescoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public FrescoView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
        mContext = context;
    }

    public FrescoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public FrescoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mContext = context;
    }

    /**
     * 获取ImageView2功能操作类 实例
     */
    public Image image() {
        if (mImage == null) {
            mImage = new Image(mContext, this);
        }
        return mImage;
    }

    /**
     * 重置ImageView2参数
     */
    public Image reset() {
        if (mImage != null) {
            mImage.reset();
        }
        return mImage;
    }

    /**
     * ImageView2 功能操作类
     */
    public final class Image {
        private Context mContext;
        private FrescoView mImageView2;
        private String mImageUri;
        private String mLowImageUri;
        private String mHighImageUri;
        private String[] mFirstAvailableImageUris;
        private Object mCallerContext;
        private Postprocessor mPostprocessor;// 后处理器 -> 图片加载完成后对图片进行特殊处理(不支持动图加载)
        private ResizeOptions mResizeOptions;// Resizing缩放模式所需宽高参数
        private boolean isAutoPlayAnimations = false; // 动画图是否自动播放
        private int isAutoRotateEnabled = -1;// 自动旋转(支持JPEG格式) 默认:-1  不启用:0   启用:1 (不管图片方向是怎样的,显示时都会将图片的方向与设备屏幕的方向保持一致)
        private int isLocalThumbnailPreviewsEnabled = -1; // 缩略图式加载  默认:-1  不启用:0   启用:1
        private int isProgressiveRenderingEnabled = -1; // 开启渐进式加载  默认:-1  不启用:0   启用:1

        private Image(@NonNull Context pContext, @NonNull FrescoView pImageView2) {
            mContext = pContext;
            mImageView2 = pImageView2;
        }

        /**
         * 重置所有配置
         */
        public void reset() {
            init();
            getGenericDraweeHierarchy().reset();
            mPipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
        }

        /**
         * 设置图片请求地址 *格式说明 (Content provider -> 待验证 -> content://)
         */
        //支持网络资源
        public Image setImageURI(@NonNull String pImageUri) {
            if (pImageUri.startsWith("http://") || pImageUri.startsWith("https://")) {
                setImageURI(pImageUri, null);
            }
            return getThis();
        }

        //支持res目录下的资源
        public Image setImageURI(@AnyRes int resId) {
            setImageURI(FrescoUtils.appendStrs("res://", FrescoManager.APP_PKG_NAME, "/", resId), null);
            return getThis();
        }

        //支持本地文件资源
        public Image setImageURI_File(@NonNull String filePath) {
            setImageURI(FrescoUtils.appendStrs("file://", filePath), null);
            return getThis();
        }

        //支持asset目录下的资源
        public Image setImageURI_Asset(@NonNull String assetPath) {
            setImageURI(FrescoUtils.appendStrs("asset://", assetPath), null);
            return getThis();
        }

        /**
         * 先显示低分辨率的图,然后是高分辨率的图(优先级 > setHighImageURI > setImageURI)
         */
        public Image setLowImageURI(@NonNull String pLowImageUri) {
            if (TextUtils.isEmpty(pLowImageUri)) {
                throw new NullPointerException("lowImageURI can't null or ''");
            }
            mLowImageUri = pLowImageUri;
            return getThis();
        }

        /**
         * 先显示低分辨率的图,然后是高分辨率的图(优先级 > setImageURI)
         */
        public Image setHighImageURI(@NonNull String pHighImageUri) {
            if (TextUtils.isEmpty(pHighImageUri)) {
                throw new NullPointerException("highImageURI can't null or ''");
            }
            mHighImageUri = pHighImageUri;
            return getThis();
        }

        /**
         * 加载最先可用的图像(同一个图片有多个Uri的情况下,设置多个Uri路径)
         * * 显示规则:检查集合中任意uri是否存在,如存在则显示,如不存在,则寻找磁盘缓存,如不存在,则外部请求
         */
        public Image setFirstAvailableImageURIs(@NonNull String... pFirstAvailableImageUris) {
            if (pFirstAvailableImageUris == null || pFirstAvailableImageUris.length <= 0) {
                throw new NullPointerException("highImageURI can't null or ''");
            }
            mFirstAvailableImageUris = pFirstAvailableImageUris;
            return getThis();
        }

        /**
         * 修改占位图
         */
        public Image setPlaceholderImage(@DrawableRes int resId) {
            getGenericDraweeHierarchy().setPlaceholderImage(resId);
            return getThis();
        }

        public Image setPlaceholderImage(@DrawableRes int resId, ScalingUtils.ScaleType pScaleType) {
            getGenericDraweeHierarchy().setPlaceholderImage(resId, pScaleType);
            return getThis();
        }

        public Image setPlaceholderImage(@NonNull Drawable pDrawable) {
            getGenericDraweeHierarchy().setPlaceholderImage(pDrawable);
            return getThis();
        }

        public Image setPlaceholderImage(@NonNull Drawable pDrawable, @NonNull ScalingUtils.ScaleType pScaleType) {
            getGenericDraweeHierarchy().setPlaceholderImage(pDrawable, pScaleType);
            return getThis();
        }

        /**
         * 修改请求失败的图像
         */
        public Image setFailureImage(@DrawableRes int resId) {
            getGenericDraweeHierarchy().setFailureImage(resId);
            return getThis();
        }

        public Image setFailureImage(@DrawableRes int resId, @NonNull ScalingUtils.ScaleType pScaleType) {
            getGenericDraweeHierarchy().setFailureImage(resId, pScaleType);
            return getThis();
        }

        public Image setFailureImage(@NonNull Drawable pDrawable) {
            getGenericDraweeHierarchy().setFailureImage(pDrawable);
            return getThis();
        }

        public Image setFailureImage(@NonNull Drawable pDrawable, @NonNull ScalingUtils.ScaleType pScaleType) {
            getGenericDraweeHierarchy().setFailureImage(pDrawable, pScaleType);
            return getThis();
        }

        /**
         * 加载失败重试显示的图片,会触发点击重试四次,如果还是加载失败,则显示请求失败的图像
         */
        public Image setRetryImage(@DrawableRes int resId) {
            getPipelineDraweeControllerBuilder().setTapToRetryEnabled(true);
            getGenericDraweeHierarchy().setRetryImage(resId);
            return getThis();
        }

        public Image setRetryImage(@DrawableRes int resId, @NonNull ScalingUtils.ScaleType pScaleType) {
            getPipelineDraweeControllerBuilder().setTapToRetryEnabled(true);
            getGenericDraweeHierarchy().setRetryImage(resId, pScaleType);
            return getThis();
        }

        public Image setRetryImage(@NonNull Drawable pDrawable) {
            getPipelineDraweeControllerBuilder().setTapToRetryEnabled(true);
            getGenericDraweeHierarchy().setRetryImage(pDrawable);
            return getThis();
        }

        public Image setRetryImage(@NonNull Drawable pDrawable, @NonNull ScalingUtils.ScaleType pScaleType) {
            getPipelineDraweeControllerBuilder().setTapToRetryEnabled(true);
            getGenericDraweeHierarchy().setRetryImage(pDrawable, pScaleType);
            return getThis();
        }

        /**
         * 显示进度条图片 -> view底部会有一个深蓝色的矩形进度条
         * * 支持AnimationDrawable动画,xml自定义动画
         * * 如需精确显示进度 参考-> https://www.fresco-cn.org/docs/progress-bars.html
         */
        public Image setProgressBarImage(@DrawableRes int resId) {
            getGenericDraweeHierarchy().setProgressBarImage(resId);
            return getThis();
        }

        public Image setProgressBarImage(@DrawableRes int resId, @NonNull ScalingUtils.ScaleType pScaleType) {
            getGenericDraweeHierarchy().setProgressBarImage(resId, pScaleType);
            return getThis();
        }

        public Image setProgressBarImage(@NonNull Drawable pDrawable) {
            getGenericDraweeHierarchy().setProgressBarImage(pDrawable);
            return getThis();
        }

        public Image setProgressBarImage(@NonNull Drawable pDrawable, @NonNull ScalingUtils.ScaleType pScaleType) {
            getGenericDraweeHierarchy().setProgressBarImage(pDrawable, pScaleType);
            return getThis();
        }

        /**
         * 设置背景图像
         */
        public Image setBackgroundImage(@NonNull Drawable pDrawable) {
            getGenericDraweeHierarchy().setBackgroundImage(pDrawable);
            return getThis();
        }

        /**
         * 设置一个新的覆盖图像 -> 在指定的索引位置。
         */
        public Image setOverlayImage(@NonNull Drawable pDrawable) {
            getGenericDraweeHierarchy().setOverlayImage(pDrawable);
            return getThis();
        }

        public Image setOverlayImage(@IntRange(from = 0) int index, @NonNull Drawable pDrawable) {
            getGenericDraweeHierarchy().setOverlayImage(index, pDrawable);
            return getThis();
        }

        /**
         * 设置控制器监听
         */
        public Image setControllerListener(@NonNull ControllerListener<? super ImageInfo> pListener) {
            getPipelineDraweeControllerBuilder().setControllerListener(pListener);
            return getThis();
        }

        /**
         * 指定ImageView2的宽高比
         */
        public Image setAspectRatio(@IntRange(from = 0) int widthRatio, @IntRange(from = 0) int heightRatio) {
            if (widthRatio <= 0 || heightRatio <= 0) {
                throw new ArithmeticException("widthRatio or heightRatio must > 0");
            }
            mImageView2.setAspectRatio(Float.parseFloat(String.valueOf(widthRatio)) / heightRatio);
            return getThis();
        }

        /**
         * 修改"要显示的图片"的缩放类型
         */
        public Image setActualImageScaleType(@NonNull ScalingUtils.ScaleType pScaleType) {
            getGenericDraweeHierarchy().setActualImageScaleType(pScaleType);
            return getThis();
        }

        /**
         * 修改"要显示的图片"的color filter
         */
        public Image setActualImageColorFilter(@NonNull ColorFilter pColorFilter) {
            getGenericDraweeHierarchy().setActualImageColorFilter(pColorFilter);
            return getThis();
        }

        /**
         * 如果选择缩放类型为ScalingUtils.ScaleType.FOCUS_CROP
         * 需要指定一个居中点:new PointF(0.5f,0.5f)
         */
        public Image setActualImageFocusPoint(@NonNull PointF pFocusPoint) {
            getGenericDraweeHierarchy().setActualImageFocusPoint(pFocusPoint);
            return getThis();
        }

        /**
         * 修改圆角|圆形参数
         */
        public Image setRoundingParams(@FloatRange(from = 0) float pRadius, @ColorInt int pOverlayColor, @IntRange(from = 0) int pBorder, @ColorInt int pBorderColor, boolean isCircle) {
            RoundingParams roundingParams = getGenericDraweeHierarchy().getRoundingParams();
            if (roundingParams == null) {
                roundingParams = new RoundingParams();
            }
            roundingParams.setCornersRadius(pRadius); // 设置圆角|圆形的角度 180度会变成圆形
            roundingParams.setOverlayColor(pOverlayColor); // 设置覆盖的颜色
            roundingParams.setBorder(pBorderColor, pBorder); // 设置边框尺寸和颜色
            roundingParams.setRoundAsCircle(isCircle); // 是否设置为圆型
            setRoundingParams(roundingParams);
            return getThis();
        }

        public Image setRoundingParams(@NonNull RoundingParams pRoundingParams) {
            getGenericDraweeHierarchy().setRoundingParams(pRoundingParams);
            return getThis();
        }

        /**
         * 渐显的时长 -> 毫秒
         */
        public Image setFadeDuration(@IntRange(from = 0) int pFadeDuration) {
            getGenericDraweeHierarchy().setFadeDuration(pFadeDuration);
            return getThis();
        }

        /**
         * Resizing缩放模式所需宽高参数
         */
        public Image setResizeOptions(@IntRange(from = 0) int width, @IntRange(from = 0) int height) {
            return setResizeOptions(new ResizeOptions(width, height));
        }

        public Image setResizeOptions(@NonNull ResizeOptions pResizeOptions) {
            getPipelineDraweeControllerBuilder();
            mResizeOptions = pResizeOptions;
            return getThis();
        }

        /**
         * 自动旋转(支持JPEG格式) -> 不管图片方向是怎样的,显示时都会将图片的方向与设备屏幕的方向保持一致
         */
        public Image setAutoRotateEnabled(boolean isEnabled) {
            getPipelineDraweeControllerBuilder();
            isAutoRotateEnabled = isEnabled ? 1 : 0;
            return getThis();
        }

        /**
         * 设置后处理器 -> 图片加载完成后对图片进行特殊处理(不支持动图加载)
         */
        public Image setPostprocessor(@NonNull Postprocessor pPostprocessor) {
            getPipelineDraweeControllerBuilder();
            mPostprocessor = pPostprocessor;
            return getThis();
        }

        /**
         * 是否开启缩略图式加载
         */
        public Image setLocalThumbnailPreviewsEnabled(boolean isEnabled) {
            getPipelineDraweeControllerBuilder();
            isLocalThumbnailPreviewsEnabled = isEnabled ? 1 : 0;
            return getThis();
        }

        /**
         * 是否开启渐进式加载
         */
        public Image setProgressiveRenderingEnabled(boolean isEnabled) {
            getPipelineDraweeControllerBuilder();
            isProgressiveRenderingEnabled = isEnabled ? 1 : 0;
            return getThis();
        }

        /**
         * 动画图加载完是否自动播放
         */
        public Image setAutoPlayAnimations(boolean isAuto) {
            getPipelineDraweeControllerBuilder();
            isAutoPlayAnimations = isAuto;
            return getThis();
        }

        /**
         * 图片为 GIF | WebP 格式时,设置播放
         */
        public void animatableStart() {
            if (mImageView2 != null && mImageView2.getController() != null && mImageView2.getController().getAnimatable() != null) {
                mImageView2.getController().getAnimatable().start();
            }
        }

        /**
         * 图片为 GIF | WebP 格式时,设置暂停
         */
        public void animatableStop() {
            if (mImageView2 != null && mImageView2.getController() != null && mImageView2.getController().getAnimatable() != null) {
                mImageView2.getController().getAnimatable().stop();
            }
        }

        /**
         * 图片为 GIF | WebP 格式时,检查是否在播放
         */
        public boolean animatableIsRunning() {
            if (mImageView2 != null && mImageView2.getController() != null && mImageView2.getController().getAnimatable() != null) {
                return mImageView2.getController().getAnimatable().isRunning();
            }
            return false;
        }

        /**
         * 提交生效
         */
        public void commit() {
            ImageRequest[] imageRequests = null;
            String uriStr = "";
            if (mFirstAvailableImageUris != null && mFirstAvailableImageUris.length > 0) {
                int len = mFirstAvailableImageUris.length;
                imageRequests = new ImageRequest[len];
                for (int i = 0; i < len; i++) {
                    imageRequests[i] = ImageRequest.fromUri(mFirstAvailableImageUris[i]);
                }
                getPipelineDraweeControllerBuilder();
            } else if (!TextUtils.isEmpty(mLowImageUri)) {
                uriStr = mLowImageUri;
                getPipelineDraweeControllerBuilder();
            } else if (!TextUtils.isEmpty(mHighImageUri)) {
                uriStr = mHighImageUri;
                getPipelineDraweeControllerBuilder();
            } else if (!TextUtils.isEmpty(mImageUri)) {
                uriStr = mImageUri;
            }
            // 检查路径
            if (TextUtils.isEmpty(uriStr)) {
                throw new NullPointerException("uri can't null");
            }
            if (mPipelineDraweeControllerBuilder != null) {
                ImageRequest request = mPipelineDraweeControllerBuilder.getImageRequest();
                ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(Uri.parse(uriStr));
                boolean isImageRequestBuilder = false;
                // 优先级 : 低分辨率&高分辨率模式 > 缩略图式加载 > 渐进式加载 > 普通加载
                // 设置低分辨率&高分辨率模式
                if (!TextUtils.isEmpty(mLowImageUri) || !TextUtils.isEmpty(mHighImageUri)) {
                    if (!TextUtils.isEmpty(mLowImageUri)) {
                        mPipelineDraweeControllerBuilder.setLowResImageRequest(ImageRequest.fromUri(mLowImageUri));
                    }
                    if (!TextUtils.isEmpty(mHighImageUri)) {
                        mPipelineDraweeControllerBuilder.setImageRequest(ImageRequest.fromUri(mHighImageUri));
                    }
                } else if (isLocalThumbnailPreviewsEnabled == 0 || isLocalThumbnailPreviewsEnabled == 1) {
                    boolean isEnabled = isLocalThumbnailPreviewsEnabled == 1;
                    // 设置缩略图式加载
                    if (request == null || request.getLocalThumbnailPreviewsEnabled() != isEnabled) {
                        imageRequestBuilder.setLocalThumbnailPreviewsEnabled(isEnabled);
                        isImageRequestBuilder = true;
                    }
                } else if (isProgressiveRenderingEnabled == 0 || isProgressiveRenderingEnabled == 1) {
                    boolean isEnabled = isProgressiveRenderingEnabled == 1;
                    // 设置渐进式加载
                    if (request == null || request.getProgressiveRenderingEnabled() != isEnabled) {
                        imageRequestBuilder.setProgressiveRenderingEnabled(isEnabled);
                        isImageRequestBuilder = true;
                    }
                } else {
                    // 设置图片请求地址
                    mPipelineDraweeControllerBuilder.setUri(uriStr);
                }
                // 设置动画图是否自动播放
                if (isAutoPlayAnimations != mPipelineDraweeControllerBuilder.getAutoPlayAnimations()) {
                    mPipelineDraweeControllerBuilder.setAutoPlayAnimations(isAutoPlayAnimations);
                }
                // 加载最先可用的图像
                if (imageRequests != null && imageRequests.length > 0) {
                    mPipelineDraweeControllerBuilder.setFirstAvailableImageRequests(imageRequests);
                }
                // 设置自动旋转(支持JPEG格式) -> 不管图片方向是怎样的,显示时都会将图片的方向与设备屏幕的方向保持一致
                if (isAutoRotateEnabled != -1) {
                    boolean isEnabled = isAutoRotateEnabled == 1;
                    imageRequestBuilder.setAutoRotateEnabled(isEnabled);
                    isImageRequestBuilder = true;
                }
                // 后处理器 -> 图片加载完成后对图片进行特殊处理(不支持动图加载)
                if (mPostprocessor != null) {
                    imageRequestBuilder.setPostprocessor(mPostprocessor);
                    isImageRequestBuilder = true;
                }
                // 设置Resizing缩放模式
                if (mResizeOptions != null) {
                    imageRequestBuilder.setResizeOptions(mResizeOptions);
                    isImageRequestBuilder = true;
                }
                // 设置ImageRequest
                if (isImageRequestBuilder) {
                    mPipelineDraweeControllerBuilder.setImageRequest(imageRequestBuilder.build());
                }
                mPipelineDraweeControllerBuilder.setOldController(mImageView2.getController());
                mImageView2.setController(mPipelineDraweeControllerBuilder.build());
            } else {
                // 设置图片请求地址
                mImageView2.setImageURI(uriStr, mCallerContext);
            }
        }

        //------------------------- 阉割部分功能,无法正常支持-------------------------

        /**
         * ImageRequest的更多功能
         */
        private Image setImageDecodeOptions() {
            /*ImageDecodeOptions decodeOptions = ImageDecodeOptions.newBuilder()
                    .setBackgroundColor(Color.GREEN)
                    .build();
            ImageRequest.setImageDecodeOptions(decodeOptions);*/
            return getThis();
        }

        /**
         * 设置图片请求等级 : 参考-> https://www.fresco-cn.org/docs/image-requests.html
         */
        private Image setLowestPermittedRequestLevel() {
            //ImageRequest.setLowestPermittedRequestLevel(RequestLevel.FULL_FETCH);
            return getThis();
        }

        /**
         * 设置按压状态下的叠加图
         */
        private Image setPressedStateOverlay(@NonNull Drawable pDrawable) {
            //GenericDraweeHierarchyBuilder.setPressedStateOverlay(pDrawable);
            return getThis();
        }

        //------------------------------private 函数 -> 无需关心 -----------------------------
        private Image getThis() {
            return this;
        }

        private void init() {
            mImageUri = "";
            mLowImageUri = "";
            mHighImageUri = "";
            mFirstAvailableImageUris = null;
            mCallerContext = null;
            mPostprocessor = null;
            mResizeOptions = null;
            isAutoPlayAnimations = false;
            isAutoRotateEnabled = -1;
            isLocalThumbnailPreviewsEnabled = -1;
            isProgressiveRenderingEnabled = -1;
        }

        private Image setImageURI(@NonNull String pImageUri, @NonNull Object pCallerContext) {
            if (TextUtils.isEmpty(pImageUri)) {
                throw new NullPointerException("imageUri can't null or ''");
            }
            mImageUri = pImageUri;
            mCallerContext = pCallerContext;
            return getThis();
        }

        private GenericDraweeHierarchy mGenericDraweeHierarchy;

        private GenericDraweeHierarchy getGenericDraweeHierarchy() {
            mGenericDraweeHierarchy = mImageView2.getHierarchy();
            if (mGenericDraweeHierarchy == null) {
                mImageView2.setHierarchy(new GenericDraweeHierarchyBuilder(mContext.getResources()).build());
                mGenericDraweeHierarchy = mImageView2.getHierarchy();
            }
            return mGenericDraweeHierarchy;
        }

        private PipelineDraweeControllerBuilder mPipelineDraweeControllerBuilder;

        private PipelineDraweeControllerBuilder getPipelineDraweeControllerBuilder() {
            if (mPipelineDraweeControllerBuilder == null) {
                mPipelineDraweeControllerBuilder = Fresco.newDraweeControllerBuilder();
            }
            return mPipelineDraweeControllerBuilder;
        }
    }
}
