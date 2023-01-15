package com.tdms.mahyco.nxg.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.Nullable;

//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
import java.io.IOException;

public class ScalingUtils {

    public static final int[] getImageHgtWdt( Context $this$getImageHgtWdt,  Uri uri) {
        //Intrinsics.checkParameterIsNotNull($this$getImageHgtWdt, "$this$getImageHgtWdt");
        //Intrinsics.checkParameterIsNotNull(uri, "uri");
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        try {
            FileUtilImage.getBitmapFromUri($this$getImageHgtWdt, uri, opt);
        } catch (IOException e) {
            Log.d("MSG","DATA:"+e.getMessage());
        }
        float actualHgt = (float)opt.outHeight;
        float actualWdt = (float)opt.outWidth;
        float maxHeight = 720.0F;
        float maxWidth = 1280.0F;
        float imgRatio = actualWdt / actualHgt;
        float maxRatio = maxWidth / maxHeight;
        if (actualHgt > maxHeight || actualWdt > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHgt;
                actualWdt = imgRatio * actualWdt;
                actualHgt = maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWdt;
                actualHgt = imgRatio * actualHgt;
                actualWdt = maxWidth;
            } else {
                actualHgt = maxHeight;
                actualWdt = maxWidth;
            }
        }

        int arr[]={(int)actualHgt, (int)actualWdt};
        //return new Pair((int)actualHgt, (int)actualWdt);
        return arr;
    }

    @Nullable
    public static final Bitmap decodeFile( Context context,  Uri uri, int dstWidth, int dstHeight, FileUtilImage.ScalingLogic scalingLogic) {
        //Intrinsics.checkParameterIsNotNull(context, "context");
        //Intrinsics.checkParameterIsNotNull(uri, "uri");
        //Intrinsics.checkParameterIsNotNull(scalingLogic, "scalingLogic");
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            FileUtilImage.getBitmapFromUri(context, uri, options);
        } catch (IOException e) {
            Log.d("MSG","DATA:"+e.getMessage());
        }
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options.outWidth, options.outHeight, dstWidth, dstHeight, scalingLogic);
        try {
            return FileUtilImage.getBitmapFromUri(context, uri, options);
        } catch (IOException e) {
            Log.d("MSG","DATA:"+e.getMessage());
        }
        return null;
    }

    public static final int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight,  FileUtilImage.ScalingLogic scalingLogic) {
        //Intrinsics.checkParameterIsNotNull(scalingLogic, "scalingLogic");
        float srcAspect;
        float dstAspect;
        if (scalingLogic == FileUtilImage.ScalingLogic.FIT) {
            srcAspect = (float)srcWidth / (float)srcHeight;
            dstAspect = (float)dstWidth / (float)dstHeight;
            return srcAspect > dstAspect ? srcWidth / dstWidth : srcHeight / dstHeight;
        } else {
            srcAspect = (float)srcWidth / (float)srcHeight;
            dstAspect = (float)dstWidth / (float)dstHeight;
            return srcAspect > dstAspect ? srcHeight / dstHeight : srcWidth / dstWidth;
        }
    }
}

