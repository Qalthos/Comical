package com.linkybook.comical;

import androidx.palette.graphics.Palette;
import androidx.palette.graphics.Target;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkybook.comical.data.SiteInfo;

import java.util.Collections;
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
        if(site.favorite == true) {
            holder.fav.setText("♥");
        } else {
            holder.fav.setText("");
        }

        if(site.favicon != null) {
            Palette.Builder pb = Palette.from(site.favicon);
            pb.addTarget(Target.VIBRANT);
            Palette p = pb.generate();
            Palette.Swatch color = p.getDominantSwatch();

            holder.name.setTextColor(color.getTitleTextColor());
            holder.url.setTextColor(color.getBodyTextColor());
            holder.itemView.setBackgroundColor(color.getRgb());
        }

        holder.itemView.setTag(site);
        holder.itemView.setOnClickListener(clickListener);
        holder.itemView.setOnLongClickListener(longClickListener);
    }

    @Override
    public int getItemCount() {
        return siteList.size();
    }

    public void addItems(List<SiteInfo> siteList) {
        Collections.sort(siteList);
        this.siteList = siteList;
        notifyDataSetChanged();
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView url;
        private TextView fav;
        private ImageView icon;

        RecyclerViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.card_name);
            url = (TextView) view.findViewById(R.id.card_url);
            icon = (ImageView) view.findViewById(R.id.card_icon);
            fav = (TextView) view.findViewById(R.id.card_favorite);
        }
    }
}