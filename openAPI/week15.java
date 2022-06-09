import org.json.simple.*;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class week15 {
    public static void main(String[] args) {
        String clientId = "dXCURzggiKyBTDjjvftK"; // 애플리케이션 클라이언트 아이디값";
        String clientSecret = "I5su4thvdS"; // 애플리케이션 클라이언트 시크릿값";

        Scanner sc = new Scanner(System.in);
        System.out.print("검색어를 입력하세요: ");
        String query = sc.nextLine();

        try{
            // 1. API 호출에 필요한 정보 입력
            String text = URLEncoder.encode(query, "UTF-8"); // s: query(검색어), enc: 인코딩
            String apiURL = "https://openapi.naver.com/v1/search/movie.json?query=" + text; // json 결과
            // String apiURL = "https://openapi.naver.com/v1/search/movie.xml?query=" + text; // xml 결과
            URL url = new URL(apiURL); // URL 객체 생성
            HttpURLConnection con = (HttpURLConnection) url.openConnection(); // server와 client를 url로 연결
            // 결과를 주고 받는 방식 지정
            con.setRequestMethod("GET"); // GET: url에 text를 넘겨 주고 받는 방식
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);

            // 2. API 응답 수신
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if (responseCode == 200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream())); // con의 inputStream을 저장
            } else { // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream())); // con의 에러를 저장
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) { // 한 줄 씩 읽어 옴
                response.append(inputLine);
            }
            br.close();

            // System.out.println(response);

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(response.toString());
            JSONArray infoArray = (JSONArray) jsonObject.get("items");

            for (int i = 0; i < infoArray.size(); i++) {
                System.out.println("=item_" + i + "========================================");
                JSONObject itemObject = (JSONObject) infoArray.get(i);
                System.out.println("title:\t" + itemObject.get("title"));
                System.out.println("subtitle:\t" + itemObject.get("subtitle"));
                System.out.println("director:\t" + itemObject.get("director"));
                System.out.println("actor:\t" + itemObject.get("actor"));
                System.out.println("userRating:\t" + itemObject.get("userRating") + "\n");
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
