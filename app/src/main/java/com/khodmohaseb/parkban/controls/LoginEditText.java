package com.khodmohaseb.parkban.controls;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.khodmohaseb.parkban.R;

public class LoginEditText extends RelativeLayout {

    TextView title;
    PersianEditText value;
    RelativeLayout mainLayout;
    ImageView image;

    private final View v;
    private Context context;

    private OnCardMenuListener onCardMenuListener;

    public LoginEditText(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.control_login_edit_text, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditText, 0, 0);

        title = v.findViewById(R.id.title);
        title.setOnClickListener(onClickListener);
        title.setText(typedArray.getString(R.styleable.EditText_BTitle));

        value = v.findViewById(R.id.value);
        value.setOnClickListener(onClickListener);
        value.setText(typedArray.getString(R.styleable.EditText_BValue));

        mainLayout = v.findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(onClickListener);

        image = v.findViewById(R.id.image_view);
        image.setImageResource(typedArray.getResourceId(R.styleable.EditText_Image, 0));
        image.setOnClickListener(onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //  value.setCursorVisible(true);
            // value.setCursorVisible(true);
            if (onCardMenuListener != null)
                onCardMenuListener.onClick(getThis());
        }
    };

    public void setOnClickListener(OnCardMenuListener onCardMenuListener) {
        this.onCardMenuListener = onCardMenuListener;
    }

    public interface OnCardMenuListener {
        void onClick(LoginEditText v);
    }


    private LoginEditText getThis() {
        return this;
    }

    public void setLabel(String text) {
        title.setText(text);
    }

    public void setLabel(int text) {
        setLabel(context.getString(text));
    }

    public void setValue(String text) {
        value.setText(text.toString());
    }


//    public void setPasswordInputType() {
//
//        value.setInputType(InputType.TYPE_CLASS_TEXT |
//                InputType.TYPE_TEXT_VARIATION_PASSWORD);
//    }

    public void setCursorInvisible() {
        // value.setCursorVisible(false);

    }

}