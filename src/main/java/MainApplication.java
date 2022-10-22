import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MainApplication {
    public static void main(String[] args) throws IOException {
        checkList("list.csv");
    }

    private static void checkList(String fileName) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                records.add(Arrays.asList(values));
            }
        }
        for (List<String> list : records) {
            String name = list.get(0) +
                    " " +
                    list.get(1) +
                    " " +
                    list.get(2);
            for (String s : getNames(name))
                if (s.contains(name.toLowerCase()))
                    System.out.println(name);
        }

    }

    private static List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<String>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(";");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }
        return values;
    }

    private static Set<String> getNames(String textRequest) throws IOException {
        int pagesCount = getPages(textRequest);
        Document doc = Jsoup.connect("https://nemez1da.ru/?s=" + textRequest)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elements = doc.select("#simple-grid-posts-wrapper > div.simple-grid-posts-content > div.simple-grid-posts.simple-grid-posts-grid");
        Set<String> names = new HashSet<>();
        for (Element element : elements)
            for (Element element1 : element.getElementsByClass("simple-grid-grid-post-title"))
                names.add(element1.select("a").text().toLowerCase());
        for (int i = 2; i <= pagesCount; i++) {
            doc = Jsoup.connect("https://nemez1da.ru/page/" + i + "/?s=" + textRequest)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
            elements = doc.select("#simple-grid-posts-wrapper > div.simple-grid-posts-content > div.simple-grid-posts.simple-grid-posts-grid");
            for (Element element : elements)
                for (Element element1 : element.getElementsByClass("simple-grid-grid-post-title"))
                    names.add(element1.select("a").text().toLowerCase());
        }
        return names;
    }

    private static int getPages(String textRequest) throws IOException {
        Document doc = Jsoup.connect("https://nemez1da.ru/?s=" + textRequest)
                .userAgent("Chrome/4.0.249.0 Safari/532.5")
                .referrer("http://www.google.com")
                .get();
        Elements elements = doc.select("#simple-grid-posts-wrapper > div.simple-grid-posts-content > nav > div").select("a");
        int result = 0;
        try {
            for (Element element : elements)
                if (Integer.parseInt(element.text()) > result)
                    result = Integer.parseInt(element.text());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
