package com.goyo.in;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.goyo.in.Utils.FileUtils;
import com.goyo.in.Utils.FileUtilsCompressImage;
import com.goyo.in.Utils.Preferences;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class AddMedicineImage extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_upload;
    private TextView actionbar_title;
    private Button btn_upload;
    private static int RESULT_PROFILE_IMG = 0;
    private static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    Uri selectedImage;
    ProgressDialog dialog;
    private String filePathProfile = "";
    private EditText et_feedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_add_medicine_image);

        initUI();
    }

    private void initUI() {
        img_upload = (ImageView) findViewById(R.id.img_upload);
        actionbar_title = (TextView) findViewById(R.id.actionbar_title);
        btn_upload = (Button) findViewById(R.id.btn_upload);
        et_feedback = (EditText) findViewById(R.id.et_feedback);


        btn_upload.setOnClickListener(this);
        img_upload.setOnClickListener(this);

        actionbar_title.setText("Upload Prescription");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_upload:
                UplaodValidation();
                break;

            case R.id.img_upload:
                pickImageFromGallery();
                break;


        }
    }

    private void UplaodValidation() {
        new upload_image_asyn().execute();
    }


    private void pickImageFromGallery() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE, getString(R.string.permission_read_storage_rationale), REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent galleryIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, RESULT_PROFILE_IMG);

        }

    }

    private class upload_image_asyn extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            dialog = ProgressDialog.show(AddMedicineImage.this, "", "", true);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... url) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        executeMultipartPost();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);

            dialog.dismiss();
        }
    }

    private void executeMultipartPost() throws Exception {

        if (filePathProfile.equals("")) {
        } else {
            File file = new File(filePathProfile);
            Ion.with(this)
                    //.load(Constant.saveMedicineInfo)
                    .load("http://192.168.1.103:8082/goyoapi/saveMedicineInfo")
                    .setMultipartParameter("mdcdate", java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()))
                    .setMultipartParameter("uid", Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID))
                    .setMultipartParameter("cuid", Preferences.getValue_String(getApplicationContext(), Preferences.V_ID))
                    .setMultipartParameter("remark", et_feedback.getText().toString())
                    .setMultipartFile("uploadimg", file)
                    .asJsonObject()
                    .setCallback(new FutureCallback<JsonObject>() {
                        @Override
                        public void onCompleted(Exception e, JsonObject result) {
                            // do stuff with the result or error
                            try {

                                JsonObject o = result.get("data").getAsJsonArray().get(0).getAsJsonObject().get("funsave_medicineinfo").getAsJsonObject();
                                Toast.makeText(getApplicationContext(), o.get("msg").toString(), Toast.LENGTH_SHORT).show();
                                onBackPressed();

                            } catch (Exception ea) {
                                ea.printStackTrace();
                            }
                        }
                    });
        }

//        try {
//            HttpClient httpClient = new DefaultHttpClient();
//            HttpPost postRequest = new HttpPost(Constant.saveMedicineInfo);
//            MultipartEntity reqEntity = new MultipartEntity(
//                    HttpMultipartMode.BROWSER_COMPATIBLE);
//            reqEntity.addPart("mdcdate", new StringBody(java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime())));
//            reqEntity.addPart("uid", new StringBody(Preferences.getValue_String(getApplicationContext(), Preferences.USER_ID)));
//            reqEntity.addPart("cuid", new StringBody(Preferences.getValue_String(getApplicationContext(), Preferences.V_ID)));
//            reqEntity.addPart("remark", new StringBody(et_feedback.getText().toString()));
//
//            if (filePathProfile.equals("")) {
//            } else {
//                File file = new File(filePathProfile);
//                reqEntity.addPart("mdcfile", new FileBody(file));
//            }
//            postRequest.setEntity(reqEntity);
//            HttpResponse response = httpClient.execute(postRequest);
//            BufferedReader reader = new BufferedReader(new InputStreamReader(
//                    response.getEntity().getContent(), "UTF-8"));
//            String sResponse;
//            StringBuilder s = new StringBuilder();
//            while ((sResponse = reader.readLine()) != null) {
//                s = s.append(sResponse);
//            }
//            System.out.println("Response Register: " + s.toString());
//            final JSONObject jsonObject = new JSONObject(s.toString());
//            final String success = jsonObject.optString("status").toString();
//
//            JSONObject data = (JSONObject) jsonObject.getJSONArray("data").getJSONObject(0).get("funsave_medicineinfo");
//            final String messaage = data.optString("msg").toString();
//            if (success == "200") {
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), messaage, Toast.LENGTH_SHORT).show();
//                        onBackPressed();
//                    }
//                });
//
//            } else {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getApplicationContext(), messaage, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        } catch (Exception e) {
//            Log.d("exception", "     " + e);
//            e.printStackTrace();
//        }
    }

    private void requestPermission(final String permission, String rationale, final int requestCode) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.permission_title_rationale));
            builder.setMessage(rationale);
            builder.setPositiveButton(getString(R.string.label_ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ActivityCompat.requestPermissions(AddMedicineImage.this, new String[]{permission}, requestCode);
                }
            });
            builder.setNegativeButton(getString(R.string.label_cancel), null);
            builder.show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_PROFILE_IMG && resultCode == RESULT_OK
                    && null != data) {

                selectedImage = data.getData();
                String file = FileUtils.getPath(this, selectedImage);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                filePathProfile = saveBitmapToLocal(bitmap, AddMedicineImage.this);
                img_upload.setImageBitmap(BitmapFactory
                        .decodeFile(filePathProfile));
            }

        } catch (Exception e) {
            Toast toast = Toast.makeText(getApplicationContext(), "Something went wrong", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public String saveBitmapToLocal(Bitmap bm, Context context) {
        String path = null;
        try {
            File file = FileUtilsCompressImage.getInstance(context).createTempFile("IMG_", ".jpg");
            //File file = new File(context.getCacheDir(), imgName + ".JPEG");
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 20, fos);
            fos.flush();
            fos.close();
            path = file.getAbsolutePath();
            Log.e("Tag", "Path = " + path);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return path;
    }

    //action bar menu button click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

}
