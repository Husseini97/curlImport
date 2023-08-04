package curl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class CurlTransformer2 {

    public static void generateClassFile(String className, String packageName, CurlRequest request) {
        String classTemplate = "package %s;\n\n" +
                "public class %s {\n\n" +
                "    private String url;\n" +
                "    private String method;\n" +
                "    private String headers;\n" +
                "    private String body;\n\n" +
                "    public %s(String url, String method, String headers, String body) {\n" +
                "        this.url = url;\n" +
                "        this.method = method;\n" +
                "        this.headers = headers;\n" +
                "        this.body = body;\n" +
                "    }\n\n" +
                "    public String getUrl() {\n" +
                "        return url;\n" +
                "    }\n\n" +
                "    public String getMethod() {\n" +
                "        return method;\n" +
                "    }\n\n" +
                "    public String getHeaders() {\n" +
                "        return headers;\n" +
                "    }\n\n" +
                "    public String getBody() {\n" +
                "        return body;\n" +
                "    }\n" +
                "}\n";

        String classContent = String.format(classTemplate, packageName, className, className);

        try {
            FileWriter fileWriter = new FileWriter(className + ".java");
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(classContent);
            printWriter.close();
            System.out.println("Class file generated: " + className + ".java");
        } catch (IOException e) {
            System.out.println("Error generating class file: " + e.getMessage());
        }
    }

    public static class CurlRequest {
        private String url;
        private String method;
        private String headers;
        private String body;

        public CurlRequest(String url, String method, String headers, String body) {
            this.url = url;
            this.method = method;
            this.headers = headers;
            this.body = body;
        }

        public String getUrl() {
            return url;
        }

        public String getMethod() {
            return method;
        }

        public String getHeaders() {
            return headers;
        }

        public String getBody() {
            return body;
        }
    }

    public static CurlRequest transformCurl(String curlCommand) {
        String[] curlParts = curlCommand.split("\\s+");

        // Extract URL
        String url = curlParts[1].replace("'", "");

        // Extract method
        String method = "";
        for (String part : curlParts) {
            if (part.equals("-X")) {
                method = curlParts[curlParts.length - 1];
                break;
            }
        }

        // Extract headers
        String headers = "";
        for (int i = 0; i < curlParts.length; i++) {
            if (curlParts[i].equals("-H")) {
                headers += curlParts[i + 1] + " ";
            }
        }

        // Extract body
        String body = "";
        for (int i = 0; i < curlParts.length; i++) {
            if (curlParts[i].equals("--data")) {
                body = curlParts[i + 1].replace("'", "");
                break;
            }
        }

        return new CurlRequest(url, method, headers, body);
    }

    public static void main(String[] args) {
        String curlCommand = "curl example";
        CurlRequest request = transformCurl(curlCommand);

        String className = "GeneratedRequest";
        String packageName = "com.example.generated";
        generateClassFile(className, packageName, request);
    }
}