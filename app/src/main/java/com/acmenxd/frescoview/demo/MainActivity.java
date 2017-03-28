package com.acmenxd.frescoview.demo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.acmenxd.frescoview.FrescoCallback;
import com.acmenxd.frescoview.FrescoView;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.image.QualityInfo;
import com.facebook.imagepipeline.request.BasePostprocessor;
import com.facebook.imagepipeline.request.Postprocessor;

/**
 * @author AcmenXD
 * @version v1.0
 * @github https://github.com/AcmenXD
 * @date 2017/2/6 17:35
 * @detail 包含了所有集成功能, 所以篇幅有点长
 */
public class MainActivity extends AppCompatActivity {
    private FrescoView iv1;
    private FrescoView iv2;
    private FrescoView iv3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        setContentView(R.layout.activity_main);
        iv1 = (FrescoView) findViewById(R.id.imageView1);
        iv2 = (FrescoView) findViewById(R.id.imageView2);
        iv3 = (FrescoView) findViewById(R.id.imageView3);
        iv2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) dp2px(200)));
        iv3.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) dp2px(200)));
    }


    public void btnClick1(View view) {
        // 加载回调
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
        iv2.image()
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
    }


    public void btnClick2(View view) {
        // 后处理器
        Postprocessor customPostprocessor = new BasePostprocessor() {
            @Override
            public String getName() {
                return "customPostprocessor";
            }

            //* 不要重写多于 1 个的 process 方法。这么做可能造成无法预测的结果。
            @Override
            public void process(Bitmap bitmap) {
                // 图片加了红色网格
                for (int x = 0; x < bitmap.getWidth(); x += 10) {
                    for (int y = 0; y < bitmap.getHeight(); y += 10) {
                        bitmap.setPixel(x, y, Color.RED);
                    }
                }
            }

           /* @Override
            public void process(Bitmap destBitmap, Bitmap sourceBitmap) {
                // 复制 bitmap
                // 不要修改源图片。在未来的版本中这会抛出一个异常。
                // 不要保存对任何一个图片的引用。它们的内存会由 image pipeline 进行管理，目标图片会在 Drawww 或 DataSource 中正常地销毁。
                for (int x = 0; x < destBitmap.getWidth(); x++) {
                    for (int y = 0; y < destBitmap.getHeight(); y++) {
                        destBitmap.setPixel(destBitmap.getWidth() - x, y, sourceBitmap.getPixel(x, y));
                    }
                }
            }*/

            /*@Override
            public CloseableReference<Bitmap> process(Bitmap sourceBitmap, PlatformBitmapFactory bitmapFactory) {
                // 复制成不同大小的bitmap
                // 将源图片复制为 1 / 4 大小
                // 不要使用 Android 中 Bitmap.createBitmap() 方法，它会在 Java 堆内存中产生一个 bitmap 对象
                CloseableReference<Bitmap> bitmapRef = bitmapFactory.createBitmap(
                        sourceBitmap.getWidth() / 2,
                        sourceBitmap.getHeight() / 2);
                try {
                    Bitmap destBitmap = bitmapRef.get();
                    for (int x = 0; x < destBitmap.getWidth(); x+=2) {
                        for (int y = 0; y < destBitmap.getHeight(); y+=2) {
                            destBitmap.setPixel(x, y, sourceBitmap.getPixel(x, y));
                        }
                    }
                    return CloseableReference.cloneOrNull(bitmapRef);
                } finally {
                    CloseableReference.closeSafely(bitmapRef);
                }
            }*/
        };
        iv3.image()
                .setHighImageURI("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=2994123101,43895145&fm=23&gp=0.jpg")
                .setRoundingParams(100, Color.BLACK, 10, Color.YELLOW, false)
                .setFadeDuration(1000)
                .setPostprocessor(customPostprocessor)
                .commit();
    }

    public void btnClick3(View view) {
        iv2.image().animatableStart();
    }

    public void btnClick4(View view) {
        iv2.image().animatableStop();
    }

    /**
     * 串拼接
     *
     * @param strs 可变参数类型
     * @return 拼接后的字符串
     */
    public String appendStrs(Object... strs) {
        StringBuilder sb = new StringBuilder();
        if (strs != null && strs.length > 0) {
            for (Object str : strs) {
                sb.append(String.valueOf(str));
            }
        }
        return sb.toString();
    }

    /**
     * 根据手机的分辨率从 dp 的单位转成 px(像素)
     */
    public float dp2px(float dp) {
        return dp2px(this.getResources(), dp);
    }

    public static float dp2px(Resources resources, float dp) {
        final float scale = resources.getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }
}
