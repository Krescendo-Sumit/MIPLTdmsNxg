package com.tdms.mahyco.nxg.utils;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;

import android.util.Log;


import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class FileUtilImage
    {

        private static final String tag = "FileUtilImage";

        public enum ScalingLogic {
            CROP,
            FIT;
        }
        public  static  String savefilepath = "";
    /*public static Bitmap getBitmapFromUri$default(Context var0, Uri var1, BitmapFactory.Options var2, int var3, Object var4) throws IOException {
        if ((var3 & 2) != 0) {
            var2 = (BitmapFactory.Options)null;
        }
        return getBitmapFromUri(var0, var1, var2);
    }*/

        public static final Bitmap getBitmapFromUri( Context $this$getBitmapFromUri,  Uri uri,  BitmapFactory.Options options) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = $this$getBitmapFromUri.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor != null ? parcelFileDescriptor.getFileDescriptor() : null;
        Bitmap image = options != null ? BitmapFactory.decodeFileDescriptor(fileDescriptor, (Rect)null, options) : BitmapFactory.decodeFileDescriptor(fileDescriptor);
        if (parcelFileDescriptor != null) {
            parcelFileDescriptor.close();
        }

        return image;
    }

        public static final String getTimestampString() {
        Calendar date = Calendar.getInstance();
        SimpleDateFormat var10000 = new SimpleDateFormat("yyyy MM dd hh mm ss", Locale.ENGLISH);
        String var1 = var10000.format(date.getTime());
        //return replace$default(var1, " ", "", false, 4, (Object)null);
        String dateName = var1.replace(" ","");
        return dateName;
    }

    /*

        public  static  void writeMediaStoreFile(final Context context, String imgPath,
                                                 ImageView imgChoosen ) {
            Glide.with(context)
                    .asBitmap()
                    .load(imgPath)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable com.bumptech.glide.request.transition.Transition<? super Bitmap> transition) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "NewImage");
                            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/MyActivity/");
                            Uri localImageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                            if (localImageUri == null) {
                                // Toast.makeText(context, "Failed to create new MediaStore record",  Toast.LENGTH_SHORT).show();
                            } else {
                                // textViewPath.setText("Image Path: \n" + localImageUri);
                                // Toast.makeText(context, "Created new MediaStore record",  Toast.LENGTH_SHORT).show();
                                try {
                                    OutputStream outputStream = context.getContentResolver().openOutputStream(localImageUri);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                    outputStream.close();
                                    imgChoosen.setImageBitmap(bitmap);
                                    // Toast.makeText(context, "Bitmap created",  Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                                Log.d("MSG","DATA:"+e.getMessage());
                                    // Toast.makeText(context, "Failed to store bitmap",  Toast.LENGTH_SHORT).show();
                                }
                            }
                        }


                    });


//
        }
  */
        public static String compressImageFile(String path,
            Uri uri, Context context,String Imagename){
        boolean shouldOverride = false;
        Bitmap scaledBitmap = null;
            Bitmap bm=null;
        try {
            //Pair(hgt, wdt)
            int[] arr = ScalingUtils.getImageHgtWdt(context,uri);
            try {
                 bm = getBitmapFromUri(context,uri,null);
                Log.d(tag, "original bitmap height:"+bm.getHeight()+" width:"+bm.getWidth());
                Log.d(tag, "changed bitmap height:"+arr[0]+" width:"+arr[1]);
            } catch (Exception e) {
                Log.d("MSG","DATA:"+e.getMessage());

            }
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtils.decodeFile(context,uri, arr[0], arr[1], ScalingLogic.FIT);
            if (unscaledBitmap != null) {
                if (!(unscaledBitmap.getWidth() <= 800 && unscaledBitmap.getHeight() <= 800)) {
                    // Part 2: Scale image
                    scaledBitmap = createScaledBitmap(unscaledBitmap, arr[0], arr[1], ScalingLogic.FIT);
                } else {
                    scaledBitmap = unscaledBitmap;
                }
            }

            // Store to tmp file
            // File mFolder = new File("filesDir/Images");
            File mFolder = new File(String.valueOf(context.getExternalFilesDir(Environment.DIRECTORY_DCIM))+"/Images");
            if (!mFolder.exists()) {
                mFolder.mkdir();
            }

            File tmpFile = new File(mFolder.getAbsolutePath(), Imagename+".jpg");

           /*
            //savefilepath=tmpFile.getAbsolutePath();
            //ContentValues contentValues = new ContentValues();
            //contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, Imagename);
           // contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
           // contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES+"/MyActivity/");
           // Uri localImageUri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            //OutputStream outputStream = context.getContentResolver().openOutputStream(localImageUri);
           // bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
          */

            savefilepath=tmpFile.getAbsolutePath();
            Log.d(tag,"Tmp File:"+tmpFile.getAbsolutePath()+" Exist : "+tmpFile.exists()+" Image quality="+getImageQualityPercent(tmpFile));
            Log.d(tag,"Scaled bitmap: Height:"+scaledBitmap.getHeight()+" Width:"+scaledBitmap.getWidth());
            FileOutputStream fos = null;
            try {
                try {
                    fos = new FileOutputStream(tmpFile);
                    if (!tmpFile.exists()) {
                        tmpFile.createNewFile();
                    }
                } catch (FileNotFoundException e) {
                    Log.d(tag,"Tmp File not found inner");
                    Log.d("MSG","DATA:"+e.getMessage());
                }
                scaledBitmap.compress(
                        Bitmap.CompressFormat.JPEG,
                        getImageQualityPercent(tmpFile),
                        fos);
                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(tag,"Tmp File not found outer");
                Log.d("MSG","DATA:"+e.getMessage());
            }

            String compressedPath = "";
            if (tmpFile.exists() && tmpFile.length() > 0) {
                compressedPath = tmpFile.getAbsolutePath();
                if (shouldOverride) {
                    Log.d(tag,"Path = "+path);
                    Log.d(tag,"tempFile = "+tmpFile.getAbsolutePath());
                    File srcFile = new File(path);
                    Log.d(tag,"srcFile = "+srcFile.getAbsolutePath());
                    //File result =  tmpFile.copyTo(srcFile, true);
                    File result =  copy(tmpFile,srcFile);// tmpFile.copyTo(srcFile, true);
                    Log.d(tag, "copied file :"+result.getAbsolutePath());
                    Log.d(tag, "Delete temp file");
                    tmpFile.delete();
                }
            }

            scaledBitmap.recycle();

            if(shouldOverride){
                Log.d(tag,"RETURN path : "+path);
                return path;
            }
            else {
                Log.d(tag,"RETURN compressedPath : "+compressedPath);
                return compressedPath;
            }
        } catch (Throwable e) {
            Log.d("MSG","DATA:"+e.getMessage());
        }
        return "";
    }

        public static File copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
        return dst;
    }

        public static final int getImageQualityPercent( File file) {
        long sizeInBytes = file.length();
        long sizeInKB = sizeInBytes / (long)1024;
        long sizeInMB = sizeInKB / (long)1024;
        return sizeInMB <= 1L ? 80 : (sizeInMB <= (long)2 ? 60 : 40);
    }

        public static final Bitmap createScaledBitmap( Bitmap unscaledBitmap, int dstWidth, int dstHeight,  ScalingLogic scalingLogic) {
        /*Intrinsics.checkParameterIsNotNull(unscaledBitmap, "unscaledBitmap");
        Intrinsics.checkParameterIsNotNull(scalingLogic, "scalingLogic");*/
        Rect srcRect = calculateSrcRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Rect dstRect = calculateDstRect(unscaledBitmap.getWidth(), unscaledBitmap.getHeight(), dstWidth, dstHeight, scalingLogic);
        Bitmap scaledBitmap = Bitmap.createBitmap(dstRect.width(), dstRect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.drawBitmap(unscaledBitmap, srcRect, dstRect, new Paint(2));
        //Intrinsics.checkExpressionValueIsNotNull(scaledBitmap, "scaledBitmap");
        return scaledBitmap;
    }

        public static final int calculateSampleSize(int srcWidth, int srcHeight, int dstWidth, int dstHeight,  ScalingLogic scalingLogic) {
        //Intrinsics.checkParameterIsNotNull(scalingLogic, "scalingLogic");
        float srcAspect;
        float dstAspect;
        if (scalingLogic == ScalingLogic.FIT) {
            srcAspect = (float)srcWidth / (float)srcHeight;
            dstAspect = (float)dstWidth / (float)dstHeight;
            return srcAspect > dstAspect ? srcWidth / dstWidth : srcHeight / dstHeight;
        } else {
            srcAspect = (float)srcWidth / (float)srcHeight;
            dstAspect = (float)dstWidth / (float)dstHeight;
            return srcAspect > dstAspect ? srcHeight / dstHeight : srcWidth / dstWidth;
        }
    }


        public static final Rect calculateSrcRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,  ScalingLogic scalingLogic) {
        //Intrinsics.checkParameterIsNotNull(scalingLogic, "scalingLogic");
        if (scalingLogic == ScalingLogic.CROP) {
            float srcAspect = (float)srcWidth / (float)srcHeight;
            float dstAspect = (float)dstWidth / (float)dstHeight;
            Rect var10000;
            int srcRectWidth;
            int srcRectLeft;
            if (srcAspect > dstAspect) {
                srcRectWidth = (int)((float)srcHeight * dstAspect);
                srcRectLeft = (srcWidth - srcRectWidth) / 2;
                var10000 = new Rect(srcRectLeft, 0, srcRectLeft + srcRectWidth, srcHeight);
            } else {
                srcRectWidth = (int)((float)srcWidth / dstAspect);
                srcRectLeft = (srcHeight - srcRectWidth) / 2;
                var10000 = new Rect(0, srcRectLeft, srcWidth, srcRectLeft + srcRectWidth);
            }

            return var10000;
        } else {
            return new Rect(0, 0, srcWidth, srcHeight);
        }
    }


        public static final Rect calculateDstRect(int srcWidth, int srcHeight, int dstWidth, int dstHeight,  ScalingLogic scalingLogic) {
        //Intrinsics.checkParameterIsNotNull(scalingLogic, "scalingLogic");
        Rect var10000;
        if (scalingLogic == ScalingLogic.FIT) {
            float srcAspect = (float)srcWidth / (float)srcHeight;
            float dstAspect = (float)dstWidth / (float)dstHeight;
            var10000 = srcAspect > dstAspect ? new Rect(0, 0, dstWidth, (int)((float)dstWidth / srcAspect)) : new Rect(0, 0, (int)((float)dstHeight * srcAspect), dstHeight);
        } else {
            var10000 = new Rect(0, 0, dstWidth, dstHeight);
        }

        return var10000;
    }


    }
