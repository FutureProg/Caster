package com.caster.caster_android.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Nick on 16-02-05.
 */
public class RoundedImageView extends ImageView{


    public RoundedImageView(Context context) {
        super(context);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null || getWidth() == 0 || getHeight() == 0){
            return;
        }

        Bitmap bitmap = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && drawable instanceof VectorDrawable){
            ((VectorDrawable)drawable).draw(canvas);
            bitmap = Bitmap.createBitmap(canvas.getWidth(),canvas.getHeight(),Bitmap.Config.ARGB_8888);
            Canvas canvas1 = new Canvas();
            canvas1.setBitmap(bitmap);
            drawable.draw(canvas1);
        }else{
            bitmap = ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap2 = bitmap.copy(Bitmap.Config.ARGB_8888,true);
        int w = getWidth();
        int h = getHeight();
        Bitmap roundBitmap = getCroppedBitmap(bitmap2,w);
        canvas.drawBitmap(roundBitmap,0,0,null);
    }

    Bitmap getCroppedBitmap(Bitmap bitmap,int radius){
        Bitmap bmp;
        if (bitmap.getWidth() != radius || bitmap.getHeight() != radius){
            bmp = Bitmap.createScaledBitmap(bitmap,radius,radius,false);
        }else{
            bmp = bitmap;
        }
        Bitmap re = Bitmap.createBitmap(bmp.getWidth(),bmp.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas  = new Canvas(re);

        final int colour = 0xffa19774;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0,0,bmp.getWidth(),bmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor("#bab399"));
        canvas.drawCircle(bmp.getWidth() / 2 + 0.7f, bmp.getHeight() / 2 + 0.7f, bmp.getWidth() / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bmp,rect,rect,paint);

        return re;
    }
}
