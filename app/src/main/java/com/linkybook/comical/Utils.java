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

package com.linkybook.comical;

import android.text.TextUtils;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class Utils {
    public static String urlDomain(String url) {
        String[] domainBits = {};
        int l = 0;
        try {
            URI uri = new URI(url);
            domainBits = uri.getHost().split("\\.");
            l = domainBits.length;
        } catch (URISyntaxException e) {
            return "";
        }
        // TODO: Can't use String.join until API 26
        return TextUtils.join(".", Arrays.copyOfRange(domainBits, l-2, l));
    }

    public static void importFromFile(Context ctx) {
        File file = new File(ctx.getExternalFilesDir(null), "data.json");
        if(isExternalStorageReadable()) {
            SiteViewModel svm = ViewModelProviders.of((FragmentActivity) ctx).get(SiteViewModel.class);
            String dataAsJson = "";
            try {
                BufferedReader in = new BufferedReader(new FileReader(file));
                String line = in.readLine();
                while(line != null) {
                    dataAsJson += line;
                    line = in.readLine();
                }
                in.close();
            } catch (IOException e) {
                Toast.makeText(ctx, "Failed to read file", Toast.LENGTH_LONG).show();
                return;
            }
            svm.importFromJson(dataAsJson);

        } else {
            Toast.makeText(ctx, "Data is not readable", Toast.LENGTH_LONG).show();
        }
    }

    public static void exportToFile(Context ctx) {
        File file = new File(ctx.getExternalFilesDir(null), "data.json");
        if(isExternalStorageWritable()) {
            SiteViewModel svm = ViewModelProviders.of((FragmentActivity) ctx).get(SiteViewModel.class);
            String dataAsJson = svm.exportToJson();
            try {
                Writer out = new BufferedWriter(new FileWriter(file));
                out.write(dataAsJson);
                out.close();
            } catch (IOException e) {
                Toast.makeText(ctx, "Failed to write file", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(ctx, "Data is not writable", Toast.LENGTH_LONG).show();
        }
    }

    /* Checks if external storage is available for read and write */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }
}
