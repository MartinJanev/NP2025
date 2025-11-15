package Kolokviumski.first10;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

interface IFile extends Comparable<IFile> {
    String getFileName();

    long getFileSize();

    String getFileInfo(int indent);

    void sortBySize();

    long findLargestFile();
}

class IndentPrinter {
    public static String printIndent(int indent) {
        return IntStream.range(0, indent)
                .mapToObj(i -> "    ")
                .collect(Collectors.joining());
    }
}

class FileNameExistsException extends Exception {
    public FileNameExistsException(String filename, String folder) {
        super(String.format("There is already a file named %s in the folder %s",
                filename, folder));
    }
}

class File implements IFile {
    protected String fileName;
    private long fileSize;

    public File(String fileName, long fileSize) {
        this.fileName = fileName;
        this.fileSize = fileSize;
    }

    public File(String fileName) {
        this.fileName = fileName;
        this.fileSize = 0L;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public long getFileSize() {
        return fileSize;
    }

    @Override
    public String getFileInfo(int indent) {
        return String.format("%sFile name: %10s File size: %10d\n",
                IndentPrinter.printIndent(indent),
                getFileName(),
                getFileSize());
    }

    @Override
    public void sortBySize() {
    }

    @Override
    public long findLargestFile() {
        return this.fileSize;
    }

    @Override
    public int compareTo(IFile o) {
        return Long.compare(this.getFileSize(), o.getFileSize());
    }
}

class Folder extends File implements IFile {
    List<IFile> files;

    public Folder(String fileName) {
        super(fileName);
        this.files = new ArrayList<>();
    }

    private boolean ch(String file) {
        return files.stream().map(IFile::getFileName).anyMatch(f -> f.equals(file));
    }

    public void addFile(IFile file) throws FileNameExistsException {
        if (ch(file.getFileName())) {
            throw new FileNameExistsException(file.getFileName(), this.fileName);
        } else {
            files.add(file);
        }
    }

    @Override
    public String getFileName() {
        return this.fileName;
    }

    @Override
    public long getFileSize() {
        return files.stream().mapToLong(IFile::getFileSize).sum();
    }

    @Override
    public String getFileInfo(int indent) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%sFolder name: %10s Folder size: %10d\n",
                IndentPrinter.printIndent(indent),
                fileName,
                this.getFileSize()));

        files.forEach(file -> sb.append(file.getFileInfo(indent + 1)));
        return sb.toString();
    }

    @Override
    public void sortBySize() {
        Comparator<IFile> comparator =
                Comparator.comparingLong(IFile::getFileSize);
        files.sort(comparator);
        files.forEach(IFile::sortBySize);
    }

    @Override
    public long findLargestFile() {
        return files.stream()
                .mapToLong(IFile::findLargestFile)
                .max()
                .orElse(0L);
    }

}

class FileSystem {
    Folder root;

    public FileSystem() {
        this.root = new Folder("root");
    }

    void addFile(IFile file) throws FileNameExistsException {
        root.addFile(file);
    }

    public void sortBySize() {
        root.sortBySize();
    }

    public long findLargestFile() {
        return root.findLargestFile();
    }

    @Override
    public String toString() {
        return this.root.getFileInfo(0);
    }
}


public class FileSystemTest {

    public static Folder readFolder(Scanner sc) {

        Folder folder = new Folder(sc.nextLine());
        int totalFiles = Integer.parseInt(sc.nextLine());

        for (int i = 0; i < totalFiles; i++) {
            String line = sc.nextLine();

            if (line.startsWith("0")) {
                String fileInfo = sc.nextLine();
                String[] parts = fileInfo.split("\\s+");
                try {
                    folder.addFile(new File(parts[0], Long.parseLong(parts[1])));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            } else {
                try {
                    folder.addFile(readFolder(sc));
                } catch (FileNameExistsException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        return folder;
    }

    public static void main(String[] args) {

        //file reading from input

        Scanner sc = new Scanner(System.in);

        System.out.println("===READING FILES FROM INPUT===");
        FileSystem fileSystem = new FileSystem();
        try {
            fileSystem.addFile(readFolder(sc));
        } catch (FileNameExistsException e) {
            System.out.println(e.getMessage());
        }

        System.out.println("===PRINTING FILE SYSTEM INFO===");
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING FILE SYSTEM INFO AFTER SORTING===");
        fileSystem.sortBySize();
        System.out.println(fileSystem.toString());

        System.out.println("===PRINTING THE SIZE OF THE LARGEST FILE IN THE FILE SYSTEM===");
        System.out.println(fileSystem.findLargestFile());


    }
}