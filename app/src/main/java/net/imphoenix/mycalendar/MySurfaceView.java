package net.imphoenix.mycalendar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*
=============================
        首先，我們介紹一下SurfaceView 的特色,它擁有獨立的繪圖表面
        所以不會和父級ui共享同一個繪圖表面,由於它擁有獨立的繪圖表面
        因此SurfaceView的UI就可以在一個獨立的線程中進行繪製
        而且獨立線程不會占用主線程資源
        所以SurfaceView一方面可以實現複雜而高效的UI
        另一方面又不會導致用戶輸入得不到及時響應

        Android遊戲開發中常用的三種視圖是：

        View：必須在UI主線程內更新畫面，速度較慢，提供圖形繪製函數、觸屏事件、按鍵事件函數等

        SurfaceView：適合2D遊戲的開發；是view的子類，類似使用雙緩機制，在新的線程中更新畫面所以刷新介面速度比view快。

        GLSurfaceView：基於SurfaceView視圖再次進行拓展的視圖類，專用於3D遊戲開發的視圖；是SurfaceView的子類，openGL專用。

        =============================
*/
public class MySurfaceView extends SurfaceView
    implements SurfaceHolder.Callback   //繼承並實現生命週期 回調介面
{
    RcActivity activity;
    Paint paint;//畫筆
    int currentAlpha=0;//當前的不透明值

    int screenWidth=1440;//屏幕寬度
    int screenHeight=2560;//屏幕高度
    int sleepSpan=50;//動畫的時延ms

    Bitmap[] logos=new Bitmap[2];//logo圖片數組
    Bitmap currentLogo;//當前logo圖片引用
    int currentX;
    int currentY;

    public MySurfaceView(RcActivity activity) {
        super(activity);
        this.activity = activity;
        this.getHolder().addCallback(this); //設置生命週期回調接口的實現者
        paint = new Paint(); //創建畫筆
        paint.setAntiAlias(true); //打開抗鋸齒

        //加載圖片
        logos[0] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.welcome1);
        logos[1] = BitmapFactory.decodeResource(activity.getResources(), R.drawable.phoenixok);
    }

    public void onDraw(Canvas canvas) {
        //繪製介面方法, 首先繪置黑色矩形, 然後再對該矩型進行平面貼面

        //繪製黑填充矩形清背景
        paint.setColor(Color.BLACK);  //設置畫筆顏色
        paint.setAlpha(255);
        canvas.drawRect(0,0,screenWidth,screenHeight,paint);

        //進行平面貼圖
        if (currentLogo==null) return;
        paint.setAlpha(currentAlpha);
        canvas.drawBitmap(currentLogo,currentX, currentY, paint);
    }

    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {

    }

    public void surfaceCreated(SurfaceHolder holder) { //創建時被調用
     new Thread() {
         public void run() {
             //取得螢幕解析度
             DisplayMetrics dm = new DisplayMetrics();
             activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
             screenWidth=(int) (dm.widthPixels * 1);//屏幕寬度
             screenHeight=(int)(dm.heightPixels * 1) ;//屏幕高度

             for (Bitmap bm : logos) {
                 currentLogo = bm;
                 //計算圖片位置
                 currentX = screenWidth / 2 - bm.getWidth() / 2;
                 currentY = screenHeight / 2 - bm.getHeight() / 2;

                 for (int i = 255; i > -10; i = i - 10) {//動態更改圖片的透明度值並不斷重繪
                     currentAlpha = i;
                     if (currentAlpha < 0) {
                         currentAlpha = 0;
                     }
                     SurfaceHolder myholder = MySurfaceView.this.getHolder();
                     Canvas canvas = myholder.lockCanvas(); //獲取畫布
                     try {
                         synchronized (myholder) {
                             onDraw(canvas); //繪製
                         }
                     } catch (Exception e) {
                         e.printStackTrace();
                     } finally {
                         if (canvas != null) {
                             myholder.unlockCanvasAndPost(canvas);
                         }
                     }

                     try {
                         if (i == 255) {//若是新圖片，多等待一會
                             Thread.sleep(1000);
                         }
                         Thread.sleep(sleepSpan);
                     } catch (Exception e) {
                         e.printStackTrace();
                     }
                 }
             }
             //activity.hd.sendEmptyMessage(0);
         }
     }.start();
  }

  public void surfaceDestroyed(SurfaceHolder arg0){ //銷毀時被調用

  }
}
