package com.pauphilet_romero.destagram;

import android.content.Context;
import android.support.v4.widget.SlidingPaneLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
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
        /*
         * If not slideable or if the "sliding" pane is open, let super method
         * handle it.
         */
        if (!isSlideable() || isOpen())
            return super.onInterceptTouchEvent(e);

        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN: {
                mTouchedDown = true;

            /*
             * "50" should be defined as a constant. Also this should be
             * re-calculated in case the "sliding" pane is put at right side.
             */
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
    }// onInterceptTouchEvent()
}