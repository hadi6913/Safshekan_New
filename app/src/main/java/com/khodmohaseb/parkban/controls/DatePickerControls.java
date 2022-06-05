package com.khodmohaseb.parkban.controls;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.helper.DateTimeHelper;
import com.mohamadamin.persianmaterialdatetimepicker.date.DatePickerDialog;
import com.mohamadamin.persianmaterialdatetimepicker.utils.PersianCalendar;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DatePickerControls extends RelativeLayout implements DatePickerDialog.OnDateSetListener {

    PersianTextView title, value;
    RelativeLayout mainLayout;

    private Context _context;

    PersianCalendar persianCalendar;
    OnCardMenuListener onCardMenuListener;

    public DatePickerControls(Context context) {
        super(context);
        _context = context;
    }

    public DatePickerControls(Context context, AttributeSet attrs) {
        super(context, attrs);

        _context = context;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.control_date_card_view, this, true);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CardView, 0, 0);

        if (!isInEditMode()) {

            title = v.findViewById(R.id.title);
            title.setText(typedArray.getString(R.styleable.CardView_Title));

            value = v.findViewById(R.id.value);
            value.setText(typedArray.getString(R.styleable.CardView_Value));

            mainLayout = v.findViewById(R.id.main_layout);
            mainLayout.setOnClickListener(onClickListener);

            persianCalendar = new PersianCalendar();
            persianCalendar.setTime(Calendar.getInstance().getTime());
            persianCalendar.setTimeZone(TimeZone.getTimeZone("Asia/Tehran"));

        }
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (onCardMenuListener != null)
                onCardMenuListener.onClick(getThis());

            DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                    DatePickerControls.this,
                    persianCalendar.getPersianYear(),
                    persianCalendar.getPersianMonth(),
                    persianCalendar.getPersianDay()
            );
            datePickerDialog.show(((Activity) _context).getFragmentManager(), _context.getString(R.string.date));
        }
    };

    public void setOnClickListener(OnCardMenuListener onCardMenuListener) {
        this.onCardMenuListener = onCardMenuListener;
    }

    public interface OnCardMenuListener {
        void onClick(DatePickerControls v);
    }

    public void setLabel(String text) {
        title.setText(text);
    }

    public void setLabel(int text) {
        setLabel(_context.getString(text));
    }

    public void setValue(String text) {
        value.setText(text);
    }

    public void setValue(int text) {
        setValue(_context.getString(text));
    }

    private DatePickerControls getThis() {
        return this;
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        persianCalendar.setPersianDate(year, monthOfYear, dayOfMonth);
        setValue(String.format("%1$02d/%2$02d/%3$02d", year, (monthOfYear + 1), dayOfMonth));
    }

    public String getPersianDate() {
        return persianCalendar.getPersianShortDate();
    }

    public String getDate() {

        Date date = persianCalendar.getTime();

        if (DateTimeHelper.getHour(date) <= 4)
            date = DateTimeHelper.setHour(date, 5);

        return DateTimeHelper.DateToString(date);
    }

    public void setDisplayDate(Date date) {
        if (date == null)
            return;
        if (DateTimeHelper.getHour(date) <= 4)
            date = DateTimeHelper.setHour(date, 5);
        persianCalendar.setTime(date);
        setValue(persianCalendar.getPersianShortDate());
    }

}
