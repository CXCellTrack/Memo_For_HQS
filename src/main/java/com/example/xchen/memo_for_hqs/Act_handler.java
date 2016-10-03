package com.example.xchen.memo_for_hqs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import org.jsoup.nodes.Element;

/**
 * Created by xchen on 2016/10/1.
 */
public class Act_handler {
    public static void set_visiable(final ProgressBar pb, Handler hl) {
        // 子线程内通过handle更新界面，设置pb可见
        hl.post(new Runnable() {
            public void run () {
                pb.setVisibility(View.VISIBLE);
            }
        });
    }

    public static void set_invisiable(final ProgressBar pb, Handler hl) {
        // 子线程内通过handle更新界面，设置pb不可见
        hl.post(new Runnable() {
            public void run () {
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    public static void connect_error(final Activity activity, Handler hl){
        // 报连接错误
        hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("错误");
                ab.setMessage("网络连接失败！");
                ab.setIcon(android.R.drawable.ic_dialog_alert);
                ab.show();
            }
        });
    }

    public static void show_tips(final Activity activity, final String text, Handler hl){
        // 显示网页上的msg
        hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("每日话语");
                ab.setMessage("from CX:\n      " + text);
                ab.setIcon(android.R.drawable.ic_dialog_email);
                ab.show();
            }
        });
    }

    public static void show_version(final Activity activity, final String text, Handler hl){
        // 显示网页上的msg
        hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("版本信息");
                ab.setMessage(text);
                ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        // todo 下载新版本


                    }
                }).setIcon(android.R.drawable.ic_dialog_info);
                ab.show();
            }
        });
    }



}
