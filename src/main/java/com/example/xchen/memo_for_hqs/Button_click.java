package com.example.xchen.memo_for_hqs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xml.sax.SAXException;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by xchen on 2016/6/10.
 */
public class Button_click {

    //======================================
    // 按键1的响应事件（查询）
    //======================================
    public static void click_bt_get() {
        String name = Login.editText1.getText().toString();
        if (name.equals(""))
            return;
        String[] selectionArgs = new String[]{"%" + name + "%"};
        Cursor cs = null;
        // 使用like查询必须这么写，把%%写到selectionArgs中
        cs = Login.db.rawQuery("select * from memo where name like ?", selectionArgs);
        if (cs.moveToFirst()) {
            String res = cs.getString(cs.getColumnIndex("name"));
            Login.editText1.setText(res);
            res = cs.getString(cs.getColumnIndex("id"));
            Login.editText2.setText(res);
            res = cs.getString(cs.getColumnIndex("password"));
            Login.editText3.setText(res);
        } else {
            Login.editText2.setText("not found!");
            Login.editText3.setText("not found!");
        }
        cs.close();
    }

    //======================================
    // 按键2的响应事件（操作数据库进行记录）
    //======================================
    public static void click_bt_set(final Activity activity) {
        final String name = Login.editText1.getText().toString();
        final String id = Login.editText2.getText().toString();
        final String ps = Login.editText3.getText().toString();
        if (name.equals("") || ps.equals(""))
            return;
        // 查询输入与之前是否重复
        Cursor cs = Login.db.rawQuery("select * from memo where name like ?", new String[]{"%" + name + "%"});
        if (cs.moveToFirst()) {
            // 处理重复的方法
            __process_conflict(cs, name, id, ps, activity);
        } else {
            ContentValues cv = new ContentValues();
            cv.put("name", name);
            cv.put("id", id);
            cv.put("password", ps);
            Login.db.insert("memo", null, cv);
            AlertDialog.Builder ab = new AlertDialog.Builder(activity);
            ab.setMessage("记录成功！");
            ab.show();
        }
        cs.close();
    }

    //======================================
    // 按键3的响应事件（查看全部记录）
    //======================================
    public static void click_bt_see(final Activity activity) {
        Intent intent = new Intent();
        Bundle bd = new Bundle();
        //======================================
        // 查看数据库获得全部的帐号信息
        //======================================
        Cursor cs = Login.db.rawQuery("select * from memo", new String[]{});
        ArrayList<String> data = new ArrayList();
//        data.add("帐号类型\t\t用户名\t\t密码");
        if (cs.moveToFirst()){
            String name, id, ps;
            for (int i=0;i<cs.getCount();i++){
                StringBuilder sb = new StringBuilder();
                cs.moveToPosition(i);
                name = cs.getString(cs.getColumnIndex("name"));
                id = cs.getString(cs.getColumnIndex("id"));
                if (id.equals(""))
                    id = "空";
                ps = cs.getString(cs.getColumnIndex("password"));
                sb.append("帐号:     " + name + '\n');
                sb.append("用户名: " + id + '\n');
                sb.append("密码:     " + ps + '\n');
                sb.append("======================\n");
                data.add(sb.toString());
            }
        }
        cs.close();
        bd.putStringArrayList("data", data);
        intent.putExtras(bd);
        /* 指定intent要启动的类 */
        intent.setClass(activity, Show_all_info.class);
        /* 启动一个新的Activity */
        activity.startActivity(intent);
        /* 关闭当前的Activity */
        activity.finish();
    }

