package com.example.myapplication;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class AudioFileParser {

    public static List<Audios> parseXml(String xmlFilePath) {
        List<Audios> audioFiles = new ArrayList<>();

        try {
            File xmlFile = new File(xmlFilePath);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            Element rootElement = document.getDocumentElement();
            NodeList nodeList = rootElement.getElementsByTagName("file");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element fileElement = (Element) node;
                    String fileName = fileElement.getElementsByTagName("name").item(0).getTextContent();
                    String filePath = fileElement.getElementsByTagName("path").item(0).getTextContent();
                    Audios audioFile = new Audios(fileName, filePath);
                    audioFiles.add(audioFile);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return audioFiles;
    }

}
