package com.example.xchen.memo_for_hqs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
                ab.setMessage("FROM C.X.:\n        " + text);
                ab.setIcon(android.R.drawable.ic_dialog_email);
                ab.show();
            }
        });
    }

    public static void show_msg(final Activity activity, final String text){
        // 显示网页上的msg
        Login.hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("提示");
                ab.setMessage(text);
                ab.setIcon(android.R.drawable.ic_dialog_info);
                ab.show();
            }
        });
    }

    public static void check_version(final Activity activity, final String text,
                                     final String download_path){
        // 显示版本并下载
        Login.hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("版本信息");
                ab.setMessage(text);
                ab.setIcon(android.R.drawable.ic_dialog_info);
                ab.setNegativeButton("取消", null);
                ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {

                        // 建立下载进度条
                        final ProgressDialog pd = new ProgressDialog(activity);
                        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                        pd.setCanceledOnTouchOutside(false);
                        pd.setIcon(android.R.drawable.ic_menu_send);

                        // 下载新版本（另开线程）
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                File apk = null;
                                try {
                                    // 下载
                                    apk = __download_apk(activity, download_path, pd);
                                } catch (IOException e) {
                                    Act_handler.show_error(activity, "下载失败");
                                    return;
                                }
                                // 安装
                                __install_apk(activity, apk);
                            }
                        }).start();
                    }
                });
                ab.show();

            }
        });
    }

    //======================================
    // 从github上下载apk
    //======================================
    public static File __download_apk(final Activity activity, final String download_path,
                                      final ProgressDialog pd) throws IOException {
        final File apk = new File(Login.app_fullpath);
//        if (apk.exists()){
//            Act_handler.show_msg(activity, "文件已经存在！");
//            return;
//        }

        // 开始进行连接
        URL url = new URL(download_path);
        URLConnection conn = url.openConnection();
        // 以流的形式进行下载
        InputStream in = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(Login.app_fullpath);
        // 计算文件大小
        final int sz = conn.getContentLength()/1024;

        // 更新ProgressDialog的设置
        Login.hl.post(new Runnable() {
            public void run() {
                pd.setMessage(String.format("连接成功！目标文件大小 %d K\n正在下载...", sz));
                pd.setMax(sz);
                pd.show();
            }
        });

        //======================================
        // 开始下载
        //======================================
        byte[] buffer = new byte[1024]; // 每次下载10k
        int len;
        int total = 0;
        while ((len = in.read(buffer)) > 0) {
            fos.write(buffer, 0, len);
            total += len;
            final int total_copy = total/1024;
            // 使用handle_pd更新界面（注意本函数在hl内部）
            Login.hl.post(new Runnable() {
                public void run() {
                    pd.setProgress(total_copy);
                }
            });
        }
        fos.close();
        in.close();
        // 关闭进度条
        pd.dismiss();
        return apk;
    }

    //======================================
    // 安装APK
    //======================================
    public static void __install_apk(final Activity activity, final File apk){
        Login.hl.post(new Runnable() {
            public void run() {
                AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                ab.setTitle("提示");
                ab.setMessage("是否立刻安装 " + Login.app_fullpath + " ？");
                ab.setIcon(android.R.drawable.ic_menu_help);
                ab.setNegativeButton("稍后", null);
                ab.setPositiveButton("好的", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        Intent installer = new Intent();
                        //执行动作
                        installer.setAction(Intent.ACTION_VIEW);
                        //执行的数据类型
                        installer.setDataAndType(Uri.fromFile(apk), "application/vnd.android.package-archive");
                        // 启动一个新的Activity
                        activity.startActivity(installer);
                        // 关闭当前的Activity
                        activity.finish();
                    }
                });
                ab.show();
            }
        });
    }

}
