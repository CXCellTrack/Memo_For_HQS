package com.example.xchen.memo_for_hqs;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;


public class Show_all_info extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_info);
        //======================================
        // 显示当前所有的帐号信息
        //======================================
        TextView tv = (TextView) findViewById(R.id.textView_all_info);
        ArrayList<String> data = this.getIntent().getStringArrayListExtra("data");
        StringBuilder sb = new StringBuilder();
        for(String x: data){
            sb.append(x +'\n');
        }
//        tv.setTextSize(20);
        tv.setHorizontallyScrolling(true);
        tv.setFocusable(true);
        // 设置滚动条启动
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        tv.setText(sb.toString());
    }


    //======================================
    // 按退出键回到主界面
    //======================================
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent it = new Intent(this, Login.class);
            /* 启动一个新的Activity */
            this.startActivity(it);
            /* 关闭当前的Activity */
            this.finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.fade_out);
        }
        return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_all_info, menu);
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


    //======================================
    // 将全角转换为全角
    //======================================
    public static String toDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i< c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }if (c[i]> 65280&& c[i]< 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    //======================================
    // 将半角转换为全角，方便对齐
    //======================================
    public static String toSBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == ' ') {
                c[i] = '\u3000';
            } else if (c[i] < '\177') {
                c[i] = (char) (c[i] + 65248);

            }
        }
        return new String(c);
    }
}
