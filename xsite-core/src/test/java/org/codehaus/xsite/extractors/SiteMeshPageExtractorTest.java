package org.codehaus.xsite.extractors;

import static org.junit.Assert.assertEquals;

import org.codehaus.xsite.extractors.sitemesh.rules.AddFirstChildClassToHeader;
import org.codehaus.xsite.extractors.sitemesh.rules.DropDivOfClassSection;
import org.codehaus.xsite.io.CommonsFileSystem;
import org.codehaus.xsite.model.Page;
import org.junit.Test;

import com.opensymphony.module.sitemesh.html.TagRule;
import com.opensymphony.module.sitemesh.html.TextFilter;
import com.opensymphony.module.sitemesh.html.rules.TagReplaceRule;

/**
 * @author J&ouml;rg Schaible
 */
public class SiteMeshPageExtractorTest {

    private SiteMeshPageExtractor pageExtractor = new SiteMeshPageExtractor(
                new TagRule[]{
                	new AddFirstChildClassToHeader(),
                	new DropDivOfClassSection(),
                	new TagReplaceRule("ul", "ol")
                }, new TextFilter[0], new CommonsFileSystem());

    @Test
    public void canExtractPageBodyStartingWithHeader() {
        final String html = "<html><head><title>JUnit</title></head><body><h1>Header</h1></body></html>";
        final Page page = pageExtractor.extractPage("JUnit.html", html);
        assertEquals("JUnit", page.getTitle());
        assertEquals("<h1 class=\"FirstChild\">Header</h1>", page.getBody());
    }
    
    @Test
    public void canExtractPagesWithoutSharingLinksBetweenPages() {
        final String html1 = "<html><head><title>JUnit</title></head><body><a href=\"foo.html\">Header</a></body></html>";
        final String html2 = "<html><head><title>Test</title></head><body><p>Header</p></body></html>";
        final Page page1 = pageExtractor.extractPage("JUnit.html", html1);
        final Page page2 = pageExtractor.extractPage("Test.html", html2);
        assertEquals(1, page1.getLinks().size());
        assertEquals(0, page2.getLinks().size());
        assertEquals("<p>Header</p>", page2.getBody());
    }
    
    @Test
    public void canExtractPagesWithReplacedParagraphTags() {
        final String html = "<html><head><title>JUnit</title></head><body><div><ul><li>Header</li></ul></div></body></html>";
        final Page page = pageExtractor.extractPage("JUnit.html", html);
        assertEquals("JUnit", page.getTitle());
        assertEquals("<div><ol><li>Header</li></ol></div>", page.getBody());
    }
    
    @Test
    public void canExtractPagesWithDroppedDivsOfClassSection() {
        final String html = "<html><head><title>JUnit</title></head><body><div class='section'><h1>H1</h1><div>Text</div><div class='section'><h2>H2</h2><div>Text</div></div></div></body></html>";
        final Page page = pageExtractor.extractPage("JUnit.html", html);
        assertEquals("JUnit", page.getTitle());
        assertEquals("<h1 class=\"FirstChild\">H1</h1><div>Text</div><h2>H2</h2><div>Text</div>", page.getBody());
    }
}
