package jp.futuresoftware.android.sakura.common;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;
import org.xml.sax.SAXException;

/**
 * XMLパーサークラス
 * 
 * @author toshiyuki
 *
 */
public class RecursiveNode
{
    private Node currentNode;

    /**
     * コンストラクタ
     * 
     * @param node XMLノード
     */
    public RecursiveNode(Node node) {
        if (node != null) {
            this.currentNode = node;
        } else {
            System.out.print("#RecursiveNode.java/RecursiveNode::=");
            System.out.println();
        }
    }

    /**
     * XMLパーサー
     * 
     * @param source
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static RecursiveNode parse(InputStream source)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document d = builder.parse(source);
        return new RecursiveNode(d);
    }

    /**
     * XMLパーサー
     * 
     * @param source
     * @return
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    public static RecursiveNode parse(String source)
            throws ParserConfigurationException, SAXException, IOException {
    	return RecursiveNode.parse(new ByteArrayInputStream(source.getBytes()));
    }

    /**
     * 該当するタグの数を返却する
     * 
     * @param tag
     * @return
     */
    public int count(String tag) {
        int counter = 0;
        NodeList nodes = this.currentNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().equals(tag)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * @param tag
     */
    public void movedown(String tag) {
        this.movedown(tag, 0);
    }

    /**
     * @param tag
     * @param index
     */
    public void movedown(String tag, int index) {
        int counter = 0;
        NodeList nodes = this.currentNode.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            if (nodes.item(i).getNodeName().equals(tag)) {
                if (index == counter) {
                    this.currentNode = nodes.item(i);
                } else {
                    counter++;
                }
            }
        }
    }
    
    /**
     * 
     */
    public void moveup() {
        Node n = this.currentNode.getParentNode();
        if (n != null) {
            this.currentNode = n;
        }
    }
    
    /**
     * @param tag
     * @return
     */
    public RecursiveNode n(String tag) {
        return this.n(tag,0);
    }

