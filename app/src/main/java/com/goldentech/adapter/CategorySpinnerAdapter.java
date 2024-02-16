package com.goldentech.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.goldentech.R;
import com.goldentech.model.CommonSpinner;

import java.util.List;

public class CategorySpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<CommonSpinner> lists;
    private LayoutInflater inflter;

    public CategorySpinnerAdapter(Context context, List<CommonSpinner> lists) {
        this.context = context;
        this.lists = lists;
        inflter = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        if (lists != null) {
            return lists.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.adapter_spinner_list, null);
        TextView names = view.findViewById(R.id.text_spinner_text);
        if (i == 0) {
            names.setTextColor(context.getResources().getColor(R.color.black_overlay));
        } else {
            names.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        }
        names.setText(lists.get(i).getName());
        return view;
    }
}
