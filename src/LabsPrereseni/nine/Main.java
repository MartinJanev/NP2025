package LabsPrereseni.nine;

import java.util.*;

class DocumentViewer {
    private Map<String, IDocument> documents;

    public DocumentViewer() {
        documents = new HashMap<>();
    }


    public void addDocument(String docId, String text) {
        documents.putIfAbsent(docId, new BasicDocument(text));
    }

    public void enableLineNumbers(String id) {
        IDocument document = documents.get(id);
        if (document != null) {
            documents.put(id, new LineNumberDecorator(document));
        }
    }

    public void enableWordCount(String id) {
        IDocument document = documents.get(id);
        if (document != null) {
            documents.put(id, new WordCountDecorator(document));
        }
    }

    public void enableRedaction(String id, List<String> forbiddenWords) {
        IDocument document = documents.get(id);
        if (document != null) {
            documents.put(id, new RedactionDecorator(document, forbiddenWords));
        }
    }

    public void display(String id) {
        IDocument document = documents.get(id);
        if (document != null) {
            System.out.printf("=== Document %s ===%n", id);
            System.out.println(document.getContent());
        }
    }
}

interface IDocument {
    String getContent();
}

class BasicDocument implements IDocument {

    private String text;

    public BasicDocument(String text) {
        this.text = text;
    }

    @Override
    public String getContent() {
        return text;
    }
}

abstract class DocumentDecorator implements IDocument {
    protected IDocument document;

    public DocumentDecorator(IDocument document) {
        this.document = document;
    }
}

class LineNumberDecorator extends DocumentDecorator {
    public LineNumberDecorator(IDocument document) {
        super(document);
    }

    @Override
    public String getContent() {
        String content = document.getContent();
        String[] lines = content.split("\n", -1);
        StringBuilder res = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            res.append(i + 1).append(": ").append(lines[i]);
            if (i < lines.length - 1) {
                res.append("\n");
            }
        }
        return res.toString();
    }
}

class WordCountDecorator extends DocumentDecorator {
    public WordCountDecorator(IDocument document) {
        super(document);
    }

    @Override
    public String getContent() {
        String content = document.getContent();
        String[] words = content.split("\\s+");
        return content + "\nWords: " + words.length;
    }
}

class RedactionDecorator extends DocumentDecorator {

    private List<String> forbiddenWords;

    public RedactionDecorator(IDocument document, List<String> forbiddenWords) {
        super(document);
        this.forbiddenWords = forbiddenWords;
    }

    @Override
    public String getContent() {
        String content = document.getContent();
        for (String word : forbiddenWords) {
            content = content.replaceAll("(?i)\\b" + word + "\\b", "*");
        }
        return content;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DocumentViewer viewer = new DocumentViewer();

        /* 1. Read number of documents */
        int n = Integer.parseInt(sc.nextLine().trim());

        /* 2. Read documents */
        for (int i = 0; i < n; i++) {
            String docId = sc.nextLine().trim();
            int lines = Integer.parseInt(sc.nextLine().trim());

            StringBuilder content = new StringBuilder();
            for (int j = 0; j < lines; j++) {
                content.append(sc.nextLine());
                if (j < lines - 1) {
                    content.append("\n");
                }
            }

            viewer.addDocument(docId, content.toString());
        }

        /* 3. Process commands */
        while (sc.hasNextLine()) {
            String line = sc.nextLine().trim();
            if (line.equals("exit")) {
                return;
            }

            if (line.startsWith("enableLineNumbers")) {
                String[] parts = line.split("\\s+");
                viewer.enableLineNumbers(parts[1]);

            } else if (line.startsWith("enableWordCount")) {
                String[] parts = line.split("\\s+");
                viewer.enableWordCount(parts[1]);

            } else if (line.startsWith("enableRedaction")) {
                String[] parts = line.split("\\s+");
                String docId = parts[1];

                List<String> forbidden = new ArrayList<>();
                for (int i = 2; i < parts.length; i++) {
                    forbidden.add(parts[i]);
                }

                viewer.enableRedaction(docId, forbidden);

            } else if (line.startsWith("display")) {
                String[] parts = line.split("\\s+");
                viewer.display(parts[1]);
            }
        }

        sc.close();
    }
}


