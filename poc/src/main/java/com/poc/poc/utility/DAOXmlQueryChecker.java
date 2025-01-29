//package com.poc.poc.utility;
//
//import java.io.File;
//
//import javax.xml.parsers.DocumentBuilder;
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;
//
//import org.w3c.dom.Document;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//
//public class DAOXmlQueryChecker {
//
//    private static final String DAO_XML_DIR = "src/main/resources/mapper";
//
//    public void processDaoXmlFiles() {
//        File dir = new File(DAO_XML_DIR);
//        if (!dir.exists() || !dir.isDirectory()) {
//            System.out.println("Invalid directory path: " + DAO_XML_DIR);
//            return;
//        }
//
//        File[] files = dir.listFiles((d, name) -> name.endsWith(".xml"));
//        if (files != null) {
//            for (File file : files) {
//                processFile(file);
//            }
//        }
//    }
//
//    private void processFile(File file) {
//        try {
//            System.out.println("Processing file: " + file.getName());
//            Document doc = parseXml(file);
//            NodeList queryNodes = doc.getElementsByTagName("select");
//
//            for (int i = 0; i < queryNodes.getLength(); i++) {
//                Node queryNode = queryNodes.item(i);
//                String sqlQuery = queryNode.getTextContent();
//                System.out.println("Original SQL: " + sqlQuery);
//
//                if (!isCompatibleWithPostgreSQL(sqlQuery)) {
//                    System.out.println("Incompatible query detected. Modifying...");
//                    String modifiedQuery = modifyQuery(sqlQuery);
//                    updateXmlNode(queryNode, modifiedQuery);
//                    System.out.println("Modified SQL: " + modifiedQuery);
//                }
//            }
//
//            saveXml(doc, file);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Document parseXml(File file) throws Exception {
//        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//        Document doc = dBuilder.parse(file);
//        doc.getDocumentElement().normalize();
//        return doc;
//    }
//
//    private boolean isCompatibleWithPostgreSQL(String sqlQuery) {
//        if (sqlQuery.contains("LIMIT")) {
//            return false;
//        }
//        return true;
//    }
//
//    private String modifyQuery(String sqlQuery) {
//        String modifiedQuery = sqlQuery.replaceAll("LIMIT", "LIMIT OFFSET");
//        modifiedQuery = modifiedQuery.replaceAll("CONCAT", "||");
//        return modifiedQuery;
//    }
//
//    private void updateXmlNode(Node queryNode, String modifiedQuery) {
//        queryNode.setTextContent(modifiedQuery);
//    }
//
//    private void saveXml(Document doc, File file) throws Exception {
//        TransformerFactory transformerFactory = TransformerFactory.newInstance();
//        Transformer transformer = transformerFactory.newTransformer();
//        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//        DOMSource source = new DOMSource(doc);
//        StreamResult result = new StreamResult(file);
//        transformer.transform(source, result);
//    }
//
//    public static void main(String[] args) {
//        DAOXmlQueryChecker utility = new DAOXmlQueryChecker();
//        utility.processDaoXmlFiles();
//    }
//}
