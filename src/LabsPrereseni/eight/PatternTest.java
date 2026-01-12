package LabsPrereseni.eight;


import java.util.ArrayList;
import java.util.List;

interface State {
    //actions
    void pressPlay();

    void pressStop();

    void pressForward();

    void pressRewind();

    //transitions
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
    public void pressStop() {
        System.out.println("Song " + player.getIndexOfCurrentSong() + " is paused");
        player.setState(player.pause);
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
    public void pressPlay() {
        System.out.println("Song " + player.getIndexOfCurrentSong() + " is playing");
        player.setState(player.play);
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are stopped");
        player.setIndexOfCurrentSong(0);
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
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void rewind() {
        System.out.println("Illegal action");
    }
}

class StopState extends AbstractState {

    public StopState(MP3Player player) {
        super(player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Song " + player.getIndexOfCurrentSong() + " is playing");
        player.setState(player.play);
    }

    @Override
    public void pressStop() {
        System.out.println("Songs are already stopped");
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
    public void pressPlay() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressStop() {
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
    public void forward() {
        player.songForward();
        player.setState(player.pause);
    }

    @Override
    public void rewind() {
        System.out.println("Illegal action");
    }
}

class RewindState extends AbstractState {
    public RewindState(MP3Player player) {
        super(player);
    }

    @Override
    public void pressPlay() {
        System.out.println("Illegal action");
    }

    @Override
    public void pressStop() {
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
    public void forward() {
        System.out.println("Illegal action");
    }

    @Override
    public void rewind() {
        player.songRewind();
        player.setState(player.pause);
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

    @Override
    public String toString() {
        return "Song{" +
                "title=" + title +
                ", artist=" + artist +
                '}';
    }
}

class MP3Player {
    private final List<Song> songs;
    private int indexOfCurrentSong;

    State state;
    State play, stop, pause, forward, rewind;

    public MP3Player(List<Song> songs) {
        this.songs = songs;
        this.indexOfCurrentSong = 0;
        initialize();
    }

    final void initialize() {
        play = new PlayState(this);
        stop = new StopState(this);
        pause = new PauseState(this);
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

    public void pressStop() {
        state.pressStop();
    }

    public void pressFWD() {
        state.pressForward();
        state.forward();
    }

    public void pressREW() {
        state.pressRewind();
        state.rewind();
    }

    public void printCurrentSong() {
        System.out.println(songs.get(indexOfCurrentSong));
    }

    public int getIndexOfCurrentSong() {
        return indexOfCurrentSong;
    }

    public void setIndexOfCurrentSong(int indexOfCurrentSong) {
        this.indexOfCurrentSong = indexOfCurrentSong % songs.size();
    }

    public void songForward() {
        indexOfCurrentSong = (indexOfCurrentSong + 1) % songs.size();
    }

    public void songRewind() {
        indexOfCurrentSong = (indexOfCurrentSong - 1 + songs.size()) % songs.size();
    }

    @Override
    public String toString() {
        return "MP3Player{" +
                "currentSong = " + indexOfCurrentSong +
                ", songList = " + songs +
                '}';
    }
}


public class PatternTest {
    public static void main(String[] args) {
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

//Vasiot kod ovde