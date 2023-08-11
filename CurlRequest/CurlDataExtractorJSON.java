package CurlRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CurlDataExtractorJSON {
    public static void main(String[] args) {
        try {
            // Read the JSON file
            BufferedReader reader = new BufferedReader(new FileReader("Files/file.json"));
            StringBuilder jsonContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonContent.append(line);
            }
            reader.close();

            // Extract the curl command as a string
            String curlCommand = jsonContent.toString().trim();

            // Extract relevant data from the curl command
            String url = extractUrl(curlCommand);
            String method = extractMethod(curlCommand);
            String body = extractBody(curlCommand);
            String headers = extractHeaders(curlCommand);

            // Generate the test class content
            StringBuilder testClassContent = new StringBuilder();
            testClassContent.append("import org.junit.Test;\n");
            testClassContent.append("import static io.restassured.RestAssured.*;\n");
            testClassContent.append("import static org.hamcrest.Matchers.*;\n");
            testClassContent.append("\n");
            testClassContent.append("public class ExampleTest {\n");
            testClassContent.append("\n");
            testClassContent.append("\t@Test\n");
            testClassContent.append("\tpublic void exampleTest() throws IOException {\n");
            testClassContent.append("\t\tgiven().\n");
            testClassContent.append("\t\t\tspec(\"" + headers + "\").\n");
            testClassContent.append("\t\twhen().\n");
            testClassContent.append("\t\t\t" + method.toLowerCase() + "(\"" + url + "\").\n");
            testClassContent.append("\t\tthen().\n");
            testClassContent.append("\t\t\tstatusCode(200);\n");
            testClassContent.append("\t}\n");
            testClassContent.append("}\n");

            // Generate the payload class content
            StringBuilder payloadClassContent = new StringBuilder();
            payloadClassContent.append("public class ExamplePayload {\n");
            payloadClassContent.append("\n");
            payloadClassContent.append("\tpublic static String examplePayload() {\n");
            payloadClassContent.append("\t\treturn \"" + body + "\";\n");
            payloadClassContent.append("\t}\n");
            payloadClassContent.append("}\n");

            // Write the test class to a new file
            FileWriter testClassWriter = new FileWriter("ExampleTest.java");
            testClassWriter.write(testClassContent.toString());
            testClassWriter.close();

            // Write the payload class to a new file
            FileWriter payloadClassWriter = new FileWriter("ExamplePayload.java");
            payloadClassWriter.write(payloadClassContent.toString());
            payloadClassWriter.close();

            System.out.println("ExampleTest.java and ExamplePayload.java files have been generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            beginIndex += "--data-raw".length();
            int endIndex = curlCommand.indexOf("' \\", beginIndex);
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