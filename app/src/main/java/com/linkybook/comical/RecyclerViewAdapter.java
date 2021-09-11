package com.linkybook.comical;

import static com.linkybook.comical.Utils.decodeUpdates;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.palette.graphics.Palette;
import androidx.palette.graphics.Target;
import androidx.recyclerview.widget.RecyclerView;

import com.linkybook.comical.data.SiteInfo;

import java.time.DayOfWeek;
import java.util.Collections;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private List<SiteInfo> siteList;
    private final View.OnClickListener clickListener;
    private final View.OnLongClickListener longClickListener;

    public RecyclerViewAdapter(List<SiteInfo> siteList, View.OnClickListener clickListener) {
        this.siteList = siteList;
        this.clickListener = clickListener;
        this.longClickListener = (View.OnLongClickListener) clickListener;
    }

    @Override
    @NonNull
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

        if (site.visits > 0) {
            StringBuilder schedule = new StringBuilder();
            for (DayOfWeek day : decodeUpdates(site.visits)) {
                schedule.append(day.toString().charAt(0));
            }
            holder.updates.setText(String.join(" ", schedule));
        } else {
            holder.updates.setText("");
        }

        if (site.favorite) {
            holder.fav.setText("♥");
        } else {
            holder.fav.setText("");
        }

        if (site.favicon != null) {
            Palette.Builder pb = Palette.from(site.favicon);
            pb.addTarget(Target.VIBRANT);
            Palette p = pb.generate();
            Palette.Swatch color = p.getDominantSwatch();

            if (color != null) {
                holder.name.setTextColor(color.getTitleTextColor());
                holder.url.setTextColor(color.getBodyTextColor());
                holder.updates.setTextColor(color.getBodyTextColor());
                holder.fav.setTextColor(color.getBodyTextColor());
                holder.itemView.setBackgroundColor(color.getRgb());
            }
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
        private final TextView name;
        private final TextView url;
        private final TextView updates;
        private final TextView fav;
        private final ImageView icon;

        RecyclerViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.card_name);
            url = view.findViewById(R.id.card_url);
            icon = view.findViewById(R.id.card_icon);
            updates = view.findViewById(R.id.card_updates);
            fav = view.findViewById(R.id.card_favorite);
        }
    }
}