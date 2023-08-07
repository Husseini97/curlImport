package CurlRequest;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CurlDataExtractor {
    public static void main(String[] args) {
        try {
            // Read the JSON file
            BufferedReader reader = new BufferedReader(new FileReader("CurlRequest/file.json"));
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

            // Generate the class
            StringBuilder classContent = new StringBuilder();
            classContent.append("public class CurlData {\n");
            classContent.append("\n");
            classContent.append("\tprivate String url = \"" + url + "\";\n");
            classContent.append("\tprivate String method = \"" + method + "\";\n");
            classContent.append("\tprivate String body = \"" + body + "\";\n");
            classContent.append("\tprivate String headers = \"" + headers + "\";\n");
            classContent.append("\n");
            classContent.append("\tpublic CurlData() {\n");
            classContent.append("\t}\n");
            classContent.append("\n");
            classContent.append("\tpublic String getUrl() {\n");
            classContent.append("\t\treturn url;\n");
            classContent.append("\t}\n");
            classContent.append("\n");
            classContent.append("\tpublic String getMethod() {\n");
            classContent.append("\t\treturn method;\n");
            classContent.append("\t}\n");
            classContent.append("\n");
            classContent.append("\tpublic String getBody() {\n");
            classContent.append("\t\treturn body;\n");
            classContent.append("\t}\n");
            classContent.append("\n");
            classContent.append("\tpublic String getHeaders() {\n");
            classContent.append("\t\treturn headers;\n");
            classContent.append("\t}\n");
            classContent.append("\n");
            classContent.append("}\n");

            // Write the class to a new file
            FileWriter writer = new FileWriter("CurlData.java");
            writer.write(classContent.toString());
            writer.close();

            System.out.println("CurlData.java file has been generated successfully.");
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