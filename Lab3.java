import java.util.stream.Stream;
import java.nio.file.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.function.*;

/*
 * kc: Main plagiarism detection program.
 *     Reads files, builds an n-gram index, computes similarity scores,
 *     and reports the most similar pairs of documents.
 */
// The main plagiarism detection program.
// You only need to change buildIndex() and findSimilarity().
public class Lab3 {
    public static void main(String[] args) {
        try {
            String directory;
            if (args.length == 0) {
                System.out.print("Name of directory to scan: ");
                System.out.flush();
                directory = new Scanner(System.in).nextLine();
            } else directory = args[0];
            Path[] paths = Files.list(Paths.get(directory)).toArray(Path[]::new);
            Arrays.sort(paths);

            // Stopwatches time how long each phase of the program
            // takes to execute.
            Stopwatch stopwatch = new Stopwatch();
            Stopwatch stopwatch2 = new Stopwatch();

            // Read all input files
            BST<Path, Ngram[]> files = readPaths(paths);
            stopwatch.finished("Reading all input files");

            // Build index of n-grams
            BST<Ngram, ArrayList<Path>> index = buildIndex(files);
            stopwatch.finished("Building n-gram index");

            // Compute similarity of all file pairs
            BST<PathPair, Integer> similarity = findSimilarity(files, index);
            stopwatch.finished("Computing similarity scores");

            // Find most similar file pairs, arranged in
            // decreasing order of similarity
            ArrayList<PathPair> mostSimilar = findMostSimilar(similarity);
            stopwatch.finished("Finding the most similar files");
            stopwatch2.finished("In total the program");

            // Print out some statistics
            System.out.println("\nBalance statistics:");
            System.out.println("  files: " + files.statistics());
            System.out.println("  index: " + index.statistics());
            System.out.println("  similarity: " + similarity.statistics());
            System.out.println("");

            // Print out the plagiarism report!
            System.out.println("Plagiarism report:");
            mostSimilar.stream().limit(50).forEach((PathPair pair) -> {
                System.out.printf("%5d similarity: %s\n", similarity.get(pair), pair);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * kc: Phase 1 - Read each file and split it into 5-grams.
     *     Duplicates are removed so each n-gram is counted only once per file.
     */
    // Phase 1: Read in each file and chop it into n-grams.
    static BST<Path, Ngram[]> readPaths(Path[] paths) throws IOException {
        BST<Path, Ngram[]> files = new BST<>();
        for (Path path: paths) {
            String contents = new String(Files.readAllBytes(path));
            Ngram[] ngrams = Ngram.ngrams(contents, 5);
            // Remove duplicates from the ngrams list
            // Uses the Java 8 streams API - very handy Java feature
            // which we don't cover in the course. If you want to
            // learn about it, see e.g.
            // https://docs.oracle.com/javase/8/docs/api/java/util/stream/package-summary.html#package.description
            // or https://stackify.com/streams-guide-java-8/
            ngrams = Arrays.stream(ngrams).distinct().toArray(Ngram[]::new);
            files.put(path, ngrams);
        }

        return files;
    }

    /*
     * kc: Phase 2 - Build an inverted index mapping each 5-gram to the list of
     *     files that contain it.
     *
     *     Algorithm:
     *       For every file, for every n-gram in that file:
     *         - If the n-gram is already in the index, add the file to its list.
     *         - Otherwise, create a new list with just this file and add it.
     *
     *     This turns the O(N^2) brute-force search in findSimilarity into
     *     a much faster lookup.
     */
    // Phase 2: build index of n-grams
    static BST<Ngram, ArrayList<Path>> buildIndex(BST<Path, Ngram[]> files) {
        // TODO: build index of n-grams
        BST<Ngram, ArrayList<Path>> index = new BST<>();

        // kc: go through each file in the collection
        for (Path path : files.keys()) {
            // kc: go through each n-gram of that file
            for (Ngram gram : files.get(path)) {
                if (index.contains(gram)) {
                    // kc: n-gram already seen before — just add this file to its list
                    index.get(gram).add(path);
                } else {
                    // kc: n-gram seen for the first time — create a new list for it
                    ArrayList<Path> pathList = new ArrayList<>();
                    pathList.add(path);
                    index.put(gram, pathList);
                }
            }
        }

        return index;
    }

    /*
     * kc: Phase 3 - Count shared n-grams between every pair of files.
     *
     *     Efficient algorithm using the index:
     *       For every n-gram in the index:
     *         Look at all pairs of files that share that n-gram.
     *         Increment the similarity counter for each such pair.
     *
     *     This is much faster than the brute-force 4-nested-loop approach
     *     because we only look at n-grams that actually appear in multiple files.
     */
    // Phase 3: Count how many n-grams each pair of files has in common.
    static BST<PathPair, Integer> findSimilarity(BST<Path, Ngram[]> files, BST<Ngram, ArrayList<Path>> index) {
        // TODO: use index to make this loop much more efficient
        // N.B. Path is Java's class for representing filenames
        // PathPair represents a pair of Paths (see PathPair.java)
        BST<PathPair, Integer> similarity = new BST<>();

        // kc: iterate over every n-gram in the index
        for (Ngram gram : index.keys()) {
            ArrayList<Path> sharedFiles = index.get(gram);

            // kc: only care about n-grams that appear in more than one file
            if (sharedFiles.size() < 2) continue;

            // kc: for every pair of files that share this n-gram, increment their similarity
            for (int i = 0; i < sharedFiles.size(); i++) {
                for (int j = i + 1; j < sharedFiles.size(); j++) {
                    Path path1 = sharedFiles.get(i);
                    Path path2 = sharedFiles.get(j);

                    PathPair pair = new PathPair(path1, path2);

                    // kc: if pair is new, start count at 0; then add 1
                    if (!similarity.contains(pair)) {
                        similarity.put(pair, 0);
                    }
                    similarity.put(pair, similarity.get(pair) + 1);
                }
            }
        }

        return similarity;
    }

    /*
     * kc: Phase 4 - Filter pairs with similarity >= 30 and sort them in
     *     descending order. Only the top 50 results will be printed.
     */
    // Phase 4: find all pairs of files with more than 30 n-grams
    // in common, sorted in descending order of similarity.
    static ArrayList<PathPair> findMostSimilar(BST<PathPair, Integer> similarity) {
        // Find all pairs of files with more than 100 n-grams in common.
        ArrayList<PathPair> mostSimilar = new ArrayList<>();
        for (PathPair pair: similarity.keys()) {
            if (similarity.get(pair) < 30) continue;
            // Only consider each pair of files once - (a, b) and not
            // (b,a) - and also skip pairs consisting of the same file twice
            if (pair.path1.compareTo(pair.path2) <= 0) continue;

            mostSimilar.add(pair);
        }

        // Sort to have the most similar pairs first.
        Collections.sort(mostSimilar, Comparator.comparing((PathPair pair) -> similarity.get(pair)));
        Collections.reverse(mostSimilar);
        return mostSimilar;
    }
}
