package com.jdrago.slide;

import android.content.Context;
import android.opengl.GLSurfaceView;

class SlideView extends GLSurfaceView
{
    public SlideView(Context context)
    {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(new SlideGame(context));
    }
}