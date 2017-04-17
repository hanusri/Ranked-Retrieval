import java.io.Serializable;

/**
 * Created by Srikanth on 3/16/2017.
 */
public class PostingNode implements Serializable {
    private int documentId;
    private int termFrequency;

    public PostingNode(int documentId) {
        this.documentId = documentId;
    }

    public int getTermFrequency() {
        return termFrequency;
    }

    public void setTermFrequency(int termFrequency) {
        this.termFrequency = termFrequency;
    }

    public int getDocumentId() {
        return documentId;
    }
}
