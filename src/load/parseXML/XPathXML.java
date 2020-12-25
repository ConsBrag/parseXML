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
import java.io.File;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class XPathXML {
    public static void main(String[] args) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Scanner in = new Scanner(System.in);
            System.out.println("Enter the path to file.xml or file.jmx");
            String path = in.nextLine();
            path = path.replace("\\","\\\\");

            Document document = documentBuilder.parse(new File(path)); //Документ
            String search = null; // Строка поиска
            int seqNumber = 0;
            System.out.println("Input number case: \n  case 1 - Errors\n  case 2 - Copy Body\n  case default - Print All");
            seqNumber = in.nextInt();;
            switch (seqNumber) {
                //Errors
                case (1):
                {
                    System.out.println("Save to text file? - print \"yes\"");
                    path = in.next();

                    if("yes".equals(path)){
                        // Создаём поток для сохранения выводимых данных
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);

                        // Сохраняем оригинальный поток вывода
                        PrintStream originalStdOut = System.out;

                        // Заменяем поток вывода на наш буфер
                        System.setOut(ps);

                        // Вызываем функцию, вывод которой нужно перехватить
                        printError(document);

                        // Восстанавливаем оригинальный поток вывода
                        System.out.flush();
                        System.setOut(originalStdOut);

                        // Сохраняем в файл перехваченные данные
                        try (OutputStream fos = new FileOutputStream("ERRORS.txt")) {
                            baos.writeTo(fos);
                        }
                    }
                    else printError(document);
                }
                    break;

                //Copy Body
                case (2):
                {
                    System.out.println("Input object name");
                    search = in.next();

                    System.out.println("Save to text file? - print \"yes\"");
                    path = in.next();

                    if("yes".equals(path)){
                        // Создаём поток для сохранения выводимых данных
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);

                        // Сохраняем оригинальный поток вывода
                        PrintStream originalStdOut = System.out;

                        // Заменяем поток вывода на наш буфер
                        System.setOut(ps);

                        // Вызываем функцию, вывод которой нужно перехватить
                        copyBodyDataHTTPRequest(document, search);

                        // Восстанавливаем оригинальный поток вывода
                        System.out.flush();
                        System.setOut(originalStdOut);

                        // Сохраняем в файл перехваченные данные
                        try (OutputStream fos = new FileOutputStream("ERRORS.txt")) {
                            baos.writeTo(fos);
                        }
                    }
                    else copyBodyDataHTTPRequest(document,search);
                }
                    break;

                //Print All
                default:
                {
                    System.out.println("Save to text file? - print \"yes\"");
                    path = in.next();

                    if("yes".equals(path)){
                        // Создаём поток для сохранения выводимых данных
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos);

                        // Сохраняем оригинальный поток вывода
                        PrintStream originalStdOut = System.out;

                        // Заменяем поток вывода на наш буфер
                        System.setOut(ps);

                        // Вызываем функцию, вывод которой нужно перехватить
                        printAll(document);

                        // Восстанавливаем оригинальный поток вывода
                        System.out.flush();
                        System.setOut(originalStdOut);

                        // Сохраняем в файл перехваченные данные
                        try (OutputStream fos = new FileOutputStream("ERRORS.txt")) {
                            baos.writeTo(fos);
                        }
                    }
                    else printAll(document);
                }
                    break;
            }

        } catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace(System.out);
        }
    }

    // Errors
    private static void printError(Document document) throws DOMException, XPathExpressionException {

        System.out.println("Outputting errors don't correlated elements");
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        String checkValueFull = "", checkURL, checkMethod, checkValue, checkName;
        String checkValueFind = null;
        boolean input0 = false, input1 = false;
        int VALUE_ERROR = 0, URL_ERROR = 0, k = 0;

        XPathExpression expr = xpath.compile("//HTTPSamplerProxy");
        XPathExpression exprName = xpath.compile("//HTTPSamplerProxy/attribute::testname");
        XPathExpression exprMethod = xpath.compile("//HTTPSamplerProxy//stringProp[@name='HTTPSampler.method']");
        XPathExpression exprURL = xpath.compile("//HTTPSamplerProxy//stringProp[@name='HTTPSampler.path']");

        NodeList nodesValue = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        NodeList nodesName = (NodeList) exprName.evaluate(document, XPathConstants.NODESET);
        NodeList nodesMethod = (NodeList) exprMethod.evaluate(document, XPathConstants.NODESET);
        NodeList nodesURL = (NodeList) exprURL.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < nodesValue.getLength(); i++) {
            input0 = false;
            input1 = false;

            Node value = nodesValue.item(i);
            Node name = nodesName.item(i);
            Node method = nodesMethod.item(i);
            Node url = nodesURL.item(i);

            checkName = name.getTextContent();
            checkMethod = method.getTextContent();
            checkURL = url.getTextContent();

            checkValueFull = value.getTextContent();
            //System.out.println("Value: " + checkValueFull);

            Pattern patternURL = Pattern.compile("[/](\\d{3}\\d+)[/]");// [\\](\d{3}\d+)[\\]
            Matcher matcherURL = patternURL.matcher(checkURL);

            while (matcherURL.find()) {
                System.out.println("URL_ERROR:");
                System.out.println(checkURL.substring(matcherURL.start(1), matcherURL.end(1)));
                URL_ERROR++;
                input0 = true;
            }

            if(value.getTextContent() != null) checkValueFull = value.getTextContent();
            Pattern patternValueFind = Pattern.compile("([^$])[{](.+)([}])\\n");
            Matcher matcherValueFind = patternValueFind.matcher(checkValueFull);

            while (matcherValueFind.find()) {
                checkValueFind = checkValueFull.substring(matcherValueFind.start(), matcherValueFind.end());
            }

            if("POST".equals(checkMethod)){
                Pattern patternValue = Pattern.compile("[\\[{:\"](\\d{3}\\d+)[:\"},\\\\\\]]"); //[^a-zA-Z|^-](\d{4}\d+)[^a-zA-Z|-]
                assert checkValueFind != null;
                Matcher matcherValue = patternValue.matcher(checkValueFind);

                while (matcherValue.find()) {
                    System.out.println("VALUE_ERROR: ");
                    System.out.println(checkValueFind.substring(matcherValue.start(1), matcherValue.end(1)));
                    VALUE_ERROR++;
                    input1 = true;
                }
                if(input1) {
                    if (checkValueFind != null && checkValueFind.length() > 0) {
                        checkValueFind = checkValueFind.substring(0, checkValueFind.length() - 1);
                    }
                    System.out.println("Value: " + checkValueFind);
                }
            }

            if(input0 || input1){
                System.out.println("Name: " + checkName);
                System.out.println("Method: " + checkMethod);
                System.out.println("URL: " + checkURL + "\n");
            }
            checkValueFind = "";
            checkName = "";
            checkMethod = "";
            checkURL = "";
        }
        System.out.println("VALUE_ERROR: " + VALUE_ERROR);
        System.out.println("URL_ERROR: " + URL_ERROR);
    }

    // Copy Body Data
    private static void copyBodyDataHTTPRequest(Document document, String search) throws DOMException, XPathExpressionException {

        System.out.println("Outputting of found items");
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        String checkValueFull, checkURL, checkMethod, checkName;
        String checkValueFind = null;

        XPathExpression expr = xpath.compile("//HTTPSamplerProxy");
        XPathExpression exprName = xpath.compile("//HTTPSamplerProxy/attribute::testname");
        XPathExpression exprMethod = xpath.compile("//HTTPSamplerProxy//stringProp[@name='HTTPSampler.method']");
        XPathExpression exprURL = xpath.compile("//HTTPSamplerProxy//stringProp[@name='HTTPSampler.path']");

        NodeList nodesValue = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        NodeList nodesName = (NodeList) exprName.evaluate(document, XPathConstants.NODESET);
        NodeList nodesMethod = (NodeList) exprMethod.evaluate(document, XPathConstants.NODESET);
        NodeList nodesURL = (NodeList) exprURL.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < nodesValue.getLength(); i++) {

            Node value = nodesValue.item(i);
            Node name = nodesName.item(i);
            Node method = nodesMethod.item(i);
            Node url = nodesURL.item(i);

            checkName = name.getTextContent();
            checkMethod = method.getTextContent();
            checkURL = url.getTextContent();
            checkValueFull = value.getTextContent();

            if(value.getTextContent() != null) checkValueFull = value.getTextContent();
            Pattern patternValueFind = Pattern.compile("([^$])[{](.+)([}])\\n");
            Matcher matcherValueFind = patternValueFind.matcher(checkValueFull);

            while (matcherValueFind.find()) {
                checkValueFind = checkValueFull.substring(matcherValueFind.start(), matcherValueFind.end());
            }

            if (checkValueFind != null && checkValueFind.length() > 0) {
                checkValueFind = checkValueFind.substring(0, checkValueFind.length() - 1);
            }

            if(search.equals(checkName)){
                System.out.println("Name: " + checkName);
                if(!"".equals(checkValueFind)){
                    System.out.println("Value: " + checkValueFind);
                }
                System.out.println("Method: " + checkMethod);
                System.out.println("URL: " + checkURL + "\n");
            }

            checkValueFind = "";
            checkName = "";
            checkMethod = "";
            checkURL = "";

        }
    }

    // Print All
    private static void printAll(Document document) throws DOMException, XPathExpressionException {

        System.out.println("Outputting all elements");
        XPathFactory pathFactory = XPathFactory.newInstance();
        XPath xpath = pathFactory.newXPath();
        String checkValueFull = "", checkURL, checkMethod, checkValue, checkName;
        String checkValueFind = null;

        XPathExpression expr = xpath.compile("//HTTPSamplerProxy");
        XPathExpression exprName = xpath.compile("//HTTPSamplerProxy/attribute::testname");
        XPathExpression exprMethod = xpath.compile("//HTTPSamplerProxy//stringProp[@name='HTTPSampler.method']");
        XPathExpression exprURL = xpath.compile("//HTTPSamplerProxy//stringProp[@name='HTTPSampler.path']");

        NodeList nodesValue = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
        NodeList nodesName = (NodeList) exprName.evaluate(document, XPathConstants.NODESET);
        NodeList nodesMethod = (NodeList) exprMethod.evaluate(document, XPathConstants.NODESET);
        NodeList nodesURL = (NodeList) exprURL.evaluate(document, XPathConstants.NODESET);

        for (int i = 0; i < nodesValue.getLength(); i++) {

            Node value = nodesValue.item(i);
            Node name = nodesName.item(i);
            Node method = nodesMethod.item(i);
            Node url = nodesURL.item(i);

            checkName = name.getTextContent();
            checkMethod = method.getTextContent();
            checkURL = url.getTextContent();
            //checkValueFull = value.getTextContent();

            if(value.getTextContent() != null) checkValueFull = value.getTextContent();
            Pattern patternValueFind = Pattern.compile("([^$])[{](.+)([}])\\n");
            Matcher matcherValueFind = patternValueFind.matcher(checkValueFull);

            while (matcherValueFind.find()) {
                checkValueFind = checkValueFull.substring(matcherValueFind.start(), matcherValueFind.end());
            }

            if (checkValueFind != null && checkValueFind.length() > 0) {
                checkValueFind = checkValueFind.substring(0, checkValueFind.length() - 1);
            }

            System.out.println("Name: " + checkName);
            if(!"".equals(checkValueFind)){
                System.out.println("Value: " + checkValueFind);
            }
            System.out.println("Method: " + checkMethod);
            System.out.println("URL: " + checkURL + "\n");

            checkValueFind = "";
            checkName = "";
            checkMethod = "";
            checkURL = "";
        }
    }
}

