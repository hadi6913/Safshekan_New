package com.safshekan.parkban.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.EditText;

import com.safshekan.parkban.R;
import com.safshekan.parkban.helper.FontHelper;

@SuppressLint("AppCompatCustomView")
public class PersianEditText extends EditText {
    Context context;

    public PersianEditText(Context context) {
        super(context);
        setInitialize(context);
    }

    public PersianEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        setInitialize(context);
    }

    public PersianEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setInitialize(context);
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null) {
            text = FontHelper.toPersianNumber(text.toString());
        }
        super.setText(text, type);
    }

    private void setInitialize(Context context) {
        if (!isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface());
//        setHintTextColor(getResources().getColor(R.color.colorPrimary));
        setCompoundDrawablePadding(10);
    }

    public void IsOnlyEnglishDigit(Boolean isEnglishDigit) {
        if (!isEnglishDigit && !isInEditMode())
            setTypeface(FontHelper.getInstance(context).getPersianTextTypeface());
    }

    public Drawable setUserIcon(Context context, String type) {
        Drawable drawable;
        if (type == "user")
            drawable = ContextCompat.getDrawable(context, R.mipmap.avatar);
        else
            drawable = ContextCompat.getDrawable(context, R.mipmap.pass);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.5),
                (int) (drawable.getIntrinsicHeight() * 0.5));
        ScaleDrawable sd = new ScaleDrawable(drawable, 0, 20, 20);
        return sd.getDrawable();
    }

    public Drawable setPassIcon(Context context) {
        Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.pass);
        drawable.setBounds(0, 0, (int) (drawable.getIntrinsicWidth() * 0.5),
                (int) (drawable.getIntrinsicHeight() * 0.5));
        ScaleDrawable sd = new ScaleDrawable(drawable, 0, 20, 20);
        return sd.getDrawable();
    }


}

