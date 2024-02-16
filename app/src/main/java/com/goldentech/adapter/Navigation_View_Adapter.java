package com.goldentech.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.goldentech.R;
import com.goldentech.model.NavViewModel;

import java.util.List;

public class Navigation_View_Adapter extends RecyclerView.Adapter {

    private List<NavViewModel> navViewModelList;
    private Context mContext;

    public interface OnButtonClickListener {
        public void onClickNavItem(NavViewModel navViewModels);
    }

    public Navigation_View_Adapter.OnButtonClickListener mListener;

    public Navigation_View_Adapter(Context context, List<NavViewModel> navViewModelList,
                                   Navigation_View_Adapter.OnButtonClickListener listener, RecyclerView recyclerView) {
        this.navViewModelList = navViewModelList;
        mContext = context;
        this.mListener = listener;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = null;

        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.adapter_nav_view_items, parent, false);
                break;
            case 2:
                view = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.adapter_nav_header_list_item, parent, false);
                break;
        }
        return new NavItemHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        return navViewModelList.get(position) != null ?
                navViewModelList.get(position).getViewType()
                : navViewModelList.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final NavViewModel nmodel = navViewModelList.get(holder.getAdapterPosition());

        if (nmodel != null) {
            switch (nmodel.getViewType()) {
                case 1:

                    ((NavItemHolder) holder).tv_text_nav.setText(nmodel.getTextName());

                    ((NavItemHolder) holder).image_nav.setImageResource(nmodel.getImage());

                    View.OnClickListener clickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (mListener != null)
                                mListener.onClickNavItem(nmodel);

                        }
                    };
                    ((NavItemHolder) holder).itemView.setOnClickListener(clickListener);
                    ((NavItemHolder) holder).tv_text_nav.setOnClickListener(clickListener);
                    ((NavItemHolder) holder).image_nav.setOnClickListener(clickListener);

//                    if (null != nmodel.getCount()) {
//                        ((NavItemHolder) holder).tv_count.setVisibility(View.VISIBLE);
//                        ((NavItemHolder) holder).tv_count.setText(nmodel.getCount());
//                    } else {
//                        ((NavItemHolder) holder).tv_count.setVisibility(View.GONE);
//                        ((NavItemHolder) holder).tv_count.setText("");
//                    }
                    break;
                case 2:
                    ((NavItemHolder) holder).tv_header_text_nav.setText(nmodel.getTextHeaderName());
                    break;
            }

        }
    }

    @Override
    public int getItemCount() {
        if (navViewModelList != null) {
            return navViewModelList.size();
        }
        return 0;
    }

    private class NavItemHolder extends RecyclerView.ViewHolder {
        private TextView tv_text_nav;
        private TextView tv_header_text_nav;
        private ImageView image_nav;

        private NavItemHolder(View view) {
            super(view);
            tv_header_text_nav = view.findViewById(R.id.tv_header_text_nav);
            tv_text_nav = view.findViewById(R.id.tv_text_nav);
            image_nav = view.findViewById(R.id.image_nav);
        }
    }

}
