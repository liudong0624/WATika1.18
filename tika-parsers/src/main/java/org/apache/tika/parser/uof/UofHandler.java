package org.apache.tika.parser.uof;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.sax.ContentHandlerDecorator;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class UofHandler extends ContentHandlerDecorator {
    public Metadata metadata;
    private boolean flag = false;
    private boolean flag1 = false;
    String currentElement;

    public UofHandler(Metadata metadata,ContentHandler handler){
        super(handler);
        this.metadata = metadata;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if("字:文本串".equals(qName))
        {
            flag = true;
            super.startElement(uri, localName, qName, attributes);
        }else if("uof:元数据".equals(qName)){
            flag1 = true;
        }else if(flag1) {
            currentElement = qName;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if("字:文本串".equals(qName))
        {
            flag = false;
            super.endElement(uri, localName, qName);
        }else if("uof:元数据".equals(qName)){
            flag1 = false;
        }else if(flag1) {
            currentElement = null;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if(flag) {
            super.characters(ch, start, length);
        } else if(flag1){
            metadata.set(currentElement.substring(currentElement.indexOf(':')+1,currentElement.length()),
                    new String(ch,start,length));
        }
    }

}
