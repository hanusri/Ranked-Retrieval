import java.util.*;

/**
 * Created by Srikanth on 4/16/2017.
 */
public class QueryDocumentNode {
    private double score;
    private HashSet<String> querySet;
    private int documentId;

    public QueryDocumentNode() {
        querySet = new HashSet<>();
    }

    public HashSet<String> getQuerySet() {
        return querySet;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getDocumentId() {
        return documentId;
    }

    public void setDocumentId(int documentId) {
        this.documentId = documentId;
    }
}
