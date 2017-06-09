package com.acmenxd.frescoview;

import android.graphics.drawable.Animatable;
import android.support.annotation.NonNull;

import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.imagepipeline.image.ImageInfo;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/1/22 10:31
 * @detail 图片加载回调
 */
public abstract class FrescoCallback extends BaseControllerListener<ImageInfo> {
    @Override
    public final void onFinalImageSet(String id, ImageInfo imageInfo, Animatable animatable) {
        super.onFinalImageSet(id, imageInfo, animatable);
        succeed(id, imageInfo, animatable);
    }

    @Override
    public final void onFailure(String id, Throwable throwable) {
        super.onFailure(id, throwable);
        failed(id, throwable);
    }

    @Override
    public final void onIntermediateImageSet(String id, ImageInfo imageInfo) {
        super.onIntermediateImageSet(id, imageInfo);
        //如果允许呈现渐进式JPEG，同时图片也是渐进式图片，onIntermediateImageSet会在每个扫描被解码后回调
    }

    @Override
    public final void onIntermediateImageFailed(String id, Throwable throwable) {
        super.onIntermediateImageFailed(id, throwable);
    }

    @Override
    public final void onRelease(String id) {
        super.onRelease(id);
    }

    @Override
    public final void onSubmit(String id, Object callerContext) {
        super.onSubmit(id, callerContext);
    }

    /**
     * 请求成功被回调
     * * 非必须重写函数,如需要进行重写
     *
     * @param id         : 图片的id值
     * @param imageInfo  : 图片的一些信息,如尺寸等
     * @param animatable : 此参数用来操作相关动画,如gif|webp等动画格式
     */
    public void succeed(@NonNull String id, @NonNull ImageInfo imageInfo, @NonNull Animatable animatable) {

    }

    /**
     * 请求失败被回调
     * * 非必须重写函数,如需要进行重写
     *
     * @param id        : 图片的id值
     * @param throwable : 错误信息
     */
    public void failed(@NonNull String id, @NonNull Throwable throwable) {

    }
}
