package com.safshekan.parkban.controls;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.safshekan.parkban.R;

public class HomeCardView extends RelativeLayout {

    TextView text_view;
    LinearLayout home_card_view;
    ImageView image_view;

    private final View v;
    private Context context;
    private int widthPixels;

    private OnCardMenuListener onCardMenuListener;

    @SuppressLint("ResourceType")
    public HomeCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.control_home_card_view,this,true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.homeCardView, 0, 0);

        home_card_view = v.findViewById(R.id.home_card_view);
        home_card_view.setOnClickListener(onClickListener);

        text_view = v.findViewById(R.id.text_view);
        text_view.setText(typedArray.getString(R.styleable.homeCardView_Label));

        image_view = v.findViewById(R.id.image_view);
        image_view.setImageResource(typedArray.getResourceId(R.styleable.homeCardView_LogoImage, 0));

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
        void onClick(HomeCardView v);
    }

    public void setLabel(String text) {
        text_view.setText(text);
    }

    public void setImageView(int image_id) {
        image_view.setImageResource(image_id);
    }

    private HomeCardView getThis() {
        return this;
    }
}

