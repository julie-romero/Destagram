package com.pauphilet_romero.destagram.viewElements;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Surcharge de l'élément SlidingPaneLayout
 * Created by Jimmy on 11/01/2015.
 */
public class MySlidingPaneLayout extends SlidingPaneLayout{

    private boolean mTouchedDown = false;
    private boolean mForwardTouchesToChildren = false;

    public MySlidingPaneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        // Si on ne peut pas "slider" ou si le panneau est ouvert, on appelle la super méthode
        if (!isSlideable() || isOpen())
            return super.onInterceptTouchEvent(e);

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mTouchedDown = true;

                if (e.getX() > 50) {
                    mForwardTouchesToChildren = true;
                    return false;
                } else
                    mForwardTouchesToChildren = false;

                break;
            }

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                mTouchedDown = false;
                mForwardTouchesToChildren = false;

                break;
            }
        }

        if (mTouchedDown && mForwardTouchesToChildren)
            return false;

        if (mTouchedDown)
            return true;

        return super.onInterceptTouchEvent(e);
    }
}