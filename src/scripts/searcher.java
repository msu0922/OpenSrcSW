package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;

public class searcher {

    private String input_file;
    private String input_query;

    public searcher(String file) {
        this.input_file = file; // ./index.post
    }

    public void CalcSim(String query) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException {
        this.input_query = query;

        KeywordExtractor ke = new KeywordExtractor();
        KeywordList kl = ke.extractKeyword(input_query, true); // input_query를 분리하여 kl 리스트에 저장

        // keywordMap에 query의 keyword를 Key로, weight(tf)를 Value로 저장
        HashMap keywordMap = new HashMap();

        for (int k = 0; k < kl.size(); k++) { // 형태소 출력 및 빈도수 파악
            Keyword kwrd = kl.get(k); // 형태소 추출 후 저장한 리스트 kl에서 keyword 하나씩 가져오기
            keywordMap.put(kwrd, kwrd.getCnt()); // Key: 키워드, Value: 빈도수
        }

        // index.post Hashmap 불러오기 - object에 저장되어 있음
        FileInputStream fileInputStream = new FileInputStream(input_file);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Object object = objectInputStream.readObject();
        objectInputStream.close();

        HashMap indexerMap = (HashMap) object;

        // idCount를 구하기 위해
        Keyword tempKwrd = kl.get(0);
        String tempIndexerVal = (String) indexerMap.get(tempKwrd.getString()); // 전체(indexerMap)에서 (kl에 저장되어있는)query의 kwrd를 검색하여 value 추출: 0 W0 1 W1 2 W2 3 W3 4 W4
        String[] tempIndexerSplit = tempIndexerVal.split(" ");
        int idCount = tempIndexerSplit.length / 2;

        double[][] indexerValArr = new double[kl.size()][idCount];
        double[] resultArr = new double[idCount]; // 결과를 저장할 배열

        for (int k = 0; k < kl.size(); k++) {
            Keyword kwrd = kl.get(k);
            String indexerVal = (String) indexerMap.get(kwrd.getString()); // 전체(indexerMap)에서 (kl에 저장되어있는)query의 kwrd를 검색하여 value 추출: 0 W0 1 W1 2 W2 3 W3 4 W4
            String[] indexerSplit = indexerVal.split(" ");
            for (int i = 0; i < idCount; i++) { // indexerVal에서 각 id별 weight값만 추출하여 indexerValArr[]에 저장
                indexerValArr[k][i] = Double.parseDouble((indexerSplit[2*i + 1]));
            }
        }

        // id = i와 keyword k의 유사도 조사
        for (int i = 0; i < idCount; i++) {
            for (int k = 0; k < kl.size(); k++) {
                resultArr[i] += indexerValArr[k][i] * kl.get(k).getCnt();
            }
        }

        // collection.xml을 통해 title 값 불러오기
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream("./collection.xml"));
        NodeList docList = doc.getElementsByTagName("doc");

        String[] title = new String[docList.getLength()];

        for (int i = 0; i < docList.getLength(); i++) {
            NodeList docChildNodes = docList.item(i).getChildNodes();

            for (int j = 0; j < docChildNodes.getLength(); j++) {
                if ("title".equalsIgnoreCase(docChildNodes.item(j).getNodeName())) {
                    title[i] = docChildNodes.item(j).getTextContent(); // 제목을 title 배열에 저장
                    // System.out.println(title[i]);
                }
            }
        }

        // 유사도 상위 3개 출력
        System.out.println();
        double first, second, third;
        int[] id = new int[3]; // 상위 3개의 index값을 저장 = 문서의 id 값

        first = resultArr[findLargest(resultArr)];
        if (findLargest(resultArr) != 0) {
            id[0] = findLargest(resultArr); // first의 index값
            System.out.println(title[id[0]] + " " + first);
            resultArr[id[0]] = -1;
        } else {
            id[0] = -1;
        }

        second = resultArr[findLargest(resultArr)];
        if (findLargest(resultArr) != 0) {
            id[1] = findLargest(resultArr); // second의 index값
            System.out.println(title[id[1]] + " " + second);
            resultArr[id[1]] = -1;
        } else {
            id[1] = -1;
        }

        third = resultArr[findLargest(resultArr)];
        if (findLargest(resultArr) != 0) {
            id[2] = findLargest(resultArr); // third의 index값
            System.out.println(title[id[2]] + " " + third);
            resultArr[id[2]] = -1;
        } else {
            id[2] = -1;
        }

        if (id[0] == -1 && id[1] == -1  && id[2] == -1)
            System.out.println("검색된 문서가 없습니다.");
    }

    public int findLargest(double[] arr) {
        double max = arr[0];
        int index = 0;

        for (int i = 0; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                index = i;
            }
        }
        return index;
    }
}