package com.company;

import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static String FILE_NAME = "file.thr";

    public static void main(String[] args) throws UnknownHostException {
        removeFile();
        System.out.println("Waiting for threads to execute...");
        List<Thread> listOfThreads = new ArrayList<>();

        listOfThreads.add(new Thread(() -> getRunnableThread(FILE_NAME)));
        listOfThreads.add(new Thread(() -> getRunnableThread(FILE_NAME)));
        listOfThreads.add(new Thread(() -> getRunnableThread(FILE_NAME)));
        listOfThreads.add(new Thread(() -> getRunnableThread(FILE_NAME)));
        listOfThreads.add(new Thread(() -> getRunnableThread(FILE_NAME)));

        listOfThreads.forEach(Thread::start);

        listOfThreads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("All threads have been executed!");
        System.out.println("Processing repeated numbers...");
        showRepeatedNumbers(FILE_NAME);
        System.out.println("Repeated numbers processing completed!");
    }

    private static void removeFile() {
        File fFile = new File(FILE_NAME);
        if (fFile.exists())
            fFile.delete();
    }

    private static void getRunnableThread(String fileName) {
        String threadName = Thread.currentThread().getName();
        String threadMessage = "I'm " + threadName;
        System.out.println(threadMessage + " - BEGIN");
        try {
            File fFile = new File(fileName);
            FileOutputStream file = new FileOutputStream(fFile, true);
            Stream
                .generate(() -> new Random().nextInt(200))
                .limit(200)
                .forEach(n -> {
                    try {
                        file.write(("Written by thread " + threadName + " => " +
                            n.toString().concat("\n")).getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(threadMessage + " - END");
    }

    private static void showRepeatedNumbers(String fileName) {
        try {
            Map<Integer, List<Integer>> mapOfNumbers = getStreamOfNumbers(fileName)
                .collect(Collectors.groupingBy(e -> e));

            mapOfNumbers
                .forEach((number, elements) -> {
                    long totalElements = elements.stream().count();
                    if (totalElements > 1)
                        System.out.println(number + ": " + totalElements);
                });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedReader getFileStream(String fileName) throws FileNotFoundException {
        return new BufferedReader(
            new InputStreamReader(
                new BufferedInputStream(
                    new FileInputStream(fileName)),
                StandardCharsets.UTF_8));
    }

    private static Stream<Integer> getStreamOfNumbers(String fileName) throws FileNotFoundException {
        return getFileStream(fileName)
            .lines()
            .map(line -> Integer.valueOf(line.split("=>")[1].trim()));
    }
}
