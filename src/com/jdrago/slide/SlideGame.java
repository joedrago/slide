package com.jdrago.slide;

import android.content.Context;
import javax.microedition.khronos.opengles.GL10;

public class SlideGame extends QuadRenderer
{
    public SlideGame(Context context)
    {
        super(context);
    }

    public void onDrawFrame(GL10 glUnused)
    {
        renderBegin(0.0f, 0.0f, 1.0f);
        renderQuad(5,5,50,50, 1, 1, 0, 1);
        renderQuad(20,20,50,50, 1, 0, 1, 0.5f);
        renderEnd();
    }
}
