package Aud.aud6;

import java.util.*;

class AttachmentsSizeExceededException extends Exception {
    public AttachmentsSizeExceededException(String message) {
        super(message);
    }
}

class FileNotSupportedException extends Exception {
    public FileNotSupportedException(String message) {
        super(message);
    }
}

class Attachment {
    private final String fileName;
    private final int sizeInMB;

    public Attachment(String fileName, int sizeInMB) {
        this.fileName = fileName;
        this.sizeInMB = sizeInMB;
    }

    public String getFileName() {
        return fileName;
    }

    public int getSizeInMB() {
        return sizeInMB;
    }
}


class LLMModelDetails {
    private final String modelName;
    private final double inputTokenPrice;
    private final double outputTokenPrice;
    private final double mbAttachmentPrice;

    public LLMModelDetails(String modelName, double inputTokenPrice, double outputTokenPrice, double mbAttachmentPrice) {
        this.modelName = modelName;
        this.inputTokenPrice = inputTokenPrice;
        this.outputTokenPrice = outputTokenPrice;
        this.mbAttachmentPrice = mbAttachmentPrice;
    }

    public double getInputTokenPrice() {
        return inputTokenPrice;
    }

    public double getOutputTokenPrice() {
        return outputTokenPrice;
    }

    public double getMbAttachmentPrice() {
        return mbAttachmentPrice;
    }

}

class Interaction {
    private final String question;
    private final long timestampQuestion;
    private final String answer;
    private final long timestampAnswer;
    private final List<Attachment> attachments;
    private final LLMModelDetails modelDetails;

    public Interaction(String question, long timestampQuestion, String answer, long timestampAnswer, List<Attachment> attachments, LLMModelDetails modelDetails) {
        this.question = question;
        this.timestampQuestion = timestampQuestion;
        this.answer = answer;
        this.timestampAnswer = timestampAnswer;
        this.attachments = attachments;
        this.modelDetails = modelDetails;
    }

    public int attachmentsCount() {
        return attachments.size();
    }

    public int totalAttachmentsSize() {
        return attachments.stream().mapToInt(Attachment::getSizeInMB).sum();
    }

    public long processingTime() {
        return timestampAnswer - timestampQuestion;
    }

    public double inputTokens() {
        return question.length() / 4.0;
    }

    public double outputTokens() {
        return answer.length() / 4.0;
    }

    public double totalSize() {
        return attachments.stream().mapToInt(Attachment::getSizeInMB).sum();
    }

    public double price() {
        return inputTokens() * modelDetails.getInputTokenPrice() + outputTokens() * modelDetails.getOutputTokenPrice()
                + totalSize() * modelDetails.getMbAttachmentPrice();
    }

    @Override
    public String toString() {
        /*
        Q: What is AI?
        Attachments: 2
        A: AI stands for Artificial Intelligence.
        Processing time: 35 Price: 0.05
        * */

        return String.format(
                "Q: %s\nAttachmments: %d\nA: %s\nProcessing time: %d Price: %.2f",
                question,
                attachments.size(),
                answer,
                processingTime(),
                price()
        );
    }

    public long getTimestampQuestion() {
        return timestampQuestion;
    }

    public long getTimestampAnswer() {
        return timestampAnswer;
    }
}

class Session {
    private final String sessionId;
    private final Set<Interaction> interactions;

    public Session(String sessionId) {
        this.sessionId = sessionId;
        this.interactions = new TreeSet<>
                (Comparator.comparing(Interaction::getTimestampQuestion)
                        .thenComparing(Interaction::getTimestampAnswer)
                );
    }

    public Set<Interaction> getInteractions() {
        return interactions;
    }

    public void addInteraction(Interaction interaction) {
        interactions.add(interaction);
    }

    public void print() {
        interactions.forEach(System.out::println);
    }

    public double totalPrice() {
        return interactions.stream()
                .mapToDouble(Interaction::price)
                .sum();
    }

    public double totalOutput() {
        return interactions.stream()
                .mapToDouble(Interaction::outputTokens)
                .sum();
    }

    public double totalInput() {
        return interactions.stream()
                .mapToDouble(Interaction::inputTokens)
                .sum();
    }

    public int totalAttachmentsSize() {
        return interactions.stream()
                .mapToInt(Interaction::totalAttachmentsSize)
                .sum();
    }

    public int totalAttachmentsCount() {
        return interactions.stream()
                .mapToInt(Interaction::attachmentsCount)
                .sum();
    }

    public long totalProcessingTime() {
        return interactions.stream()
                .mapToLong(Interaction::processingTime)
                .sum();
    }

    public void printDetails() {
        /*
        Session ID: session1
        Interactions: 3
        Input tokens: 15
        Output tokens: 45
        Price: 0.22
        Processing time: 175
        Number of attachments: 3
        Total attachment size: 9
        * */

        System.out.println("Session ID: " + sessionId);
        System.out.println("Interactions: " + interactions.size());
        System.out.printf("Input tokens: %.0f%n", totalInput());
        System.out.printf("Output tokens: %.0f%n", totalOutput());
        System.out.printf("Price: %.2f%n", totalPrice());
        System.out.println("Processing time: " + totalProcessingTime());
        System.out.println("Number of attachments: " + totalAttachmentsCount());
        System.out.println("Total attachment size: " + totalAttachmentsSize());

    }


}

class User {
    private final String userId;
    private final Map<String, Session> sessions;

    public User(String userId) {
        this.userId = userId;
        this.sessions = new HashMap<>();
    }

    public void addInteraction(String sessionId, String question, long timestampQuestion, String answer, long timestampAnswer, List<Attachment> attachments, LLMModelDetails llmModelDetails) {
        sessions.putIfAbsent(sessionId, new Session(sessionId));
        Session s = sessions.get(sessionId);

        s.addInteraction(new Interaction(question, timestampQuestion, answer, timestampAnswer, attachments, llmModelDetails));
    }

