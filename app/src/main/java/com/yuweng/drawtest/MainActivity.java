package com.yuweng.drawtest;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Handler;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Draw draw;
    Timer timer;
    List<Point> list;
    int i;
    int dis_height;
    int dis_width;


    public static class MyHandler extends Handler{
        private final WeakReference<Activity> myActivity;
        public MyHandler(MainActivity act){
            myActivity = new WeakReference<Activity>(act);
        }
        @Override
        public void handleMessage(Message msg) {
            Bundle data;
            switch(msg.what){
                case 0://Line
                    data = msg.getData();
                    double x1 = data.getDouble("x1");
                    double y1 = data.getDouble("y1");
                    double x2 = data.getDouble("x2");
                    double y2 = data.getDouble("y2");
                    //((MainActivity)myActivity.get()).draw.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    ((MainActivity)myActivity.get()).draw.drawLineNoThread(x1,y1,x2,y2);
                case 1://Point
                    data = msg.getData();
                    double x = data.getDouble("x");
                    double y = data.getDouble("y");
                    ((MainActivity)myActivity.get()).draw.canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    ((MainActivity)myActivity.get()).draw.drawCircleNoThread(x,y,4);
            }
            super.handleMessage(msg);
        }
    }
    MyHandler handler = new MyHandler(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WindowManager wm = this.getWindowManager();
        dis_height = wm.getDefaultDisplay().getHeight();
        dis_width = wm.getDefaultDisplay().getWidth();
        Toast.makeText(this,dis_height+" sd "+dis_width,Toast.LENGTH_SHORT).show();
        draw = new Draw(this,dis_height,dis_width);


        setContentView(R.layout.activity_main);

        RelativeLayout relative_layout = (RelativeLayout)findViewById(R.id.relative_layout);
        relative_layout.addView(draw);
        //drawP();
    }

    public void start(View v){
        Button btn = (Button)findViewById(R.id.btn);
        btn.setVisibility(View.INVISIBLE);
        Random ran = new Random();
        int num = 1;
        while (num>0){
            num--;
            //statue = false;
            int x1 = ran.nextInt(dis_width);
            int y1 = ran.nextInt(dis_height);
            int x2 = ran.nextInt(dis_width);
            int y2 = ran.nextInt(dis_height);
            draw.drawCircle(x1,y1,4);
            draw.drawLineNoThread(x1,y1,x2,y2);
            Toast.makeText(this,x1+" "+y1+" "+x2+" "+y2,Toast.LENGTH_LONG).show();
            Point p1 = new Point(x1,y1);
            Point p2 = new Point(x2,y2);
            list = Point.getLinePoints(p1,p2);
            i = 0;
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Message msg = handler.obtainMessage();
                    Bundle data = new Bundle();
                    msg.what = 1;
                    data.putDouble("x",list.get(i).x);
                    data.putDouble("y",list.get(i).y);
                    msg.setData(data);
                    handler.sendMessage(msg);
                    i++;
                    if (i ==list.size()){
                        timer.cancel();
                        timer = null;
                        list.clear();
                        list = null;
                        System.gc();
                    }
                }
            },1000,50);
//            p1 =null;
//            p2 = null;
//            list.clear();
//            list = null;
//            System.gc();
        }
        btn.setVisibility(View.VISIBLE);

    }

}
