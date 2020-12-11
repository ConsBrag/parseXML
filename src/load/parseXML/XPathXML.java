package load.parseXML;

import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XPathXML {
    public static void main(String[] args) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = documentBuilder.parse("C:\\Users\\admin\\IdeaProjects\\parseXML\\src\\xml\\example\\record\\testXML.xml");

            printAll(document);
//            printCost2(document);
//            printCost3(document);
//            printCost4(document);
//            printCost5(document);

        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    // Печать всех элементов All
    private static void printAll(Document document) throws DOMException, XPathExpressionException {
        System.out.println("Example 1 - Печать всех элементов HTTPSamplerProxy");
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        String s;

        // Пример записи XPath
        // Подный путь до элемента
        //XPathExpression expr = xpath.compile("testXML/jmeterTestPlan/TestPlan/stringProp");
        // Все элементы с таким именем
        //XPathExpression expr = xpath.compile("//stringProp");
        // Элементы, вложенные в другой элемент
        XPathExpression exprName = xpath.compile("//jmeterTestPlan/hashTree/hashTree/hashTree/hashTree/hashTree/HTTPSamplerProxy/attribute::testname");
        XPathExpression expr = xpath.compile("//jmeterTestPlan/hashTree/hashTree/hashTree/hashTree/hashTree/HTTPSamplerProxy/elementProp/collectionProp/elementProp//stringProp[@name='Argument.value']");
        XPathExpression exprMethod = xpath.compile("//jmeterTestPlan/hashTree/hashTree/hashTree/hashTree/hashTree/HTTPSamplerProxy//stringProp[@name='HTTPSampler.method']");
        XPathExpression exprURL = xpath.compile("//jmeterTestPlan/hashTree/hashTree/hashTree/hashTree/hashTree/HTTPSamplerProxy//stringProp[@name='HTTPSampler.path']");

        NodeList nodesName = (NodeList) exprName.evaluate(document, XPathConstants.NODESET);
        NodeList nodesValue = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        NodeList nodesMethod = (NodeList) exprMethod.evaluate(document, XPathConstants.NODESET);
        NodeList nodesURL = (NodeList) exprURL.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < nodesName.getLength(); i++) {
            Node name = nodesName.item(i);
            Node n = nodesValue.item(i);
            Node method = nodesMethod.item(i);
            Node url = nodesURL.item(i);

            System.out.println("Name:" + name.getTextContent());
            s = method.getTextContent();
            if(!s.equals("GET")){
                System.out.println("Value:" + n.getTextContent());
            }
            System.out.println("Method:" + method.getTextContent());
            System.out.println("URL:" + url.getTextContent() + "\n");

        }
        System.out.println();
    }

//    // Печать элемента Cost у которого атрибут currency='USD'
//    private static void printCost2(Document document) throws DOMException, XPathExpressionException {
//        System.out.println("Example 2 - Печать элемента Cost у которого атрибут currency='USD'");
//        XPathFactory pathFactory = XPathFactory.newInstance();
//        XPath xpath = pathFactory.newXPath();
//        XPathExpression expr = xpath.compile("BookCatalogue/Book/Cost[@currency='USD']");
//        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
//        for (int i = 0; i < nodes.getLength(); i++) {
//            Node n = nodes.item(i);
//            System.out.println("Value:" + n.getTextContent());
//        }
//        System.out.println();
//    }
//
//    // Печать элементов Book у которых значение Cost > 4
//    private static void printCost3(Document document) throws DOMException, XPathExpressionException {
//        System.out.println("Example 3 - Печать элементов Book у которых значение Cost > 4");
//        XPathFactory pathFactory = XPathFactory.newInstance();
//        XPath xpath = pathFactory.newXPath();
//        XPathExpression expr = xpath.compile("BookCatalogue/Book[Cost>4]");
//        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
//        for (int i = 0; i < nodes.getLength(); i++) {
//            Node n = nodes.item(i);
//            System.out.println("Value:" + n.getTextContent());
//        }
//        System.out.println();
//    }
//
//    // Печать первого элемента Book
//    private static void printCost4(Document document) throws DOMException, XPathExpressionException {
//        System.out.println("Example 4 - Печать первого элемента Book");
//        XPathFactory pathFactory = XPathFactory.newInstance();
//        XPath xpath = pathFactory.newXPath();
//        XPathExpression expr = xpath.compile("BookCatalogue/Book[2]");
//        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
//        for (int i = 0; i < nodes.getLength(); i++) {
//            Node n = nodes.item(i);
//            System.out.println("Value:" + n.getTextContent());
//        }
//        System.out.println();
//    }
//
//    // Печать цены книги у которой Title начинается с Yogasana
//    // Варианты доступа к относительным узлам:
//    // ancestor , ancestor-or-self, descendant, descendant-or-self
//    // following, following-sibling, namespace, preceding, preceding-sibling
//    private static void printCost5(Document document) throws DOMException, XPathExpressionException {
//        System.out.println("Example 5 - Печать цены книги у которой Title начинается с 'Yogasana'");
//        XPathFactory pathFactory = XPathFactory.newInstance();
//        XPath xpath = pathFactory.newXPath();
//        XPathExpression expr = xpath.compile("BookCatalogue/Book/Cost"
//                + "[starts-with(preceding-sibling::Title, 'Yogasana')"
//                + " or "
//                + "starts-with(following-sibling::Title, 'Yogasana')]");
//        NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
//        for (int i = 0; i < nodes.getLength(); i++) {
//            Node n = nodes.item(i);
//            System.out.println("Value:" + n.getTextContent());
//        }
//        System.out.println();
//    }

}

