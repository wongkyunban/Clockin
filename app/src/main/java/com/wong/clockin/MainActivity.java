package com.wong.clockin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wong.clockin.util.DBHelper;
import com.wong.clockin.util.DataBean;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private RelativeLayout relatelayout;
    private Button fa_plus;
    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private List<DataBean> listItems = new ArrayList<DataBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface font = Typeface.createFromAsset(getAssets(),"fontawesome-webfont.ttf");
        this.fa_plus = (Button) findViewById(R.id.fa_plus);
        this.fa_plus.setTypeface(font);
        this.fa_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainActivity.this,NewItemActivity.class);
                startActivity(intent);
            }
        });
        this.relatelayout = (RelativeLayout)findViewById(R.id.relatelayout);
        listView = (ListView)findViewById(R.id.main_list);
        this.listItems = getListItems();
        if(!listItems.isEmpty()){
            setListItems(listItems);
        }else{
            this.relatelayout = (RelativeLayout)findViewById(R.id.relatelayout);
            this.relatelayout.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onRestart(){
        super.onRestart();
        this.listItems = getListItems();
        Toast.makeText(this,listItems.isEmpty()+"",Toast.LENGTH_SHORT).show();
        if(!listItems.isEmpty()){
            this.relatelayout.setVisibility(View.GONE);
            setListItems(listItems);
        }else{
            this.relatelayout.setVisibility(View.VISIBLE);
        }
    }
    private List<DataBean> getListItems(){
        //从sqlite数据库获取数据
        DBHelper helper = new DBHelper(this);
        listItems = helper.queryAll();
        return listItems;
    }

    //设置列表
    private void setListItems(List<DataBean> listItems) {
        //listview装载适配器
        listViewAdapter = new ListViewAdapter(this, listItems); //创建适配器
        listView.setAdapter(listViewAdapter);
    }



}

 class ListViewAdapter extends BaseAdapter {

    private Context context;                        //运行上下文
    private List<DataBean> listItems;               //信息集合
    private LayoutInflater listContainer;           //视图容器
    private boolean[] hasChecked;                   //记录商品选中状态
    public final class ListItemView{                //自定义控件集合
        public TextView title;
        public TextView day;
        public Button btn;
    }


    public ListViewAdapter(Context context, List<DataBean> listItems) {
        this.context = context;
        listContainer = LayoutInflater.from(context);   //创建视图容器并设置上下文
        this.listItems = listItems;
        hasChecked = new boolean[getCount()];
    }
     @Override
     public int getCount() {
        return listItems.size();
    }
    @Override
    public Object getItem(int arg0) {
        return listItems.get(arg0);
    }
    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

     /**
      * ListView Item设置
      */
     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         final int selectID = position;
         //自定义视图
         ListItemView  listItemView = null;
         if (convertView == null) {
             listItemView = new ListItemView();
             //获取list_item布局文件的视图
             convertView = listContainer.inflate(R.layout.main_listview_item, null);
             //获取控件对象
             listItemView.title = (TextView)convertView.findViewById(R.id.titleItem);
             listItemView.day = (TextView)convertView.findViewById(R.id.clockinDay);
             listItemView.btn = (Button)convertView.findViewById(R.id.clockinBtn);
             //设置控件集到convertView
             convertView.setTag(listItemView);
         }else {
             listItemView = (ListItemView)convertView.getTag();
         }

         //设置文字
         listItemView.title.setText(listItems.get(position)
                 .getTitle());
         listItemView.day.setText("第"+String.valueOf(listItems.get(position).getAmount())+"天");
         listItemView.btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //显示物品详情
                 showDetailInfo(selectID);
             }
         });

         return convertView;
     }

    public boolean hasChecked(int checkedID) {
        return hasChecked[checkedID];
    }

    private void showDetailInfo(final int clickID) {
        new AlertDialog.Builder(context)
                .setTitle("马上签到")
                .setMessage("恭喜你又坚持了一天！")
                .setPositiveButton("签到", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBHelper helper = new DBHelper(context);
                        Toast.makeText(context,clickID+"",Toast.LENGTH_SHORT).show();
                        int d = listItems.get(clickID).getAmount();
                        d += 1;
                        listItems.get(clickID).setAmount(d);
                        helper.update(listItems.get(clickID));
                    }
                })
                .setNegativeButton("快完成了",null)
                .show();
    }



}

