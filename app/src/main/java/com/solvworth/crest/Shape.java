package com.solvworth.crest;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;

/**
 * Created by temp on 3/28/2018.
 */
public class Shape {
    public static ShapeDrawable drawBall(int width, int height, int color){
        ShapeDrawable drawable = new ShapeDrawable();
        drawable.setShape(new OvalShape());
        drawable.getPaint().setColor(color);
        drawable.setIntrinsicWidth(width);
        drawable.setIntrinsicHeight(height);
        return drawable;
    }
}
