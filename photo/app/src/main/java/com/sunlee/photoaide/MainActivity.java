package com.sunlee.photoaide;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends Activity implements MyRecyclerViewItem {


    TextView luj;
    ListView listView;
    ImageView addFile;

    FileAdapter fileAdapter;

    File[] data;

    RecyclerView mRecyclerView;

    private List<String> filePathList = new ArrayList<>();
    private HomeAdapter homeAdapter;

    private LinearLayout ll_no_data;

    private TextView tv_determine;
    private String filePath;
    private String nowFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luj = findViewById(R.id.luj);
        setView();

        setOnClickListener();

        // 初始化
        initView();

    }

    private void setOnClickListener() {
        tv_determine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileAdapter != null) {
                    filePath = fileAdapter.getSelectedDirestoryPath();

                    if (filePath != null) {
                        SharedPreferences.Editor edit = getSharedPreferences("config", Context.MODE_PRIVATE).edit();
                        edit.putString("path", filePath);
                        edit.commit();
                    }

                    if ("null".equals(filePath)) {
                        Toast.makeText(MainActivity.this, "请先选中文件夹", Toast.LENGTH_LONG).show();
                    } else {
                        finish();
                    }

                }
            }
        });
    }


    private void setView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.id_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        ll_no_data = (LinearLayout) findViewById(R.id.ll_no_data);
        tv_determine = (TextView) findViewById(R.id.tv_determine);
        Log.e("hxl", "filePathList===" + filePathList.size());
    }

    private void setAdapter() {
        homeAdapter = new HomeAdapter(this, filePathList);
        mRecyclerView.setAdapter(homeAdapter);
        this.homeAdapter.setOnItemClickListener(this);
    }

    private List<String> mListPath = new ArrayList<>();

    private void traversalFile() {
        for (int i = 0; i < data.length; i++) {
            mListPath.add(data[i].getAbsolutePath());
        }

        Collections.sort(mListPath);
        for (int i = 0; i < data.length; i++) {
            data[i] = new File(mListPath.get(i));
        }
    }

    /**
     * 文件夹文件排序
     *
     * @param files
     * @return
     */
    public static File[] sortFile(File[] files) {
        List<File> listfile = Arrays.asList(files);
        Collections.sort(listfile, new CustomComparator());   //按照指定的规则进行一个排序
        File[] array = listfile.toArray(new File[listfile.size()]);
        return array;
    }


    private void initView() {
        listView = findViewById(R.id.listView);
        addFile = findViewById(R.id.addFile);

        // 获得外部存储的路径
        File path = Environment.getExternalStorageDirectory();
        data = path.listFiles();
        filePathList.add(path.getAbsolutePath());
        setAdapter();
        data = sortFile(data);
        fileAdapter = new FileAdapter(this, data);
        listView.setAdapter(fileAdapter);

        // 注册监听器
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(
                            AdapterView<?> parent,
                            View view,
                            int position,
                            long id) {
                        File f = fileAdapter.getItem(position);
                        // 如果是目录 进入目录
                        if (f.isDirectory()) {
                            if (isDirectory(f)) {
                                filePathList.add(f.getAbsolutePath());
                                if (homeAdapter != null) {
                                    homeAdapter.notifyDataSetChanged();
                                    mRecyclerView.smoothScrollToPosition(filePathList.size() - 1);
                                }
                                if (fileAdapter != null) {
                                    fileAdapter.setAllData(sortFile(f.listFiles()));
                                }
                            } else {  //当文件夹没有文件也没有文件夹时
                                filePathList.add(f.getAbsolutePath());
                                ll_no_data.setVisibility(View.VISIBLE);
                                if (homeAdapter != null) {
                                    homeAdapter.notifyDataSetChanged();
                                    mRecyclerView.smoothScrollToPosition(filePathList.size() - 1);
                                }
                                Log.e("hxl", "====您选中的是空文件夹====");
                            }


                        } else {
                            Log.e("hxl", "====您选中的不是文件夹====");
                        }
                        // Toast
                        Log.e("hxl", "path=========" + f.getAbsolutePath());
//                        Toast.makeText(MainActivity.this, f.getAbsolutePath(), Toast.LENGTH_SHORT).show();

                        nowFilePath = f.getAbsolutePath();
                    }
                }
        );


        //增加文件夹
        addFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final EditText et;
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("输入文件夹名称")
                        .setIcon(R.drawable.addicon)
                        .setView(et = new EditText(MainActivity.this))
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                int index = nowFilePath.lastIndexOf("/storage/emulated/0");
                                String substring = nowFilePath.substring(index);
