package com.linkybook.comical;

import java.net.URI;
import java.net.URISyntaxException;

public class Utils {
    public static String urlDomain(String url) {
        try {
            URI uri = new URI(url);
            return uri.getHost();
        } catch (URISyntaxException e) {
            return "";
        }
    }
}
