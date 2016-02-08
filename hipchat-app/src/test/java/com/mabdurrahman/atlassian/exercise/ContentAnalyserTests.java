package com.mabdurrahman.atlassian.exercise;

import com.mabdurrahman.atlassian.exercise.model.ContentEntity;
import com.mabdurrahman.atlassian.exercise.utils.ContentAnalyser;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Mahmoud Abdurrahman (m.abdurrahman@startappz.com) on 2/8/16.
 */
@RunWith(Suite.class)
@SuiteClasses({
        ContentAnalyserTests.MentionsTest.class,
        ContentAnalyserTests.EmoticonsTest.class,
        ContentAnalyserTests.URLsTest.class,
        ContentAnalyserTests.AdvancedEntitiesTest.class
})
public class ContentAnalyserTests {

    protected static ContentAnalyser analyser;

    @BeforeClass
    public static void setUp() throws Exception {
        analyser = new ContentAnalyser();
    }

    /**
     * Tests for the extractMentions{WithIndices} methods
     */
    public static class MentionsTest {

        @Test
        public void testMentionAtTheBeginning() throws Exception {
            List<String> extracted = analyser.extractMentions("@user mention");
            assertList("Failed to extract mention at the beginning", new String[]{"user"}, extracted);
        }

        @Test
        public void testMentionWithLeadingSpace() throws Exception {
            List<String> extracted = analyser.extractMentions(" @user mention");
            assertList("Failed to extract mention with leading space", new String[]{"user"}, extracted);
        }

        @Test
        public void testMentionInMidText() throws Exception {
            List<String> extracted = analyser.extractMentions("mention @user here");
            assertList("Failed to extract mention in mid text", new String[]{"user"}, extracted);
        }

        @Test
        public void testMultipleMentions() throws Exception {
            List<String> extracted = analyser.extractMentions("mention @user1 here and @user2 here");
            assertList("Failed to extract multiple mentioned users", new String[]{"user1", "user2"}, extracted);
        }

        @Test
        public void testInvalidMention() throws Exception {
            List<String> extracted = analyser.extractMentions("@ invalid mention.");
            assertEmptyList("Extracted an invalid mention: " + extracted, extracted);
        }

        @Test
        public void testMentionWithIndices() throws Exception {
            List<ContentEntity> extracted = analyser.extractMentionsWithIndices(" @user1 mention @user2 here @user3 ");
            assertEquals(extracted.size(), 3);
            assertEquals(extracted.get(0).getStart(), 1);
            assertEquals(extracted.get(0).getEnd(), 7);
            assertEquals(extracted.get(1).getStart(), 16);
            assertEquals(extracted.get(1).getEnd(), 22);
            assertEquals(extracted.get(2).getStart(), 28);
            assertEquals(extracted.get(2).getEnd(), 34);
        }
        
    }

    /**
     * Tests for the extractEmoticons{WithIndices} methods
     */
    public static class EmoticonsTest {

        @Test
        public void testEmoticonAtTheBeginning() throws Exception {
            List<String> extracted = analyser.extractEmoticons("(smile) emoticon");

            assertList("Failed to extract emoticon at the beginning", new String[]{"smile"}, extracted);
        }

        @Test
        public void testEmoticonWithLeadingSpace() throws Exception {
            List<String> extracted = analyser.extractEmoticons(" (smile) emoticon");

            assertList("Failed to extract emoticon with leading space", new String[]{"smile"}, extracted);
        }

        @Test
        public void testEmoticonInMidText() throws Exception {
            List<String> extracted = analyser.extractEmoticons("emoticon (smile) here");

            assertList("Failed to extract emoticon in mid text", new String[]{"user"}, extracted);
        }

        @Test
        public void testMultipleEmoticons() throws Exception {
            List<String> extracted = analyser.extractEmoticons("emoticon (smile) and (anger)");

            assertList("Failed to extract multiple emoticons", new String[]{"smile", "anger"}, extracted);
        }

        @Test
        public void testInvalidEmoticons() throws Exception {
            List<String> extracted = analyser.extractEmoticons("Good morning! () (@coffee) (a) (challengeaccepted) (atlassian)");

            assertList("Extracted invalid emoticons: " + extracted, new String[]{"a", "atlassian"}, extracted);
        }

        @Test
        public void testEmoticonWithIndices() throws Exception {
            List<ContentEntity> extracted = analyser.extractEmoticonsWithIndices(" (smile) and (anger) and (sad) are all emoticons ");
            assertEquals(extracted.size(), 3);
            assertEquals(extracted.get(0).getStart(), 1);
            assertEquals(extracted.get(0).getEnd(), 8);
            assertEquals(extracted.get(1).getStart(), 13);
            assertEquals(extracted.get(1).getEnd(), 20);
            assertEquals(extracted.get(2).getStart(), 25);
            assertEquals(extracted.get(2).getEnd(), 30);
        }

    }

    /**
     * Tests for the extractURLs{WithIndices} methods
     */
    public static class URLsTest {

