/*
 * Comical: An Android webcomic manager
 * Copyright (C) 2017  Nathaniel Case
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.linkybook.comical.data;

import static com.linkybook.comical.utils.Schedule.decodeUpdates;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.annotations.JsonAdapter;
import com.linkybook.comical.data.serializers.BitmapSerializer;
import com.linkybook.comical.data.serializers.LocalDateSerializer;
import com.linkybook.comical.utils.Orientation;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

@Entity(tableName = "site",
        indices = {@Index(value = {"name"}, unique = true)}
)
public class SiteInfo implements Parcelable, Comparable<SiteInfo> {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;

    public String url;

    @JsonAdapter(BitmapSerializer.class)
    public Bitmap favicon;

    @ColumnInfo(name = "last_visit")
    @JsonAdapter(LocalDateSerializer.class)
    public LocalDate lastVisit = LocalDate.now();

    @ColumnInfo(name = "decay_date")
    @JsonAdapter(LocalDateSerializer.class)
    public LocalDate decayDate = LocalDate.now();

    public boolean favorite = false;
    @ColumnInfo(name = "visits")
    public int update_schedule = 0;
    public Orientation orientation = Orientation.ANY;

    public ArrayList<DayOfWeek> schedule() {
        return decodeUpdates(this.update_schedule);
    }

    public int hasNewProbably() {
        /*
        -1: Longer than a month since last visit
        0: Probably not
        1: Longer than two weeks since last visit
        2: Update schedule indicates an update
         */
        LocalDate now = LocalDate.now();
        LocalDate testDate = this.lastVisit;
        if (testDate.until(now).toTotalMonths() > 0) {
            return -1;
        }
        if (testDate.until(now).getDays() > 14) {
            return 1;
        }
        if (this.update_schedule > 0) {
            while (testDate.compareTo(now) < 0) {
                testDate = testDate.plus(Period.ofDays(1));
                if (this.schedule().contains(testDate.getDayOfWeek())) {
                    return 2;
                }
            }
        }
        return 0;
    }

    @Override
    public int compareTo(SiteInfo other) {
        // Return reverse sorted list by decayDate
        int newness;
        if ((newness = other.hasNewProbably() - this.hasNewProbably()) != 0) {
            return newness;
        }
        return -this.decayDate.compareTo(other.decayDate);
    }

    public void visit() {
        this.lastVisit = LocalDate.now();
        double lambda = Math.log(2) / 30;
        double score = Math.exp(lambda * this.lastVisit.until(this.decayDate, ChronoUnit.DAYS));

        double visitValue = 2;
        if (this.favorite) {
            visitValue *= 2;
        }

        int weekly = decodeUpdates(this.update_schedule).size();
        // Defined updates are valuable, but try to normalize impact
        if (weekly > 0) {
            visitValue *= 2.0 / weekly;
        } else {
            visitValue *= 1.0 / 7;
        }
        score += visitValue;

        // Render score back to time-to-score-one
        this.decayDate = this.lastVisit.plusDays((int) (Math.log(score) / lambda));
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        Gson gson = new Gson();
        out.writeString(gson.toJson(this));
    }

    public static final Parcelable.Creator<SiteInfo> CREATOR = new Parcelable.Creator<SiteInfo>() {
        public SiteInfo createFromParcel(Parcel in) {
            Gson gson = new Gson();
            return gson.fromJson(in.readString(), SiteInfo.class);
        }

        public SiteInfo[] newArray(int size) {
            return new SiteInfo[size];
        }
    };

}
