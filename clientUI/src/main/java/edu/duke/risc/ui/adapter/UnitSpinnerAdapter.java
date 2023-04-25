package edu.duke.risc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;
import edu.duke.risc.R;
import edu.duke.risc.ui.model.UnitSpinnerDataModel;

public class UnitSpinnerAdapter extends ArrayAdapter<UnitSpinnerDataModel> {
    // Layout inflater
    private final LayoutInflater layoutInflater;

    /**
     * Constructor
     *
     * @param data    the data
     * @param context the context
     */
    public UnitSpinnerAdapter(ArrayList<UnitSpinnerDataModel> data, Context context) {
        super(context, 0, data);
        this.layoutInflater = LayoutInflater.from(context);
    }

    /**
     * Get the view
     *
     * @param position    the position
     * @param convertView the convert view
     * @param parent      the parent
     * @return the view
     */
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

    /**
     * Get the drop down view
     *
     * @param position    the position
     * @param convertView the convert view
     * @param parent      the parent
     * @return the drop down view
     */
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = layoutInflater.inflate(R.layout.unit_spinner_item, parent, false);
        if (position == 0) {
            view.findViewById(R.id.spinner_arrow).setVisibility(View.VISIBLE);
        } else {
            view.findViewById(R.id.spinner_arrow).setVisibility(View.GONE);
        }
        UnitSpinnerDataModel data = getItem(position);
        if (data != null) {
            setSpinnerUnitItem(view, data);
        }
        return view;
    }

    /**
     * Get the item
     *
     * @param position the position
     * @return the item
     */
    @Override
    public UnitSpinnerDataModel getItem(int position) {
        return super.getItem(position);
    }

    /**
     * Get the count
     *
     * @return the count
     */
    @Override
    public int getCount() {
        return super.getCount();
    }

    /**
     * Is enabled
     *
     * @param position the position
     * @return the boolean
     */
    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    /**
     * Set the spinner unit item
     *
     * @param view the view
     * @param data the data
     */
    private void setSpinnerUnitItem(View view, UnitSpinnerDataModel data) {
        TextView unit_name = view.findViewById(R.id.spinner_name);
        ShapedImageView unit_img = view.findViewById(R.id.spinner_img);
        unit_name.setText(data.getName());
        unit_img.setImageResource(data.getDrawableId());
    }
}
