package com.mobiquityinc.packer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class PackagingTaskParser {

    private static final int MAXIMUM_THINGS_ALLOWED = 15;
    private static final int MINIMUM_THINGS_ALLOWED = 1;
    private static final BigDecimal MAX_WEIGHT_LIMIT = BigDecimal.valueOf(100L);
    private static final BigDecimal MAX_THING_WEIGHT = BigDecimal.valueOf(100L);
    private static final BigDecimal MAX_THING_COST = BigDecimal.valueOf(100L);
    private static final String TASK_PARTS_SEPARATOR = ":";
    private static final int TASK_PARTS_NUMBER = 2;
    private static final Pattern WEIGHT_PART_PATTERN = Pattern.compile("^\\h*((?:\\d*\\.)?\\d+)\\h*$");
    private static final Pattern THING_PATTERN = Pattern
            .compile("\\((\\d)\\h*,\\h*((?:\\d*\\.)?\\d+)\\h*,\\D*((?:\\d*\\.)?\\d+)\\h*\\)");
    private static final int OPENING_BRACE_CODEPOINT = Character.codePointAt("(", 0);
    private static final int CLOSING_BRACE_CODEPOINT = Character.codePointAt(")", 0);
    private static final String PARSE_ERROR_SEPARATOR = "; ";

    private PackagingTaskParser() {
        //No need to instantiate this class
    }

    static PackagingTask parse(String taskLine) throws InvalidTaskStringException {
        String[] taskLineArray = taskLine.split(TASK_PARTS_SEPARATOR);
        if (taskLineArray.length != TASK_PARTS_NUMBER) {
            throw new InvalidTaskStringException("The packaging task string must contain weight limit,"
                + " separator ':' and things data");
        }
        List<String> parseErrorMessages = new ArrayList<>();
        BigDecimal weightLimit = parseWeightLimit(taskLineArray[0], parseErrorMessages);
        List<Thing> things = parseThings(taskLineArray[1], parseErrorMessages);
        if (!parseErrorMessages.isEmpty()) {
            throw new InvalidTaskStringException(String.join(PARSE_ERROR_SEPARATOR, parseErrorMessages));
        }
        return new PackagingTask(weightLimit, things);
    }

    private static BigDecimal parseWeightLimit(String weightPart, List<String> errorMessages) {
        Matcher matcher = WEIGHT_PART_PATTERN.matcher(weightPart);
        if (!matcher.matches()) {
            errorMessages.add("Failed to parse '" + weightPart + "' to the weight limit."
                    + " Value must be a positive decimal number with optional fraction part");
            return BigDecimal.ZERO;
        }
        BigDecimal weightLimit = new BigDecimal(matcher.group(1));
        if (weightLimit.compareTo(MAX_WEIGHT_LIMIT) > 0) {
            errorMessages.add("Maximum allowed package weight limit is "
                + MAX_WEIGHT_LIMIT + ", but got " + weightLimit);
        }
        return weightLimit;
    }

    private static List<Thing> parseThings(String thingsPart, List<String> errorMessages) {
        Optional<Integer> bracePairsCount = checkBracePairs(thingsPart, errorMessages);
        if (!bracePairsCount.isPresent()) {
            //No sense to continue parsing if braces are not in balance
            return Collections.emptyList();
        }
        int expectedThingsNumber = bracePairsCount.get();
        Matcher matcher = THING_PATTERN.matcher(thingsPart);
        List<Thing> result = new ArrayList<>();
        while (matcher.find()) {
            boolean valid = true;
            int index = Integer.parseInt(matcher.group(1));
            BigDecimal weight = new BigDecimal(matcher.group(2));
            if (weight.compareTo(MAX_THING_WEIGHT) > 0) {
                valid = false;
                errorMessages.add("Maximum allowed thing weight is " + MAX_THING_WEIGHT + ", but got " + weight);
            }
            BigDecimal cost = new BigDecimal(matcher.group(3));
            if (cost.compareTo(MAX_THING_COST) > 0) {
                valid = false;
                errorMessages.add("Maximum allowed thing cost is " + MAX_THING_COST + ", but got " + cost);
            }
            if (valid) {
                result.add(new Thing(index, weight, cost));
            }
        }
        if (result.size() != expectedThingsNumber) {
            errorMessages.add("Found " + result.size()
                    + " valid thing definitions: '" + result + "' in string '"
                    + thingsPart + "', but there are "
                    + expectedThingsNumber + " brace pairs. Each brace pair must contain a valid thing definition");
            return result;
        }
        if (result.size() < MINIMUM_THINGS_ALLOWED || result.size() > MAXIMUM_THINGS_ALLOWED) {
            errorMessages.add("Found " + result.size()
                    + " valid thing definitions: '" + result + "' in string '"
                    + thingsPart + "', but allowed quantity is "
                    + MINIMUM_THINGS_ALLOWED + "-" + MAXIMUM_THINGS_ALLOWED);
        }
        return result;
    }

    private static Optional<Integer> checkBracePairs(String thingsPart, List<String> errorMessages) {
        int stringLength = thingsPart.codePointCount(0, thingsPart.length());
        int openingBraceCount = 0;
        int closingBraceCount = 0;
        for (int i = 0; i < stringLength; i++) {
            //Compare codepoints instead of chars, because string could contain unicode symbols
            if (thingsPart.codePointAt(i) == OPENING_BRACE_CODEPOINT) {
                openingBraceCount++;
            }
            if (thingsPart.codePointAt(i) == CLOSING_BRACE_CODEPOINT) {
                closingBraceCount++;
            }
        }
        if (openingBraceCount != closingBraceCount) {
            errorMessages.add("Parser found " + openingBraceCount + " opening braces and " + closingBraceCount
                + " closing braces in the things string '" + thingsPart + "', but all braces must be paired");
            return Optional.empty();
        }
        return Optional.of(openingBraceCount);
    }

}
