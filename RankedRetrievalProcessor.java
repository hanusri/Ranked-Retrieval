import java.io.File;
import java.util.*;

/**
 * Created by Srikanth on 4/16/2017.
 */
public class RankedRetrievalProcessor {

    private HashMap<String, Double> queryWeightMap;
    private HashMap<Integer, QueryDocumentNode> queryDocumentMap;

    public RankedRetrievalProcessor() {
        queryWeightMap = new HashMap<>();
        queryDocumentMap = new HashMap<>();
    }

    public void processCosineScore(QueryNode queryNode) {
        for (Map.Entry<String, Integer> queryEntry : queryNode.getMap().entrySet()) {
            String term = queryEntry.getKey();
            int termOccurrence = queryEntry.getValue();

            //3. calculate query weight
            double queryScore = (0.4 + 0.6 * Math.log10(termOccurrence + 0.5) / Math.log10(queryNode.getMaxTF() + 1.0))
                    * (Math.log10(20 / ApplicationRunner.getGlobalQueryMap().get(term)) / Math.log10(20));
            queryWeightMap.put(term, queryScore);

            LinkedList<PostingNode> queryPostingList = ApplicationRunner.getLemmaDictionary().get(term);

            if (queryPostingList == null)
                continue;

            for (PostingNode queryPostingNode : queryPostingList) {
                int documentId = queryPostingNode.getDocumentId();
                int termFrequency = queryPostingNode.getTermFrequency();
                int maxTf = ApplicationRunner.getLemmaDocumentMap().get(documentId).getMaxTermFrequency();
                // calculate the weight
                double queryDocumentScore = (0.4 + 0.6 * Math.log10(termFrequency + 0.5) / Math.log10(maxTf + 1.0)) *
                        (Math.log10(ApplicationRunner.getLemmaDocumentMap().size() / queryPostingList.size()) /
                                Math.log10(ApplicationRunner.getLemmaDocumentMap().size()));
                // multiply with queryscore and add it to doc id; add the term also to the list that made it
                QueryDocumentNode queryDocumentNode = queryDocumentMap.getOrDefault(documentId, null);
                if (queryDocumentNode == null)
                    queryDocumentNode = new QueryDocumentNode();
                queryDocumentNode.setScore(queryDocumentNode.getScore() + (queryDocumentScore * queryScore));
                queryDocumentNode.setDocumentId(documentId);
                queryDocumentNode.getQuerySet().add(term);
                queryDocumentMap.put(documentId, queryDocumentNode);
            }
        }
        normalize();
    }

    private void normalize() {
        // normalizing querydocument score
        double normalize = 0.0;
        for (QueryDocumentNode queryDocumentNode : queryDocumentMap.values()) {
            if (queryDocumentNode != null) {
                normalize += Math.pow(queryDocumentNode.getScore(), 2);
            }
        }
        normalize = Math.sqrt(normalize);
        for (Map.Entry<Integer, QueryDocumentNode> entry : queryDocumentMap.entrySet()) {
            QueryDocumentNode queryDocumentNode = entry.getValue();
            queryDocumentNode.setScore(queryDocumentNode.getScore() / normalize);
            queryDocumentMap.put(entry.getKey(), queryDocumentNode);
        }

        // normalizing query score
        normalize = 0.0;
        for (Double val : queryWeightMap.values()) {
            normalize += Math.pow(val, 2);
        }
        normalize = Math.sqrt(normalize);
        for (Map.Entry<String, Double> entry : queryWeightMap.entrySet()) {
            queryWeightMap.put(entry.getKey(), entry.getValue() / normalize);
        }
    }

    public ArrayList<QueryDocumentNode> topKDocuments() {
        PriorityQueue<QueryDocumentNode> priorityQueue = new PriorityQueue<>(Constants.TOP_DOCUMENTS_COUNT, new Comparator<QueryDocumentNode>() {
            @Override
            public int compare(QueryDocumentNode o1, QueryDocumentNode o2) {
                Double value = o1.getScore() - o2.getScore();
                if (value > 0.00)
                    return 1;
                else if (value < 0.00)
                    return -1;

                return 0;
            }
        });

        int i = 0;

        for (Map.Entry<Integer, QueryDocumentNode> entry : queryDocumentMap.entrySet()) {
            if (i < Constants.TOP_DOCUMENTS_COUNT)
                priorityQueue.add(entry.getValue());
            else if (priorityQueue.peek().getScore() < entry.getValue().getScore()) {
                priorityQueue.remove();
                priorityQueue.add(entry.getValue());
            }
            i++;
        }
        return new ArrayList<>(priorityQueue);
    }

    public void printVectorRepresentation(ArrayList<QueryDocumentNode> queryDocumentNodes) {
        Collections.sort(queryDocumentNodes, new Comparator<QueryDocumentNode>() {
            @Override
            public int compare(QueryDocumentNode o1, QueryDocumentNode o2) {
                Double value = o2.getScore() - o1.getScore();
                if (value > 0.00)
                    return 1;
                else if (value < 0.00)
                    return -1;

                return 0;
            }
        });
        System.out.println("**************************************************");
        System.out.println("Query in vector representation based on W2 Function");
        System.out.println(queryWeightMap.entrySet());
        System.out.println("**************************************************");

        System.out.println("*****************************************************************************");
        System.out.println("Documents retrieved from rank search based on W1 function");
        System.out.format("%-10s%-30s%-30s%-30s\n", "Rank", "Score", "Document Indentifier", "Heading");

        for (int i = 0; i < queryDocumentNodes.size(); i++) {
            QueryDocumentNode queryDocumentNode = queryDocumentNodes.get(i);
            System.out.format("%-10s%-30s%-30s%-30s\n", (i + 1), queryDocumentNode.getScore(),
                    ApplicationRunner.getLemmaDocumentMap().get(queryDocumentNode.getDocumentId()).getDocumentName(),
                    ApplicationRunner.getLemmaDocumentMap().get(queryDocumentNode.getDocumentId()).getDocumentTitle());


        }

        System.out.println("*************************************************************************");
        System.out.println("Document vector based on W1 Function");

        for (int i = 0; i < queryDocumentNodes.size(); i++) {
            QueryDocumentNode queryDocumentNode = queryDocumentNodes.get(i);
            System.out.println("====================================================");
            HashSet<String> querySet = queryDocumentNode.getQuerySet();
            System.out.println("Document vector of query w.r.t to document " + queryDocumentNode.getDocumentId());
            for (String term : querySet) {
                System.out.println("Term " + term + " Weight: " + queryWeightMap.get(term));
            }
            System.out.println("======================================================");

        }
        System.out.println("****************************************************************************");
    }
}
