package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.*;

public class makeKeyword {

    private String input_file;
    private String output_file = "./index.xml";

    public makeKeyword(String file) {
        this.input_file = file; // ./collection.xml
    }

    public void convertXml() throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, TransformerException {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(new FileInputStream(input_file));

        // ===== 내용 바꾸기 =====
        NodeList docList = doc.getElementsByTagName("doc");

        for (int i = 0; i < docList.getLength(); i++) {
            Node docNode = docList.item(i); // <doc>의 자식 노드들: <title> & <body>
            NodeList docChildNodes = docNode.getChildNodes();

            for (int j = 0; j < docChildNodes.getLength(); j++) {
                Node item = docChildNodes.item(j); // <title> & <body>

                if ("body".equalsIgnoreCase(item.getNodeName())) { // <body> 특정화
                    String bodyData = item.getTextContent(); // <body>를 String에 저장
                    String result = ""; // 결과를 누적하여 저장 할 String
                    String strKwrd = "";

                    KeywordExtractor ke = new KeywordExtractor();
                    KeywordList kl = ke.extractKeyword(bodyData, true); // 형태소 분리하여 리스트에 저장

                    for (int k = 0; k < kl.size(); k++) { // 형태소 출력 및 빈도수 파악
                        Keyword kwrd = kl.get(k);

                        if (k == kl.size()-1) {
                            strKwrd = kwrd.getString() + ":" + kwrd.getCnt(); // 각 키워드별로 출력 형태를 String에 저장
                        } else {
                            strKwrd = kwrd.getString() + ":" + kwrd.getCnt() + "#"; // 각 키워드별로 출력 형태를 String에 저장
                        }
                        result = result.concat(strKwrd); // 출력 결과를 result에 이어붙여서 저장
                    }

                    item.setTextContent(result); // <body> 내용을 result로 변경
                }
            }
        }

        // ===== 불필요한 공백 제거 (Pretty Print) =====
        XPath xp = XPathFactory.newInstance().newXPath();
        NodeList nl = (NodeList) xp.evaluate("//text()[normalize-space()='']", doc, XPathConstants.NODESET);

        for (int i = 0; i < nl.getLength(); ++i) {
            Node node = nl.item(i);
            node.getParentNode().removeChild(node);
        }

        // ===== xml 파일 생성 =====
        TransformerFactory trFac = TransformerFactory.newInstance();
        Transformer tr = trFac.newTransformer();

        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // set encoding type
        tr.setOutputProperty(OutputKeys.INDENT, "yes"); // set indent
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4"); // set indent amount

        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new FileOutputStream(new File(output_file)));

        tr.transform(domSource, streamResult);

    }
}