package com.threed.jpct.skeleton.util;

import com.threed.jpct.util.XMLFactory;

public class XMLParserFactory
{
	private static XMLParserFactory defaultInstance = new XMLParserFactory();
	
	public static XMLParserFactory getInstance()
	{
		return defaultInstance;
	}
	public XMLElement parseXML(String input)
	{
		return new XMLElement(XMLFactory.getInstance().parseXML(input));
	}
}