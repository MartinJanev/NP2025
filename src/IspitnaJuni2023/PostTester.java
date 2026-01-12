package IspitnaJuni2023;

import java.util.*;

class Comment {
    private String username;
    private String commentId;
    private String content;
    private int likes;
    private List<Comment> replies;

    public Comment(String username, String commentId, String content) {
        this.username = username;
        this.commentId = commentId;
        this.content = content;
        this.likes = 0;
        this.replies = new ArrayList<>();
    }

    public String getCommentId() {
        return commentId;
    }

    public void addLike() {
        likes++;
    }

    public void addReply(Comment reply) {
        replies.add(reply);
    }

    public Comment findComment(String id) {
        if (commentId.equals(id)) {
            return this;
        }
        for (Comment reply : replies) {
            Comment found = reply.findComment(id);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    public int getTotalLikes() {
        int total = likes;
        for (Comment reply : replies) {
            total += reply.getTotalLikes();
        }
        return total;
    }

    public int getDirectLikes() {
        return likes;
    }

    public String toString(int indent) {
        StringBuilder sb = new StringBuilder();

        // Add indentation
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }

        sb.append("Comment: ").append(content).append("\n");

        // Add indentation for "Written by"
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append("Written by: ").append(username).append("\n");

        // Add indentation for "Likes"
        for (int i = 0; i < indent; i++) {
            sb.append(" ");
        }
        sb.append("Likes: ").append(getDirectLikes()).append("\n");

        // Sort replies by total likes in descending order
        replies.sort((c1, c2) -> Integer.compare(c2.getTotalLikes(), c1.getTotalLikes()));

        // Add replies with increased indentation
        for (Comment reply : replies) {
            sb.append(reply.toString(indent + 4));
        }

        return sb.toString();
    }
}
class Post {
    private String username;
    private String postContent;
    private List<Comment> comments;

    public Post(String username, String postContent) {
        this.username = username;
        this.postContent = postContent;
        this.comments = new ArrayList<>();
    }

    public void addComment(String username, String commentId, String content, String replyToId) {
        Comment newComment = new Comment(username, commentId, content);

        if (replyToId == null) {
            comments.add(newComment);
        } else {
            for (Comment comment : comments) {
                Comment parent = comment.findComment(replyToId);
                if (parent != null) {
                    parent.addReply(newComment);
                    return;
                }
            }
        }
    }

    public void likeComment(String commentId) {
        for (Comment comment : comments) {
            Comment target = comment.findComment(commentId);
            if (target != null) {
                target.addLike();
                return;
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Post: ").append(postContent).append("\n");
        sb.append("Written by: ").append(username).append("\n");
        sb.append("Comments:\n");

        // Sort comments by total likes in descending order
        comments.sort((c1, c2) -> Integer.compare(c2.getTotalLikes(), c1.getTotalLikes()));

        for (Comment comment : comments) {
            sb.append(comment.toString(8)); // Start with 8 spaces indentation
        }

        return sb.toString();
    }
}

public class PostTester {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String postAuthor = sc.nextLine();
        String postContent = sc.nextLine();

        Post p = new Post(postAuthor, postContent);

        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            String[] parts = line.split(";");
            String testCase = parts[0];

            if (testCase.equals("addComment")) {
                String author = parts[1];
                String id = parts[2];
                String content = parts[3];
                String replyToId = null;
                if (parts.length == 5) {
                    replyToId = parts[4];
                }
                p.addComment(author, id, content, replyToId);
            } else if (testCase.equals("likes")) { //likes;1;2;3;4;1;1;1;1;1 example
                for (int i = 1; i < parts.length; i++) {
                    p.likeComment(parts[i]);
                }
            } else {
                System.out.println(p);
            }

        }
    }
}

