package com.mei.tangramdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.tmall.wireless.vaf.framework.VafContext;
import com.tmall.wireless.vaf.framework.ViewManager;
import com.tmall.wireless.vaf.virtualview.Helper.ImageLoader;
import com.tmall.wireless.vaf.virtualview.view.image.ImageBase;


/**
 * @author wenshi
 * @github
 * @Description
 * @since 2019/8/14
 */
public class VirtualViewUtils {

    private VafContext sVafContext;

    private ViewManager sViewManager;

    public VafContext getsVafContext() {
        return sVafContext;
    }

    public ViewManager getsViewManager() {
        return sViewManager;
    }

    public VirtualViewUtils initVaf(final Context context) {
        if (sVafContext == null) {
            sVafContext = new VafContext(context.getApplicationContext());
            sVafContext.setImageLoaderAdapter(new ImageLoader.IImageLoaderAdapter() {
                @Override
                public void bindImage(String uri, ImageBase imageBase, int reqWidth, int reqHeight) {
                    if (uri == null) return;
                    if (uri.startsWith("http://") || uri.startsWith("https://")) {
                        RequestBuilder requestBuilder =
                                Glide.with(context.getApplicationContext()).asBitmap().load(uri);
                        if (reqWidth > 0 || reqHeight > 0) {
                            requestBuilder.submit(reqWidth, reqHeight);
                        }
                        ImageTarget imageTarget = new ImageTarget(imageBase);
                        requestBuilder.into(imageTarget);

                    } else {
                        try {
                            // 获取本地uri
                            int drawableResourceId = context.getResources().getIdentifier(uri, "drawable", context.getPackageName());
                            if (drawableResourceId > 0) {
                                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResourceId);
                                imageBase.setBitmap(bitmap);
                            } else if (drawableResourceId == 0) {
                                int mipmapResourceId = context.getResources().getIdentifier(uri, "mipmap", context.getPackageName());
                                if (mipmapResourceId > 0) {
                                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), mipmapResourceId);
                                    imageBase.setBitmap(bitmap);
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void getBitmap(String uri, int reqWidth, int reqHeight, ImageLoader.Listener lis) {
                    RequestBuilder requestBuilder =
                            Glide.with(context.getApplicationContext()).asBitmap().load(uri);
                    if (reqWidth > 0 || reqHeight > 0) {
                        requestBuilder.submit(reqWidth, reqHeight);
                    }
                    ImageTarget imageTarget = new ImageTarget(lis);
                    requestBuilder.into(imageTarget);
                }
            });

            sViewManager = sVafContext.getViewManager();
            sViewManager.init(context.getApplicationContext());
        }

        return this;
    }


    public VirtualViewUtils() {
    }

    public static class VirtualViewUtilsInstance {
        private static final VirtualViewUtils INSTANCE = new VirtualViewUtils();
    }

    public static VirtualViewUtils getInstance() {
        return VirtualViewUtilsInstance.INSTANCE;
    }


    public class ImageTarget extends SimpleTarget<Bitmap> {
        ImageBase mImageBase;
        ImageLoader.Listener mListener;

        public ImageTarget(ImageBase imageBase) {
            mImageBase = imageBase;
        }

        public ImageTarget(ImageLoader.Listener listener) {
            mListener = listener;
        }

        @Override
        public void onResourceReady(@NonNull Bitmap resource,
                                    @Nullable Transition<? super Bitmap> transition) {
            mImageBase.setBitmap(resource, true);
            if (mListener != null) {
                mListener.onImageLoadSuccess(resource);
            }
        }

        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {
            if (mListener != null) {
                mListener.onImageLoadFailed();
            }
        }
    }

}