//                                Log.i("sunjulei", substring + "/" + et.getText());
                                File destDir = new File(substring + "/" + et.getText());
                                if (!destDir.exists()) {
                                    destDir.mkdirs();
                                    Toast.makeText(MainActivity.this, "创建成功", Toast.LENGTH_SHORT).show();

                                    //----------------------这该死的温柔
                                    File f = fileAdapter.getItem(0);
                                    // 如果是目录 进入目录
                                    if (f.isDirectory()) {
                                        if (isDirectory(f)) {
                                            filePathList.add(f.getAbsolutePath());
                                            if (homeAdapter != null) {
                                                homeAdapter.notifyDataSetChanged();
                                            }
                                            if (fileAdapter != null) {
                                                fileAdapter.setAllData(sortFile(f.listFiles()));
                                            }
                                        } else {  //当文件夹没有文件也没有文件夹时
                                            filePathList.add(f.getAbsolutePath());
                                            ll_no_data.setVisibility(View.VISIBLE);
                                            if (homeAdapter != null) {
                                                homeAdapter.notifyDataSetChanged();
                                                mRecyclerView.smoothScrollToPosition(filePathList.size() - 1);
                                            }
                                        }
                                    }
                                    try {
                                        Runtime runtime = Runtime.getRuntime();
                                        runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
                                    } catch (IOException e) {
                                        Log.e("Exception when doBack", e.toString());
                                    }
                                    //----------------------GG了
                                } else {
                                    Toast.makeText(MainActivity.this, "创建失败，该文件夹已存在", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("cancel", null)
                        .show();
            }
        });
    }


    /**
     * 监听Back键按下事件,方法
     * 注意:
     * 返回值表示:是否能完全处理该事件
     * 在此处返回false,所以会继续传播该事件.
     * 在具体项目中此处的返回值视情况而定.
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            ll_no_data.setVisibility(View.GONE);//隐藏布局
            if (filePathList.size() > 1) {
                filePathList.remove(filePathList.size() - 1);
                nowFilePath = "";
                if (homeAdapter != null) {
                    homeAdapter.notifyDataSetChanged();
                }
                if (fileAdapter != null) {
                    fileAdapter.setAllData(sortFile(new File(filePathList.get(filePathList.size() - 1)).listFiles()));
                }

            } else {
                System.out.println("按下了back键   onKeyDown()");
                finish();
            }
            //返回当前路径

            for (int i = 0; i < filePathList.size(); i++) {
                nowFilePath += filePathList.get(i);
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }


    /**
     * 判断子目录是否存在文件或者是文件夹
     */
    File[] mFileList;
    private boolean isDirectory;

    public boolean isDirectory(File file) {
        isDirectory = false;
        File[] fileArray = file.listFiles();
        if (fileArray.length > 0) {
            isDirectory = true;
        }
        return isDirectory;
    }

    /**
     * 监听RecyclerView
     *
     * @param view
     * @param postion
     */
    @Override
    public void onItemClick(View view, int postion) {
        ll_no_data.setVisibility(View.GONE);//隐藏布局
        Log.e("hxl", "filePathList.get(postion)===========" + filePathList.get(postion));
        for (int i = filePathList.size() - 1; filePathList.size() - 1 > postion; i--) {
            filePathList.remove(i);
        }
        if (homeAdapter != null) {
            homeAdapter.notifyDataSetChanged();
            mRecyclerView.smoothScrollToPosition(filePathList.size() - 1);
            if (fileAdapter != null) {
                fileAdapter.setAllData(sortFile(new File(filePathList.get(postion)).listFiles()));
            }
        }

    }
}

