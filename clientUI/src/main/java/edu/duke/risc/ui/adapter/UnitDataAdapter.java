package edu.duke.risc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;
import edu.duke.risc.R;
import edu.duke.risc.ui.model.UnitDataModel;

public class UnitDataAdapter extends ArrayAdapter<UnitDataModel> implements View.OnClickListener {
    // data set
    private final ArrayList<UnitDataModel> dataSet;
    // Context
    Context mContext;
    // CostListener
    private UnitDataAdapter.CostListener costListener;

    /**
     * Constructor
     *
     * @param data    the data
     * @param context the context
     */
    public UnitDataAdapter(ArrayList<UnitDataModel> data, Context context) {
        super(context, R.layout.order_item, data);
        this.dataSet = data;
        this.mContext = context;
    }

    /**
     * Set the cost listener
     *
     * @param costListener the cost listener
     */
    public void setCostListener(UnitDataAdapter.CostListener costListener) {
        this.costListener = costListener;
    }

    /**
     * On click
     *
     * @param v the view
     */
    @Override
    public void onClick(View v) {

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
        // Get the data item for this position
        UnitDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.order_item, parent, false);
            viewHolder.name = convertView.findViewById(R.id.unit_name);
            viewHolder.image = convertView.findViewById(R.id.unit_img);
            viewHolder.number = convertView.findViewById(R.id.order_item_num);
            viewHolder.seekbar = convertView.findViewById(R.id.seek_bar);
            viewHolder.textView = convertView.findViewById(R.id.number_input);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dataModel.getName());
        viewHolder.number.setText(String.valueOf(dataModel.getMax()));
        viewHolder.seekbar.setMax(dataModel.getMax());
        viewHolder.seekbar.setMin(0);
        viewHolder.textView.setText("1");
        viewHolder.seekbar.setProgress(0);
        viewHolder.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewHolder.textView.setText(String.valueOf(progress));
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

        switch (dataModel.getName()) {
            case "Gnome":
                viewHolder.image.setImageResource(R.drawable.gnome);
                break;
            case "Dwarf":
                viewHolder.image.setImageResource(R.drawable.dwarf);
                break;
            case "House-elf":
                viewHolder.image.setImageResource(R.drawable.house_elf);
                break;
            case "Goblin":
                viewHolder.image.setImageResource(R.drawable.goblin);
                break;
            case "Vampire":
                viewHolder.image.setImageResource(R.drawable.vampire);
                break;
            case "Centaur":
                viewHolder.image.setImageResource(R.drawable.centaur);
                break;
            case "Werewolf":
                viewHolder.image.setImageResource(R.drawable.werewolf);
                break;
            default:
                viewHolder.image.setImageResource(R.drawable.gnome);
                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }

    /**
     * Get the total number
     *
     * @return the total number
     */
    public int getTotalNumber() {
        int total_cost = 0;
        for (UnitDataModel dataModel : dataSet) {
            total_cost += dataModel.getNumber();
        }
        return total_cost;
    }

    /**
     * The cost listener
     */
    public interface CostListener {
        void onCostChange();
    }

    /**
     * Set the view holder
     */
    private static class ViewHolder {
        TextView name;
        ShapedImageView image;
        TextView number;
        SeekBar seekbar;
        TextView textView;
    }
}