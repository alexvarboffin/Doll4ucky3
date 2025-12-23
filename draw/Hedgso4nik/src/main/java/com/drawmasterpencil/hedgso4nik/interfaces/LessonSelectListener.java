package com.drawmasterpencil.hedgso4nik.interfaces;

import android.view.View;

// parent activity will implement this method to respond to click events
public interface LessonSelectListener {
    void onLessonClicked(View view, int position);
}
