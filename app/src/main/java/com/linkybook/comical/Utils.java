package com.linkybook.comical;

import android.text.TextUtils;

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
}
