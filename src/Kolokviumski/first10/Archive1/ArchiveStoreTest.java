package Kolokviumski.first10.Archive1;

import java.util.*;
import java.util.stream.Collectors;

class NonExistingItemException extends Exception {
    public NonExistingItemException(String message) {
        super(message);
    }
}

abstract class Archive {
    private final int id;
    private Date dateArchived;

    public Archive(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setDateArchived(Date dateArchived) {
        this.dateArchived = dateArchived;
    }

    public abstract void open(Date date, List<String> logs);
}

class LockedArchive extends Archive {
    private final Date dateToOpen;

    public LockedArchive(int id, Date dateToOpen) {
        super(id);
        this.dateToOpen = dateToOpen;
    }

    @Override
    public void open(Date date, List<String> logs) {
        if (date.before(dateToOpen)) {
            logs.add(String.format("Item %d cannot be opened before %s", getId(), dateToOpen));
        } else {
            logs.add(String.format("Item %d opened at %s", getId(), date));
        }
    }
}

class SpecialArchive extends Archive {
    private final int maxOpen;
    private int opened;

    public SpecialArchive(int id, int maxOpen) {
        super(id);
        this.maxOpen = maxOpen;
        this.opened = 0;
    }

    @Override
    public void open(Date date, List<String> logs) {
        if (opened < maxOpen) {
            opened++;
            logs.add(String.format("Item %d opened at %s", getId(), date));
        } else {
            logs.add(String.format("Item %d cannot be opened more than %d times", getId(), maxOpen));
        }
    }
}

class ArchiveStore {
    private final List<Archive> archiveList;
    private final List<String> logs;

    public ArchiveStore() {
        this.archiveList = new ArrayList<>();
        this.logs = new ArrayList<>();
    }

    public void archiveItem(Archive item, Date date) {
        item.setDateArchived(date);
        archiveList.add(item);
        logs.add(String.format("Item %d archived at %s", item.getId(), date));
    }

    public void openItem(int id, Date date) throws NonExistingItemException {
        Archive archive = archiveList.stream()
                .filter(a -> a.getId() == id)
                .findFirst()
                .orElseThrow(() ->
                        new NonExistingItemException(
                                String.format("Item with id %d doesn't exist", id)
                        ));
        archive.open(date, logs);
    }

    public String getLog() {
        return logs.stream().collect(Collectors.joining("\n"));
    }
}

public class ArchiveStoreTest {
    public static void main(String[] args) {
        ArchiveStore store = new ArchiveStore();
        Date date = new Date(113, 10, 7); // Thu Nov 07 00:00:00 UTC 2013
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        int n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        int i;
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            long days = scanner.nextLong();
            Date dateToOpen = new Date(date.getTime() + (days * 24L * 60 * 60 * 1000));
            LockedArchive lockedArchive = new LockedArchive(id, dateToOpen);
            store.archiveItem(lockedArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        n = scanner.nextInt();
        scanner.nextLine();
        scanner.nextLine();
        for (i = 0; i < n; ++i) {
            int id = scanner.nextInt();
            int maxOpen = scanner.nextInt();
            SpecialArchive specialArchive = new SpecialArchive(id, maxOpen);
            store.archiveItem(specialArchive, date);
        }
        scanner.nextLine();
        scanner.nextLine();
        while(scanner.hasNext()) {
            int open = scanner.nextInt();
            try {
                store.openItem(open, date);
            } catch(NonExistingItemException e) {
                System.out.println(e.getMessage());
            }
        }
        System.out.println(store.getLog());
    }
}
