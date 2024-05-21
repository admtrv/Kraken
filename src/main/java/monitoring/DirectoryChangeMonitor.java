package monitoring;

import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class DirectoryChangeMonitor implements Runnable{

    private final Path mainDirectory;
    private final Path stateFile;

    public DirectoryChangeMonitor(Path mainDirectory, Path stateFile) {
        this.mainDirectory = mainDirectory;
        this.stateFile = stateFile;
    }

    @Override
    public void run() {
        try {
            comparePreviousState();
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void saveCurrentState() throws IOException {
        Map<String, String> fileHashes = new HashMap<>();
        Files.walkFileTree(mainDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                fileHashes.put(file.toString(), computeHash(file));
                return FileVisitResult.CONTINUE;
            }
        });
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stateFile.toFile()))) {
            oos.writeObject(fileHashes);
        }
    }

    @SuppressWarnings("unchecked")
    public void comparePreviousState() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        if (!Files.exists(stateFile)) {
            System.out.println("No previous state found. Saving current state.");
            saveCurrentState();
            return;
        }

        Map<String, String> previousFileHashes;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(stateFile.toFile()))) {
            previousFileHashes = (Map<String, String>) ois.readObject();
        }

        Map<String, String> currentFileHashes = new HashMap<>();
        Files.walkFileTree(mainDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                currentFileHashes.put(file.toString(), computeHash(file));
                return FileVisitResult.CONTINUE;
            }
        });

        int newFiles = 0;
        int modifiedFiles = 0;
        int deletedFiles = 0;

        // Сравнение текущих хэш-сумм с предыдущими
        for (Map.Entry<String, String> entry : currentFileHashes.entrySet()) {
            String filePath = entry.getKey();
            String currentHash = entry.getValue();
            String previousHash = previousFileHashes.get(filePath);
            if (previousHash == null) {
                System.out.println("New file: " + filePath);
                newFiles++;
            } else if (!currentHash.equals(previousHash)) {
                System.out.println("Modified file: " + filePath);
                modifiedFiles++;
            }
        }

        for (String filePath : previousFileHashes.keySet()) {
            if (!currentFileHashes.containsKey(filePath)) {
                System.out.println("Deleted file: " + filePath);
                deletedFiles++;
            }
        }

        if (newFiles == 0 && modifiedFiles == 0 && deletedFiles == 0) {
            System.out.println("No changes detected.");
        }

        // Сохранение текущего состояния
        saveCurrentState();
    }

    private String computeHash(Path file) throws IOException {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] fileBytes = Files.readAllBytes(file);
            byte[] hashBytes = digest.digest(fileBytes);
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found", e);
        }
    }
}
