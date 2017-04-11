package com.wong.clockin;

import android.content.DialogInterface;
import android.graphics.Typeface;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.wong.clockin.util.DBHelper;
import com.wong.clockin.util.DataBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class NewItemActivity extends AppCompatActivity {

    private Button backBtn;
    private Button addBtn;
    private EditText itemNameEdt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);
        Typeface font = Typeface.createFromAsset(getAssets(),"fontawesome-webfont.ttf");
        this.backBtn = (Button) findViewById(R.id.backBtn);
        this.backBtn.setTypeface(font);
        this.addBtn = (Button) findViewById(R.id.addBtn);
        this.addBtn.setTypeface(font);
        this.itemNameEdt = (EditText)findViewById(R.id.itemNameEdt);
        this.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemTitle = NewItemActivity.this.itemNameEdt.getText().toString();
                // Toast.makeText(NewItemActivity.this,itemTitle.isEmpty()+"",Toast.LENGTH_LONG).show();
                if(!itemTitle.isEmpty()){
                    DBHelper helper = new DBHelper(NewItemActivity.this);
                    DataBean data = new DataBean();
                    data.setAmount(0);
                    data.setTitle(itemTitle);
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);//设置日期格式
                    String timeStr = df.format(new Date());
                    data.setClockinTime(timeStr);
                    data.setIsClockIn(0);
                    long r = helper.insert(data);
                   // Toast.makeText(NewItemActivity.this,r+"",Toast.LENGTH_LONG).show();
                    if(r != -1){
                        new AlertDialog.Builder(NewItemActivity.this)
                                .setTitle("")
                                .setMessage("添加成功！")
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                })
                                .show();
                    }else{
                        new AlertDialog.Builder(NewItemActivity.this)
                                .setTitle("")
                                .setMessage("添加失败！")
                                .setPositiveButton("OK",null)
                                .show();
                    }


                }else{
                    Toast.makeText(NewItemActivity.this,getResources().getString(R.string.notEmpty),Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}
