package com.safshekan.parkban.controls;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.safshekan.parkban.R;
import com.safshekan.parkban.RecordPlateActivity;

public class Header extends RelativeLayout {

    PersianTextView title;
    RelativeLayout cameraIcon;

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.control_header , this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Header, 0, 0);

        title = v.findViewById(R.id.header_title);
        title.setText(typedArray.getString(R.styleable.Header_titleHeader));

        cameraIcon = v.findViewById(R.id.camera_icon);
        cameraIcon.setVisibility(typedArray.getInt(R.styleable.Header_cameraIcon ,1) == 1 ? VISIBLE : GONE);
        cameraIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), RecordPlateActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                v.getContext().startActivity(i);
            }
        });
    }

    public void setTitle(String text){
        title.setText(text);
    }

    public void setTitle(int text){
        title.setText(text);
    }
}
