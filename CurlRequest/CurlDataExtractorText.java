package CurlRequest;

import org.json.JSONObject;
import org.testng.annotations.Test;
import java.util.Map;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.StringJoiner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static groovy.json.JsonOutput.toJson;


public class CurlDataExtractorText {

    @Test
    public void generateTestClass() throws IOException {
        // Read the text file
        String fileContent = new String(Files.readAllBytes(Paths.get("Files/file.txt")));
        String[] curlData = fileContent.split("---");


        for (String curl : curlData) {
            String[] lines = curl.trim().split("\n");

            String className = lines[0].trim();
            String curlCommand = lines[1].trim();
            String spec = lines[2].trim();
            int expectedStatusCode = Integer.parseInt(lines[3].trim());
            String outputLocation = lines[4].trim();


            // Extract relevant data from the curl command
            String url = extractUrl(curlCommand);
            String method = extractMethod(curlCommand);
            String body = extractBody(curlCommand);
            String headers = extractHeaders(curlCommand);


            // Generate the test class content
            StringBuilder testClassContent = new StringBuilder();
            testClassContent.append("import org.testng.annotations.Test;\n");
            testClassContent.append("import java.io.*;\n");
            testClassContent.append("import static io.restassured.RestAssured.*;\n");
            testClassContent.append("\n");
            testClassContent.append("public class ").append(capitalizeFirstLetter(className)).append("Test \n");
            testClassContent.append("{\n");
            testClassContent.append("\t@Test\n");
            testClassContent.append("\tpublic void ").append(className.toLowerCase()).append("Test() throws IOException \n");
            testClassContent.append("\t{\n");
            testClassContent.append("\t\tgiven().\n");
            testClassContent.append("\t\t\tspec(\"").append(spec).append("\").\n");
            testClassContent.append("\t\twhen().\n");
            testClassContent.append("\t\t\t").append(method.toLowerCase()).append("(\"").append(url).append("\").\n");
            testClassContent.append("\t\tthen().\n");
            testClassContent.append("\t\t\tstatusCode(").append(expectedStatusCode).append(");\n");
            testClassContent.append("\t}\n");
            testClassContent.append("}\n");
            // Generate the payload class content
            StringBuilder payloadClassContent = new StringBuilder();
            payloadClassContent.append("\n");
            payloadClassContent.append("public class ").append(capitalizeFirstLetter(className)).append("Payload\n");
            payloadClassContent.append("{\n");
            payloadClassContent.append("\tpublic static String ").append(className.toLowerCase()).append("Payload() \n");
            payloadClassContent.append("\t{\n");
            payloadClassContent.append("\t\treturn ").append(toJson(body)).append(";\n");
            payloadClassContent.append("\t}\n");
            payloadClassContent.append("}\n");

            // Write the test class to a new file
            FileWriter testClassWriter = new FileWriter(outputLocation + "/" + capitalizeFirstLetter(className) + "Test.java");
            testClassWriter.write(testClassContent.toString());
            testClassWriter.close();

            // Write the payload class to a new file
            FileWriter payloadClassWriter = new FileWriter(outputLocation + "/" + capitalizeFirstLetter(className) + "Payload.java");
            payloadClassWriter.write(payloadClassContent.toString());
            payloadClassWriter.close();

            System.out.println(className + ".java file has been generated successfully.");

        }
    }
            public static String capitalizeFirstLetter (String word){
                if (word == null || word.isEmpty()) {
                    return word;
                }
                return Character.toUpperCase(word.charAt(0)) + word.substring(1);
            }

            private static String extractUrl (String curlCommand){
                String[] tokens = curlCommand.split("\\s+");
                for (int i = 0; i < tokens.length; i++) {
                    if (tokens[i].startsWith("'") && tokens[i].endsWith("'")) {
                        return tokens[i].substring(1, tokens[i].length() - 1);
                    }
                }
                return "";
            }

            private static String extractMethod (String curlCommand) {
                // Remove backslashes and line breaks
                String cleanedCommand = curlCommand.replaceAll("\\\\", "").replaceAll("\n", "");

                // Extract method using regular expression
                Pattern pattern = Pattern.compile("curl .* -X ([A-Za-z]+) .*");
                Matcher matcher = pattern.matcher(cleanedCommand);

                if (matcher.find()) {
                    String method = matcher.group(1).toUpperCase();
                    if (method.equals("PUT") || method.equals("POST") || method.equals("GET")) {
                        return method;
                    }
                }

                if (curlCommand.contains("--data-raw")) {
                    return "POST";
                }

                return "GET";
            }



            private static String extractBody (String curlCommand){
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



            private static String extractHeaders (String curlCommand){
                StringBuilder headers = new StringBuilder();
                String[] tokens = curlCommand.split("\\s+");
                for (int i = 0; i < tokens.length; i++) {
                    if (tokens[i].startsWith("-H")) {
                        headers.append(tokens[i + 1].replace("'", ""));
                        headers.append("\\n"); // Adding a newline character after each header
                    }
                }
                return headers.toString();
            }
        }


