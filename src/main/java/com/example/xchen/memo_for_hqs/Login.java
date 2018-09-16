package com.example.xchen.memo_for_hqs;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import java.io.File;

public class Login extends ActionBarActivity {


    //======================================
    // 版本号、handler（用于在子线程中向外传递消息）
    //======================================
    public static String cur_version;
    public static Handler hl = new Handler(); // 不管哪个线程，只用这个handle就行

    //======================================
    // 声明控件，放在里面也行
    //======================================
    public static EditText editText1;
    public static EditText editText2;
    public static EditText editText3;
    public static ProgressBar pb;
    public static SQLiteDatabase db;

    //======================================
    // 3个清行按钮
    //======================================
    public static ImageButton imbt1;
    public static ImageButton imbt2;
    public static ImageButton imbt3;
    public static ImageButton imbt_update; // app更新按钮
    public static ImageButton imbt_clear; // 数据库清空按钮
    public static Switch switch_clear;
    public static ImageButton imbt_reload; // 数据库还原按钮
    public static Switch switch_reload;

    //======================================
    // 下载地址
    //======================================
    public static String url_tips = "http://blog.sina.com.cn/s/blog_612ec26f0102we3w.html";
    public static String url_ver = "https://raw.githubusercontent.com/CXCellTrack/" +
            "Memo_For_HQS/master/build.gradle";

    //======================================
    //
    //======================================
    // 目录地址
    public static String sdcard_path = android.os.Environment.
            getExternalStorageDirectory().getAbsolutePath(); // 得到外部存储卡的数据库的路径名
    public static String app_name = null;
    public static String app_dir = null;
    public static String app_fullpath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 使用getResources获取资源
        app_name = getResources().getString(R.string.app_name);
        app_dir = sdcard_path + "/" + app_name;

        //======================================
        // 获取当前的版本信息（在project structrue中设置）
        //======================================
        PackageInfo package_info = null;
        try {
            package_info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        cur_version = package_info.versionName;


        //======================================
        // 建立目录
        //======================================
        File rootfile = new File(app_dir);
        if (!rootfile.exists())
            rootfile.mkdir();


        //======================================
        // 控件绑定
        //======================================
        imbt1 = (ImageButton) findViewById(R.id.imageButton1);
        imbt2 = (ImageButton) findViewById(R.id.imageButton2);
        imbt3 = (ImageButton) findViewById(R.id.imageButton3);
        imbt_update = (ImageButton) findViewById(R.id.imageButton_dog);
        imbt_clear = (ImageButton) findViewById(R.id.imageButton_clear);
        switch_clear = (Switch) findViewById(R.id.switch_clear);
        imbt_reload = (ImageButton) findViewById(R.id.imageButton_reload);
        switch_reload = (Switch) findViewById(R.id.switch_reload);

        editText1 = (EditText) findViewById(R.id.editText1);
        editText2 = (EditText) findViewById(R.id.editText2);
        editText3 = (EditText) findViewById(R.id.editText3);
        editText1.setText("");
        editText2.setText("");
        editText3.setText("");
        pb = (ProgressBar) findViewById(R.id.progressBar);
        pb.setVisibility(View.INVISIBLE);
        // 初始数据库
        db = createDB();


        //======================================
        // 使用说明处的文字显示
        //======================================
        TextView tv_help = (TextView) findViewById(R.id.textView_help);
        tv_help.setText(String.format(
                "使用说明: \n" +
                        "    1) 查询: 输入[帐号种类]，如'微博'，即可查看微博帐号密码信息\n" +
                        "    2) 记录: 输入[帐号类型][帐号(可省略)][密码]进行记录\n" +
                        "    3) 查看全部: 查看已经记录的全部信息\n\n" +
                        "@App  %s\n" +
                        "@Date  2016.10.1\n" +
                        "@Author  C.X.", app_name
        ));


        //======================================
        // imbt: 清空数据库
        //======================================
        imbt_clear.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_click.click_bt_clear(Login.this);
            }
        });


        //======================================
        // imbt: 还原数据库
        //======================================
        imbt_reload.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_click.click_bt_reload(Login.this); // 更新版本
            }
        });


        //======================================
        // imbt: app更新
        //======================================
        imbt_update.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_click.click_imbt_update(Login.this); // 更新版本
            }
        });


        //======================================
        // imbt: 清除输入
        //======================================
        imbt1.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText1.setText("");
            }
        });
        imbt2.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText2.setText("");
            }
        });
        imbt3.setOnClickListener(new ImageButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText3.setText("");
            }
        });

        //======================================
        // button: 查询
        //======================================
        Button button_get = (Button) findViewById(R.id.button_get);
        button_get.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_click.click_bt_get(); // 通过数据库进行查询
            }

        });

        //======================================
        // button: 存入
        //======================================
        Button button_set = (Button) findViewById(R.id.button_set);
        button_set.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_click.click_bt_set(Login.this);
            }
        });

        //======================================
        // button: 查看全部
        //======================================
        Button button_see = (Button) findViewById(R.id.button_see);
        button_see.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_click.click_bt_see(Login.this);
            }
        });

        //======================================
        // button: 删除单条记录
        //======================================
        Button button_delete = (Button) findViewById(R.id.button_delete);
        button_delete.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_click.click_bt_delete(Login.this);
            }
        });


        //======================================
        // button：清空全部记录（已将功能转移到imbt_clear中）
        //======================================
        Button button_clear = (Button) findViewById(R.id.button_none);


        //======================================
        // button: 获取每日话语
        //======================================
        Button button_tips = (Button) findViewById(R.id.button_tips);
        button_tips.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_click.click_bt_tips(Login.this);
            }
        });

    }


    //======================================//
    // 自定义函数
    //======================================//
    // 创建数据库函数
    public SQLiteDatabase createDB(){
        DBhelper dbhelper = new DBhelper(Login.this);
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        return db;
    }


    //======================================
    // 以下是原先自带的函数
    //======================================
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
