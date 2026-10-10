package com.resumematch.ats.semantic;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class SemanticSimilarityService {

    private static final Set<String> STOP_WORDS = Set.of(
            "a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "aren't",
            "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "can",
            "cannot", "could", "did", "do", "does", "doing", "don't", "down", "during", "each", "few", "for", "from",
            "further", "had", "has", "have", "having", "he", "her", "here", "hers", "herself", "him", "himself",
            "his", "how", "i", "if", "in", "into", "is", "isn't", "it", "its", "itself", "let's", "me", "more",
            "most", "my", "myself", "no", "nor", "not", "of", "off", "on", "once", "only", "or", "other", "ought",
            "our", "ours", "ourselves", "out", "over", "own", "same", "she", "should", "so", "some", "such", "than",
            "that", "the", "their", "theirs", "them", "themselves", "then", "there", "these", "they", "this", "those",
            "through", "to", "too", "under", "until", "up", "very", "was", "wasn't", "we", "were", "weren't", "what",
            "when", "where", "which", "while", "who", "whom", "why", "with", "would", "you", "your", "yours"
    );

    /**
     * Calculates cosine similarity between text bodies using term-frequency vectors.
     * Normalized to a percentage score [0..100].
     */
    public double calculateCosineSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null || text1.isBlank() || text2.isBlank()) {
            return 0.0;
        }

        Map<String, Integer> freq1 = getTermFrequencies(text1);
        Map<String, Integer> freq2 = getTermFrequencies(text2);

        Set<String> allWords = new HashSet<>(freq1.keySet());
        allWords.addAll(freq2.keySet());

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (String word : allWords) {
            int v1 = freq1.getOrDefault(word, 0);
            int v2 = freq2.getOrDefault(word, 0);

            dotProduct += (double) v1 * v2;
            normA += Math.pow(v1, 2);
            normB += Math.pow(v2, 2);
        }

        if (normA == 0.0 || normB == 0.0) {
            return 0.0;
        }

        double similarity = dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
        // Normalize cosine range [0, 1] to human-calibrated ATS scale [0..100]
        // Cosine text overlap between distinct resume and JD documents rarely exceeds 0.65;
        // calibrate so reasonable alignment corresponds to 70-90% range.
        double calibrated = Math.min(100.0, (similarity * 1.35) * 100.0);
        return Math.round(calibrated * 10.0) / 10.0;
    }

    private Map<String, Integer> getTermFrequencies(String text) {
        Map<String, Integer> freq = new HashMap<>();
        String[] tokens = text.toLowerCase().replaceAll("[^a-zA-Z0-9\\s]", " ").split("\\s+");
        for (String token : tokens) {
            if (token.length() > 2 && !STOP_WORDS.contains(token)) {
                freq.put(token, freq.getOrDefault(token, 0) + 1);
            }
        }
        return freq;
    }
}
