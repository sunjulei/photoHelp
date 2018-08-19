package com.sunlee.photoaide;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Administrator on 2018/8/11 0011.
 */

public class AboutActivity extends Main2Activity {
    private Button subBtn;
    private EditText subText;
    private EditText name;
    private String TAG = "ssss";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        subBtn = findViewById(R.id.subBtn);
        subText = findViewById(R.id.subText);
        name = findViewById(R.id.name);

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (subText.getText().toString().trim().length() <= 0) {
                    Toast.makeText(AboutActivity.this, "建议不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                final Thread thread = new Thread(new Runnable() {
                    Connection conn = null;

                    @Override
                    public void run() {
                            // 2.设置好IP/端口/数据库名/用户名/密码等必要的连接信息
                            String ip = "47.106.172.48";
                            int port = 3306;
                            String dbName = "photoDB";
                            String url = "jdbc:mysql://" + ip + ":" + port
                                    + "/" + dbName+"?useUnicode\\=true&characterEncoding\\=UTF-8"; // 构建连接mysql的字符串
                            String user = "root";
                            String password = "123456";

                            // 3.连接JDBC
                            try {
                                conn = DriverManager.getConnection(url, user, password);
                                PreparedStatement psql;

                                psql = conn.prepareStatement("INSERT INTO `photoDB`.`advice` (`advice`, `conn`) VALUES (?, ?);");
                                psql.setString(1, subText.getText().toString());
                                psql.setString(2, name.getText().toString());
                                psql.executeUpdate();
//                        Log.i(TAG, "远程连接成功!");

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AboutActivity.this, "提交成功，感谢你的建议", Toast.LENGTH_SHORT).show();
                                    }
                                });


                                return;
                            } catch (SQLException e) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(AboutActivity.this, "发送失败，请稍后重试", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } finally {
                                if (conn != null) {
                                    try {
                                        conn.close();
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                });
                thread.start();
            }

        });

    }

    //菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.righttopmenu, menu);
        return true;

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.quit:
                // 1. 通过Context获取ActivityManager
                ActivityManager activityManager = (ActivityManager) this.getApplicationContext().getSystemService(ACTIVITY_SERVICE);

                // 2. 通过ActivityManager获取任务栈
                List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();

                // 3. 逐个关闭Activity
                for (ActivityManager.AppTask appTask : appTaskList) {
                    appTask.finishAndRemoveTask();
                }

            case R.id.help:
                Intent intent = new Intent(AboutActivity.this, HelpActivity.class);
                startActivity(intent);
                finish();
            case R.id.about:

            default:
                return false;
        }
    }
}
