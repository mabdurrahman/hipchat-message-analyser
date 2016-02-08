package com.mabdurrahman.atlassian.exercise;

import com.mabdurrahman.atlassian.exercise.utils.ContentRegex;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
public class ContentRegexTests {

    @Test
    public void testMentionsRegex() {
        assertCaptureCount(3, ContentRegex.VALID_MENTION, "@username");
    }

    @Test
    public void testExtractMentions() {
        assertCaptureCount(3, ContentRegex.VALID_MENTION, "sample @user mention");
    }

    @Test
    public void testInvalidMentions() {
        char[] invalidChars = new char[]{'!', '@', '#', '$', '%', '&', '*'};
        for (char c : invalidChars) {
            assertFalse("Failed to ignore a mention preceded by " + c, ContentRegex.VALID_MENTION.matcher("f" + c + "@kn").find());
        }
    }

    @Test
    public void testEmoticonsRegex() {
        assertCaptureCount(3, ContentRegex.VALID_EMOTICON, "(emoticon)");
    }

    @Test
    public void testExtractEmoticons() {
        assertCaptureCount(3, ContentRegex.VALID_EMOTICON, "sample (emoticon) within message");
    }

    @Test
    public void testValidURL() {
        assertCaptureCount(8, ContentRegex.VALID_URL, "http://example.com");
        assertCaptureCount(8, ContentRegex.VALID_URL, "http://はじめよう.みんな");
        assertCaptureCount(8, ContentRegex.VALID_URL, "http://はじめよう.香港");
        assertCaptureCount(8, ContentRegex.VALID_URL, "http://はじめよう.الجزائر");
        assertCaptureCount(8, ContentRegex.VALID_URL, "http://test.scot");
    }

    @Test
    public void testValidURLDoesNotCrashOnLongPaths() {
        String longPathIsLong = "Check out http://example.com/aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        assertTrue("Failed to correctly match a very long path", ContentRegex.VALID_URL.matcher(longPathIsLong).find());
    }

    @Test
    public void testValidUrlDoesNotTakeForeverOnRepeatedPunctuationAtEnd() {
        String[] repeatedPaths = {
                "Try http://example.com/path**********************",
                "http://foo.org/bar/foo-bar-foo-bar.aspx!!!!!! Test"
        };

        for (String text : repeatedPaths) {
            long start = System.currentTimeMillis();
            final int NUM_RUNS = 100;
            for (int i = 0; i < NUM_RUNS - 1; i++) {
                ContentRegex.VALID_URL.matcher(text).find();
                ContentRegex.VALID_URL.matcher(text).matches();
            }
            boolean isValid = ContentRegex.VALID_URL.matcher(text).find();
            ContentRegex.VALID_URL.matcher(text).matches();
            long end = System.currentTimeMillis();

            assertTrue("Should be able to extract a valid URL even followed by punctuations", isValid);

            long duration = (end - start);
            assertTrue("Matching a repeated path end should take less than 10ms (took " + (duration / NUM_RUNS) + "ms)", (duration < 10 * NUM_RUNS) );
        }
    }

    @Test
    public void testValidURLWithoutProtocol() {
        assertTrue("Matching a URL with gTLD without protocol.",
                ContentRegex.VALID_URL.matcher("twitter.com").matches());

        assertTrue("Matching a URL with ccTLD without protocol.",
                ContentRegex.VALID_URL.matcher("www.foo.co.jp").matches());

        assertTrue("Matching a URL with gTLD followed by ccTLD without protocol.",
                ContentRegex.VALID_URL.matcher("www.foo.org.za").matches());

        assertTrue("Should not match a short URL with ccTLD without protocol.",
                ContentRegex.VALID_URL.matcher("http://t.co").matches());

        assertFalse("Should not match a short URL with ccTLD without protocol.",
                ContentRegex.VALID_URL.matcher("it.so").matches());

        assertFalse("Should not match a URL with invalid gTLD.",
                ContentRegex.VALID_URL.matcher("www.xxxxxxx.baz").find());

        assertTrue("Match a short URL with ccTLD and '/' but without protocol.",
                ContentRegex.VALID_URL.matcher("t.co/blahblah").matches());
    }

    @Test
    public void testValidUrlDoesNotOverflowOnLongDomains() {
        String domainIsLong = "cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool.cool";
        assertTrue("Match a super long url", ContentRegex.VALID_URL.matcher(domainIsLong).matches());
    }

    @Test
    public void testInvalidUrlWithInvalidCharacter() {
        char[] invalid_chars = new char[]{'\u202A', '\u202B', '\u202C', '\u202D', '\u202E'};
        for (char c : invalid_chars) {
            assertFalse("Should not extract URLs with invalid character",
                    ContentRegex.VALID_URL.matcher("http://twitt" + c + "er.com").find());
        }
    }

    private void assertCaptureCount(int expectedCount, Pattern pattern, String sample) {
        assertTrue("Pattern failed to match sample: '" + sample + "'",
                pattern.matcher(sample).find());
        assertEquals("Does not have " + expectedCount + " captures as expected: '" + sample + "'",
                expectedCount,
                pattern.matcher(sample).groupCount());
    }
}
