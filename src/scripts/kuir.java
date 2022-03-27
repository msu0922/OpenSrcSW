package scripts;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException {

        // Build 이후 Run 하는 방법:
        // Run - Edit Configurations... - Program arguments에 필요한 값 작성
        // makeCollection.java 실행 시:    -c ./data/

        String command = args[0];
        String path = args[1];

        if (command.equals("-c")) {
            makeCollection collection = new makeCollection(path); // "./data/"
            collection.makeXml();
        }
    }
}
