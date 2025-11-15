package Kolokviumski.second10;

import java.util.*;
import java.util.stream.Collectors;

class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException(String message) {
        super(String.format("Category %s was not found", message));
    }
}

class Category {
    private String name;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        Category category = (Category) o;
        return this.name.equals(category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}

abstract class NewsItem {
    private String title;
    private Date datePublished;
    private Category category;

    public NewsItem(String title, Date datePublished, Category category) {
        this.title = title;
        this.datePublished = datePublished;
        this.category = category;
    }

    public String getTitle() {
        return title;
    }

    public abstract String getTeaser();


    public Category getCategory() {
        return category;
    }

    public int when() {
        Date now = new Date();
        long ms = now.getTime() - datePublished.getTime();
        return (int) (ms / 1000) / 60;
    }

}

class TextNewsItem extends NewsItem {
    private String text;

    public TextNewsItem(String title, Date datePublished, Category category, String text) {
        super(title, datePublished, category);
        this.text = text;
    }

    @Override
    public String getTeaser() {
        String teaser = text;
        if (text.length() > 80) {
            teaser = text.substring(0, 80);
        }
        return String.format("%s\n%d\n%s\n", getTitle(), when(), teaser);
    }
}

class MediaNewsItem extends NewsItem {
    private String url;
    private int numViews;

    public MediaNewsItem(String title, Date datePublished, Category category, String url, int numViews) {
        super(title, datePublished, category);
        this.url = url;
        this.numViews = numViews;
    }

    public String getUrl() {
        return url;
    }

    public int getNumViews() {
        return numViews;
    }

    @Override
    public String getTeaser() {
        return String.format("%s\n%d\n%s\n%d\n", getTitle(), when(), url, numViews);
    }

}

class FrontPage {
    List<NewsItem> news;
    Category[] categories;

    public FrontPage(Category[] categories) {
        this.categories = categories;
        this.news = new ArrayList<NewsItem>();
    }


    public void addNewsItem(NewsItem newsItem) {
        news.add(newsItem);
    }


    public List<NewsItem> listByCategory(Category c) {
        return news.stream()
                .filter(newsItem -> newsItem.getCategory().equals(c))
                .collect(Collectors.toList());
    }

    public List<NewsItem> listByCategoryName(String name) throws CategoryNotFoundException {
        Category category = Arrays.stream(categories)
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new CategoryNotFoundException(name));
        return listByCategory(category);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (NewsItem ni : news) {
            sb.append(ni.getTeaser());
        }
        return sb.toString();
    }
}

public class FrontPageTest {
    public static void main(String[] args) {
        // Reading
        Scanner scanner = new Scanner(System.in);
        String line = scanner.nextLine();
        String[] parts = line.split(" ");
        Category[] categories = new Category[parts.length];
        for (int i = 0; i < categories.length; ++i) {
            categories[i] = new Category(parts[i]);
        }
        int n = scanner.nextInt();
        scanner.nextLine();
        FrontPage frontPage = new FrontPage(categories);
        Calendar cal = Calendar.getInstance();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            cal = Calendar.getInstance();
            int min = scanner.nextInt();
            cal.add(Calendar.MINUTE, -min);
            Date date = cal.getTime();
            scanner.nextLine();
            String text = scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            TextNewsItem tni = new TextNewsItem(title, date, categories[categoryIndex], text);
            frontPage.addNewsItem(tni);
        }

        n = scanner.nextInt();
        scanner.nextLine();
        for (int i = 0; i < n; ++i) {
            String title = scanner.nextLine();
            int min = scanner.nextInt();
            cal = Calendar.getInstance();
            cal.add(Calendar.MINUTE, -min);
            scanner.nextLine();
            Date date = cal.getTime();
            String url = scanner.nextLine();
            int views = scanner.nextInt();
            scanner.nextLine();
            int categoryIndex = scanner.nextInt();
            scanner.nextLine();
            MediaNewsItem mni = new MediaNewsItem(title, date, categories[categoryIndex], url, views);
            frontPage.addNewsItem(mni);
        }
        // Execution
        String category = scanner.nextLine();
        System.out.println(frontPage);
        for (Category c : categories) {
            System.out.println(frontPage.listByCategory(c).size());
        }
        try {
            System.out.println(frontPage.listByCategoryName(category).size());
        } catch (CategoryNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
