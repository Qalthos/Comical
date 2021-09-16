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

package com.linkybook.comical.utils;

import android.net.Uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class URL {
    public static String urlDomain(String url) {
        String[] domainBits;
        int l;
        try {
            URI uri = new URI(url);
            domainBits = uri.getHost().split("\\.");
            l = domainBits.length;
        } catch (URISyntaxException e) {
            return "";
        }
        return String.join(".", Arrays.copyOfRange(domainBits, l-2, l));
    }

    public static String urlDomain(Uri uri) {
        String[] domainBits;
        int l;
        domainBits = uri.getHost().split("\\.");
        l = domainBits.length;
        return String.join(".", Arrays.copyOfRange(domainBits, l-2, l));
    }
}
