package com.example.huangjinyuxuexiqiangguo.util

import android.content.Context
import com.luck.picture.lib.engine.ImageEngine
import com.bumptech.glide.Glide
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import com.luck.picture.lib.listener.OnImageCompleteCallback
import com.bumptech.glide.request.target.ImageViewTarget
import android.graphics.Bitmap
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.BitmapImageViewTarget
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import com.luck.picture.lib.R
import com.luck.picture.lib.tools.MediaUtils

class GlideEngine private constructor() : ImageEngine {
    // 加载图片
    override fun loadImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .load(url)
                .into(imageView)
    }
    //加载网络图片适配长图方案
    override fun loadImage(context: Context, url: String,
                           imageView: ImageView,
                           longImageView: SubsamplingScaleImageView, callback: OnImageCompleteCallback) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : ImageViewTarget<Bitmap?>(imageView) {
                    override fun onLoadStarted(placeholder: Drawable?) {
                        super.onLoadStarted(placeholder)
                        if (callback != null) {
                            callback.onShowLoading()
                        }
                    }
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        if (callback != null) {
                            callback.onHideLoading()
                        }
                    }
                    override fun setResource(resource: Bitmap?) {
                        if (callback != null) {
                            callback.onHideLoading()
                        }
                        if (resource != null) {
                            val eqLongImage = MediaUtils.isLongImg(resource.width,
                                    resource.height)
                            longImageView.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                            imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                            if (eqLongImage) {
                                // 加载长图
                                longImageView.isQuickScaleEnabled = true
                                longImageView.isZoomEnabled = true
                                longImageView.setDoubleTapZoomDuration(100)
                                longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                                longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                                longImageView.setImage(ImageSource.bitmap(resource),
                                        ImageViewState(0f, PointF(0f, 0f), 0))
                            } else {
                                // 普通图片
                                imageView.setImageBitmap(resource)
                            }
                        }
                    }
                })
    }

    //加载网络图片适配长图方案
    override fun loadImage(context: Context, url: String,
                           imageView: ImageView,
                           longImageView: SubsamplingScaleImageView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : ImageViewTarget<Bitmap?>(imageView) {
                    override fun setResource(resource: Bitmap?) {
                        if (resource != null) {
                            val eqLongImage = MediaUtils.isLongImg(resource.width,
                                    resource.height)
                            longImageView.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                            imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
                            if (eqLongImage) {
                                // 加载长图
                                longImageView.isQuickScaleEnabled = true
                                longImageView.isZoomEnabled = true
                                longImageView.setDoubleTapZoomDuration(100)
                                longImageView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                                longImageView.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                                longImageView.setImage(ImageSource.bitmap(resource),
                                        ImageViewState(0f, PointF(0f, 0f), 0))
                            } else {
                                // 普通图片
                                imageView.setImageBitmap(resource)
                            }
                        }
                    }
                })
    }

    //加载相册目录
    override fun loadFolderImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .asBitmap()
                .load(url)
                .override(180, 180)
                .centerCrop()
                .sizeMultiplier(0.5f)
                .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
                .into(object : BitmapImageViewTarget(imageView) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, resource)
                        circularBitmapDrawable.cornerRadius = 8f
                        imageView.setImageDrawable(circularBitmapDrawable)
                    }
                })
    }

    /**
     * 加载gif
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadAsGifImage(context: Context, url: String,
                                imageView: ImageView) {
        Glide.with(context)
                .asGif()
                .load(url)
                .into(imageView)
    }

    /**
     * 加载图片列表图片
     *
     * @param context   上下文
     * @param url       图片路径
     * @param imageView 承载图片ImageView
     */
    override fun loadGridImage(context: Context, url: String, imageView: ImageView) {
        Glide.with(context)
                .load(url)
                .override(200, 200)
                .centerCrop()
                .apply(RequestOptions().placeholder(R.drawable.picture_image_placeholder))
                .into(imageView)
    }

    companion object {
        private var instance: GlideEngine? = null
        @JvmStatic
        fun createGlideEngine(): GlideEngine? {
            if (null == instance) {
                synchronized(GlideEngine::class.java) {
                    if (null == instance) {
                        instance = GlideEngine()
                    }
                }
            }
            return instance
        }
    }
}