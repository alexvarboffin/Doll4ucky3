package com.drawfanatteberad.mortalfived.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class GradientTextView extends AppCompatTextView {

    private static final int COLOR_1 = Color.parseColor("#005898");
    private static final int COLOR_2 = Color.parseColor("#04C7F0");


    public GradientTextView(Context context) {
        super(context, null, -1);
    }

    public GradientTextView(Context context, AttributeSet attrs) {
        super(context, attrs, -1);
    }

    public GradientTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            getPaint().setShader(
                    new LinearGradient(0, getHeight(), 0, 0,
                            COLOR_1,
                            COLOR_2,
                            Shader.TileMode.CLAMP)

            );
        }
    }

//    @Override
//    protected void onDraw(Canvas canvas) {
//        // draw the shadow
//        getPaint().setShadowLayer(1, 1, 1, 0xbf000000); // or whatever shadow you use
//        getPaint().setShader(null);
//        super.onDraw(canvas);
//
//        // draw the gradient filled text
//        getPaint().clearShadowLayer();
//        getPaint().setShader(
//                new LinearGradient(0, getHeight(), 0, 0,
//                COLOR_1,
//                COLOR_2,
//                Shader.TileMode.CLAMP)); // or whatever gradient/shader you use
//        super.onDraw(canvas);
//    }
}