    //======================================
    // 按键delete的相应事件（删除一条记录）
    //======================================
    public static void click_bt_delete(final Activity activity){
        if (Login.editText1.getText().toString().equals(""))
            return;

        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        ab.setTitle("提示");
        final String name = Login.editText1.getText().toString();
        ab.setMessage("确认删除记录 " + name + "?");
        ab.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                __delete_record(name);
            }
        });
        ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
            }
        });
        ab.setIcon(android.R.drawable.ic_menu_close_clear_cancel);
        ab.show();
    }


    //======================================
    // 按键clear的响应事件（清空全部记录）
    //======================================
    public static void click_bt_clear(final Activity activity) {
        AlertDialog.Builder ab = new AlertDialog.Builder(activity);
        ab.setTitle("提示");
        ab.setMessage("此操作会清空所有本地记录，确认继续？");
        ab.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                        try {
                            // 获取记录数目
                            Cursor cs = Login.db.rawQuery("select count(*) from memo", null);
                            int num = 0;
                            if(cs.moveToFirst()){
                                num = cs.getInt(0);
                            }
                            cs.close();
                            Login.db.execSQL("delete from memo;");
                            AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                            ab.setTitle("提示");
                            ab.setMessage("共删除了" + num + "条记录");
                            ab.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        ab.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialoginterface, int i) {
                    }
                });
        ab.setIcon(android.R.drawable.ic_delete);
        ab.show();
    }


    //======================================
    // 按键tips的相应事件，从网上获取一段话
    //======================================
    public static void click_bt_tips(final Activity activity){
        Login.pb.setVisibility(View.VISIBLE);
        final Handler hl = new Handler();
        // 新建一个线程用于下载
        Thread th_download = new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc;
                try {
                    // 必须设置header伪装为浏览器，否则会按手机版网页解析
                    doc = Jsoup.connect(Login.url_tips).timeout(3000)
                            .header("User-Agent",
                                    "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) " +
                                    "Chrome/50.0.2661.102 Safari/537.36")
                            .get();
                } catch (Exception e) {
                    Act_handler.connect_error(activity, hl);
                    return;
                } finally {
                    Act_handler.set_invisiable(Login.pb, hl);
                }

                Element elt = doc.body().getElementsByClass("BNE_cont").first();
                Element data = elt.getElementsByTag("p").first(); // 只读取第一段
                Act_handler.show_tips(activity, data.text(), hl);

            }
        });
        // 开始下载
        th_download.start();

    }


    //======================================
    // 点击狗图片，进行版本更新
    //======================================
    public static void click_imbt_dog(final Activity activity){
        Login.pb.setVisibility(View.VISIBLE);
        final Handler hl = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                Document doc;
                try {
                    doc = Jsoup.connect(Login.url_ver).timeout(3000).get();
                } catch (Exception e){
                    Act_handler.connect_error(activity, hl);
                    return;
                } finally {
                    Act_handler.set_invisiable(Login.pb, hl);
                }
                // 解析文件，获得版本
                String version = doc.getElementsByTag("version").first().text();
                String download_path = doc.getElementsByTag("download_path").first().text();
                StringBuilder info = new StringBuilder("当前版本 " + Login.cur_version +
                        "\n最新版本" + version + "\n");
                if (version.equals(Login.cur_version)){
                    info.append("无需更新，请点击取消");
                }else{
                    info.append("是否需要更新？");
                }
                // 选择是否更新
                Act_handler.show_version(activity, info.toString(), hl);

            }
        }).start();
    }


    // =========================================================================================== //
    // 下面是相关子函数
    
    //======================================
    // 用于dialog中点击确认后，删去记录
    //======================================
    public static void __delete_record(String name){
        //删除操作的SQL语句
        String sql = "delete from memo where name like '%" + name + "%';";
        Login.db.execSQL(sql); //执行删除操作
        return;
    }

    //======================================
    // 处理name冲突的方法
    //======================================
    public static void __process_conflict(Cursor cs, final String name, final String id, final String ps, final Activity activity){
        String id_old = cs.getString(cs.getColumnIndex("id"));
        String ps_old = cs.getString(cs.getColumnIndex("password"));
        if (!id_old.equals(id) || !ps_old.equals(ps)) {
            AlertDialog.Builder ab = new AlertDialog.Builder(activity);
            ab.setTitle("提示");
            ab.setMessage(String.format("过去帐号%s,密码%s\n" +
                            "当前帐号%s,密码%s\n是否选择覆盖?",
                    id_old, ps_old, id, ps));
            ab.setPositiveButton("是",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            __update_PS(name, id, ps);
                            AlertDialog.Builder ab = new AlertDialog.Builder(activity);
                            ab.setMessage("记录成功！");
                            ab.show();
                        }
                    });
            ab.setNegativeButton("否",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            return;
                        }
                    });
            ab.setIcon(android.R.drawable.ic_menu_edit);
            ab.show();
        }
    }

    //======================================
    // 更新表，修改以前记录的内容
    //======================================
    public static void __update_PS(String name, String id, String ps) {
        // 有2种方法可以进行更新
        if (true) {
            ContentValues cv = new ContentValues(); //实例化ContentValues
            cv.put("id", id);
            cv.put("password", ps);                 //添加要更改的字段及内容
            String whereClause = "name=?";            //修改条件
            String[] whereArgs = {name};              //修改条件的参数
            Login.db.update("memo", cv, whereClause, whereArgs);//执行修改
        } else {
            String sql = String.format("update memo set id=%s,password=%s where name=%s" , id, ps, name);
            Login.db.execSQL(sql);
        }
    }

    //======================================
    // 解析xml
    //======================================
    public static String __xml_parse(String docString){
        // step 1: 获得dom解析器工厂（工作的作用是用于创建具体的解析器）
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // step 2:获得具体的dom解析器
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            return null;
        }
        // step3: 解析一个xml文档，获得Document对象（根结点）
        org.w3c.dom.Document document = null;
        try {
            document = db.parse(docString);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (document==null)
            return "doc is null";
        org.w3c.dom.NodeList nodeList = document.getElementsByTagName("version");
//        org.w3c.dom.Node node = document.getElementsByTagName("version").item(0);
//        return node.getNodeValue();
        return nodeList.toString();
    }



}
