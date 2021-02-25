package com.example.jacoco_epic;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jacoco_epic.model.MainModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private int mPrvateInt;
    private static String mPrvateString;
    private final static List<String> mListString = new ArrayList<>();
    private MainModel mMainModel;
    private ListView mList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMainModel = new MainModel();
        mList = (ListView) findViewById(R.id.list);

        MyAdapter adapter = new MyAdapter(
                MainActivity.this, android.R.layout.simple_list_item_1, new String[] {"11","22"}
        );
        mList.setAdapter(adapter);
    }

    private void addString(String s) {
        mListString.add(s);
    }

    private static List<String> getString() {
        return mListString;
    }

    public void callModelMethod() {
        if (mMainModel.getBoolean()) {
            System.out.println("callModelMethod - true");
        } else {
            System.out.println("callModelMethod - false");
        }
    }
    private class WindowView {
        private String mString;
        private void method(String s) {
            mString = s;
       }
    }

    private class WindowViewWithParam {
        private String mString;
        WindowViewWithParam(String s) {
            mString = s;
        }
        private void method(String s) {
            mString = s;
        }
    }
    private static class WindowViewStatic {
        private String mString;
        WindowViewStatic(String s) {
            mString = s;
        }
        private void method(String s) {
            mString = s;
        }
    }
    private class MyAdapter extends ArrayAdapter<String> {

        public MyAdapter(@NonNull Context context, int resource, String[] objs) {
            super(context, resource, objs);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                System.out.println("MyAdapter - convertView == null");
            }
            return super.getView(position, convertView, parent);
        }
    }
    public void test() {
        TextView textView = (TextView) findViewById(R.id.textView1);
        textView.setText("222");
    }
}