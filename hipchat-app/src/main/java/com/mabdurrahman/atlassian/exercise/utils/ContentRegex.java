package com.mabdurrahman.atlassian.exercise.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class ContentRegex {

    private static final String URL_VALID_GTLD =
            "(?:(?:" +
                    join(TldLists.GTLDS, "|") +
                    ")(?=[^\\p{Alnum}@]|$))";
    private static final String URL_VALID_CCTLD =
            "(?:(?:" +
                    join(TldLists.CTLDS, "|") +
                    ")(?=[^\\p{Alnum}@]|$))";

    private static final String UNICODE_SPACES = "[" +
            "\\u0009-\\u000d" +     //  # White_Space # Cc   [5] <control-0009>..<control-000D>
            "\\u0020" +             // White_Space # Zs       SPACE
            "\\u0085" +             // White_Space # Cc       <control-0085>
            "\\u00a0" +             // White_Space # Zs       NO-BREAK SPACE
            "\\u1680" +             // White_Space # Zs       OGHAM SPACE MARK
            "\\u180E" +             // White_Space # Zs       MONGOLIAN VOWEL SEPARATOR
            "\\u2000-\\u200a" +     // # White_Space # Zs  [11] EN QUAD..HAIR SPACE
            "\\u2028" +             // White_Space # Zl       LINE SEPARATOR
            "\\u2029" +             // White_Space # Zp       PARAGRAPH SEPARATOR
            "\\u202F" +             // White_Space # Zs       NARROW NO-BREAK SPACE
            "\\u205F" +             // White_Space # Zs       MEDIUM MATHEMATICAL SPACE
            "\\u3000" +             // White_Space # Zs       IDEOGRAPHIC SPACE
            "]";

    private static final String CYRILLIC_CHARS = "\\u0400-\\u04FF"; // Cyrillic
    private static final String LATIN_ACCENTS_CHARS = "\\u00c0-\\u00d6\\u00d8-\\u00f6\\u00f8-\\u00ff" + // Latin-1
            "\\u0100-\\u024f" + // Latin Extended A and B
            "\\u0253\\u0254\\u0256\\u0257\\u0259\\u025b\\u0263\\u0268\\u026f\\u0272\\u0289\\u028b" + // IPA Extensions
            "\\u02bb" + // Hawaiian
            "\\u0300-\\u036f" + // Combining diacritics
            "\\u1e00-\\u1eff"; // Latin Extended Additional (mostly for Vietnamese)

    /* URL related hash regex collection */
    private static final String URL_VALID_PRECEEDING_CHARS = "(?:[^A-Z0-9@＠$#＃\u202A-\u202E]|^)";

    private static final String URL_VALID_CHARS = "[\\p{Alnum}" + LATIN_ACCENTS_CHARS + "]";
    private static final String URL_VALID_SUBDOMAIN = "(?>(?:" + URL_VALID_CHARS + "[" + URL_VALID_CHARS + "\\-_]*)?" + URL_VALID_CHARS + "\\.)";
    private static final String URL_VALID_DOMAIN_NAME = "(?:(?:" + URL_VALID_CHARS + "[" + URL_VALID_CHARS + "\\-]*)?" + URL_VALID_CHARS + "\\.)";
    /* Any non-space, non-punctuation characters. \p{Z} = any kind of whitespace or invisible separator. */
    private static final String URL_VALID_UNICODE_CHARS = "[.[^\\p{Punct}\\s\\p{Z}\\p{InGeneralPunctuation}]]";
    private static final String URL_PUNYCODE = "(?:xn--[0-9a-z]+)";
    private static final String SPECIAL_URL_VALID_CCTLD = "(?:(?:" + "co|tv" + ")(?=[^\\p{Alnum}@]|$))";

    private static final String URL_VALID_DOMAIN =
            "(?:" +                                                   // subdomains + domain + TLD
                URL_VALID_SUBDOMAIN + "+" + URL_VALID_DOMAIN_NAME +   // e.g. www.twitter.com, foo.co.jp, bar.co.uk
                "(?:" + URL_VALID_GTLD + "|" + URL_VALID_CCTLD + "|" + URL_PUNYCODE + ")" +
            ")" +
            "|(?:" +                                                  // domain + gTLD + some ccTLD
                URL_VALID_DOMAIN_NAME +                                 // e.g. twitter.com
                "(?:" + URL_VALID_GTLD + "|" + URL_PUNYCODE + "|" + SPECIAL_URL_VALID_CCTLD + ")" +
            ")" +
            "|(?:" + "(?<=https?://)" +
                "(?:" +
                    "(?:" + URL_VALID_DOMAIN_NAME + URL_VALID_CCTLD + ")" +  // protocol + domain + ccTLD
                    "|(?:" +
                        URL_VALID_UNICODE_CHARS + "+\\." +                     // protocol + unicode domain + TLD
                        "(?:" + URL_VALID_GTLD + "|" + URL_VALID_CCTLD + ")" +
                    ")" +
                ")" +
            ")" +
            "|(?:" +                                                  // domain + ccTLD + '/'
                URL_VALID_DOMAIN_NAME + URL_VALID_CCTLD + "(?=/)" +     // e.g. t.co/
            ")";

    private static final String URL_VALID_PORT_NUMBER = "[0-9]++";

    private static final String URL_VALID_GENERAL_PATH_CHARS = "[a-z" + CYRILLIC_CHARS + "0-9!\\*';:=\\+,.\\$/%#\\[\\]\\-_~\\|&@" + LATIN_ACCENTS_CHARS + "]";
    /** Allow URL paths to contain up to two nested levels of balanced parentheses
     *  1. Used in Wikipedia URLs like /Primer_(film)
     *  2. Used in IIS sessions like /S(dfd346)/
     *  3. Used in Rdio URLs like /track/We_Up_(Album_Version_(Edited))/
     **/
    private static final String URL_BALANCED_PARENS =
            "\\(" +
                "(?:" +
                    URL_VALID_GENERAL_PATH_CHARS + "+" +
                    "|" +
                    // allow one nested level of balanced parentheses
                    "(?:" +
                        URL_VALID_GENERAL_PATH_CHARS + "*" +
                        "\\(" +
                            URL_VALID_GENERAL_PATH_CHARS + "+" +
                        "\\)" +
                        URL_VALID_GENERAL_PATH_CHARS + "*" +
                    ")" +
                ")" +
            "\\)";

    /** Valid end-of-path characters (so /foo. does not gobble the period).
     *   2. Allow =&# for empty URL parameters and other URL-join artifacts
     **/
    private static final String URL_VALID_PATH_ENDING_CHARS = "[a-z" + CYRILLIC_CHARS + "0-9=_#/\\-\\+" + LATIN_ACCENTS_CHARS + "]|(?:" + URL_BALANCED_PARENS + ")";

    private static final String URL_VALID_PATH =
            "(?:" +
                "(?:" +
                    URL_VALID_GENERAL_PATH_CHARS + "*" +
                    "(?:" + URL_BALANCED_PARENS + URL_VALID_GENERAL_PATH_CHARS + "*)*" +
                    URL_VALID_PATH_ENDING_CHARS +
                ")" +
                "|" +
                "(?:@" +
                    URL_VALID_GENERAL_PATH_CHARS +
                "+)" +
            ")";

    private static final String URL_VALID_URL_QUERY_CHARS = "[a-z0-9!?\\*'\\(\\);:&=\\+\\$/%#\\[\\]\\-_\\.,~\\|@]";
    private static final String URL_VALID_URL_QUERY_ENDING_CHARS = "[a-z0-9_&=#/]";

    private static final String VALID_URL_PATTERN_STRING =
            "(" +                                                            //  $1 total match
                "(" + URL_VALID_PRECEEDING_CHARS + ")" +                       //  $2 Preceeding chracter
                "(" +                                                          //  $3 URL
                    "(https?://)?" +                                             //  $4 Protocol (optional)
                    "(" + URL_VALID_DOMAIN + ")" +                               //  $5 Domain(s)
                    "(?::(" + URL_VALID_PORT_NUMBER + "))?" +                     //  $6 Port number (optional)
                    "(/" +
                        URL_VALID_PATH + "*+" +
                    ")?" +                                                      //  $7 URL Path and anchor
                    "(\\?" + URL_VALID_URL_QUERY_CHARS + "*" +                   //  $8 Query String
                        URL_VALID_URL_QUERY_ENDING_CHARS + ")?" +
                ")" +
            ")";

    private static final String AT_SIGNS_CHARS = "@";

  /* Begin public constants */
    public static final Pattern RTL_CHARACTERS;

    public static final Pattern AT_SIGNS;
    public static final Pattern VALID_MENTION;
    public static final int VALID_MENTION_GROUP_BEFORE = 1;
    public static final int VALID_MENTION_GROUP_AT = 2;
    public static final int VALID_MENTION_GROUP_USERNAME = 3;

    public static final Pattern VALID_EMOTICON;
    public static final int VALID_EMOTICON_GROUP_LEFT_PAREN = 1;
    public static final int VALID_EMOTICON_GROUP_VALUE = 2;
    public static final int VALID_EMOTICON_GROUP_RIGHT_PAREN = 3;

    /**
     * Regex to extract URL (it also includes the text preceding the url).
     *
     * This regex does not reflect its name and {@link ContentRegex#VALID_URL_GROUP_URL} match
     * should be checked in order to match a valid url. This is not ideal, but the behavior is
     * being kept to ensure backwards compatibility. Ideally this regex should be
     * implemented with a negative lookbehind as opposed to a negated character class
     * but lack of JS support increases main overhead if the logic is different by
     * platform.
     */

    public static final Pattern VALID_URL;
    public static final int VALID_URL_GROUP_ALL          = 1;
    public static final int VALID_URL_GROUP_BEFORE       = 2;
    public static final int VALID_URL_GROUP_URL          = 3;
    public static final int VALID_URL_GROUP_PROTOCOL     = 4;
    public static final int VALID_URL_GROUP_DOMAIN       = 5;
    public static final int VALID_URL_GROUP_PORT         = 6;
    public static final int VALID_URL_GROUP_PATH         = 7;
    public static final int VALID_URL_GROUP_QUERY_STRING = 8;

    public static final Pattern INVALID_URL_WITHOUT_PROTOCOL_MATCH_BEGIN;

    public static final Pattern VALID_DOMAIN;

    // initializing in a static synchronized block, there appears to be thread safety issues with Pattern.compile in android
    static {
        synchronized(ContentRegex.class) {
            RTL_CHARACTERS = Pattern.compile("[\u0600-\u06FF\u0750-\u077F\u0590-\u05FF\uFE70-\uFEFF]");
            AT_SIGNS = Pattern.compile("[" + AT_SIGNS_CHARS + "]");

            VALID_MENTION = Pattern.compile("([^a-z0-9_!#$%&*" + AT_SIGNS_CHARS + "]|^|(?:^|[^a-z0-9_+~.-])RT:?)(" + AT_SIGNS + "+)([a-z0-9_]+)?", Pattern.CASE_INSENSITIVE);

            VALID_EMOTICON = Pattern.compile("(\\()([a-z0-9]{1,15})(\\))", Pattern.CASE_INSENSITIVE);

            VALID_URL = Pattern.compile(VALID_URL_PATTERN_STRING, Pattern.CASE_INSENSITIVE);
            INVALID_URL_WITHOUT_PROTOCOL_MATCH_BEGIN = Pattern.compile("[-_./]$");

            VALID_DOMAIN = Pattern.compile(URL_VALID_DOMAIN, Pattern.CASE_INSENSITIVE);
        }
    }

    private static String join(Collection<?> col, String delim) {
        final StringBuilder sb = new StringBuilder();
        final Iterator<?> iter = col.iterator();
        if (iter.hasNext())
            sb.append(iter.next().toString());
        while (iter.hasNext()) {
            sb.append(delim);
            sb.append(iter.next().toString());
        }
        return sb.toString();
    }
}
