package com.linkybook.comical;

import android.content.res.ColorStateList;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkybook.comical.data.SiteInfo;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private List<SiteInfo> siteList;
    private View.OnClickListener clickListener;
    private View.OnLongClickListener longClickListener;

    public RecyclerViewAdapter(List<SiteInfo> siteList, View.OnClickListener clickListener) {
        this.siteList = siteList;
        this.clickListener = clickListener;
        this.longClickListener = (View.OnLongClickListener) clickListener;
    }

    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new RecyclerViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.site_widget_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(final RecyclerViewHolder holder, int position) {
        SiteInfo site = siteList.get(position);
        holder.name.setText(site.name);
        holder.url.setText(site.url);
        holder.icon.setImageBitmap(site.favicon);

        /*if(site.favicon != null) {
            Palette p = Palette.from(site.favicon).generate();
            Palette.Swatch color = p.getDominantSwatch();

            int[][] states = new int[][] {new int[] { android.R.attr.state_enabled}};
            int[] fore_colors = new int[] {color.getTitleTextColor()};
            int[] back_colors = new int[] {color.getRgb()};
            holder.itemView.setForegroundTintList(new ColorStateList(states, fore_colors));
            holder.itemView.setBackgroundTintList(new ColorStateList(states, back_colors));
        }*/

        holder.itemView.setTag(site);
        holder.itemView.setOnClickListener(clickListener);
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }

    public void addItems(List<SiteInfo> siteList) {
        this.siteList = siteList;
        notifyDataSetChanged();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView url;
        private ImageView icon;

        RecyclerViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.card_name);
            url = (TextView) view.findViewById(R.id.card_url);
            icon = (ImageView) view.findViewById(R.id.card_icon);
        }
    }
}