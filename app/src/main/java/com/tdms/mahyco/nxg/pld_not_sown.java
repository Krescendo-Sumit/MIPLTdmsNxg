package com.tdms.mahyco.nxg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.tdms.mahyco.nxg.utils.AppConstant;
import com.tdms.mahyco.nxg.utils.EncryptDecryptManager;
import com.tdms.mahyco.nxg.utils.FileUtilImage;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class pld_not_sown extends AppCompatActivity {
    databaseHelper databaseHelper1;
    public String userCode, Imagepath1 = "";
    public String flag;
    public TextView TRIL_CODE;
    public Messageclass msclass;
    EditText txtReason;
    RadioButton rbtNotSown, rbtPLD;
    int imageselect;
    Context context;
    File photoFile = null;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    ImageView ivImage;
    private static final String IMAGE_DIRECTORY_NAME = "VISITPHOTO";
    Button btnPhoto, btnSave;
    String imageFileName = "";

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pld_not_sown);

        databaseHelper1 = new databaseHelper(this);
        txtReason = (EditText) findViewById(R.id.txtReason);
        rbtNotSown = (RadioButton) findViewById(R.id.rbtNotSown);
        rbtPLD = (RadioButton) findViewById(R.id.rbtPLD);

        ivImage = (ImageView) findViewById(R.id.ivImage);
        btnPhoto = (Button) findViewById(R.id.btnPhoto);
        btnSave = (Button) findViewById(R.id.btnSave);
        msclass = new Messageclass(this);

        TRIL_CODE = (TextView) findViewById(R.id.txtTrialCode);

        Intent i = getIntent();
        String name = i.getStringExtra("Trail_code");
        String sownStatus = i.getStringExtra(AppConstant.SOWN_STATUS);
        TRIL_CODE.setText(name);

        if (sownStatus.equals("0")) {
            getSupportActionBar().setTitle("Not Sown");
            rbtNotSown.setChecked(true);
            rbtPLD.setVisibility(View.GONE);
        } else {
            getSupportActionBar().setTitle("PLD");
            rbtPLD.setChecked(true);
            rbtNotSown.setVisibility(View.GONE);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Cursor data = databaseHelper1.fetchusercode();
        context = this;
        if (data.getCount() == 0) {
            msclass.showMessage("No Data Available... ");
        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    //userCode = data.getString((data.getColumnIndex("user_code")));
                    String userCodeEncrypt = data.getString((data.getColumnIndex("user_code")));
                    userCode = EncryptDecryptManager.decryptStringData(userCodeEncrypt);
                    Log.d("userCode", "userCode" + userCode);
                } while (data.moveToNext());

            }
            data.close();
        }

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(context, "No permit" +
                            "", Toast.LENGTH_SHORT).show();
                }
                imageselect = 1;
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        captureImage();
                    } else {
                        captureImage2();
                    }
                } catch (Exception ex) {
                    Log.d("Msg", ex.getMessage());
                    msclass.showMessage("Something went wrong, please try again later");
                }
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    String status = "";
                    if (rbtNotSown.isChecked()) {
                        status = rbtNotSown.getText().toString();
                    } else if (rbtPLD.isChecked()) {
                        status = rbtPLD.getText().toString();
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    String entrydate = sdf.format(new Date());
                    boolean isInserted = databaseHelper1.insertPLDNotSown(userCode, TRIL_CODE.getText().toString(), status,
                            txtReason.getText().toString().trim(), entrydate, imageFileName
                            , Imagepath1, "0", "0");
                    if (isInserted) {
                        msclass.showMessageandRedirectToPrevious("Details saved successfully", true, context);
                    }
                }
            }
        });

        fetchAndShowPLDData(TRIL_CODE.getText().toString());

    }


    private void captureImage2() {

        try {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

            if (imageselect == 1) {
                photoFile = createImageFile4();
                if (photoFile != null) {
                    Log.i("Mayank", photoFile.getAbsolutePath());
                    Uri photoURI = Uri.fromFile(photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(cameraIntent, REQUEST_CAMERA);
                }
            }
        } catch (Exception ex) {
            Log.d("Msg", ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
    }

    private void captureImage() {

        try {

            if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.CAMERA}, 0);
            } else {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                    // Create the File where the photo should go
                    try {
                        if (imageselect == 1) {
                            photoFile = createImageFile();

                            if (photoFile != null) {
                                /*Uri photoURI = FileProvider.getUriForFile(context,
                                        "com.newtrial.mahyco.trail",
                                        photoFile);*/
                                Uri photoURI = FileProvider.getUriForFile(context,
                                        "com.newtrail.mahyco.trail" + ".provider", photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, REQUEST_CAMERA);
                            }
                        }

                    } catch (Exception ex) {

                        msclass.showMessage("Something went wrong, please try again later");
                        Log.d("Msg", ex.getMessage());
                    }


                } else {
                }
            }
        } catch (Exception ex) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg", ex.getMessage());
        }
    }

    private File createImageFile4() //  throws IOException
    {
        File mediaFile = null;
        try {
            // External sdcard location
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    IMAGE_DIRECTORY_NAME);
            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null;
                }
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                    Locale.getDefault()).format(new Date());
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } catch (Exception ex) {
            Log.d("Msg", ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
        return mediaFile;
    }

    private File createImageFile() {
        // Create an image file name
        File image = null;
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            imageFileName = "JPEG_" + timeStamp + "_";
            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );

        } catch (Exception ex) {
            Log.d("Msg", ex.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }

        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
            }
        } catch (Exception e) {
            Log.d("Msg", e.getMessage());
            msclass.showMessage("Something went wrong, please try again later");
        }
    }

    private void onCaptureImageResult(Intent data) {


        try {
            if (imageselect == 1) {

                try {
                    /*BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;

                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                    Imagepath1 = photoFile.getAbsolutePath();
                    ivImage.setImageBitmap(myBitmap);*/

                    /*------------------------------Start -----------------------*/
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // options.inJustDecodeBounds = true;
                    options.inSampleSize = 2;
                    Bitmap myBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                    // myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                    // this only get capture photo
                    //************ 30 Dec 2020
                    Date entrydate = new Date();
                    String InTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(entrydate);
                    AppConstant.queryImageUrl = photoFile.getAbsolutePath();
                    AppConstant.imageUri = Uri.fromFile(new File(AppConstant.queryImageUrl));
                    AppConstant.Imagename = this.getClass().getSimpleName() + userCode + String.valueOf(entrydate.getTime());
                    FileUtilImage.compressImageFile(AppConstant.queryImageUrl, AppConstant.imageUri,
                            context, AppConstant.Imagename);
                    // need to set commpress image path
                    Imagepath1 = FileUtilImage.savefilepath;// photoFile.getAbsolutePath();  old ssave
                    ivImage.setImageBitmap(myBitmap);
                    //************** 30 Dec 2020
                    /*------------------------------End -----------------------*/

                } catch (Exception e) {
                    msclass.showMessage("Something went wrong, please try again later");
                    Log.d("Msg", e.getMessage());
                }
            }

        } catch (Exception e) {
            msclass.showMessage("Something went wrong, please try again later");
            Log.d("Msg", e.getMessage());
        }

    }

    private void onSelectFromGalleryResult(Intent data) {


        Bitmap bm = null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(context.getApplicationContext().getContentResolver(), data.getData());
            } catch (Exception e) {
                Log.d("Msg", e.getMessage());
            }
        }

        if (imageselect == 1) {
            ivImage.setImageBitmap(bm);
        }

    }

    private boolean validation() {
        try {
            boolean flag = true;

            if (!rbtPLD.isChecked() && !rbtNotSown.isChecked()) {
                msclass.showMessage("Please check Not sown or PLD option.");
                return false;

            }
            if (txtReason.getText().length() == 0) {
                msclass.showMessage("Please enter reason.");
                return false;

            }

            if (ivImage.getDrawable() == null) {
                msclass.showMessage("Please upload photo");
                return false;
            }


        } catch (Exception ex) {
            //msclass.showMessage("validation Function" + ex.getMessage());
            msclass.showMessage("validation Function failed");
            Log.d("Msg", ex.getMessage());
        }
        return true;
    }

    private void fetchAndShowPLDData(String trialCode) {
        Cursor data = databaseHelper1.fetchPLDDATA(trialCode);

        if (data.getCount() == 0) {

        } else {
            data.moveToFirst();
            if (data != null) {
                do {
                    String remark = data.getString((data.getColumnIndex("remark"))).toString();
                    Log.d("remark", remark);
                    txtReason.setText(remark);
                    txtReason.setEnabled(false);
                    btnPhoto.setEnabled(false);
                    btnSave.setEnabled(false);
                } while (data.moveToNext());

            }
            data.close();
        }

    }


}
