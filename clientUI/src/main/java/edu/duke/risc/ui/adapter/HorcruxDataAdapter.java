package edu.duke.risc.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import cn.gavinliu.android.lib.shapedimageview.ShapedImageView;
import edu.duke.risc.R;
import edu.duke.risc.ui.model.HorcruxDataModel;
import edu.duke.shared.player.Horcrux;

public class HorcruxDataAdapter extends ArrayAdapter<HorcruxDataModel> implements View.OnClickListener {
    // data set
    private final ArrayList<HorcruxDataModel> dataSet;
    // Context
    Context mContext;
    // CostListener
    private UseListener useListener;

    /**
     * Set the cost listener
     *
     * @param useListener the cost listener
     */
    public void setUseListener(UseListener useListener) {
        this.useListener = useListener;
    }

    /**
     * The cost listener
     */
    public interface UseListener {
        void onUseChange(Horcrux horcrux, int number);
    }

    /**
     * Set the view holder
     */
    private static class ViewHolder {
        TextView name;
        ShapedImageView image;
        TextView number;
        TextView desc;
        Button use;
    }

    /**
     * Constructor
     *
     * @param data    the data
     * @param context the context
     */
    public HorcruxDataAdapter(ArrayList<HorcruxDataModel> data, Context context) {
        super(context, R.layout.horcrux_item, data);
        this.dataSet = data;
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
        HorcruxDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.horcrux_item, parent, false);
            viewHolder.name = convertView.findViewById(R.id.horcrux_name);
            viewHolder.image = convertView.findViewById(R.id.horcrux_img);
            viewHolder.number = convertView.findViewById(R.id.horcrux_num);
            viewHolder.use = convertView.findViewById(R.id.horcrux_btn);
            viewHolder.desc = convertView.findViewById(R.id.horcrux_desc);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.name.setText(dataModel.getNameString());
        viewHolder.number.setText(String.valueOf(dataModel.getMax()));
        viewHolder.desc.setText(dataModel.getDesc());
        viewHolder.use.setOnClickListener(v -> {
            if (useListener != null) {
                useListener.onUseChange(dataModel.getName(), dataModel.getNumber());
                dataModel.addNumber();
                dataModel.setMax(dataModel.getMax() - 1);
            }
        });

        switch (dataModel.getName()) {
            case HAT:
                viewHolder.image.setImageResource(R.drawable.gnome);
                break;
            case DIARY:
                viewHolder.image.setImageResource(R.drawable.dwarf);
                break;
            case LOCKET:
                viewHolder.image.setImageResource(R.drawable.house_elf);
                break;
            case RING:
                viewHolder.image.setImageResource(R.drawable.goblin);
                break;
            case CUP:
                viewHolder.image.setImageResource(R.drawable.vampire);
                break;
            case SNAKE:
                viewHolder.image.setImageResource(R.drawable.centaur);
                break;
        }

        // Return the completed view to render on screen
        return convertView;
    }

}
