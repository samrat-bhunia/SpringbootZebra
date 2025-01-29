package com.poc.poc.utility;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DAOXmlQueryFormatter {

    private static final String DAO_XML_FILE = "src/main/resources/mapper/TransactionMapper.xml";  // Path to the XML file

    public void processDaoXmlFile() {
        File file = new File(DAO_XML_FILE);
        if (!file.exists()) {
            System.out.println("File not found: " + DAO_XML_FILE);
            return;
        }

        processFile(file);
    }

    private void processFile(File file) {
        try {
            System.out.println("Processing file: " + file.getName());
            Document doc = parseXml(file);
            NodeList queryNodes = doc.getElementsByTagName("*"); // Get all nodes

            for (int i = 0; i < queryNodes.getLength(); i++) {
                Node queryNode = queryNodes.item(i);

                if (isSqlTag(queryNode)) {
                    String sqlQuery = queryNode.getTextContent().trim();
                    String queryId = queryNode.getAttributes().getNamedItem("id").getNodeValue();
                    String formattedQuery = formatQuery(queryId, sqlQuery);
                    System.out.println(formattedQuery);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document parseXml(File file) throws Exception {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(file);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private boolean isSqlTag(Node queryNode) {
        String nodeName = queryNode.getNodeName().toLowerCase();
        return nodeName.equals("select") || nodeName.equals("insert") || nodeName.equals("update") || nodeName.equals("delete");
    }

    private String formatQuery(String queryId, String sqlQuery) {
        StringBuilder formattedQuery = new StringBuilder(queryId + ": {");

        // Extracting parameter placeholders (like #{parameter})
        String[] parts = sqlQuery.split("[#{}]");  // Split by parameter placeholders
        for (int i = 0; i < parts.length; i++) {
            if (i % 2 != 0) { // Odd indices are the parameters
                if (i > 1) {
                    formattedQuery.append(", ");
                }
                formattedQuery.append(parts[i].trim() + ": value"); // Replace "value" with actual values if you have them
            }
        }

        formattedQuery.append(" }");
        return formattedQuery.toString();
    }

    public static void main(String[] args) {
        DAOXmlQueryFormatter utility = new DAOXmlQueryFormatter();
        utility.processDaoXmlFile();
    }
}
