package com.khodmohaseb.parkban.adapters;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.khodmohaseb.parkban.R;

public class BindingAdapters {

    @BindingAdapter("image_src")
    public static void setImageBitmap(ImageView imageView, Bitmap imageBitmap) {
        imageView.setImageBitmap(imageBitmap);
    }

    @BindingAdapter("app:data_adapter")
    public static void setRecyclerViewBindingAdapter(RecyclerView recyclerView, RecyclerView.Adapter adapter) {
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @BindingAdapter("layout_visibility")
    public static void setVisibility(View view, boolean isVisible) {
        view.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("circle_back")
    public static void setBackground(View view, boolean allSend) {
        if (allSend)
            view.setBackground(view.getContext().getResources().getDrawable(R.drawable.circle_text_green));
        else
            view.setBackground(view.getContext().getResources().getDrawable(R.drawable.circle_text_view));
    }

    @BindingAdapter("save_color")
    public static void setBackgroundColor(View view, boolean status) {
        if (status)
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.colorPrimary));
        else
            view.setBackgroundColor(view.getContext().getResources().getColor(R.color.light_grey));
    }

}