    public String getUserId() {
        return userId;
    }

    public Map<String, Session> getSessions() {
        return sessions;
    }

    public void print() {
        sessions.values()
                .stream()
                .sorted(Comparator.comparing(Session::totalAttachmentsCount).thenComparing(Session::totalPrice).reversed())
                .forEach(Session::printDetails);
    }

    public Interaction longestProcessingTimeInteraction() {
        return sessions.values().stream()
                .flatMap(session -> session.getInteractions().stream())
                .max(Comparator.comparing(Interaction::processingTime))
                .get();
    }

    public Interaction mostExpensive() {
        return sessions.values().stream()
                .flatMap(session -> session.getInteractions().stream())
                .max(Comparator.comparing(Interaction::price))
                .get();
    }
}

class Chatbot {
    private final LLMModelDetails llmModelDetails;
    private final List<String> notSupportedFiles;
    private final int allowedAttachmentsSize;
    private final Map<String, User> users;

    public Chatbot(LLMModelDetails llmModelDetails, List<String> notSupportedFiles, int allowedAttachmentsSize) {
        this.llmModelDetails = llmModelDetails;
        this.notSupportedFiles = notSupportedFiles;
        this.allowedAttachmentsSize = allowedAttachmentsSize;
        this.users = new HashMap<>();
    }

    public void addInteraction(String userId, String sessionId, String question, long timestampQuestion, String answer, long timestampAnswer, List<Attachment> attachments)
            throws AttachmentsSizeExceededException, FileNotSupportedException {
        int sum = attachments.stream()
                .mapToInt(Attachment::getSizeInMB)
                .sum();

        if (sum > allowedAttachmentsSize) throw new AttachmentsSizeExceededException("exceed");

        for (String e : notSupportedFiles) {
            for (Attachment a : attachments) {
                if (a.getFileName().endsWith(e)) {
                    throw new FileNotSupportedException(e);
                }
            }
        }

        users.putIfAbsent(userId, new User(userId));
        User user = users.get(userId);
        user.addInteraction(sessionId, question, timestampQuestion, answer, timestampAnswer, attachments, llmModelDetails);
    }

    public void printConversation(String userId, String sessionId) {
        users.get(userId).getSessions().get(sessionId).print();
    }

    public void printSessionDetails(String userId, String sessionId) {
        users.get(userId).getSessions().get(sessionId).printDetails();
    }

    public void printUserDetails(String userId) {
        System.out.println("User: " + userId);
        System.out.println("Sessions:");
        users.get(userId).print();
    }

    public void longestProcessingTimeInteractions() {
        users.values().forEach(u -> {
            System.out.println("User: " + u.getUserId());
            System.out.println("Longest processing time interaction:");
            System.out.println(u.longestProcessingTimeInteraction());
        });
    }

    public void mostExpensiveInteractions() {
        users.values().forEach(u -> {
            System.out.println("User: " + u.getUserId());
            System.out.println("Most expensive interaction:");
            System.out.println(u.mostExpensive());
        });
    }
}

public class ChatBot {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read LLMModelDetails properties
        String modelName = scanner.next();
        double inputTokenPrice = scanner.nextDouble();
        double outputTokenPrice = scanner.nextDouble();
        double mbAttachmentPrice = scanner.nextDouble();

        LLMModelDetails llmModelDetails = new LLMModelDetails(modelName, inputTokenPrice, outputTokenPrice, mbAttachmentPrice);

        // Read list of notSupportedFiles
        scanner.nextLine(); // Consume newline
        List<String> notSupportedFiles = Arrays.asList(scanner.nextLine().split(";"));

        // Read allowedAttachmentsSize
        int allowedAttachmentsSize = scanner.nextInt();
        scanner.nextLine();

        Chatbot chatbot = new Chatbot(llmModelDetails, notSupportedFiles, allowedAttachmentsSize);

        while (scanner.hasNext()) {
            String[] parts = scanner.nextLine().split(";");
            String command = parts[0];

            switch (command) {
                case "addInteraction": {
                    try {
                        String userId = parts[1];
                        String sessionId = parts[2];
                        String question = parts[3];
                        long timestampQuestion = Long.parseLong(parts[4]);
                        String answer = parts[5];
                        long timestampAnswer = Long.parseLong(parts[6]);
                        int attachmentCount = Integer.parseInt(parts[7]);
                        List<Attachment> attachments = new ArrayList<>();

                        for (int i = 0; i < attachmentCount; i++) {
                            String fileName = parts[8 + i * 2];
                            int fileSize = Integer.parseInt(parts[9 + i * 2]);
                            attachments.add(new Attachment(fileName, fileSize));
                        }

                        chatbot.addInteraction(userId, sessionId, question, timestampQuestion, answer, timestampAnswer, attachments);
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    break;
                }
                case "printConversation": {
                    String userId = parts[1];
                    String sessionId = parts[2];
                    chatbot.printConversation(userId, sessionId);
                    break;
                }
                case "printSessionDetails": {
                    String userId = parts[1];
                    String sessionId = parts[2];
                    chatbot.printSessionDetails(userId, sessionId);
                    break;
                }
                case "printUserDetails": {
                    String userId = parts[1];
                    chatbot.printUserDetails(userId);
                    break;
                }
                case "longestProcessingTimeInteractions": {
                    chatbot.longestProcessingTimeInteractions();
                    break;
                }
                case "mostExpensiveInteractions": {
                    chatbot.mostExpensiveInteractions();
                    break;
                }
                case "exit": {
                    return;
                }
            }
        }

        scanner.close();
    }
}