    /**
     * @param tag
     * @param index
     * @return
     */
    public RecursiveNode n(String tag, int index) {
        NodeList nodes = this.currentNode.getChildNodes();
        int num = nodes.getLength();
        int counter = 0;
        for (int i = 0; i < num; i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals(tag)) {
                if (counter == index) {
                    return new RecursiveNode(node);
                } else {
                    counter++;
                }
            }
        }
        return null;
    }

    // ここから先はNodeのラップ

    /**
     * @param newChild
     * @return
     * @throws DOMException
     */
    public Node appendChild(Node newChild) throws DOMException {
        return this.currentNode.appendChild(newChild);
    }

    /**
     * @param deep
     * @return
     */
    public Node cloneNode(boolean deep) {
        return this.currentNode.cloneNode(deep);
    }

    /**
     * @param other
     * @return
     * @throws DOMException
     */
    public short compareDocumentPosition(Node other) throws DOMException {
        return this.currentNode.compareDocumentPosition(other);
    }

    /**
     * @return
     */
    public NamedNodeMap getAttributes() {
        return this.currentNode.getAttributes();
    }

    /**
     * @return
     */
    public String getBaseURI() {
        return this.currentNode.getBaseURI();
    }

    /**
     * @return
     */
    public NodeList getChildNodes() {
        return this.currentNode.getChildNodes();
    }

    /**
     * @param feature
     * @param version
     * @return
     */
    public Object getFeature(String feature, String version) {
        return this.currentNode.getFeature(feature, version);
    }

    /**
     * @return
     */
    public Node getFirstChild() {
        return this.currentNode.getFirstChild();
    }

    /**
     * @return
     */
    public Node getLastChild() {
        return this.currentNode.getLastChild();
    }

    /**
     * @return
     */
    public String getLocalName() {
        return this.currentNode.getLocalName();
    }

    /**
     * @return
     */
    public String getNamespaceURI() {
        return this.currentNode.getNamespaceURI();
    }

    /**
     * @return
     */
    public Node getNextSibling() {
        return this.currentNode.getNextSibling();
    }

    /**
     * @return
     */
    public String getNodeName() {
        return this.currentNode.getNodeName();

    }

    /**
     * @return
     */
    public short getNodeType() {
        return this.currentNode.getNodeType();
    }

    /**
     * @return
     * @throws DOMException
     */
    public String getNodeValue() throws DOMException {
        return this.currentNode.getNodeValue();
    }

    /**
     * @return
     */
    public Document getOwnerDocument() {
        return this.currentNode.getOwnerDocument();
    }

    /**
     * @return
     */
    public Node getParentNode() {
        return this.currentNode.getParentNode();
    }

    /**
     * @return
     */
    public String getPrefix() {
        return this.currentNode.getPrefix();
    }

    /**
     * @return
     */
    public Node getPreviousSibling() {
        return this.currentNode.getPreviousSibling();
    }

    /**
     * @return
     * @throws DOMException
     */
    public String getTextContent() throws DOMException {
        return this.currentNode.getTextContent();
    }

    /**
     * @param key
     * @return
     */
    public Object getUserData(String key) {
        return this.currentNode.getUserData(key);
    }

    /**
     * @return
     */
    public boolean hasAttributes() {
        return this.currentNode.hasAttributes();
    }

    /**
     * @return
     */
    public boolean hasChildNodes() {
        return this.currentNode.hasChildNodes();
    }

    /**
     * @param newChild
     * @param refChild
     * @return
     * @throws DOMException
     */
    public Node insertBefore(Node newChild, Node refChild) throws DOMException {
        return this.currentNode.insertBefore(newChild, refChild);
    }

    /**
     * @param namespaceURI
     * @return
     */
    public boolean isDefaultNamespace(String namespaceURI) {
        return this.currentNode.isDefaultNamespace(namespaceURI);
    }

    /**
     * @param arg
     * @return
     */
    public boolean isEqualNode(Node arg) {
        return this.currentNode.isEqualNode(arg);
    }

    /**
     * @param other
     * @return
     */
    public boolean isSameNode(Node other) {
        return this.currentNode.isSameNode(other);
    }

    /**
     * @param feature
     * @param version
     * @return
     */
    public boolean isSupported(String feature, String version) {
        return this.currentNode.isSupported(feature, version);
    }

    /**
     * @param prefix
     * @return
     */
    public String lookupNamespaceURI(String prefix) {
        return this.currentNode.lookupNamespaceURI(prefix);
    }

    /**
     * @param namespaceURI
     * @return
     */
    public String lookupPrefix(String namespaceURI) {
        return this.currentNode.lookupPrefix(namespaceURI);
    }

    /**
     * 
     */
    public void normalize() {
        this.currentNode.normalize();
    }

    /**
     * @param oldChild
     * @return
     * @throws DOMException
     */
    public Node removeChild(Node oldChild) throws DOMException {
        return this.currentNode.removeChild(oldChild);
    }

    /**
     * @param newChild
     * @param oldChild
     * @return
     * @throws DOMException
     */
    public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
        return this.currentNode.replaceChild(newChild, oldChild);
    }

    /**
     * @param nodeValue
     * @throws DOMException
     */
    public void setNodeValue(String nodeValue) throws DOMException {
        this.currentNode.setNodeValue(nodeValue);
    }

    /**
     * @param prefix
     * @throws DOMException
     */
    public void setPrefix(String prefix) throws DOMException {
        this.currentNode.setPrefix(prefix);
    }

    /**
     * @param textContent
     * @throws DOMException
     */
    public void setTextContent(String textContent) throws DOMException {
        this.currentNode.setTextContent(textContent);
    }

    /**
     * @param key
     * @param data
     * @param handler
     * @return
     */
    public Object setUserData(String key, Object data, UserDataHandler handler) {
        return this.currentNode.setUserData(key, data, handler);
    }
    
    public static String getAttribute(RecursiveNode node, String attribute, String nothing)
    {
    	if (node.getAttributes().getNamedItem(attribute) == null){ return nothing; }    	
    	return node.getAttributes().getNamedItem(attribute).getTextContent();
    }
}
