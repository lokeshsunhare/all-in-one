package com.goldentech.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.goldentech.R;
import com.goldentech.common.FileUtils;
import com.goldentech.model.AttachmentImage;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class AttachImageListAdapter extends RecyclerView.Adapter {

    private List<AttachmentImage> attachmentImages;
    private Context mContext;
    private Activity activity;
    private int selectedPos = 0;
    private int pager_position = 0;

    public interface OnButtonClickListener {
        public void onDeleteButtonClick();
    }

    public AttachImageListAdapter.OnButtonClickListener mListener;
    private static final String TAG = "AttachImageListAdapter";

    public AttachImageListAdapter(Context context, List<AttachmentImage> attachmentImages
            , AttachImageListAdapter.OnButtonClickListener mListener
    ) {
        this.attachmentImages = attachmentImages;
        this.mListener = mListener;
        mContext = context;
        activity = (Activity) context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = null;
        view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.adapter_attach_image_list, parent, false);
        return new ChannelListHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        //  pager_position
        final AttachmentImage nmodel = attachmentImages.get(holder.getAdapterPosition());

        if (nmodel != null) {
            ((ChannelListHolder) holder).tv_sn.setText("" + (position + 1));

            if (!nmodel.getImage_name().equals("")) {
                //Log.d(TAG, "onBindViewHolder: " + nmodel.getImage_name());
                ((ChannelListHolder) holder).tv_image.setImageBitmap(BitmapFactory.decodeFile(nmodel.getImage_name()));

                // ((ChannelListHolder) holder).tv_image.setImageURI(nmodel.getImage_Uri());
            }

            View.OnClickListener onClickListener = v -> {
                attachmentImages.remove(nmodel);
                notifyDataSetChanged();
                if (mListener != null) {
                    mListener.onDeleteButtonClick();
                }
            };
            ((ChannelListHolder) holder).btn_delete.setOnClickListener(onClickListener);
        }
    }


    @Override
    public int getItemCount() {
        if (attachmentImages != null) {
            return attachmentImages.size();
        }
        return 0;
    }

    private class ChannelListHolder extends RecyclerView.ViewHolder {
        private TextView tv_sn;
        private ImageView tv_image;
        private ImageView btn_delete;

        private ChannelListHolder(View view) {
            super(view);
            tv_sn = view.findViewById(R.id.tv_sn);
            tv_image = view.findViewById(R.id.tv_image);
            btn_delete = view.findViewById(R.id.btn_delete);
        }
    }

}
