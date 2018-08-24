package org.apache.tika.parser.uof;

import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CloseShieldInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.parser.AbstractParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.*;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UofParser extends AbstractParser {

    private static final Set<MediaType> SUPPORTED_TYPES=
            Collections.unmodifiableSet(new HashSet<MediaType>(
                    Arrays.asList(
                            MediaType.application("uof")
                    )
            ));
    @Override
    public Set<MediaType> getSupportedTypes(ParseContext context) {
        return SUPPORTED_TYPES;
    }

    @Override
    public void parse(InputStream stream, ContentHandler handler, Metadata metadata, ParseContext context) throws IOException, SAXException, TikaException {
        if(metadata.get(Metadata.CONTENT_TYPE) == null){
            metadata.set(Metadata.CONTENT_TYPE,"application/uof");
        }
        final XHTMLContentHandler xhtml =
                new XHTMLContentHandler(handler, metadata);
        xhtml.startDocument();
        xhtml.startElement("p");

        TaggedContentHandler tagged = new TaggedContentHandler(handler);
        try {
            context.getSAXParser().parse(
                    new org.apache.commons.io.input.CloseShieldInputStream(stream),
                    new UofHandler(metadata,new OfflineContentHandler(new EmbeddedContentHandler(
                            getContentHandler(tagged, metadata, context)))));
        } catch (SAXException e) {
            tagged.throwIfCauseOf(e);
            throw new TikaException("UOF parse error", e);
        } finally {
            xhtml.endElement("p");
            xhtml.endDocument();
        }

    }

    protected ContentHandler getContentHandler(
            ContentHandler handler, Metadata metadata, ParseContext context) {
        return new TextContentHandler(handler, true);
    }
}
