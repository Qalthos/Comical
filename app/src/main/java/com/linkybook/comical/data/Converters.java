package com.linkybook.comical.data;

import androidx.room.TypeConverter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();
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

}
