package client.monitoring;

import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.security.*;
import java.util.*;

public class DirectoryChangeMonitor implements Runnable{

    // Путь к основной директории, которую необходимо отслеживать
    private final Path mainDirectory;
    // Путь к файлу, в котором хранится сохраненное состояние директории
    private final Path stateFile;

    // Конструктор инициализирует пути к основной директории и файлу состояния
    public DirectoryChangeMonitor(Path mainDirectory, Path stateFile) {
        this.mainDirectory = mainDirectory;
        this.stateFile = stateFile;
    }

    @Override
    public void run() {
        try {
            // Сравнение текущего состояния директории с сохраненным
            comparePreviousState();
        } catch (IOException | ClassNotFoundException | NoSuchAlgorithmException e) {
            System.err.println("An error occurred while comparing previous state: " + e.getMessage());
        }
    }

    // Метод сохраняет текущее состояние файлов в директории
    public void saveCurrentState() throws IOException {
        // Карта для хранения хэшей файлов
        Map<String, String> fileHashes = new HashMap<>();
        // Обход всех файлов в директории
        Files.walkFileTree(mainDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // Добавление хэша файла в карту
                fileHashes.put(file.toString(), computeHash(file));
                return FileVisitResult.CONTINUE;
            }
        });
        // Сохранение хэшей в файл состояния
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(stateFile.toFile()))) {
            oos.writeObject(fileHashes);
        }
    }

    @SuppressWarnings("unchecked")
    public void comparePreviousState() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        // Если файл состояния не существует, сохраняем текущее состояние
        if (!Files.exists(stateFile)) {
            System.out.println("No previous state found. Saving current state.");
            saveCurrentState();
            return;
        }

        // Чтение предыдущего состояния из файла
        Map<String, String> previousFileHashes;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(stateFile.toFile()))) {
            previousFileHashes = (Map<String, String>) ois.readObject();
        }

        // Карта для хранения текущих хэшей файлов
        Map<String, String> currentFileHashes = new HashMap<>();
        // Обход всех файлов в директории
        Files.walkFileTree(mainDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // Добавление хэша файла в карту
                currentFileHashes.put(file.toString(), computeHash(file));
                return FileVisitResult.CONTINUE;
            }
        });

        int newFiles = 0;
        int modifiedFiles = 0;
        int deletedFiles = 0;

        // Сравнение текущих хэшей с предыдущими
        for (Map.Entry<String, String> entry : currentFileHashes.entrySet()) {
            String filePath = entry.getKey();
            String currentHash = entry.getValue();
            String previousHash = previousFileHashes.get(filePath);
            if (previousHash == null) {
                // Новый файл
                System.out.println("New file: " + filePath);
                newFiles++;
            } else if (!currentHash.equals(previousHash)) {
                // Измененный файл
                System.out.println("Modified file: " + filePath);
                modifiedFiles++;
            }
        }

        // Поиск удаленных файлов
        for (String filePath : previousFileHashes.keySet()) {
            if (!currentFileHashes.containsKey(filePath)) {
                System.out.println("Deleted file: " + filePath);
                deletedFiles++;
            }
        }

        // Если изменений не найдено
        if (newFiles == 0 && modifiedFiles == 0 && deletedFiles == 0) {
            System.out.println("No changes detected.");
        }

        // Сохранение текущего состояния
        saveCurrentState();
    }

    // Метод вычисляет SHA-256 хэш для файла
    private String computeHash(Path file) throws IOException {
        try {
            // Создание объекта для вычисления хэшей
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            // Чтение всех байт файла
            byte[] fileBytes = Files.readAllBytes(file);
            // Вычисление хэша
            byte[] hashBytes = digest.digest(fileBytes);
            // Преобразование хэша в строку
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
