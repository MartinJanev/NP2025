package Lab.Three;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Ad implements Comparable<Ad> {
    private String id;
    private String category;
    private double bidValue;
    private double ctr;
    private String content;
    private double totalScore;

    public Ad(String id, String category, double bidValue, double ctr, String content) {
        this.id = id;
        this.category = category;
        this.bidValue = bidValue;
        this.ctr = ctr;
        this.content = content;
        this.totalScore = 0.0;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public double getBidValue() {
        return bidValue;
    }

    public double getCtr() {
        return ctr;
    }

    public String getContent() {
        return content;
    }

    public double getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(double totalScore) {
        this.totalScore = totalScore;
    }

    @Override
    public String toString() {
        // CTR се чува како 0.12, а треба да се печати 12.00%
        return String.format("%s %s (bid=%.2f, ctr=%.2f%%) %s",
                id, category, bidValue, ctr * 100, content);
    }

    @Override
    public int compareTo(Ad o) {
        Comparator<Ad> comparator =
                Comparator.comparingDouble(Ad::getBidValue).reversed()
                        .thenComparing(Ad::getId);
        return comparator.compare(this, o);
    }
}

class AdRequest {
    private String id, category;
    private double floorBid;
    private String keywords;

    public AdRequest(String id, String category, double floorBid, String keywords) {
        this.id = id;
        this.category = category;
        this.floorBid = floorBid;
        this.keywords = keywords;
    }

    public String getId() {
        return id;
    }

    public String getCategory() {
        return category;
    }

    public double getFloorBid() {
        return floorBid;
    }

    public String getKeywords() {
        return keywords;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] (floor=%.2f): %s", id, category, floorBid, keywords);
    }
}

class AdNetwork {
    private final List<Ad> ads = new ArrayList<>();
    private static final double X = 5.0;
    private static final double Y = 100.0;

    public AdNetwork() {
    }

    private int relevanceScore(Ad ad, AdRequest req) {
        int score = 0;
        if (ad.getCategory().equalsIgnoreCase(req.getCategory())) score += 10;

        String[] adWords = ad.getContent().toLowerCase().split("\\s+");
        String[] keywords = req.getKeywords().toLowerCase().split("\\s+");

        for (String kw : keywords) {
            for (String aw : adWords) {
                if (kw.equals(aw)) score++;
            }
        }
        return score;
    }

    public void readAds(BufferedReader in) throws IOException {
        String line;
        while ((line = in.readLine()) != null) {
            if (line.trim().isEmpty()) { // празна линија -> крај на рекламите
                break;
            }

            String[] words = line.trim().split("\\s+");
            if (words.length < 4) continue;

            String id = words[0];
            String category = words[1];
            double bidValue = Double.parseDouble(words[2]);
            double ctr = Double.parseDouble(words[3]);

            String content = "";
            if (words.length > 4) {
                content = String.join(" ", Arrays.copyOfRange(words, 4, words.length));
            }

            ads.add(new Ad(id, category, bidValue, ctr, content));
        }
    }

    public void placeAds(BufferedReader in, int k, PrintWriter out) throws IOException {
        String line;

        // Скокни празни линии (ако има)
        while ((line = in.readLine()) != null && line.trim().isEmpty()) {

        }
        if (line == null) return;

        // Прочитај барање
        String[] parts = line.trim().split("\\s+");
        if (parts.length < 4) return;

        String id = parts[0];
        String category = parts[1];
        double floorBid = Double.parseDouble(parts[2]);
        String keywords = String.join(" ", Arrays.copyOfRange(parts, 3, parts.length));

        AdRequest req = new AdRequest(id, category, floorBid, keywords);

        // 1. филтрирај реклами со bid >= floorBid
        List<Ad> filteredAds = ads.stream()
                .filter(ad -> ad.getBidValue() >= floorBid)
                .collect(Collectors.toList());

        // 2. пресметај totalScore
        filteredAds.forEach(ad -> {
            int relevance = relevanceScore(ad, req);
            double totalScore = relevance + X * ad.getBidValue() + Y * ad.getCtr();
            ad.setTotalScore(totalScore);
        });

        // 3. сортирај по totalScore (опаѓачки), земи top k,
        //    потоа сортирај по natural order (bid desc, id)
        List<Ad> sortedAds = filteredAds.stream()
                .sorted(Comparator.comparingDouble(Ad::getTotalScore).reversed())
                .limit(k)
                .sorted() // користи compareTo во Ad
                .collect(Collectors.toList());

        // 4. печатење
        out.printf("Top ads for request %s:%n", req.getId());
        sortedAds.forEach(ad -> out.println(ad.toString()));
    }
}

public class Main {
    public static void main(String[] args) throws IOException {
        AdNetwork network = new AdNetwork();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter pw = new PrintWriter(new OutputStreamWriter(System.out));

        int k = Integer.parseInt(br.readLine().trim());

        if (k == 0) {
            network.readAds(br);
            network.placeAds(br, 1, pw);
        } else if (k == 1) {
            network.readAds(br);
            network.placeAds(br, 3, pw);
        } else {
            network.readAds(br);
            network.placeAds(br, 8, pw);
        }

        pw.flush();
    }
}
