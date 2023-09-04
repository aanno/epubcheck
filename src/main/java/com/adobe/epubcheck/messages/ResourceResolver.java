package com.adobe.epubcheck.messages;

import java.io.File;
import java.net.URL;
import java.util.Locale;

public class ResourceResolver {

    private static final ResourceResolver INSTANCE = new ResourceResolver();

    public static ResourceResolver getInstance() {
        return INSTANCE;
    }

    private ResourceResolver() {
    }

    public URL resource2Url(String resource, Locale locale) {
        String path = resource.replaceAll("\\.", "/");
        URL result = flatResource2Url(path, locale);
        if (result == null) {
            result = flatResource2Url("/" + path, locale);
        }
        // developement ???
        if (result == null) {
            result = flatResource2Url("./src/main/resources/" + resource, locale);
        }
        if (result == null) {
            result = flatResource2Url(resource, locale);
        }
        if (result == null) {
            throw new IllegalArgumentException("Can't find resource for " + resource + " and " + locale);
        }
        return result;
    }

    private URL flatResource2Url(String resource, Locale locale) {
        URL result = null;
        if (locale != null) {
            if (!"".equals(locale.getCountry())) {
                result = from(resource + "_" + locale.toString() + ".properties");
            }
            if (result == null) {
                // in epubcheck it is convention to have an empty *_en.properties file
                // because english is contained in the (default) *.properties files
                String lang = locale.getLanguage();
                if (!"en".equals(lang)) {
                    result = from(resource + "_" + locale.getLanguage() + ".properties");
                } else {
                    result = from(resource + ".properties");
                }
            }
        }
        // fallback to default locale
        Locale dft = Locale.getDefault();
        if (result == null) {
            if (!"".equals(locale.getCountry())) {
                result = from(resource + "_" + dft.toString());
            }
            if (result == null) {
                result = from(resource + "_" + dft.getLanguage());
            }
        }
        if (result == null) {
            result = from(resource + ".properties");
        }
        return result;
    }

    private URL from(String resourceName) {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if (cl == null) {
            cl = ResourceResolver.class.getClassLoader();
        }
        /*
        if (cl == null) {
            cl = ClassLoader.getPlatformClassLoader();
        }
         */
        return cl.getResource(resourceName);
    }
}
