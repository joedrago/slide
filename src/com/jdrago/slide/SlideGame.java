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
        renderQuad(0, 0, width() / 2, height() / 2, 1, 1, 0, 1);
        renderFloat(3, 50, 50, 0, 200, 1, 1, 1, 1);
        renderEnd();
    }
}
