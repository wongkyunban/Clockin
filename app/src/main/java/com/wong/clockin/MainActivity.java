package com.wong.clockin;

import android.app.Activity;
import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wong.clockin.util.DBHelper;
import com.wong.clockin.util.DataBean;
import com.wong.clockin.util.Loading;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends Activity {

    private RelativeLayout relatelayout;
    private Button fa_plus;
    private ListView listView;
    private ListViewAdapter listViewAdapter;
    private List<DataBean> listItems = new ArrayList<DataBean>();
    private Button refreshBtn;



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
        this.listView = (ListView)findViewById(R.id.main_list);

        this.refreshBtn = (Button)findViewById(R.id.refresh);
        this.refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = Loading.createLoadingDialog(MainActivity.this,"正在刷新...");
                dialog.show();
                List<DataBean> items = getListItems();
                MainActivity.this.initStatus(items);
                dialog.dismiss();

            }
        });
        this.listItems = getListItems();
        this.initStatus(this.listItems);

    }
    //初始化状态
    private void initStatus(List<DataBean> listItems){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);//设置日期格式
        String timeStr = df.format(new Date());
        DBHelper helper = new DBHelper(MainActivity.this);

        for(DataBean bean:listItems){

           if(bean.getClockInTime().compareTo(timeStr) < 0){
                bean.setIsClockIn(0);
                helper.update(bean);
            }
        }
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
        //Toast.makeText(this,listItems.isEmpty()+"",Toast.LENGTH_SHORT).show();
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
        public ImageView clockinImg;                //己签到图片

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
             listItemView.clockinImg = (ImageView)convertView.findViewById(R.id.clockinImg);
             //设置控件集到convertView
             convertView.setTag(listItemView);
         }else {
             listItemView = (ListItemView)convertView.getTag();
         }

         //设置文字
         listItemView.title.setText(listItems.get(position)
                 .getTitle());
         listItemView.day.setText("第"+String.valueOf(listItems.get(position).getAmount())+"天");
         //判断是否已签到，是 显示已签到的图片和禁止签到按钮，否：打开按钮，隐藏己签到图片
         if(listItems.get(position).getIsClockIn() == 1){
             listItemView.clockinImg.setVisibility(View.VISIBLE);
             listItemView.btn.setEnabled(false);
         }
         if(listItems.get(position).getIsClockIn() == 0){
             listItemView.clockinImg.setVisibility(View.GONE);
             listItemView.btn.setEnabled(true);

         }

         final ListItemView itemView = listItemView;
         listItemView.btn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 //显示物品详情
                 showDetailInfo(selectID,itemView);
             }
         });

         return convertView;
     }

    public boolean hasChecked(int checkedID) {
        return hasChecked[checkedID];
    }

    private void showDetailInfo(final int clickID,final ListItemView itemView) {
        new AlertDialog.Builder(context)
                .setTitle("马上签到")
                .setMessage("恭喜你又坚持了一天！")
                .setPositiveButton("签到", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DBHelper helper = new DBHelper(context);
                        //Toast.makeText(context,clickID+"",Toast.LENGTH_SHORT).show();
                        int d = listItems.get(clickID).getAmount();
                        d += 1;
                        listItems.get(clickID).setAmount(d);
                        listItems.get(clickID).setIsClockIn(1);
                        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);//设置日期格式
                        String timeStr = df.format(new Date());
                        listItems.get(clickID).setClockinTime(timeStr);
                        helper.update(listItems.get(clickID));
                        itemView.day.setText("第"+String.valueOf(listItems.get(clickID).getAmount())+"天");
                        itemView.clockinImg.setVisibility(View.VISIBLE);
                        itemView.btn.setEnabled(false);

                    }
                })
                .setNegativeButton("快完成了",null)
                .show();
    }



}

