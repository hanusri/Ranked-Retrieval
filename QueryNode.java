import java.util.*;

/**
 * Created by Srikanth on 4/16/2017.
 */
public class QueryNode {
    private String query;
    private HashMap<String, Integer> map;
    private int maxTF;

    public QueryNode(String query) {
        this.query = query;
        map = new HashMap<>();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public HashMap<String, Integer> getMap() {
        return map;
    }

    public void setMap(HashMap<String, Integer> map) {
        this.map = map;
    }

    public int getMaxTF() {
        return maxTF;
    }

    public void setMaxTF() {
        this.maxTF = Collections.max(map.values());
    }
}
