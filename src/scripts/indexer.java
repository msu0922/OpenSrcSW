package scripts;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public class indexer {
    private String data_path;
    private String output_file = "./index.post";

    public indexer(String path) {
        this.data_path = path;
    }

    public void convertPost() throws IOException, ParserConfigurationException, SAXException, ClassNotFoundException {

        FileOutputStream fileOutputStream = new FileOutputStream(output_file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream(data_path));
        NodeList nodeList = document.getElementsByTagName("doc");

        HashMap docFreq = new HashMap();
        docData[] docArr = new docData[nodeList.getLength()];

        HashMap hashMap = new HashMap();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element element = (Element) nodeList.item(i); // <title> + <body>
            String titleData = element.getElementsByTagName("title").item(0).getTextContent();
            String bodyData = element.getElementsByTagName("body").item(0).getTextContent();
            docArr[i] = new docData(titleData);

            String[] wordList = bodyData.split("#");

            for (var each : wordList) {
                String[] split = each.split(":"); // keyword와 frequency로 자르기 // [0] : keyword / [1] : frequency
                String splitWord = split[0];
                docArr[i].words.put(splitWord, Integer.parseInt(split[1]));

                if (!(docFreq.containsKey(splitWord))) { // df 구하기 : 단어가 몇개의 문서에서 나오는지
                    docFreq.put(splitWord, 1);
                } else {
                    docFreq.put(splitWord, (int) docFreq.get(splitWord) + 1);
                }

                String[] values = {"0 0.0 ", "1 0.0 ", "2 0.0 ", "3 0.0 ", "4 0.0"}; // 기본값 설정

                if (docArr[i].words.containsKey(split[0])) {
                    int termFreq = (int) docArr[i].words.get(splitWord);
                    float weight = (float) (termFreq * (Math.log(nodeList.getLength() / (float)((int) docFreq.get(splitWord))))); // 가중치 계산

                    if (i != 4) { // 4번째일 경우 마지막 공백 미포함을 위해
                        values[i] = i + " " + String.format("%.2f", weight) + " ";
                    } else {
                        values[i] = i + " " + String.format("%.2f", weight);
                    }

                    String value = values[0].concat(values[1]).concat(values[2]).concat(values[3]).concat(values[4]);
                    hashMap.put(splitWord, value);
                }
            }
        }

        objectOutputStream.writeObject(hashMap);
        objectOutputStream.close();

        /*
        // 출력 확인용 코드
        FileInputStream fileInputStream = new FileInputStream(output_file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();
        HashMap indexMap = (HashMap) object;
        Iterator<String> it = hashMap.keySet().iterator();
        while(it.hasNext()) {
            String key = it.next();
            String value = (String) indexMap.get(key);
            System.out.println(key + " -> " + value);
        }
        */
    }
}

class docData {
    private String str;
    protected HashMap words = new HashMap();

    protected docData(String str) {
        this.str = str;
    }
}