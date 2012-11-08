package com.threed.jpct.skeleton.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.threed.jpct.util.XMLFactory;
import com.threed.jpct.util.XMLNode;

public class XMLElement
{
	XMLNode node;
	
	public XMLElement(XMLNode node)
	{
		this.node = node;
	}
	public XMLElement getChild(String name)
	{
		Vector nodes = XMLFactory.getInstance().getMatchingNodes(node.getName()+"/"+name,node);
		if(nodes.size() == 0)
			return null;
		
		XMLNode newNode = (XMLNode)nodes.get(0);
		return new XMLElement(newNode);
	}
	public List<XMLElement> getChildren(String name)
	{
		List<XMLElement> elements = new ArrayList<XMLElement>(20);
		Vector<?> nodes = XMLFactory.getInstance().getMatchingNodes(node.getName()+"/"+name,node);
		
		for(Object o : nodes)
			elements.add(new XMLElement((XMLNode)o));
		
		return elements;
	}
	public String getAttributeValue(String name)
	{
		String value = node.getAttributeValue(name);
		return (value.length() != 0) ? value : null;
	}
	public String getText()
	{
		return node.getData();
	}
	public String getChildText(String name)
	{
		XMLElement element = getChild(name);
		
		if(element != null)
			return element.getText();
		else
			return null;
	}
}