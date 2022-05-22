package com.safshekan.parkban.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.safshekan.parkban.R;

public class CardViewControl extends RelativeLayout {

    TextView title , value;
    ImageView image_view;
    RelativeLayout mainLayout;

    private final View v;
    private Context context;
    private int widthPixels;

    private OnCardMenuListener onCardMenuListener;

    @SuppressLint("ResourceType")
    public CardViewControl(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.control_card_view,this,true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardView, 0, 0);

        mainLayout = v.findViewById(R.id.main_layout);
        mainLayout.setOnClickListener(onClickListener);

        title = v.findViewById(R.id.title);
        title.setText(typedArray.getString(R.styleable.CardView_Title));

        value = v.findViewById(R.id.value);
        value.setText(typedArray.getString(R.styleable.CardView_Value));

        image_view = v.findViewById(R.id.image);
        image_view.setImageResource(typedArray.getResourceId(R.styleable.CardView_CardImage, 0));

    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onCardMenuListener != null)
                onCardMenuListener.onClick(getThis());
        }
    };

    public void setOnClickListener(OnCardMenuListener onCardMenuListener) {
        this.onCardMenuListener = onCardMenuListener;
    }

    public interface OnCardMenuListener {
        void onClick(CardViewControl v);
    }

    public void setLabel(String text) {
        title.setText(text);
    }

    public void setValue(String text) {
        value.setText(text);
    }

    public void setImageView(int image_id) {
        image_view.setImageResource(image_id);
    }

    private CardViewControl getThis() {
        return this;
    }
}

