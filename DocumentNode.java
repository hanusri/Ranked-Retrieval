import java.io.Serializable;

/**
 * Created by Srikanth on 3/16/2017.
 */
public class DocumentNode implements Serializable {
    private String maxFrequentTerm;
    private int maxTermFrequency;
    private int documentLength;
    private int documentId;
    private String documentName;
    private String documentTitle;

    public DocumentNode(int documentId) {
        maxTermFrequency = Integer.MIN_VALUE;
        this.documentId = documentId;
    }

    public String getMaxFrequentTerm() {
        return maxFrequentTerm;
    }

    public void setMaxFrequentTerm(String maxFrequentTerm) {
        this.maxFrequentTerm = maxFrequentTerm;
    }

    public int getMaxTermFrequency() {
        return maxTermFrequency;
    }

    public void setMaxTermFrequency(int maxTermFrequency) {
        this.maxTermFrequency = maxTermFrequency;
    }

    public int getDocumentLength() {
        return documentLength;
    }

    public void setDocumentLength(int documentLength) {
        this.documentLength = documentLength;
    }

    public int getDocumentId() {
        return documentId;
    }

    public String getDocumentName() {
        return documentName;
    }

    public void setDocumentName(String documentName) {
        this.documentName = documentName;
    }

    public String getDocumentTitle() {
        return documentTitle;
    }

    public void setDocumentTitle(String documentTitle) {
        this.documentTitle = documentTitle;
    }
}
