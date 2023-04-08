package edu.duke.risc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import edu.duke.risc.R;
import edu.duke.risc.ui.model.UnitUpgradeDataModel;

public class UnitUpgradeDataAdapter extends ArrayAdapter<UnitUpgradeDataModel> implements View.OnClickListener {

    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView number;
        TextView delta;

    }

    public UnitUpgradeDataAdapter(ArrayList<UnitUpgradeDataModel> data, Context context) {
        super(context, R.layout.order_item, data);
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UnitUpgradeDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.unit_prop_item, parent, false);
            viewHolder.name = convertView.findViewById(R.id.unit_upgrade_name);
            viewHolder.number = convertView.findViewById(R.id.unit_upgrade_num);
            viewHolder.delta = convertView.findViewById(R.id.unit_upgrade_delta);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dataModel.getName());
        viewHolder.number.setText(String.valueOf(dataModel.getNumber()));
        String delta = dataModel.getDelta();
        viewHolder.delta.setText(delta);
        if (delta.equals("0")) {
            viewHolder.delta.setTextColor(mContext.getResources().getColor(R.color.order_text_white));
        } else if (delta.contains("-")) {
            viewHolder.delta.setTextColor(mContext.getResources().getColor(R.color.error_prompt));
        } else {
            viewHolder.delta.setTextColor(mContext.getResources().getColor(R.color.order_text_green));
        }
        viewHolder.delta.setText(String.valueOf(dataModel.getDelta()));

        // Return the completed view to render on screen
        return convertView;
    }

}