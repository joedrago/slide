package com.jdrago.slide;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MenuItem;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;

class SlideView extends GLSurfaceView
{
    public SlideGame game_;

    public SlideView(Context context)
    {
        super(context);
        setEGLContextClientVersion(2);
        game_ = new SlideGame(context);
        setRenderer(game_);
    }

    public boolean onTouchEvent(MotionEvent event)
    {
        game_.click((int)event.getX(0), (int)event.getY(0));
        return true;
    }

    public boolean menuChoice(int itemID)
    {
        switch (itemID)
        {
            case R.id.shuffle:
                game_.shuffle(0);
                return true;
            case R.id.s3:
                game_.shuffle(3);
                return true;
            case R.id.s4:
                game_.shuffle(4);
                return true;
            case R.id.s5:
                game_.shuffle(5);
                return true;
            case R.id.s6:
                game_.shuffle(6);
                return true;
            case R.id.s7:
                game_.shuffle(7);
                return true;
            case R.id.s8:
                game_.shuffle(8);
                return true;
        }
        return false;
    }
}