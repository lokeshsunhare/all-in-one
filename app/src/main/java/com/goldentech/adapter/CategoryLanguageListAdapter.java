package com.goldentech.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.goldentech.R;
import com.goldentech.model.Category;
import com.goldentech.model.CategoryLanguage;
import com.squareup.picasso.Picasso;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.util.List;

public class CategoryLanguageListAdapter extends RecyclerView.Adapter {

    private List<CategoryLanguage> list;
    private Context mContext;
    private Activity activity;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;


    //    private final int[] colors = {R.color.greenish, R.color.orange,
//            R.color.colorAccent, R.color.end_color, R.color.blue, R.color.darkpink, R.color.green};


    public interface OnButtonClickListener {
        public void onClickCheckBoxItem(boolean isChecked);
    }

    public CategoryLanguageListAdapter.OnButtonClickListener mListener;
    private static final String TAG = "CategoryLanguageListAda";

    public CategoryLanguageListAdapter(Context context, List<CategoryLanguage> list
//            , CategoryLanguageListAdapter.OnButtonClickListener mListener

    ) {
        this.list = list;
//        this.mListener = mListener;

        mContext = context;
        activity = (Activity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_category_language_list_item, parent, false);
        return new CategoryListHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        //  pager_position

        if (holder instanceof CategoryListHolder) {

            final CategoryLanguage nmodel = list.get(holder.getAdapterPosition());

//            int strokeWidth = 0;
//            int strokeColor;
//            int fillColor;
//            if (holder.getAdapterPosition() >= colors.length) {
//                count = holder.getAdapterPosition() % colors.length;
//                //mHolder.tv_count.setBackgroundResource(colors[count]);
//
//                strokeColor = ResourcesCompat.getColor(mContext.getResources(), colors[count], null);
//                fillColor = ResourcesCompat.getColor(mContext.getResources(), colors[count], null);
//                GradientDrawable gD = new GradientDrawable();
//                gD.setColor(fillColor);
//                gD.setShape(GradientDrawable.OVAL);
//                gD.setStroke(strokeWidth, strokeColor);
//                mHolder.tv_count.setBackground(gD);
//            } else {
//                count = position;
//                //mHolder.tv_count.setBackgroundResource(colors[count]);
//                strokeColor = ResourcesCompat.getColor(mContext.getResources(), colors[count], null);
//                fillColor = ResourcesCompat.getColor(mContext.getResources(), colors[count], null);
//                GradientDrawable gD = new GradientDrawable();
//                gD.setColor(fillColor);
//                gD.setShape(GradientDrawable.OVAL);
//                gD.setStroke(strokeWidth, strokeColor);
//                mHolder.tv_count.setBackground(gD);
//            }
//
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//            int screenHeight = displaymetrics.heightPixels;
//            int screenWidth = displaymetrics.widthPixels;
//
//            mHolder.layoutCount.getLayoutParams().width = (int) (screenWidth * 0.21);
//            mHolder.layoutCount.getLayoutParams().height = (int) (screenHeight * 0.13);

//            View.OnClickListener onClickListener = v -> {
//                if (mListener != null) {
//                    mListener.onClickRowItem(nmodel);
//                }
//            };
//            ((CategoryListHolder) holder).itemView.setOnClickListener(onClickListener);
//
//            Picasso
//                    .with(mContext)
//                    .load(nmodel.getIconUrl())
//                    .placeholder(R.drawable.ic_bg_circle)
//                    .error(R.drawable.ic_bg_circle)
//                    .into(((CategoryListHolder) holder).image);

            ((CategoryListHolder) holder).title.setText(nmodel.getLanguageName());

            if (list.get(position).isChecked()) {
                ((CategoryListHolder) holder).checkbox_value.setChecked(true);
                ((CategoryListHolder) holder).expandable_layout_detail.setExpanded(true);

            } else {
                ((CategoryListHolder) holder).checkbox_value.setChecked(false);
                ((CategoryListHolder) holder).expandable_layout_detail.setExpanded(false);
                ((CategoryListHolder) holder).et_category_name.setText("");
            }
            ((CategoryListHolder) holder).checkbox_value.setOnCheckedChangeListener((buttonView, isChecked) -> {
                list.get(position).setChecked(isChecked);
                ((CategoryListHolder) holder).expandable_layout_detail.toggle();
                if (((CategoryListHolder) holder).expandable_layout_detail.isExpanded()) {
                    ((CategoryListHolder) holder).checkbox_value.setChecked(true);
                } else {
                    ((CategoryListHolder) holder).checkbox_value.setChecked(false);
                    ((CategoryListHolder) holder).et_category_name.setText("");
                }
                int checkedCount = 1;
                if (isChecked) checkedCount++;
                else checkedCount--;
                list.get(position).setCheckedCount(checkedCount);
//                if (mListener != null)
//                    mListener.onClickCheckBoxItem(isChecked);
            });
            ((CategoryListHolder) holder).et_category_name.setHint(nmodel.getLanguageName());

            ((CategoryListHolder) holder).et_category_name.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    list.get(holder.getAdapterPosition()).setCategoryName(s.toString().trim());
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    private class CategoryListHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private CheckBox checkbox_value;
        private ExpandableLayout expandable_layout_detail;
        private EditText et_category_name;

        private CategoryListHolder(View view) {
            super(view);
            checkbox_value = view.findViewById(R.id.checkbox_value);
            title = view.findViewById(R.id.title);
            expandable_layout_detail = view.findViewById(R.id.expandable_layout_detail);
            et_category_name = view.findViewById(R.id.et_category_name);

        }
    }

}
