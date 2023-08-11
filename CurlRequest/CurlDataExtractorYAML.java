package CurlRequest;

import org.testng.annotations.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

import static groovy.json.JsonOutput.toJson;
import static io.restassured.RestAssured.given;

public class CurlDataExtractorYAML {

    @Test
    public void generateTestClass() throws IOException {
        // Read the YAML file
        Yaml yaml = new Yaml();
        Map<String, Object> yamlData = yaml.load(new FileInputStream("Files/file.yaml"));

        // Extract the necessary data from the YAML
        String className = (String) yamlData.get("className");
        String curlCommand = (String) yamlData.get("curlCommand");
        String spec = (String) yamlData.get("spec");
        int expectedStatusCode = (int) yamlData.get("expectedStatusCode");
        String outputLocation = (String) yamlData.get("outputLocation");

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
        payloadClassContent.append("\tpublic static String get").append(className).append("Payload() \n");
        payloadClassContent.append("\t{\n");
        payloadClassContent.append("\t\treturn ").append(toJson(body)).append(";\n");
        payloadClassContent.append("\t}\n");
        payloadClassContent.append("}\n");

        // Write the test class to a new file
        FileWriter testClassWriter = new FileWriter(outputLocation + "/" + capitalizeFirstLetter(className) + "Test.java");
        testClassWriter.write(testClassContent.toString());
        testClassWriter.close();

        // Write the payload class to a new file
        FileWriter payloadClassWriter = new FileWriter(outputLocation + "/" + capitalizeFirstLetter(className)+ "Payload.java");
        payloadClassWriter.write(payloadClassContent.toString());
        payloadClassWriter.close();

        System.out.println(className + ".java file has been generated successfully.");
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
        if (curlCommand.contains("--data-raw")) {
            return "POST";
        }

        String[] tokens = curlCommand.split("\\s+");
        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i].equalsIgnoreCase("-X") && i + 1 < tokens.length) {
                String method = tokens[i + 1].toUpperCase();
                if (method.equals("PUT") || method.equals("POST") || method.equals("GET")) {
                    return method;
                }
            }
        }

        // Default to GET method if not specified
        return "GET";
    }

    private static String extractBody(String curlCommand) {
        int beginIndex = curlCommand.indexOf("--data-raw");
        if (beginIndex != -1) {
            beginIndex += "--data-raw '".length();
            int endIndex = curlCommand.indexOf("}'", beginIndex);
            if (endIndex != -1) {
                return curlCommand.substring(beginIndex, endIndex).trim();
            }
        }
        return "";
    }

    private static String extractHeaders(String curlCommand) {
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