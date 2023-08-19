package CurlRequest;

import org.testng.annotations.Test;
import io.restassured.specification.RequestSpecification;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import RequestSpec.RequestSpec;
import static groovy.json.JsonOutput.toJson;

public class CurlDataExtractorText {

    @Test
    public void generateTestClass() throws IOException {
        List<String> fileLines = Files.readAllLines(Paths.get("Files/file.txt"));
        StringJoiner joinedLines = new StringJoiner("\n");
        for (String line : fileLines) {
            joinedLines.add(line);
        }
        String fileContent = joinedLines.toString();

        String[] curlData = fileContent.split("---");
        for (String curl : curlData) {
            Map<String, String> keyValuePairs = extractKeyValuePairs(curl);

            String className = keyValuePairs.get("className");
            String curlCommand = keyValuePairs.get("curlCommand");

            String spec = keyValuePairs.get("spec");
            String expectedStatusCodeStr = keyValuePairs.get("expectedStatusCode");
            String outputLocation = keyValuePairs.get("outputLocation");

            String url = extractUrl(curlCommand);
            String method = extractMethod(curlCommand);
            String body = extractBody(curlCommand);

            int expectedStatusCode = expectedStatusCodeStr != null ? Integer.parseInt(expectedStatusCodeStr) : 200; // if the user did not write a status code we will expect 200

            StringBuilder testClassContent = new StringBuilder();
            testClassContent.append("import org.testng.annotations.Test;\n");
            testClassContent.append("import RequestSpec.RequestSpec.*;\n");
            testClassContent.append("import java.io.*;\n");
            testClassContent.append("import static io.restassured.RestAssured.*;\n");
            testClassContent.append("\n");
            testClassContent.append("public class ").append(capitalizeFirstLetter(className)).append("Test \n");
            testClassContent.append("{\n");
            testClassContent.append("\t@Test\n");
            testClassContent.append("\tpublic void ").append(className.toLowerCase()).append("Test() throws IOException \n");
            testClassContent.append("\t{\n");
            testClassContent.append("\t\tgiven().\n");
            if (spec.equals("-")|| spec.isEmpty()) {
                String[] lines = curlCommand.split("\\\\"); // Split by newline
                for (String line : lines) {
                    if (line.trim().startsWith("-H")) {
                        String header = extractHeaderValue(line);
                        if (header != null) {
                            String[] headerParts = header.split(": ");
                            if (headerParts.length == 2) {
                                String key = headerParts[0];
                                String value = headerParts[1];
                                testClassContent.append("\t\t\theader(\"").append(key).append("\", \"").append(value).append("\").\n");
                            }
                        }
                    }
                }
            } else {
                testClassContent.append("\t\t\tspec(").append(spec).append(").\n");
            }
            testClassContent.append("\t\twhen().\n");
            testClassContent.append("\t\t\t").append(method.toLowerCase()).append("(\"").append(url).append("\").\n");
            testClassContent.append("\t\tthen().\n");
            testClassContent.append("\t\t\tstatusCode(").append(expectedStatusCode).append(");\n");
            testClassContent.append("\t}\n");
            testClassContent.append("}\n");

            StringBuilder payloadClassContent = new StringBuilder();
            payloadClassContent.append("\n");
            payloadClassContent.append("public class ").append(capitalizeFirstLetter(className)).append("Payload\n");
            payloadClassContent.append("{\n");
            payloadClassContent.append("\tpublic static String ").append(className.toLowerCase()).append("Payload() \n");
            payloadClassContent.append("\t{\n");
            payloadClassContent.append("\t\treturn ").append(toJson(body)).append(";\n");
            payloadClassContent.append("\t}\n");
            payloadClassContent.append("}\n");

            FileWriter testClassWriter = new FileWriter(outputLocation + "/" + capitalizeFirstLetter(className) + "Test.java");
            testClassWriter.write(testClassContent.toString());
            testClassWriter.close();
            if (!method.equals("GET")) {
                FileWriter payloadClassWriter = new FileWriter(outputLocation + "/" + capitalizeFirstLetter(className) + "Payload.java");
                payloadClassWriter.write(payloadClassContent.toString());
                payloadClassWriter.close();
            }
            System.out.println(className + ".java file has been generated successfully.");
        }
    }

    private Map<String, String> extractKeyValuePairs(String curlData) {
        Map<String, String> keyValuePairs = new HashMap<>();
        String[] lines = curlData.trim().split("\n");
        for (String line : lines) {
            String[] parts = line.split(":", 2);
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                keyValuePairs.put(key, value);
            }
        }
        return keyValuePairs;
    }

    public static String capitalizeFirstLetter(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }
        return Character.toUpperCase(word.charAt(0)) + word.substring(1);
    }

    private static String extractUrl(String curlCommand) {
        String[] tokens = curlCommand.split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].startsWith("'") && tokens[i].endsWith("'")) {
                return tokens[i].substring(1, tokens[i].length() - 1);
            }
        }
        return "";
    }


    private static String extractMethod(String curlCommand) {
        // Remove backslashes and line breaks
        String cleanedCommand = curlCommand.replaceAll("\\\\", "").replaceAll("\n", "");

        // Extract method using regular expression
        Pattern pattern = Pattern.compile("(PUT|POST|DELETE|GET)");
        Matcher matcher = pattern.matcher(cleanedCommand);

        if (matcher.find()) {
            String method = matcher.group(1).toUpperCase();
            if (method.equals("PUT") || method.equals("POST") || method.equals("DELETE") || method.equals("GET")) {
                return method;
            }
        }

        if (curlCommand.contains("--data-raw")) {
            return "POST";
        }

        return "GET";
    }


    private static String extractBody(String curlCommand) {
        // Find the index of the beginning of the JSON payload
        int beginIndex = curlCommand.indexOf("--data-raw '{");
        if (beginIndex != -1) {
            beginIndex += "--data-raw '{".length();
            // Find the index of the end of the JSON payload
            int endIndex = curlCommand.indexOf("}'", beginIndex);
            if (endIndex != -1) {
                // Extract the payload string
                String payload = curlCommand.substring(beginIndex, endIndex).trim();

                // Construct the formatted payload using a StringJoiner
                StringJoiner formattedPayload = new StringJoiner(",\n", "{\n", "\n}");

                // Split the payload into key-value pairs
                String[] keyValuePairs = payload.split(",");
                for (String pair : keyValuePairs) {
                    // Split each key-value pair into separate key and value
                    String[] keyValue = pair.split(":");
                    if (keyValue.length == 2) {
                        // Extract the key and value, and format the pair string
                        String key = keyValue[0].trim().replace("\"", "");
                        String value = keyValue[1].trim().replace("\"", "");
                        String formattedPair = "        \"" + key + "\": " + "\"" + value + "\"";

                        // Add the formatted pair to the formatted payload
                        formattedPayload.add(formattedPair);
                    }
                }

                // Return the formatted payload as a string
                return formattedPayload.toString();
            }
        }
        // If the payload is not found, return an empty string
        return "";
    }
    private static String extractHeaderValue(String line) {
        if (line.contains("-H '")) {
            int startIndex = line.indexOf("-H '") + 4;
            int endIndex = line.indexOf("'", startIndex);
            if (endIndex != -1) {
                return line.substring(startIndex, endIndex);
            }
        } else if (line.contains("-H ")) {
            int startIndex = line.indexOf("-H ") + 3;
            String header = line.substring(startIndex).trim();
            if (header.startsWith("'") && header.endsWith("'")) {
                return header.substring(1, header.length() - 1);
            }
        }
        return null;
    }


}








