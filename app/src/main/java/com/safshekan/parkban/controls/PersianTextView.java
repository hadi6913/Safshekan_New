package com.safshekan.parkban.controls;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.safshekan.parkban.helper.FontHelper;

public class PersianTextView extends AppCompatTextView {

    private String TEXT_COLOR_WHITE="white";
    private String TEXT_COLOR_PRIMARY="primary";

    public PersianTextView(Context context) {
        super(context);
        setInitialize(context);
    }

    public PersianTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInitialize(context);
    }

    public PersianTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInitialize(context);
    }

    private void setInitialize(Context context) {

        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface());

       // setTextSize(13);

       // setTextColor(getResources().getColor(R.color.text_color));

//        View view=findViewWithTag(TEXT_COLOR_WHITE);
//        View primaryView=findViewWithTag(TEXT_COLOR_PRIMARY);
//
//        if (view!=null)
//            setTextColor(getResources().getColor(R.color.white));
//        if (primaryView!=null)
//            setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null)
            text = FontHelper.toPersianNumber(text.toString());
        super.setText(text, type);
    }

}
