package LabsPrereseni.five;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.TreeSet;


class NoSuchRoomException extends Exception {
    public NoSuchRoomException(String roomName) {
        super("No such room: " + roomName);
    }
}

class NoSuchUserException extends Exception {
    public NoSuchUserException(String userName) {
        super("No such user: " + userName);
    }
}

class ChatRoom implements Comparable<ChatRoom> {
    String name;
    Set<String> users;

    public ChatRoom(String name) {
        this.name = name;
        this.users = new TreeSet<>();
    }

    public void addUser(String username) {
        users.add(username);
    }

    public boolean hasUser(String username) {
        return users.contains(username);
    }

    public int numUsers() {
        return users.size();
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    @Override
    public String toString() {
        System.out.printf("%s%n", name);
        if (users.isEmpty()) {
            System.out.println("EMPTY\n");
        } else {
            users.forEach(u -> System.out.printf("%s%n", u));
        }
        return "";
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
    List<String> registeredUsers;

    public ChatSystem() {
        this.rooms = new HashMap<>();
        this.registeredUsers = new ArrayList<>();
    }

    public void addRoom(String roomName) {
        rooms.putIfAbsent(roomName, new ChatRoom(roomName));
    }

    public void removeRoom(String roomName) {
        rooms.remove(roomName);
    }

    public ChatRoom getRoom(String roomName) throws NoSuchRoomException {
        if (!rooms.containsKey(roomName)) {
            throw new NoSuchRoomException(roomName);
        }
        return rooms.get(roomName);
    }

    public void register(String username) {
        registeredUsers.add(username);

        rooms.values()
                .stream()
                .sorted()
                .findFirst()
                .ifPresent(room -> room.addUser(username));
    }

    public void registerAndJoin(String username, String roomName) {
        registeredUsers.add(username);

        rooms.putIfAbsent(roomName, new ChatRoom(roomName));
        rooms.get(roomName).addUser(username);
    }

    public void joinRoom(String username, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if (!registeredUsers.contains(username)) {
            throw new NoSuchUserException(username);
        }
        if (!rooms.containsKey(roomName)) {
            throw new NoSuchRoomException(roomName);
        } else rooms.get(roomName).addUser(username);
    }

    public void leaveRoom(String username, String roomName) throws NoSuchUserException, NoSuchRoomException {
        if (!registeredUsers.contains(username)) {
            throw new NoSuchUserException(username);
        }
        if (!rooms.containsKey(roomName)) {
            throw new NoSuchRoomException(roomName);
        } else rooms.get(roomName).removeUser(username);
    }

    public void followFriend(String username, String friendName) throws NoSuchUserException {
        if (!registeredUsers.contains(username)) {
            throw new NoSuchUserException(username);
        }
        if (!registeredUsers.contains(friendName)) {
            throw new NoSuchUserException(friendName);
        }

        for (ChatRoom room : rooms.values()) {
            if (room.hasUser(friendName))
                room.addUser(username);
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

