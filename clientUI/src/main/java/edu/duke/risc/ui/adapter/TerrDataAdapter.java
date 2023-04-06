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
import edu.duke.risc.ui.model.TerrDataModel;

public class TerrDataAdapter extends ArrayAdapter<TerrDataModel> implements View.OnClickListener {
    private final ArrayList<TerrDataModel> dataSet;

    Context mContext;
    private TerrDataAdapter.TotalNumListener totalNumListener;

    public void setTotalNumListener(TerrDataAdapter.TotalNumListener totalNumListener) {
        this.totalNumListener = totalNumListener;
    }

    public interface TotalNumListener {
        void onNumChange();
    }

    // View lookup cache
    private static class ViewHolder {
        TextView terrName;
        TextView number;
        SeekBar seekbar;
    }

    public TerrDataAdapter(ArrayList<TerrDataModel> data, Context context) {
        super(context, R.layout.terr_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        TerrDataModel dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.terr_item, parent, false);
            viewHolder.terrName = convertView.findViewById(R.id.terr_name);
            viewHolder.number = convertView.findViewById(R.id.terr_item_num);
            viewHolder.seekbar = convertView.findViewById(R.id.terr_seek_bar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.terrName.setText(dataModel.getName());
        viewHolder.number.setText(String.valueOf(dataModel.getNumber()));
        viewHolder.seekbar.setMax(24);
        viewHolder.seekbar.setProgress(0);
        viewHolder.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewHolder.number.setText(String.valueOf(progress));
                dataSet.get(position).setNumber(progress);
                if (totalNumListener != null) {
                    totalNumListener.onNumChange();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    public int getTotalNum() {
        int total_num = 0;
        for (TerrDataModel dataModel : dataSet) {
            total_num += dataModel.getNumber();
        }
        return total_num;
    }
}