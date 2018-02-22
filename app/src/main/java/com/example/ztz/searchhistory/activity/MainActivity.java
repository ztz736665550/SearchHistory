package com.example.ztz.searchhistory.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ztz.searchhistory.R;
import com.example.ztz.searchhistory.weigth.ClearEditText;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索历史功能
 */
public class MainActivity extends AppCompatActivity {

    private ClearEditText editText;
    private ImageView serchimg;
    private TagFlowLayout mTagFlowLayout;
    private ArrayList<String> tagList;
    private TextView clearTv;
    //布局管理器
    private LayoutInflater mInflater;
    //流式布局的子布局
    private TextView tag_layout;
    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    mTagFlowLayout.setAdapter(new TagAdapter<String>(tagList) {
                        @Override
                        public View getView(FlowLayout parent, int position, String s) {
                            tag_layout = (TextView) mInflater.inflate(R.layout.tag_layout,mTagFlowLayout,false);
                            tag_layout.setText(s);
                            return tag_layout;
                        }
                    });
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        serchimg = findViewById(R.id.serchimg);
        mTagFlowLayout = findViewById(R.id.flowlayout);
        clearTv = findViewById(R.id.clearTv);
        mInflater = LayoutInflater.from(this);
        tagList = new ArrayList<>();//创建集合存放tag

        loadArray(tagList);//取出集合
        //通知handler更新UI
        handler.sendEmptyMessageDelayed(1, 0);

        //进入页面判断集合是否为空，为空显示清空按钮，否则不显示
        if (tagList != null){
            clearTv.setVisibility(View.VISIBLE);
        }
        if (tagList.size() == 0){
            clearTv.setVisibility(View.GONE);//隐藏清空按钮
        }

        serchimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tagList != null){//如果集合数据，就显示清空按钮
                    clearTv.setVisibility(View.VISIBLE);
                }
                String s = editText.getText().toString();
                if (! TextUtils.isEmpty(s)){
                    String tagTv = editText.getText().toString().trim();
                    tagList.add(tagTv);
                    //通知handler更新UI
                    handler.sendEmptyMessageDelayed(1, 0);
                }
            }
        });

        //tag点击
        mTagFlowLayout.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                Toast.makeText(MainActivity.this,position+"",Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        //清空历史
        clearTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tagList.clear();//清空集合
                saveArray(tagList);//清空集合后进行保存
                handler.sendEmptyMessageDelayed(1,0);
                clearTv.setVisibility(View.GONE);//隐藏清空按钮
            }
        });
    }

    /**
     * SP存值
     * @param list
     * @return
     */
    public boolean saveArray(List<String> list) {
        SharedPreferences sp = getSharedPreferences("tag", Context.MODE_PRIVATE);
        SharedPreferences.Editor mEdit1= sp.edit();
        mEdit1.putInt("Status_size",list.size());

        for(int i=0;i<list.size();i++) {
            mEdit1.remove("Status_" + i);
            mEdit1.putString("Status_" + i, list.get(i));
        }
        return mEdit1.commit();
    }

    /**
     * SP取值
     * @param list
     */
    public void loadArray(List<String> list) {

        SharedPreferences mSharedPreference1 = getSharedPreferences("tag", Context.MODE_PRIVATE);
        list.clear();
        int size = mSharedPreference1.getInt("Status_size", 0);
        for(int i=0;i<size;i++) {
            list.add(mSharedPreference1.getString("Status_" + i, null));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveArray(tagList);//保存
    }
}
