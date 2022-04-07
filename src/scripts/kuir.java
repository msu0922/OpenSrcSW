package scripts;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;

public class kuir {
    public static void main(String[] args) throws ParserConfigurationException, IOException, TransformerException, XPathExpressionException, SAXException, ClassNotFoundException {

        // Build 이후 Run 하는 방법:
        // Run - Edit Configurations... - Program arguments에 필요한 값 작성
        // makeCollection.java 실행 시:    -c ./data/
        // makeKeyword.java 실행 시:       -k ./collection.xml
        // indexer.java 실행 시:           -i ./index.xml
        // searcher.java 실행 시:           -s ./index.post -q "query"


        String command = args[0];
        String path = args[1];

        if (command.equals("-c")) {
            makeCollection collection = new makeCollection(path); // "./data/"
            collection.makeXml();
        } else if (command.equals("-k")) {
            makeKeyword keyword = new makeKeyword(path); // collection.xml 경로
            keyword.convertXml();
        } else if (command.equals("-i")) {
            indexer index = new indexer(path); // index.xml 경로
            index.convertPost();
        } else if (command.equals("-s")) {
            searcher search = new searcher(path);

            String qCommand = args[2];
            String query = args[3];

            if (qCommand.equals("-q")) {
                search.printCosSim(search.CalcSim(query));
            }
        }
    }
}
