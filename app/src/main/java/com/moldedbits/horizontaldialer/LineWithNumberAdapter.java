package com.moldedbits.horizontaldialer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by shubham on 17/07/15.
 *
 */
public class LineWithNumberAdapter  extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context mContext;

    private List<Line> mList;

    private static final int LARGE_LINE_VIEW_TYPE = 0;

    private static final int SMALL_LINE_VIEW_TYPE = 1;

    public LineWithNumberAdapter(Context context, List<Line> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        switch (getItemViewType(i)) {
            case LARGE_LINE_VIEW_TYPE:
                return new LargeLineViewHolder(LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.layout_line_large, viewGroup, false));
            case SMALL_LINE_VIEW_TYPE:
                return new SmallLineViewHolder(LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.layout_line_small, viewGroup, false));
            default:
                return new SmallLineViewHolder(LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.layout_line_small, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        switch(getItemViewType(i)) {
            case LARGE_LINE_VIEW_TYPE :
                Line line = mList.get(i);
                LargeLineViewHolder largeLineViewHolder = (LargeLineViewHolder)viewHolder;
                largeLineViewHolder.mValueTv.setText(String.valueOf(line.getValue()));
                break;
            case SMALL_LINE_VIEW_TYPE:
                //TODO
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList != null ? mList.size() : 0;
    }


    public static class SmallLineViewHolder extends RecyclerView.ViewHolder {

        public SmallLineViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    public static class LargeLineViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.tv_value)
        TextView mValueTv;

        public LargeLineViewHolder(View view) {
            super(view);
            ButterKnife.inject(this, view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mList.get(position).getType().equalsIgnoreCase(Line.LARGE_LINE)) {
            return LARGE_LINE_VIEW_TYPE;
        } else {
            return SMALL_LINE_VIEW_TYPE;
        }
    }
}
