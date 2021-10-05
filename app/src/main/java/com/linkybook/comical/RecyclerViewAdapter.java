package com.linkybook.comical;

import static com.linkybook.comical.utils.Schedule.decodeUpdates;

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
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
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

        ArrayList<DayOfWeek> schedule = decodeUpdates(site.update_schedule);
        if (site.update_schedule > 0) {
            StringBuilder scheduleText = new StringBuilder();
            for (DayOfWeek day : schedule) {
                scheduleText.append(day.toString().charAt(0));
            }
            holder.updates.setText(String.join(" ", scheduleText));
        } else {
            holder.updates.setText("");
        }

        String flags = "";
        LocalDate now = LocalDate.now();
        if (site.lastVisit.until(now).toTotalMonths() > 0) {
            flags += "\uD83D\uDCA4";
        } else if (site.update_schedule > 0) {
            LocalDate testDate = site.lastVisit;
            while (testDate.compareTo(now) < 0) {
                testDate = testDate.plus(Period.ofDays(1));
                if (schedule.contains(testDate.getDayOfWeek())) {
                    flags += "🆕";
                    break;
                }
            }
        }
        if (site.favorite) {
            flags += "♥";
        }
        holder.flags.setText(flags);

        if (site.favicon != null) {
            Palette.Builder pb = Palette.from(site.favicon);
            pb.addTarget(Target.VIBRANT);
            Palette p = pb.generate();
            Palette.Swatch color = p.getDominantSwatch();

            if (color != null) {
                holder.name.setTextColor(color.getTitleTextColor());
                holder.url.setTextColor(color.getBodyTextColor());
                holder.updates.setTextColor(color.getBodyTextColor());
                holder.flags.setTextColor(color.getBodyTextColor());
                holder.itemView.setBackgroundColor(color.getRgb());
            }
        }
        switch (site.orientation) {
            case ANY:
                holder.rotation.setImageResource(R.drawable.ic_baseline_screen_rotation_24);
                break;
            case PORTRAIT:
                holder.rotation.setImageResource(R.drawable.ic_baseline_stay_current_portrait_24);
                break;
            case LANDSCAPE:
                holder.rotation.setImageResource(R.drawable.ic_baseline_stay_current_landscape_24);
                break;
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
        private final TextView flags;
        private final ImageView icon;
        private final ImageView rotation;

        RecyclerViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.card_name);
            url = view.findViewById(R.id.card_url);
            icon = view.findViewById(R.id.card_icon);
            updates = view.findViewById(R.id.card_updates);
            flags = view.findViewById(R.id.card_flags);
            rotation = view.findViewById(R.id.card_rotation);
        }
    }
}