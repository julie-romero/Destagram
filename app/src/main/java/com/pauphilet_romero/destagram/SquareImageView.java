package com.pauphilet_romero.destagram;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Classe qui étend ImageView afin d'avoir un rendu carré des miniatures des médias
 * Created by Jimmy on 16/12/2014.
 */
public class SquareImageView extends ImageView
{
    public SquareImageView(Context context)
    {
        super(context);
    }

    public SquareImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
    }
}
