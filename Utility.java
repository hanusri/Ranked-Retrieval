import java.io.*;
import java.util.*;

/**
 * Created by Srikanth on 1/31/2017.
 */
public class Utility {

    public static File[] getFiles(String directoryPath) {

        try {
            File folderPath = new File(directoryPath);
            return folderPath.listFiles();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return new File[0];
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortMap(final Map<K, V> mapToSort) {
        List<Map.Entry<K, V>> entries = new ArrayList<Map.Entry<K, V>>(mapToSort.size());

        entries.addAll(mapToSort.entrySet());

        Collections.sort(entries, new Comparator<Map.Entry<K, V>>() {
            public int compare(final Map.Entry<K, V> entry1, final Map.Entry<K, V> entry2) {
                return entry2.getValue().compareTo(entry1.getValue());
            }
        });

        Map<K, V> sortedMap = new LinkedHashMap<K, V>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static ArrayList<String> processFiles(File file) {
        FileInputStream fis = null;
        DataInputStream dis = null;
        BufferedReader bufferedReader = null;
        ArrayList<String> tokenizedWords = new ArrayList<>();
        try {

            if (file.isFile()) {
                // increase the document count
                fis = new FileInputStream(file);
                dis = new DataInputStream(fis);
                bufferedReader = new BufferedReader(new InputStreamReader(dis));

                String line = null;
                while ((line = bufferedReader.readLine()) != null) {
                    ArrayList<String> tokens = getTokentizedWords(line);
                    tokenizedWords.addAll(tokens);
                }
            }

        } catch (Exception ex) {
            System.out.println(ex.getCause());
        }

        return tokenizedWords;
    }

    public static ArrayList<String> getTokentizedWords(String line) {
        ArrayList<String> result = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(line, Constants.TOKENIZER_SPLIT);
        while (tokenizer.hasMoreTokens()) {
            String word = tokenizer.nextToken().trim().toLowerCase();
            // remove words which are xml tags
            if (word.matches("<[^>]+>") || word.matches("<\\/[^>]+>"))
                continue;


            // split the word if it has - or _ or white spaces like tab or \n
            String[] subWords = word.split("\\s+|\\-|\\_|\\(|\\)|\\,|\\\\|\\/");

            for (String subWord : subWords) {

                if (subWord.trim().isEmpty())
                    continue;

                // remove workds which are just numbers or just symbols
                if (subWord.matches("(\\d)*") || subWord.matches("(\\d)*.") || subWord.matches("(\\d)*.(\\d)*") ||
                        subWord.matches("[^\\w\\s]+"))
                    continue;

                // handle the 's by spliting the part and taking the actual work
                if (subWord.matches("(.*)\\'s"))
                    subWord = subWord.replace("'s", "");

                //tokenSummary.addToDictionary(subWord);
                result.add(subWord);
            }
        }

        return result;
    }

    public static ArrayList<String> getFileContents(String fileName) throws IOException {
        ArrayList<String> contents = new ArrayList<>();

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String queryStr;
        while ((queryStr = br.readLine()) != null) {
            queryStr = queryStr.trim();

            if (queryStr.isEmpty()) continue;
            contents.add(queryStr);
        }

        br.close();
        return contents;
    }

}
