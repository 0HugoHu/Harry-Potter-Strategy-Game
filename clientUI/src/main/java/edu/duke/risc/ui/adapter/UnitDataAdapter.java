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
import edu.duke.risc.ui.model.UnitDataModel;

public class UnitDataAdapter extends ArrayAdapter<UnitDataModel> implements View.OnClickListener {
    private final ArrayList<UnitDataModel> dataSet;

    Context mContext;
    private UnitDataAdapter.CostListener costListener;

    public void setCostListener(UnitDataAdapter.CostListener costListener) {
        this.costListener = costListener;
    }

    public interface CostListener {
        void onCostChange();
    }

    // View lookup cache
    private static class ViewHolder {
        TextView name;
        TextView number;
        SeekBar seekbar;
        EditText edittext;
    }

    public UnitDataAdapter(ArrayList<UnitDataModel> data, Context context) {
        super(context, R.layout.order_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UnitDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_item, parent, false);
            viewHolder.name = convertView.findViewById(R.id.unit_name);
            viewHolder.number = convertView.findViewById(R.id.order_item_num);
            viewHolder.seekbar = convertView.findViewById(R.id.seek_bar);
            viewHolder.edittext = convertView.findViewById(R.id.number_input);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dataModel.getName());
        viewHolder.number.setText(String.valueOf(dataModel.getMax()));
        viewHolder.seekbar.setMax(dataModel.getMax());
        viewHolder.edittext.setText("0");
        viewHolder.seekbar.setProgress(0);
        viewHolder.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewHolder.edittext.setText(String.valueOf(progress));
                dataSet.get(position).setNumber(progress);
                if (costListener != null) {
                    costListener.onCostChange();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Change the seek bar value when the edit text is changed
        viewHolder.edittext.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                String value = viewHolder.edittext.getText().toString();
                if (value.equals("")) {
                    viewHolder.edittext.setText("");
                    viewHolder.seekbar.setProgress(0);
                    dataSet.get(position).setNumber(0);
                } else {
                    int valueInt = Integer.parseInt(value);
                    if (valueInt > dataModel.getMax()) {
                        viewHolder.edittext.setText(String.valueOf(dataModel.getMax()));
                        viewHolder.seekbar.setProgress(dataModel.getMax());
                        dataSet.get(position).setNumber(dataModel.getMax());
                    } else {
                        viewHolder.seekbar.setProgress(valueInt);
                        dataSet.get(position).setNumber(valueInt);
                    }
                }
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    public int getTotalCost() {
        int total_cost = 0;
        for (UnitDataModel dataModel : dataSet) {
            total_cost += dataModel.getNumber();
        }
        return total_cost;
    }
}