import java.io.*;
import java.util.*;

/**
 * Created by Srikanth on 1/31/2017.
 */
public class ApplicationRunner {
    private static SortedMap<String, LinkedList<PostingNode>> lemmaDictionary;
    private static SortedMap<String, LinkedList<PostingNode>> stemmingDictionary;
    private static HashMap<Integer, DocumentNode> lemmaDocumentMap;
    private static HashMap<Integer, DocumentNode> stemmingDocumentMap;
    private static HashSet<String> stopWords;
    private static ArrayList<QueryNode> queryList;
    private static HashMap<String, Integer> globalQueryMap;
    private static IProcessor iProcessor;

    public ApplicationRunner() {

    }

    public static void main(String[] args) {
        if (args.length != 3)
            System.out.println("Please provide valid dataset path and stop words path");
        else {
            lemmaDictionary = new TreeMap<>();
            stemmingDictionary = new TreeMap<>();
            lemmaDocumentMap = new HashMap<>();
            stemmingDocumentMap = new HashMap<>();
            queryList = new ArrayList<>();
            stopWords = new HashSet<>();
            globalQueryMap = new HashMap<>();
            // load stop words
            loadStopwords(args[1]);
            processFile(args[0], args[2]);
        }
    }

    private static void loadStopwords(String stopwordsPath) {
        FileInputStream fileInputStream = null;
        DataInputStream dataInputStream = null;
        BufferedReader bufferedReader = null;
        try {
            File stopwordsFile = new File(stopwordsPath);
            fileInputStream = new FileInputStream(stopwordsFile);
            dataInputStream = new DataInputStream(fileInputStream);
            bufferedReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                stopWords.add(line.toLowerCase());
            }
        } catch (Exception e) {
            try {
                fileInputStream.close();
                dataInputStream.close();
                bufferedReader.close();
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    private static void processFile(String directoryPath, String queryPath) {
        // get the list of files
        File[] lstFiles = Utility.getFiles(directoryPath);
        Arrays.sort(lstFiles);
        if (lstFiles.length == 0)
            System.out.println("Error loading the directory");
        else {
            System.out.println("Uncompressed Index Version 1 started...");
            long startTime = System.currentTimeMillis();
            iProcessor = new UncompressedLemmaProcessor(lstFiles);
            iProcessor.execute();
            long endTime = System.currentTimeMillis();
            System.out.println("Uncompressed Index Version 1 completed...");
            System.out.println("Time taken to complete Uncompressed Version 1 is " + (endTime - startTime) + " ms");

            System.out.println("Load Query information");
            startTime = System.currentTimeMillis();
            iProcessor = new QueryProcessor(queryPath);
            iProcessor.execute();
            endTime = System.currentTimeMillis();
            System.out.println("Loading Query information completed...");
            System.out.println("Time taken to complete loading query information is " + (endTime - startTime) + " ms");


            for (QueryNode queryNode : queryList) {
                for (Map.Entry<String, Integer> entry : queryNode.getMap().entrySet()) {
                    int count = globalQueryMap.getOrDefault(entry.getKey(), 0);
                    globalQueryMap.put(entry.getKey(), count + entry.getValue());
                }
            }

            // traverse through each query and process
            for (QueryNode queryNode : queryList) {
                RankedRetrievalProcessor rankedRetrievalProcessor = new RankedRetrievalProcessor();
                rankedRetrievalProcessor.processCosineScore(queryNode);
                ArrayList<QueryDocumentNode> queryDocumentNodes = rankedRetrievalProcessor.topKDocuments();
                rankedRetrievalProcessor.printVectorRepresentation(queryDocumentNodes);
            }
        }
    }

    public static int getAverageQueryLength() {
        int totalQueryTerms = 0;
        for (QueryNode queryNode : queryList) {
            for (int val : queryNode.getMap().values()) {
                totalQueryTerms += val;
            }
        }
        return totalQueryTerms / 20;
    }

    public static SortedMap<String, LinkedList<PostingNode>> getLemmaDictionary() {
        return lemmaDictionary;
    }

    public static SortedMap<String, LinkedList<PostingNode>> getStemmingDictionary() {
        return stemmingDictionary;
    }

    public static HashMap<Integer, DocumentNode> getLemmaDocumentMap() {
        return lemmaDocumentMap;
    }

    public static HashMap<Integer, DocumentNode> getStemmingDocumentMap() {
        return stemmingDocumentMap;
    }

    public static HashSet<String> getStopWords() {
        return stopWords;
    }

    public static ArrayList<QueryNode> getQueryList() {
        return queryList;
    }

    public static HashMap<String, Integer> getGlobalQueryMap() {
        return globalQueryMap;
    }
}
