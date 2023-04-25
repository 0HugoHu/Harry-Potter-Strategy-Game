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
import edu.duke.risc.ui.model.UnitUpgradeDataModel;

public class UnitUpgradeDataAdapter extends ArrayAdapter<UnitUpgradeDataModel> implements View.OnClickListener {
    // Context
    Context mContext;

    /**
     * Set the view holder
     */
    private static class ViewHolder {
        TextView name;
        ShapedImageView image;
        TextView number;
        TextView delta;

    }

    /**
     * Constructor
     *
     * @param data    the data
     * @param context the context
     */
    public UnitUpgradeDataAdapter(ArrayList<UnitUpgradeDataModel> data, Context context) {
        super(context, R.layout.order_item, data);
        this.mContext = context;

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
            viewHolder.image = convertView.findViewById(R.id.unit_upgrade_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dataModel.getName());
        viewHolder.number.setText(String.valueOf(dataModel.getNumber()));
        String delta = dataModel.getDelta();
        viewHolder.delta.setText(delta);
        if (delta.equals("0")) {
            viewHolder.delta.setVisibility(View.INVISIBLE);
        } else if (delta.contains("-")) {
            viewHolder.delta.setVisibility(View.VISIBLE);
            viewHolder.delta.setTextColor(mContext.getResources().getColor(R.color.error_prompt));
        } else {
            viewHolder.delta.setVisibility(View.VISIBLE);
            viewHolder.delta.setTextColor(mContext.getResources().getColor(R.color.order_text_green));
        }
        viewHolder.delta.setText(String.valueOf(dataModel.getDelta()));

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

}