package dst.ass2.ws;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;


/**
 * Utility methods for XML processing.
 */
public class XMLUtils {

	/**
	 * Convert a string to an Element.
	 * @param xml
	 * @return
	 */
	public static Element toElement(String xml) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		factory.setValidating(false);
		try {
			factory.setFeature("http://xml.org/sax/features/validation", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document d = builder.parse(new InputSource(new StringReader(xml)));
			return d.getDocumentElement();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Find all descendant elements with a given name, for a parent element.
	 * @param e
	 * @param name
	 * @return
	 */
	public static List<Element> findDescendantsByName(Element e, String name) {
		List<Element> result = new LinkedList<Element>();
		findDescendantsByName(e, name, result);
		return result;
	}

	/**
	 * Return a pretty-printed string representation of a given XML Element.
	 * @param el
	 * @return
	 */
	public static String toString(Element element) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			Transformer tr = TransformerFactory.newInstance().newTransformer();
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
				tr.setOutputProperty(
						"{http://xml.apache.org/xslt}indent-amount", "2");
				tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.transform(new DOMSource(element), new StreamResult(baos));
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
		return new String(baos.toByteArray());
	}

	/**
	 * Get all direct child elements of a parent element.
	 * @param e
	 * @return
	 */
	public static List<Element> getChildElements(Element e) {
		return getChildElements(e, null);
	}

	/**
	 * Get all direct child elements with a given name, for a parent element.
	 * @param e
	 * @return
	 */
	public static List<Element> getChildElements(Element e, String name) {
		if(e == null)
			return null;
		List<Element> result = new LinkedList<Element>();
		NodeList list = e.getChildNodes();
		for(int i = 0; i < list.getLength(); i++) {
			Node n = list.item(i);
			if(n instanceof Element) {
				if(name == null || name.equals(((Element) n).getLocalName())
						|| ((Element) n).getLocalName().endsWith(":" + name)) {
					result.add((Element) n);
				}
			}
		}
		return result;
	}

	private static void findDescendantsByName(Element e, String name, List<Element> list) {
		if(e.getLocalName().equals(name))
			list.add(e);
		for(Element e1 : getChildElements(e))
			findDescendantsByName(e1, name, list);
	}

}
