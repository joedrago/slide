package com.jdrago.slide;

import android.content.Context;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.opengles.GL10;

class SlideView extends GLSurfaceView
{
    public SlideView(Context context)
    {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(new SlideGame(context));
    }

    public void onDrawFrame(GL10 glUnused)
    {
    }
}