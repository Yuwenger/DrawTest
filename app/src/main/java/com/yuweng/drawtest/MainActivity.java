package com.yuweng.drawtest;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Handler;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Draw draw;
    Timer timer;
    int i;


    public static class MyHandler extends Handler{
        private final WeakReference<Activity> myActivity;
        public MyHandler(MainActivity act){
            myActivity = new WeakReference<Activity>(act);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            double x = data.getDouble("x");
            double y = data.getDouble("y");
            ((MainActivity)myActivity.get()).draw.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            ((MainActivity)myActivity.get()).draw.drawCircle(x,y,2);


        }
    }
    MyHandler handler = new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager wm = this.getWindowManager();
        int dis_height = wm.getDefaultDisplay().getHeight();
        int dis_width = wm.getDefaultDisplay().getWidth();
        Toast.makeText(this,dis_height+" sd "+dis_width,Toast.LENGTH_SHORT).show();
        draw = new Draw(this,dis_height,dis_width);


        setContentView(R.layout.activity_main);

        LinearLayout linear_layout = (LinearLayout)findViewById(R.id.linear_layout);
        linear_layout.addView(draw);
        //drawP();
    }

    public void drawP(){

        //draw.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);


        WindowManager wm = this.getWindowManager();
        int dis_height = wm.getDefaultDisplay().getHeight();
        int dis_width = wm.getDefaultDisplay().getWidth();
        Toast.makeText(MainActivity.this,dis_height+" sd "+dis_width,Toast.LENGTH_SHORT).show();
        draw = new Draw(this,dis_height,dis_width);
        LinearLayout linear_layout = (LinearLayout)findViewById(R.id.linear_layout);
        linear_layout.addView(draw);
        draw.drawCircle(200,400,4);
        Point p1 = new Point(100,200);
        Point p2 = new Point(400,900);
        final List<Point> list = Point.getLinePoints(p1,p2);
        for (Point p:list){
            draw.drawCircle(p.x,p.y,2);
        }
        i = 0;
        //Toast.makeText(MainActivity.this,list.size(),Toast.LENGTH_SHORT).show();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                draw.drawCircleNoThread(list.get(i).x,list.get(i).y,2);
                i++;
                if (i ==list.size()){
                    timer.cancel();
                    timer = null;
                }
            }
        },1000,200);
    }


    public void start(View v){
        Button btn = (Button)findViewById(R.id.btn);
        btn.setVisibility(View.INVISIBLE);
        Point p1 = new Point(100,200);
        Point p2 = new Point(400,900);
        final List<Point> list = Point.getLinePoints(p1,p2);
        i = 0;
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message msg = handler.obtainMessage();
                Bundle data = new Bundle();
                data.putDouble("x",list.get(i).x);
                data.putDouble("y",list.get(i).y);
                msg.setData(data);
                handler.sendMessage(msg);
                i++;
                if (i ==list.size()){
                    timer.cancel();
                    timer = null;
                }
            }
        },1000,200);
    }

}