        @Test
        public void testUrlWithIndices() throws Exception {
            List<ContentEntity> extracted = analyser.extractURLsWithIndices("https://goo.gl/omqXF0 url https://www.atlassian.com ");

            assertEquals(extracted.size(), 2);
            assertEquals(extracted.get(0).getStart(), 0);
            assertEquals(extracted.get(0).getEnd(), 21);
            assertEquals(extracted.get(1).getStart(), 26);
            assertEquals(extracted.get(1).getEnd(), 51);
        }

        @Test
        public void testURLFollowedByPunctuations() throws Exception {
            String text ="http://games.aarp.org/games/mahjongg-dimensions.aspx!!!!!!";

            assertList("Failed to extract URLs followed by punctuations",
                    new String[]{"http://games.aarp.org/games/mahjongg-dimensions.aspx"},
                    analyser.extractURLs(text));
        }

        @Test
        public void testUrlWithPunctuation() throws Exception {
            String[] urls = new String[] {
                    "http://www.foo.com/foo/path-with-period./",
                    "http://www.foo.org.za/foo/bar/688.1",
                    "http://www.foo.com/bar-path/some.stm?param1=foo;param2=P1|0||P2|0",
                    "http://foo.com/bar/123/foo_&_bar/",
                    "http://foo.com/bar(test)bar(test)bar(test)",
                    "www.foo.com/foo/path-with-period./",
                    "www.foo.org.za/foo/bar/688.1",
                    "www.foo.com/bar-path/some.stm?param1=foo;param2=P1|0||P2|0",
                    "foo.com/bar/123/foo_&_bar/"
            };

            for (String url : urls) {
                List<String> extracted = analyser.extractURLs(url);

                assertEquals(extracted.size(), 1);
                assertEquals(url, extracted.get(0));
            }
        }

    }

    /**
     * Advanced tests for the extractEntities{WithIndices} method
     */
    public static class AdvancedEntitiesTest {

        @Test
        public void testEntitiesAllTogether() throws Exception {
            List<String> extracted = analyser.extractEntities("@bob @john (smile) The emoticon cheat-sheet to make you oh-so emoticon literate: https://t.co/ewYZty149a ");

            assertList("Failed to extract full entities", new String[]{"bob", "john", "smile", "https://t.co/ewYZty149a"}, extracted);
        }

        @Test
        public void testMentionOverlappingUrl() throws Exception {
            List<String> extracted = analyser.extractEntities("Here's an example of URL that overlaps with mention-like-keyword: http://example.com/@dave ");

            assertList("Failed to extract URL overlapping mention", new String[]{"http://example.com/@dave"}, extracted);
        }

        @Test
        public void testEmoticonOverlappingUrl() throws Exception {
            List<String> extracted = analyser.extractEntities("Here's an example of URL that overlaps an emoticon-like-pattern: http://example.com/test_(test) ");

            assertList("Failed to extract URL overlapping emoticon", new String[]{"http://example.com/test_(test)"}, extracted);
        }

        @Test
        public void testMentionAndEmoticonOverlappingUrl() throws Exception {
            List<String> extracted = analyser.extractEntities("Here's an example of URL that overlaps both mention-like-keyword and an emoticon-like-pattern: http://example.com/test_(test)/@dave ");

            assertList("Failed to extract URL overlapping both mention and emoticon", new String[]{"http://example.com/test_(test)/@dave"}, extracted);
        }

        @Test
        public void testMentionOverlappingEmoticon() throws Exception {
            List<String> extracted = analyser.extractEntities("Good morning! (@megusta) (@coffee)");

            assertList("Extracted invalid emoticons: " + extracted, new String[]{"a", "applePearPeach"}, extracted);
        }

        @Test
        public void testEmailAddressShouldNotConsideredAsMention() throws Exception {
            List<String> extracted = analyser.extractEntities("Example of email address: username@somewhere.foo ");

            assertEmptyList("Extracted an invalid mention: " + extracted, extracted);
        }

    }

    /**
     * Helper method for asserting that the List of extracted Strings match the expected values.
     *
     * @param message to display on failure
     * @param expected Array of Strings that were expected to be extracted
     * @param actual List of Strings that were extracted
     */
    protected static void assertList(String message, String[] expected, List<String> actual) {
        List<String> expectedList = Arrays.asList(expected);
        if (expectedList.size() != actual.size()) {
            fail(message + "\n\nExpected list and extracted list are different sizes:\n" +
                    "  Expected (" + expectedList.size() + "): " + expectedList + "\n" +
                    "  Actual   (" + actual.size() + "): " + actual);
        } else {
            for (int i=0; i < expectedList.size(); i++) {
                assertEquals(expectedList.get(i), actual.get(i));
            }
        }
    }

    /**
     * Helper method for asserting that the List of extracted Strings is empty.
     *
     * @param message to display on failure
     * @param actual List of Strings that were extracted
     */
    protected static void assertEmptyList(String message, List<String> actual) {
        if (!actual.isEmpty()) fail(message);
    }
}
