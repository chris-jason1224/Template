package com.cj.processor.util;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


public class PackageParserHandler extends DefaultHandler {
    private String value = null;
    private String packageName = null;

    public PackageParserHandler() {
    }

    public String getPackageName() {
        return this.packageName;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        System.out.println("SAX Start");
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
        System.out.println("SAX End");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if(qName.equals("manifest")) {
            int num = attributes.getLength();

            for(int i = 0; i < num; ++i) {
                if(attributes.getQName(i).equals("package")) {
                    this.packageName = attributes.getValue(i);
                    break;
                }
            }
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

}
