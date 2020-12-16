package load.parseXML;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class XPathXML {
    public static void main(String[] args) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Scanner in = new Scanner(System.in);
            System.out.println("Введите путь к file.xml");
            String path = in.nextLine();
            path = path.replace("\\","\\\\");
            //C:\Users\admin\IdeaProjects\parseXML\src\xml\example\record\testXML.xml
            Document document = documentBuilder.parse(path);
            System.out.println("Сохранить в текстовый файл? - \"print yes\"");
            path = in.nextLine();
            if("yes".equals(path)){
                // Создаём поток для сохранения выводимых данных
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);

                // Сохраняем оригинальный поток вывода
                PrintStream originalStdOut = System.out;

                // Заменяем поток вывода на наш буфер
                System.setOut(ps);

                // Вызываем функцию, вывод которой нужно перехватить
                printErrorCor(document);

                // Восстанавливаем оригинальный поток вывода
                System.out.flush();
                System.setOut(originalStdOut);

                // Сохраняем в файл перехваченные данные
                try (OutputStream fos = new FileOutputStream("ERRORS.txt")) {
                    baos.writeTo(fos);
                }
            }
            else printErrorCor(document);

        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    // Errors
    private static void printErrorCor(Document document) throws DOMException, XPathExpressionException {

        System.out.println("Вывод всех элементов HTTPSamplerProxy в терминал");
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        String checkValue = "", checkURL, checkMethod;
        int VALUE_ERROR = 0, URL_ERROR = 0, k = 0;

        // Пример записи XPath
        // Подный путь до элемента
        //XPathExpression expr = xpath.compile("testXML/jmeterTestPlan/TestPlan/stringProp");
        // Все элементы с таким именем
        //XPathExpression expr = xpath.compile("//stringProp");
        // Элементы, вложенные в другой элемент
        XPathExpression exprName = xpath.compile("//HTTPSamplerProxy/attribute::testname");
        XPathExpression expr = xpath.compile("//HTTPSamplerProxy/elementProp/collectionProp/elementProp//stringProp[@name='Argument.value']");
        XPathExpression exprMethod = xpath.compile("//HTTPSamplerProxy//stringProp[@name='HTTPSampler.method']");
        XPathExpression exprURL = xpath.compile("//HTTPSamplerProxy//stringProp[@name='HTTPSampler.path']");

        NodeList nodesName = (NodeList) exprName.evaluate(document, XPathConstants.NODESET);
        NodeList nodesValue = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        NodeList nodesMethod = (NodeList) exprMethod.evaluate(document, XPathConstants.NODESET);
        NodeList nodesURL = (NodeList) exprURL.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < nodesName.getLength(); i++) {

            Node name = nodesName.item(i);
            Node method = nodesMethod.item(i);
            Node url = nodesURL.item(i);

            checkURL = url.getTextContent();
            checkMethod = method.getTextContent();
            Pattern patternURL = Pattern.compile("[^\\D]\\d+");
            Matcher matcherURL = patternURL.matcher(checkURL);
            while (matcherURL.find()) {
                System.out.println("URL_ERROR:");
                System.out.println(checkURL.substring(matcherURL.start(), matcherURL.end()));
                URL_ERROR++;
            }
            if("POST".equals(checkMethod)){
                for (int j = k; j < nodesValue.getLength();){
                    Node value = nodesValue.item(j);
                    if(value.getTextContent() != null) checkValue = value.getTextContent();
                    Pattern patternValue = Pattern.compile("[^a-zA-Z|^-](\\d{4}\\d+)[^a-zA-Z|-]"); //[^-|a-zA-Z]\d{3}\d+[^\|[a-zA-Z]]
                    Matcher matcherValue = patternValue.matcher(checkValue);
                    while (matcherValue.find()) {
                        System.out.println("VALUE_ERROR: ");
                        System.out.println(checkValue.substring(matcherValue.start(1), matcherValue.end(1)));
                        VALUE_ERROR++;
                    }
                    System.out.println("Value: " + checkValue);
                    k++;
                    break;
                }
            }
            System.out.println("Name:" + name.getTextContent());
            System.out.println("Method: " + checkMethod);
            System.out.println("URL: " + checkURL + "\n");

        }
            System.out.println("VALUE_ERROR: " + VALUE_ERROR);
            System.out.println("URL_ERROR: " + URL_ERROR);

    }
}

