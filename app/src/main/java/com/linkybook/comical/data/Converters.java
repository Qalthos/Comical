package com.linkybook.comical.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.linkybook.comical.utils.Orientation;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Converters {
    @TypeConverter
    public static Bitmap base64ToBitmap(String data) {
        byte[] decodedBytes = Base64.decode(data, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    @TypeConverter
    public static String bitmapToBase64(Bitmap image) {
        if(image == null) {
            return "";
        }
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        byte[] imageData = byteStream.toByteArray();
        return Base64.encodeToString(imageData, Base64.DEFAULT);
    }

    @TypeConverter
    public static LocalDate timestampToDate(Long value) {
        return value == null ? null : new Date(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDate date) {
        return date == null ? null : Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();
    }

    @TypeConverter
    public static LocalDateTime timestampToDateTime(Long value) {
        return value == null ? null : new Date(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @TypeConverter
    public static Long dateTimeToTimestamp(LocalDateTime date) {
        return date == null ? null : Date.from(date.atZone(ZoneId.systemDefault()).toInstant()).getTime();
    }

    @TypeConverter
    public static Instant timestampToInstant(Long value) {
        return value == null ? null : Instant.ofEpochMilli(value);
    }

    @TypeConverter
    public static Long instantToTimestamp(Instant inst) {
        return inst == null ? null : inst.toEpochMilli();
    }

    @TypeConverter
    public static Orientation fromString(String value) {
        return Orientation.valueOf(value);
    }

    @TypeConverter
    public static String orientationToString(Orientation orientation) {
        return orientation.name();
    }

}
