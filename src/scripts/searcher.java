package scripts;

import org.snu.ids.kkma.index.Keyword;
import org.snu.ids.kkma.index.KeywordExtractor;
import org.snu.ids.kkma.index.KeywordList;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;

public class searcher { // master 브랜치 searcher.java

	private String input_file;
	private String input_query;

	public searcher(String file) {
		this.input_file = file;
	}

	public double[] CalcSim(String query) throws IOException, ClassNotFoundException {
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

		double[][] indexerValArr = new double[kl.size()][idCount]; // indexerMap에서 kwrd(k)의 가중치: W0 W1 W2 W3 W4
		double[] dotPrdt = new double[idCount]; // 결과를 저장할 배열

		for (int k = 0; k < kl.size(); k++) {
			Keyword kwrd = kl.get(k);
			String indexerVal = (String) indexerMap.get(kwrd.getString()); // 전체(indexerMap)에서 (kl에 저장되어있는)query의 kwrd를 검색하여 value 추출: 0 W0 1 W1 2 W2 3 W3 4 W4
			String[] indexerSplit = indexerVal.split(" ");
			for (int i = 0; i < idCount; i++) { // indexerVal에서 각 id별 weight값만 추출하여 indexerValArr[]에 저장
				indexerValArr[k][i] = Double.parseDouble((indexerSplit[2*i + 1]));
			}
		}

		// id = i와 keyword k의 유사도(inner product) 조사; resultArr[i] = Q⋅id(i)
		for (int i = 0; i < idCount; i++) {
			for (int k = 0; k < kl.size(); k++) {
				dotPrdt[i] += indexerValArr[k][i] * kl.get(k).getCnt();
			}
		}

		// cosine similarity 조사
		double kwrdFreq = 0.0; // 초기화
		double[] kwrdWeight = new double[idCount];
		double[] denominator = new double[idCount]; // cos의 분모값

		for (int k = 0; k < kl.size(); k++) {
			kwrdFreq += Math.pow(kl.get(k).getCnt(), 2); // 제곱해서 더하기 (query의 단어 빈도수 벡터 크기)
		}

		for (int i = 0; i < idCount; i++) {
			kwrdWeight[i] = 0.0; // 초기화
			for (int k = 0; k < kl.size(); k++) {
				kwrdWeight[i] += Math.pow(indexerValArr[k][i], 2); // 제곱해서 더하기 (문서에서 query의 유사도 벡터)
			}
			denominator[i] = Math.sqrt(kwrdFreq) * Math.sqrt(kwrdWeight[i]); // 분모 계산
		}

		double[] cosSimilarity = new double[idCount]; // cosine similarity 저장 배열

		for (int i = 0; i < idCount; i++) {
			if (denominator[i] != 0.0) { // 분모가 0이 아니면 계산
				cosSimilarity[i] = dotPrdt[i] / denominator[i];
			} else { // 분모가 0이면 NaN 예외처리
				cosSimilarity[i] = 0.0;
			}
		}

		return cosSimilarity;
	}

	public void printCosSim(double[] cosSim) throws ParserConfigurationException, IOException, SAXException {
		// collection.xml을 통해 title 값 불러오기
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new FileInputStream("./collection.xml"));
		NodeList docList = doc.getElementsByTagName("doc");

		String[] title = new String[docList.getLength()];

		// title 불러오기
		for (int i = 0; i < docList.getLength(); i++) {
			NodeList docChildNodes = docList.item(i).getChildNodes();

			for (int j = 0; j < docChildNodes.getLength(); j++) {
				if ("title".equalsIgnoreCase(docChildNodes.item(j).getNodeName())) {
					title[i] = docChildNodes.item(j).getTextContent(); // 제목을 title 배열에 저장
				}
			}
		}

		// 유사도 상위 3개 출력
		System.out.println();
		double first, second, third;
		int[] id = new int[3]; // 상위 3개의 index값을 저장 = 문서의 id 값

		first = cosSim[findLargest(cosSim)];
		if (findLargest(cosSim) != 0) {
			id[0] = findLargest(cosSim); // first의 index값
			System.out.println(title[id[0]] + " " + String.format("%.2f", first)); // 소수점 2자리까지 출력
			cosSim[id[0]] = -1;
		} else {
			id[0] = -1;
		}

		second = cosSim[findLargest(cosSim)];
		if (findLargest(cosSim) != 0) {
			id[1] = findLargest(cosSim); // second의 index값
			System.out.println(title[id[1]] + " " + String.format("%.2f", second)); // 소수점 2자리까지 출력
			cosSim[id[1]] = -1;
		} else {
			id[1] = -1;
		}

		third = cosSim[findLargest(cosSim)];
		if (findLargest(cosSim) != 0) {
			id[2] = findLargest(cosSim); // third의 index값
			System.out.println(title[id[2]] + " " + String.format("%.2f", third)); // 소수점 2자리까지 출력
			cosSim[id[2]] = -1;
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
