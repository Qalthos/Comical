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
import com.linkybook.comical.data.serializers.LocalDateTimeSerializer;
import com.linkybook.comical.utils.Orientation;
import com.linkybook.comical.utils.Status;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    @JsonAdapter(LocalDateTimeSerializer.class)
    public LocalDateTime decayDate = LocalDateTime.now();

    public boolean backlog = false;
    public boolean favorite = false;
    public boolean hiatus = false;

    @ColumnInfo(name = "visits")
    public int update_schedule = 0;
    public Orientation orientation = Orientation.ANY;

    public ArrayList<DayOfWeek> schedule() {
        return decodeUpdates(this.update_schedule);
    }

    public Status hasNewProbably() {
        if (this.hiatus) {
            return Status.hiatus;
        } else if (this.backlog) {
            return Status.backlog;
        }
        LocalDate now = LocalDate.now();
        LocalDate testDate = this.lastVisit;
        if (testDate.until(now).toTotalMonths() > 0) {
            return Status.ignored;
        }
        if (testDate.until(now).getDays() > 14) {
            return Status.limbo;
        }
        if (this.update_schedule > 0) {
            while (testDate.compareTo(now) < 0) {
                testDate = testDate.plus(Period.ofDays(1));
                if (this.schedule().contains(testDate.getDayOfWeek())) {
                    return Status.unread;
                }
            }
        }
        return Status.read;
    }

    @Override
    public int compareTo(SiteInfo other) {
        // Return reverse sorted list by decayDate
        int newness;
        if ((newness = other.hasNewProbably().compareTo(this.hasNewProbably())) != 0) {
            return newness;
        }
        return -this.decayDate.compareTo(other.decayDate);
    }

    public void visit() {
        double score = this.getScore();

        double visitValue = 1;
        if (this.favorite) {
            visitValue *= 2;
        }

        int weekly = decodeUpdates(this.update_schedule).size();
        // Defined updates are valuable, but try to normalize impact
        if (weekly > 0) {
            visitValue *= 2.0 / weekly;
        } else {
            visitValue /= 7;
        }
        score += visitValue;

        this.lastVisit = LocalDate.now();
        // We've changed the page, hiatus must be over?
        this.hiatus = false;
        this.setScore(score);
    }

    public double getScore() {
        double seconds = LocalDateTime.now().until(this.decayDate, ChronoUnit.SECONDS);
        return Math.pow(2, seconds / 2592000);
    }

    public void setScore(double score) {
        if (this.backlog) {
            // clamp backlog lower
            score = Math.min(score, 150);
        }

        // Render score back to time-to-score-one
        double seconds = Math.log(score) * 2592000 / Math.log(2);
        Duration duration = Duration.ofSeconds((long) seconds);
        this.decayDate = LocalDateTime.now().plus(duration);
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
