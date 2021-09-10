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

package com.linkybook.comical.data.serializers;

import android.graphics.Bitmap;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.linkybook.comical.data.Converters;

import java.lang.reflect.Type;

public class BitmapSerializer implements JsonSerializer<Bitmap>, JsonDeserializer<Bitmap> {
    @Override
    public JsonElement serialize(Bitmap src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(Converters.bitmapToBase64(src));
    }

    @Override
    public Bitmap deserialize(JsonElement src, Type typeOfSrc, JsonDeserializationContext context) {
        return Converters.base64ToBitmap(src.getAsString());
    }
}
