package Lab.Eight;

import java.util.ArrayList;
import java.util.List;

interface State {
    void pressPlay();

    void pressRewind();

    void pressForward();

    void pressStop();

    void forward();

    void rewind();
}

abstract class AbstractState implements State {
    protected MP3Player player;

    public AbstractState(MP3Player player) {
        this.player = player;
    }
}

class PlayState extends AbstractState {
    public PlayState(MP3Player player) {
        super(player);
    }


    @Override
    public void pressPlay() {
        System.out.println("Song is already playing");
    }

    @Override
    public void pressRewind() {
        System.out.println("Reward...");
        player.setState(player.rewind);
    }

    @Override
    public void pressForward() {
        System.out.println("Forward...");
        player.setState(player.forward);
    }

    @Override
    public void pressStop() {
        System.out.println("Song " + player.getCurrentSongIndex() + " is paused");
        player.setState(player.pause);
    }

    @Override
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void rewind() {
        System.out.println("Illegal action");
    }
}

class PauseState extends AbstractState {
    public PauseState(MP3Player player) {
        super(player);
    }

    @Override
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void rewind() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are stopped");
        player.setCurrentSongIndex(0);
        player.setState(player.stop);
    }

    @Override
    public void pressForward() {
        System.out.println("Forward...");
        player.setState(player.forward);
    }

    @Override
    public void pressRewind() {
        System.out.println("Reward...");
        player.setState(player.rewind);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
        player.setState(player.play);
    }
}

class StopState extends AbstractState {
    public StopState(MP3Player player) {
        super(player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song " + player.getCurrentSongIndex() + " is playing");
        player.setState(player.play);
    }

    @Override
    public void pressRewind() {
        System.out.println("Reward...");
        player.setState(player.rewind);
    }

    @Override
    public void pressForward() {
        System.out.println("Forward...");
        player.setState(player.forward);
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are already stopped");
    }

    @Override
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void rewind() {
        System.out.println("Illegal action");
    }
}

class ForwardState extends AbstractState {
    public ForwardState(MP3Player player) {
        super(player);
    }

    @Override
    public void pressStop() {
        System.out.println("Illegal action");
    }

    @Override
    public void forward() {
        player.songFWD();
        player.setState(player.pause);
    }

    @Override
    public void rewind() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressForward() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressRewind() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressPlay() {
        System.out.println("Illegal action");
    }
}

class RewindState extends AbstractState {
    public RewindState(MP3Player player) {
        super(player);
    }

    @Override
    public void pressStop() {
        System.out.println("Illegal action");
    }

    @Override
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void rewind() {
        player.songRWD();
        player.setState(player.pause);
    }

    @Override
    public void pressForward() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressRewind() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressPlay() {
        System.out.println("Illegal action");
    }
}


class Song {
    private String title, artist;

    public Song(String title, String artist) {
        this.title = title;
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    @Override
    public String toString() {
        return "Song{" +
                "title=" + title +
                ", artist=" + artist +
                '}';
    }
}


class MP3Player {
    List<Song> songs;
    int currentSongIndex;


    State state;

    //all possible states
    State play, pause, stop, forward, rewind;

    public MP3Player(List<Song> songs) {
        this.songs = songs;
        this.currentSongIndex = 0;
        initializeStates();
    }

    final void initializeStates() {
        play = new PlayState(this);
        pause = new PauseState(this);
        stop = new StopState(this);
        forward = new ForwardState(this);
        rewind = new RewindState(this);

        state = stop; //initial state
    }

    public void setState(State state) {
        this.state = state;
    }

    public void pressPlay() {
        state.pressPlay();
    }

    public void pressREW() {
        state.pressRewind();
        state.rewind();
    }

    public void pressFWD() {
        state.pressForward();
        state.forward();
    }

    public void pressStop() {
        state.pressStop();
    }

    public void printCurrentSong() {
        System.out.println(songs.get(currentSongIndex));
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public void setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex % songs.size();
    }

    @Override
    public String toString() {
        return "MP3Player{" +
                "currentSong = " + currentSongIndex +
                ", songList = " + songs +
                '}';
    }

    public void songRWD() {
        currentSongIndex = (currentSongIndex + songs.size() - 1) % songs.size();
    }

    public void songFWD() {
        currentSongIndex = (currentSongIndex + 1) % songs.size();
    }
}


public class PatternTest {
    public static void main(String args[]) {
        List<Song> listSongs = new ArrayList<Song>();
        listSongs.add(new Song("first-title", "first-artist"));
        listSongs.add(new Song("second-title", "second-artist"));
        listSongs.add(new Song("third-title", "third-artist"));
        listSongs.add(new Song("fourth-title", "fourth-artist"));
        listSongs.add(new Song("fifth-title", "fifth-artist"));
        MP3Player player = new MP3Player(listSongs);


        System.out.println(player.toString());
        System.out.println("First test");


        player.pressPlay();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressPlay();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Second test");


        player.pressStop();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressStop();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
        System.out.println("Third test");


        player.pressFWD();
        player.printCurrentSong();
        player.pressFWD();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressPlay();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressStop();
        player.printCurrentSong();

        player.pressFWD();
        player.printCurrentSong();
        player.pressREW();
        player.printCurrentSong();


        System.out.println(player.toString());
    }
}

