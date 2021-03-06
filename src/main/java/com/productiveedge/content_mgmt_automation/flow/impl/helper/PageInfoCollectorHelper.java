package com.productiveedge.content_mgmt_automation.flow.impl.helper;

import com.productiveedge.content_mgmt_automation.flow.exception.InvalidHrefException;
import com.productiveedge.content_mgmt_automation.flow.exception.InvalidJarRequestException;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class PageInfoCollectorHelper {
    private static final String A_TAG_REGEX = "<a[^>]* href=\"([^\"]*)\"";
    private static final Pattern A_TAG_PATTERN = Pattern.compile(A_TAG_REGEX);
    private static final String EXTRACT_DOMAIN_REGEX = "^(?:https?:\\/\\/)?(?:[^@\\/\\n]+@)?(?:www\\.)?([^:\\/?\\n]+)";
    private static final Pattern EXTRACT_DOMAIN_PATTERN = Pattern.compile(EXTRACT_DOMAIN_REGEX);
    private static final String GENERATE_KEY_REGEX = "(http(s)?://)|(www\\.)";

    public static Set<String> extractHtmlHrefs(String html) {
        Set<String> result = new HashSet<>();
        Matcher matcher = A_TAG_PATTERN.matcher(html);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result
                .parallelStream()
                .map(e -> e.substring(e.indexOf("href=\""), e.length() - 1).substring(6))
                .collect(Collectors.toSet());
    }

    public static String getDomain(String url) throws InvalidHrefException {
        try {
            Matcher matcher = EXTRACT_DOMAIN_PATTERN.matcher(url);
            matcher.find();
            String domain = matcher.group(1);
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (IllegalArgumentException e) {
            throw new InvalidHrefException("Can't extract domain from ulr " + url, e);
        }

    }

    public static boolean isUrlValid(String url) throws InvalidHrefException{
        try {
            new URL(url).toURI();
            return true;
        } catch (URISyntaxException | MalformedURLException e) {
            throw new InvalidHrefException(e);
        }
    }

    public static String generateKey(String url) {
        url = url.replaceAll(GENERATE_KEY_REGEX, "");
        if (url.contains("?")) {
            url = url.substring(0, url.indexOf("?"));

        }
        if (url.contains("#")) {
            url = url.substring(0, url.indexOf("#"));

        }
        if (url.endsWith("#")) {
            url = url.substring(0, url.length() - 1);

        }
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    public static String createHomePageUrl(String protocol, String domainName, String port, String url) throws InvalidJarRequestException {
        String homePageUrl = url;
        if (homePageUrl == null || "".equals(homePageUrl)) {
            if (Objects.isNull(protocol) || Objects.isNull(domainName) || "".equals(protocol) || "".equals(domainName)) {
                throw new InvalidJarRequestException("");
            } else {
                homePageUrl = protocol + "://" + domainName;
                if (!Objects.isNull(port) && !"".equals(port)) {
                    homePageUrl = homePageUrl + ":" + port;
                }
            }

        }
        return homePageUrl;
    }

    public static String generateNameByKey(String key) {
        return key.replaceAll("[./:]", "_");
    }
}
