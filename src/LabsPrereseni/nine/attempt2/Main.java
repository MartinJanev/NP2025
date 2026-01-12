package LabsPrereseni.nine.attempt2;

import java.util.*;

interface IDocument {
    String getContent();
}

class Document implements IDocument {
    protected String text;

    public Document(String text) {
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


class LineDecorator extends DocumentDecorator {
    public LineDecorator(IDocument document) {
        super(document);
    }

    @Override
    public String getContent() {
        String content = document.getContent();
        String[] lines = content.split("\n", -1);
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < lines.length; i++) {
            result.append(i + 1 + ": " + lines[i]);
            if (i < lines.length - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }
}

class WordCountDecorator extends DocumentDecorator {

    public WordCountDecorator(IDocument document) {
        super(document);
    }

    @Override
    public String getContent() {
        return document.getContent() + "\nWords: " + document.getContent().split("\\s+").length;
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

class DocumentViewer {
    private Map<String, IDocument> documents;

    public DocumentViewer() {
        documents = new HashMap<>();
    }


    public void addDocument(String docId, String string) {
        documents.putIfAbsent(docId, new Document(string));
    }

    public void display(String id) {
        IDocument document = documents.get(id);
        if (document != null) {
            System.out.printf("=== Document %s ===\n", id);
            System.out.println(document.getContent());
        }
    }

    public void enableLineNumbers(String id) {
        IDocument document = documents.get(id);
        if (document != null) {
            documents.put(id, new LineDecorator(document));
        }
    }

    public void enableWordCount(String id) {
        IDocument document = documents.get(id);
        if (document != null) {
            documents.put(id, new WordCountDecorator(document));
        }
    }

    public void enableRedaction(String docId, List<String> forbidden) {
        IDocument document = documents.get(docId);
        if (document != null) {
            documents.put(docId, new RedactionDecorator(document, forbidden));
        }
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


