package com.sunlee.photoaide;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

/**
 * Created by Administrator on 2018/8/7 0007.
 */

public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
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
                        ActivityManager activityManager = (ActivityManager) this.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);

                // 2. 通过ActivityManager获取任务栈
                List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();

                // 3. 逐个关闭Activity
                for (ActivityManager.AppTask appTask : appTaskList) {
                    appTask.finishAndRemoveTask();
                }

            case R.id.help:
                return true;
            case R.id.about:
                Intent intentabo = new Intent(HelpActivity.this, AboutActivity.class);
                startActivity(intentabo);
                finish();

            default:
                return false;
        }
    }
}
