package com.mabdurrahman.atlassian.exercise.utils;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/14/16.
 */
public class YahooYqlUtils {

    public static String getHtmlTitleQuery(String url) {
        return "SELECT * FROM html WHERE url=\"" + url + "\" and compat=\"html5\" and xpath=\"//xhtml:title|//title\" | truncate(count=1)";
    }

}
