package scripts;

import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;

public class MidTerm {
    private String input_file;

    public MidTerm(String file) {
        this.input_file = file; // ./collection.xml
    }

    public void showSnippet(String query) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new FileInputStream(input_file));

        NodeList docList = doc.getElementsByTagName("doc");

        String[] titleData = new String[docList.getLength()];
        String[] bodyData = new String[docList.getLength()];

        for (int i = 0; i < docList.getLength(); i++) {
            Node docNode = docList.item(i); // <doc>의 자식 노드들: <title> & <body>
            NodeList docChildNodes = docNode.getChildNodes();
            for (int j = 0; j < docChildNodes.getLength(); j++) {
                Node item = docChildNodes.item(j); // <title> & <body>
                if ("title".equalsIgnoreCase(item.getNodeName())){
                    titleData[i] = item.getTextContent(); // <title> 저장
                } else if ("body".equalsIgnoreCase(item.getNodeName())) { // <body> 특정화
                    bodyData[i] = item.getTextContent(); // <body> 저장
                }
            }
        }

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(query, true); // 질의어 형태소 분석

        for (int i = 0; i < docList.getLength(); i++) {
            for (int j = 0; j < kl.size(); j++) {
                String keyword = kl.get(j).getString();

            }
        }


    }
}
