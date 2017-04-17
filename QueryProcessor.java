import java.util.*;


/**
 * Created by Srikanth on 4/16/2017.
 */
public class QueryProcessor implements IProcessor {
    private String queryFile;

    public QueryProcessor(String queryFile) {
        this.queryFile = queryFile;
    }

    @Override
    public void execute() {
        try {
            ArrayList<String> queryStringList = Utility.getFileContents(queryFile);
            for (String query : queryStringList) {
                // get the tokenized words from the string
                ArrayList<String> tokens = Utility.getTokentizedWords(query);
                QueryNode queryNode = new QueryNode(query);
                // create a query node for ech query
                Lemmatizer lemmatizer = new Lemmatizer();
                for (String queryTerm : tokens) {
                    List<String> lemmatizedWords = lemmatizer.lemmatize(queryTerm);
                    if (lemmatizedWords == null || lemmatizedWords.size() == 0)
                        continue;
                    queryTerm = lemmatizedWords.get(0);
                    int count = queryNode.getMap().getOrDefault(queryTerm, 0);
                    queryNode.getMap().put(queryTerm, count + 1);
                }
                // load query node with map and maxtf
                queryNode.setMaxTF();
                ApplicationRunner.getQueryList().add(queryNode);
            }
        } catch (Exception ex) {
            System.out.println(ex.getStackTrace());
        }
    }

    @Override
    public void writeFile() {

    }
}
