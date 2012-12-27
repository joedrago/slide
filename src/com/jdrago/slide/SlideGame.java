package com.jdrago.slide;

import android.content.Context;
import android.view.MotionEvent;

import javax.microedition.khronos.opengles.GL10;

public class SlideGame extends QuadRenderer
{
    public SlideGame(Context context)
    {
        super(context);
        x_ = 0;
        y_ = 0;
    }

    public void update()
    {
    }

    public void onDrawFrame(GL10 glUnused)
    {
        update();

        renderBegin(0.3f, 0.3f, 0.3f);
        renderQuad(0, 0, width() / 2, height() / 2, 1, 1, 0, 1);
        renderFloat(3, x_, y_, 0, 30, 1, 1, 1, 1);
        renderEnd();
    }

    public void click(int x, int y)
    {
        x_ = x;
        y_ = y;
    }

    public void shuffle(int size)
    {
        x_ = 0;
        y_ = 0;
    }

    private int x_;
    private int y_;
}
