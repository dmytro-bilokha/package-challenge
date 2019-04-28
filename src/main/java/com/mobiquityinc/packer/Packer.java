package com.mobiquityinc.packer;

import com.mobiquityinc.exception.APIException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Packer {

    private static final String RESULTS_SEPARATOR = System.lineSeparator();
    private static final String LINE_PARSING_ERROR_SEPARATOR = System.lineSeparator();

    private Packer() {
        //No need to instantiate this class
    }

    public static String pack(String filePath) throws APIException {
        List<PackagingTask> tasks = parseInputFile(filePath);
        return resolvePackagingTasks(tasks);
    }

    private static List<PackagingTask> parseInputFile(String filePath) throws APIException {
        List<String> taskLines;
        try {
            taskLines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            throw new APIException("Failed to read a file '" + filePath + "'", e);
        }
        List<PackagingTask> parsedTasks = new ArrayList<>();
        List<String> parsingErrors = new ArrayList<>();
        for (int i = 0; i < taskLines.size(); i++) {
            try {
                parsedTasks.add(PackagingTaskParser.parse(taskLines.get(i)));
            } catch (InvalidTaskStringException e) {
                //Usually users prefer file lines numbering starting from 1
                parsingErrors.add("Failed to parse line " + (i + 1) + ": " + e.getMessage());
            }
        }
        if (!parsingErrors.isEmpty()) {
            throw new APIException(String.join(LINE_PARSING_ERROR_SEPARATOR, parsingErrors));
        }
        return parsedTasks;
    }

    private static String resolvePackagingTasks(List<PackagingTask> tasks) {
        return tasks.stream()
                .map(PackagingTaskSolver::findBestLayout)
                //After the last result also should be a separator
                .map(layout -> layout.getIndexesString() + RESULTS_SEPARATOR)
                .collect(Collectors.joining());
    }

}
