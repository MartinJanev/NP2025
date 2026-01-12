package Lab.Five;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

class NoSuchRoomException extends Exception {
    public NoSuchRoomException(String roomName) {
        System.out.println("No such room: " + roomName);
    }
}

class NoSuchUserException extends Exception {
    public NoSuchUserException(String userName) {
        System.out.println("No such user: " + userName);
    }
}

class ChatRoom implements Comparable<ChatRoom> {
    private String name;
    private TreeSet<String> users;

    public ChatRoom(String name) {
        this.name = name;
        users = new TreeSet<>();
    }

    public void addUser(String username) {
        users.add(username);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public boolean hasUser(String username) {
        return users.contains(username);
    }

    public int numUsers() {
        return users.size();
    }

    public String getName() {
        return name;
    }

    public TreeSet<String> getUsers() {
        return users;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(name + "\n");
        if (users.isEmpty()) {
            sb.append("EMPTY\n");
        }
        for (String user : users) {
            sb.append(user + "\n");
        }
        return sb.toString();
    }

    @Override
    public int compareTo(ChatRoom o) {
        int i = Integer.compare(this.numUsers(), o.numUsers());
        if (i == 0) {
            return this.name.compareTo(o.name);
        }
        return i;
    }
}

class ChatSystem {
    Map<String, ChatRoom> rooms;
    Set<String> registeredUsers;

    public ChatSystem() {
        rooms = new TreeMap<>();
        registeredUsers = new HashSet<>();
    }

    public void addRoom(String roomName) {
        rooms.putIfAbsent(roomName, new ChatRoom(roomName));
    }

    public void removeRoom(String roomName) {
        rooms.remove(roomName);
    }

    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if (!rooms.containsKey(roomName)) throw new NoSuchRoomException(roomName);
        return rooms.get(roomName);
    }

    public void register(String userName) {
        registeredUsers.add(userName);

        rooms.values()
                .stream()
                .sorted()
                .findFirst()
                .ifPresent(room -> room.addUser(userName));
    }

    public void registerAndJoin(String userName, String roomName) {
        registeredUsers.add(userName);

        rooms.putIfAbsent(roomName, new ChatRoom(roomName));
        rooms.get(roomName).addUser(userName);
    }

    public void joinRoom(String userName, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if (!registeredUsers.contains(userName))
            throw new NoSuchUserException(userName);
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).addUser(userName);
        } else throw new NoSuchRoomException(roomName);
    }

    public void leaveRoom(String userName, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if (!registeredUsers.contains(userName)) throw new NoSuchUserException(userName);
        if (rooms.containsKey(roomName)) {
            rooms.get(roomName).removeUser(userName);
        } else throw new NoSuchRoomException(roomName);
    }

    public void followFriend(String userName, String friendName) throws NoSuchUserException {
        if (!registeredUsers.contains(userName)
        ) {
            throw new NoSuchUserException(userName);
        }
        if (!registeredUsers.contains(friendName)) {
            throw new NoSuchUserException(friendName);
        }
        for (ChatRoom room : rooms.values()) {
            if (room.hasUser(friendName)) {
                room.addUser(userName);
            }
        }
    }
}

public class ChatSystemTest {

    public static void main(String[] args) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchRoomException {
        Scanner jin = new Scanner(System.in);
        int k = jin.nextInt();
        if (k == 0) {
            ChatRoom cr = new ChatRoom(jin.next());
            int n = jin.nextInt();
            for (int i = 0; i < n; ++i) {
                k = jin.nextInt();
                if (k == 0) cr.addUser(jin.next());
                if (k == 1) cr.removeUser(jin.next());
                if (k == 2) System.out.println(cr.hasUser(jin.next()));
            }
            System.out.println(cr.toString());
            n = jin.nextInt();
            if (n == 0) return;
            ChatRoom cr2 = new ChatRoom(jin.next());
            for (int i = 0; i < n; ++i) {
                k = jin.nextInt();
                if (k == 0) cr2.addUser(jin.next());
                if (k == 1) cr2.removeUser(jin.next());
                if (k == 2) cr2.hasUser(jin.next());
            }
            System.out.println(cr2.toString());
        }
        if (k == 1) {
            ChatSystem cs = new ChatSystem();
            Method[] mts = cs.getClass().getMethods();
            while (true) {
                String cmd = jin.next();
                if (cmd.equals("stop")) break;
                if (cmd.equals("print")) {
                    System.out.println(cs.getRoom(jin.next()) + "\n");
                    continue;
                }
                for (Method m : mts) {
                    if (m.getName().equals(cmd)) {
                        String[] params = new String[m.getParameterTypes().length];
                        for (int i = 0; i < params.length; ++i) params[i] = jin.next();
                        m.invoke(cs, (Object[]) params);
                    }
                }
            }
        }
    }

}
