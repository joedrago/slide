package com.jdrago.slide;

import android.app.Activity;
import android.os.Bundle;

public class SlideActivity extends Activity
{
    private SlideView view_;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.main);
        view_ = new SlideView(getApplication());
        setContentView(view_);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        view_.onPause();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        view_.onResume();
    }
}
