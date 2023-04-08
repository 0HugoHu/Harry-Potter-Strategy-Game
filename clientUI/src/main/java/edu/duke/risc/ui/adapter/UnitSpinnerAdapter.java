package edu.duke.risc.ui.adapter;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Locale;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;
import edu.duke.risc.R;
import edu.duke.risc.ui.model.UnitSpinnerDataModel;

public class UnitSpinnerAdapter extends ArrayAdapter<UnitSpinnerDataModel> {
    private final LayoutInflater layoutInflater;

    public UnitSpinnerAdapter(ArrayList<UnitSpinnerDataModel> data, Context context) {
        super(context, 0, data);
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.unit_spinner_item, parent, false);
        } else {
            view = convertView;
        }
        UnitSpinnerDataModel data = getItem(position);
        if (data != null) {
            setSpinnerUnitItem(view, data);
        }
        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        if (position == 0) {
            view = layoutInflater.inflate(R.layout.spinner_header, parent, false);
            view.setOnClickListener(v -> {
                View root = parent.getRootView();
                root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
                root.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_BACK));
            });
        } else {
            view = layoutInflater.inflate(R.layout.spinner_dropdown, parent, false);
            UnitSpinnerDataModel data = getItem(position);
            if (data != null) {
                setSpinnerUnitItem(view, data);
            }
        }
        return view;
    }

    @Override
    public UnitSpinnerDataModel getItem(int position) {
        if (position == 0) {
            return null;
        }
        return super.getItem(position - 1);
    }

    @Override
    public int getCount() {
        return super.getCount() + 1;
    }

    @Override
    public boolean isEnabled(int position) {
        return position != 0;
    }

    private void setSpinnerUnitItem(View view, UnitSpinnerDataModel data) {
        TextView unit_name = view.findViewById(R.id.spinner_name);
        ShapedImageView unit_img = view.findViewById(R.id.spinner_img);
        unit_name.setText(data.getName());
        unit_img.setImageResource(data.getDrawableId());
    }
}
