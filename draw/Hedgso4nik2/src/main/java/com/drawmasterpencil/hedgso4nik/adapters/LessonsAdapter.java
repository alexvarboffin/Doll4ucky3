package com.drawmasterpencil.hedgso4nik.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.drawmasterpencil.hedgso4nik.R;
import com.drawmasterpencil.hedgso4nik.interfaces.LessonSelectListener;
import com.drawmasterpencil.hedgso4nik.model.LessonItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class LessonsAdapter extends RecyclerView.Adapter<LessonsAdapter.ViewHolder> {

    private final String TAG = "LessonsAdapter";

    private final ArrayList<LessonItem> lessonsArray;

    private final AssetManager assetManager;
    private final LayoutInflater layoutInflater;

    private LessonSelectListener lessonSelectListener;


    public LessonsAdapter(Context context, ArrayList<LessonItem> lessonsArray) {
        this.layoutInflater = LayoutInflater.from(context);
        this.lessonsArray = lessonsArray;

        assetManager = context.getAssets();
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.row_task, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LessonItem mTheme = lessonsArray.get(position);
        String strName = mTheme.getTaskName();
        int nSteps = mTheme.getSteps();

        String lastImage = (nSteps < 10) ? "0" + nSteps + ".png" : nSteps + ".png";

        try {
            InputStream is = assetManager.open(strName + "/" + lastImage);
            Bitmap bitmap = BitmapFactory.decodeStream(is);
            holder.imvIcon.setImageBitmap(bitmap);

        } catch (IOException e) {
            e.printStackTrace();
        }

        holder.tvName.setText(strName);
    }

    @Override
    public int getItemCount() {
        return lessonsArray.size();
    }

    public void setClickListener(LessonSelectListener taskClickListener) {
        this.lessonSelectListener = taskClickListener;
    }




    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView tvName;
        public ImageView imvIcon;


        ViewHolder(View itemView) {
            super(itemView);

            imvIcon = itemView.findViewById(R.id.imv_lesson);
            tvName = itemView.findViewById(R.id.tv_lesson_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (lessonSelectListener != null) {
                lessonSelectListener.onLessonClicked(view, getAdapterPosition());
            }
        }
    }
}