package com.example.xchen.memo_for_hqs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ProgressBar;

import java.io.IOException;

/**
 * Created by xchen on 2016/10/1.
 */
public class Act_handler {
    public static void set_visiable(final ProgressBar pb) {
        // 子线程内通过handle更新界面，设置pb可见
        Login.hl.post(new Runnable() {
            public void run() {
                pb.setVisibility(View.VISIBLE);
            }
        });
    }

    public static void set_invisiable(final ProgressBar pb) {
        // 子线程内通过handle更新界面，设置pb不可见
        Login.hl.post(new Runnable() {
            public void run () {
                pb.setVisibility(View.INVISIBLE);
            }
        });
    }

    public static void show_error(final Activity activity, final String msg){
        // 报连接错误
        Login.hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("错误");
                ab.setMessage(msg);
                ab.setIcon(android.R.drawable.ic_dialog_alert);
                ab.show();
            }
        });
    }

    public static void show_tips(final Activity activity, final String text){
        // 显示网页上的msg
        Login.hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("每日话语");
                ab.setMessage("from CX:\n      " + text);
                ab.setIcon(android.R.drawable.ic_dialog_email);
                ab.show();
            }
        });
    }

    public static void show_version(final Activity activity, final String text,
                                    final String download_path){
        // 显示网页上的msg
        Login.hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("版本信息");
                ab.setMessage(text);
                ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                });
                ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        // 下载新版本
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Button_click.__download_apk(activity, download_path);
                                } catch (IOException e) {
                                    Act_handler.show_error(activity, "下载失败");
                                }
                            }
                        }).start();
                    }
                });
                ab.setIcon(android.R.drawable.ic_dialog_info).show();
            }
        });
    }
}
