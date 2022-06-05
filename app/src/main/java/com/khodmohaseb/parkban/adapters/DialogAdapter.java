package com.khodmohaseb.parkban.adapters;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.khodmohaseb.parkban.R;
import com.khodmohaseb.parkban.helper.FontHelper;
import com.khodmohaseb.parkban.persistence.models.ChargeAmount;
import com.khodmohaseb.parkban.persistence.models.ParkingSpaceStatus;
import com.khodmohaseb.parkban.services.dto.ParkingSpaceDto;
import com.khodmohaseb.parkban.services.dto.WorkShiftTypes;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DialogAdapter<T> extends BaseAdapter {

    private Context _context;
    private List<T> list;
    private ArrayList<T> arrayList;
    private String trim;
    private NumberFormat formatter = new DecimalFormat("#,###");

    public DialogAdapter(List<T> uiEntities, Context context) {
        this._context = context;
        this.list = uiEntities;
        arrayList = new ArrayList<T>();
        arrayList.addAll(list);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
         ViewHolder viewHolder;

        if (rowView == null) {
            rowView = View.inflate(_context, R.layout.adapter_dialog, null);

            // configure view holder
            viewHolder = new ViewHolder();
            viewHolder.text = rowView.findViewById(R.id.item);
            viewHolder.carImageView = rowView.findViewById(R.id.carImage);

            rowView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        T u = list.get(position);

        if (u instanceof ParkingSpaceDto) {
            viewHolder.text.setText(((ParkingSpaceDto) u).getName());
//            if (((ParkingSpaceDto) u).getSpaceStatus() == ParkingSpaceStatus.FULL) {
//                viewHolder.carImageView.setVisibility(View.VISIBLE);
//                Log.i("------------a----------", "park name " + ((ParkingSpaceDto) u).getName());
//                Log.i("------------a----------", "parkFull id " + ((ParkingSpaceDto) u).getId());
//            }else
//                viewHolder.carImageView.setVisibility(View.GONE);
            viewHolder.carImageView.setVisibility(((ParkingSpaceDto) u).getSpaceStatus() == ParkingSpaceStatus.FULL ? View.VISIBLE :  View.GONE);

            Log.i("------------a----------", "park name " + ((ParkingSpaceDto) u).getName());
            Log.i("------------a----------", "parkFull status " + ((ParkingSpaceDto) u).getSpaceStatus());
            Log.i("------------a----------", "parkFull id " + ((ParkingSpaceDto) u).getId());
        }
        else if (u instanceof String)
            viewHolder.text.setText(((String) u));

        else if (u instanceof WorkShiftTypes) {
            viewHolder.text.setText(((WorkShiftTypes) u).getDescription());
            viewHolder.carImageView.setVisibility(View.GONE);
        }
        else if (u instanceof ChargeAmount) {
            viewHolder.text.setText(formatter.format(((ChargeAmount) u).getValue()));
            viewHolder.carImageView.setVisibility(View.GONE);
        }

        return rowView;
    }

    public void filter(String charText) {

        charText = charText.toLowerCase(Locale.getDefault());

        list.clear();
        if (charText.length() == 0) {
            list.addAll(arrayList);

        } else {
            for (T postDetail : arrayList) {
                    if (charText.length() != 0 && ((ParkingSpaceDto) postDetail).getName().trim().toLowerCase(Locale.getDefault()).contains(charText) ||
                            FontHelper.toPersianNumber(((ParkingSpaceDto) postDetail).getName().trim()).contains(charText)) {
                        list.add(postDetail);
                    } else if (charText.length() != 0 && ((ParkingSpaceDto) postDetail).getName().trim().toLowerCase(Locale.getDefault()).contains(charText) ||
                            FontHelper.toPersianNumber(((ParkingSpaceDto) postDetail).getName().trim()).contains(charText)) {
                        list.add(postDetail);
                    }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder {
        TextView text;
        ImageView carImageView;
    }
}
