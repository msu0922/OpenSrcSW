package scripts;

import org.jsoup.Jsoup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class makeCollection {

    private String data_path;
    private String output_file = "./collection.xml";

    public makeCollection(String path) {
        this.data_path = path; // ./data/
    }

    public void makeXml() throws ParserConfigurationException, IOException, TransformerException {

        File dir = new File(data_path); // html 파일 위치
        File[] files = dir.listFiles(); // html FileList

        // ===== Document 객체 생성 =====
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        // ===== xml 구조 형성 =====
        Element docs = doc.createElement("docs");
        doc.appendChild(docs);

        for (int i = 0; i < files.length; i++) {
            File f_dir = new File(files[i].toString());
            org.jsoup.nodes.Document html = Jsoup.parse(f_dir, "UTF-8");
            String titleData = html.title();
            String bodyData = html.body().text();

            // <doc id = "i"> ~ </doc>
            Element docId = doc.createElement("doc");
            docs.appendChild(docId);
            docId.setAttribute("id", String.valueOf(i));

            // <title> ~ </title>
            Element title = doc.createElement("title");
            title.appendChild(doc.createTextNode(titleData));
            docId.appendChild(title);

            // <body> ~ </body>
            Element body = doc.createElement("body");
            body.appendChild(doc.createTextNode(bodyData));
            docId.appendChild(body);
        }

        // ===== xml 파일 생성 =====
        TransformerFactory trFac = TransformerFactory.newInstance();
        Transformer tr = trFac.newTransformer();

        tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8"); // Encoding Type 설정
        tr.setOutputProperty(OutputKeys.INDENT, "yes"); // Indent 설정
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource domSource = new DOMSource(doc);
        StreamResult streamResult = new StreamResult(new FileOutputStream(new File(output_file)));

        tr.transform(domSource, streamResult);
    }
}

