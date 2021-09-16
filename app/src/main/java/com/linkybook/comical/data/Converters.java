package com.linkybook.comical.data;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import androidx.room.TypeConverter;

import com.linkybook.comical.utils.Orientation;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
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
    public static LocalDate fromTimestamp(Long value) {
        return value == null ? null : new Date(value).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @TypeConverter
    public static Long dateToTimestamp(LocalDate date) {
        return date == null ? null : Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime();
    }

    @TypeConverter
    public static Orientation fromInt(String value) {
        return Orientation.valueOf(value);
    }

    @TypeConverter
    public static String orientationToString(Orientation orientation) {
        return orientation.name();
    }

}
