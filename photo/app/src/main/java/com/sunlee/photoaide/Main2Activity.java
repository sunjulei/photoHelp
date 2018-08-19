package com.sunlee.photoaide;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Main2Activity extends AppCompatActivity {


    public static final int TAKE_PHOTO = 1;

    private List<String> list = new ArrayList<String>();//创建一个String类型的数组列表。
    private TextView myTextView;
    private Spinner mySpinner;
    private ArrayAdapter<String> adapter;

    private ImageView picture;
    private Button savePhoto;
    private Button takePhoto;
    private Button btn;
    private EditText name;
    private TextView luj;
    private Spinner geshi;
    private Uri imageUri;
    Bitmap bitmap = null;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        takePhoto = (Button) findViewById(R.id.take_photo);
        savePhoto = (Button)findViewById(R.id.save_photo);
        picture = findViewById(R.id.picture);
        name = findViewById(R.id.name);
        btn = findViewById(R.id.btn);
        luj = findViewById(R.id.luj);
        geshi = findViewById(R.id.geshi);//String selectText = geshi.getSelectedItem().toString().得到文本

//        verifyStoragePermissions(this);//判断有没有权限

        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (luj.getText().toString().trim().length() <= 0||luj.getText().toString()=="未选择图片存储路径") {
                    Toast.makeText(Main2Activity.this, "图片路径不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (name.getText().toString().trim().length() <= 0) {
                    Toast.makeText(Main2Activity.this, "图片名称不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }

                File outputImage = new File(luj.getText()+"/"+name.getText()+"."+geshi.getSelectedItem());
                if (outputImage.exists()) {
                    outputImage.delete();
//                        Log.i("sunjulei","outputImage.exists()");
                }
//                    Log.i("sunjulei","NO outputImage.exists()"+luj.getText()+"/"+name.getText());
//                    outputImage.createNewFile();
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(Main2Activity.this,
                            "com.example.cameraalbumtest.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);

            }
        });

        savePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bitmap != null) {

                    saveImageToGallery(getApplicationContext(), bitmap);
                    Toast.makeText(Main2Activity.this,
                            "已保存至："+luj.getText()+"/" + name.getText() + "." + geshi.getSelectedItem().toString()
                            , Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Main2Activity.this, "请先拍照", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //如果没有权限,申请权限
                if (isGrantExternalRW(Main2Activity.this)) {//
                    Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                    startActivity(intent);

                    refFilePath();

                } else {
                    Toast.makeText(Main2Activity.this, "请检查是否开启读写权限", Toast.LENGTH_LONG).show();
                }


            }
        });


        luj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refFilePath();
                Toast.makeText(Main2Activity.this,"刷新成功",Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        picture.setImageBitmap(bitmap);


                        File outputImage = new File(luj.getText()+"/"+name.getText()+"."+geshi.getSelectedItem());
                        if (outputImage.exists()) {
                            outputImage.delete();
//                        Log.i("sunjulei","outputImage.exists()");
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }
    }

    // 首先保存图片
    public boolean saveImageToGallery(Context context, Bitmap bmp) {

        String storePath = luj.getText()+"/";
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = name.getText() + "." + geshi.getSelectedItem().toString();
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //通过io流的方式来压缩保存图片
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            fos.flush();
            fos.close();

            //把文件插入到系统图库
            //MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);

            //保存图片后发送广播通知更新数据库
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    //判断有无权限
//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }

    public void refFilePath() {
        SharedPreferences sp = getSharedPreferences("config", Context.MODE_PRIVATE);
        String in = sp.getString("path", "未选择图片存储路径");
        luj.setText(in);
    }


    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.righttopmenu, menu);  //初始化加载菜单项
//        menu.add(1, Menu.FIRST, 1, "Change Site ID");   //四个参数，groupid, itemid, orderid, title
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.quit:
                System.exit(0);


            case R.id.help:
                Intent intent = new Intent(Main2Activity.this, HelpActivity.class);
                startActivity(intent);
                return true;

            case R.id.about:
                Intent intentabo = new Intent(Main2Activity.this, AboutActivity.class);
                startActivity(intentabo);
            default:
                return false;
        }
    }

    //更新路径页面
    @Override
    protected void onRestart() {
        super.onRestart();
        refFilePath();
    }
}


