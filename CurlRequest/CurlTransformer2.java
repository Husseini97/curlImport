package CurlRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurlTransformer2 {
    public static void generateClassFile(String className, String packageName, String curlCommand, String url, Map<String, String> headers, String body) {
        String classTemplate = "package %s;\n\n" +
                "import java.util.Map;\n\n" +
                "public class %s {\n\n" +
                "    public String curlCommand;\n" +
                "    public String url;\n" +
                "    public String method;\n" +
                "    public Map<String, String> headers;\n" +
                "    public String body;\n\n" +
                "    public %s(String curlCommand, String url, String method, Map<String, String> headers, String body) {\n" +
                "        this.curlCommand = curlCommand;\n" +
                "        this.url = url;\n" +
                "        this.method = method;\n" +
                "        this.headers = headers;\n" +
                "        this.body = body;\n" +
                "    }\n" +
                "}\n";

        String classContent = String.format(classTemplate, packageName, className, className);

        String packagePath = packageName.replace(".", "/");
        String filePath = packagePath + "/" + className + ".java";

        try {
            File directory = new File(packagePath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            FileWriter fileWriter = new FileWriter(filePath);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(classContent);
            printWriter.close();

            System.out.println("Class file generated: " + filePath);
        } catch (IOException e) {
            System.out.println("Error generating class file: " + e.getMessage());
        }
    }

    public static void transformCurl(String curlCommand, String className, String packageName) {
        String urlPattern = "curl '([^']*)'";
        String methodPattern = "-X ([A-Z]+)";
        String headerPattern = "-H '([^']*)'";
        String bodyPattern = "--data '([^']*)'";

        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(curlCommand);
        String url = matcher.find() ? matcher.group(1) : "";

        pattern = Pattern.compile(methodPattern);
        matcher = pattern.matcher(curlCommand);
        String method = matcher.find() ? matcher.group(1) : "";

        pattern = Pattern.compile(headerPattern);
        matcher = pattern.matcher(curlCommand);
        Map<String, String> headers = new HashMap<>();
        while (matcher.find()) {
            String header = matcher.group(1);
            String[] parts = header.split(":");
            if (parts.length == 2) {
                headers.put(parts[0].trim(), parts[1].trim());
            }
        }

        pattern = Pattern.compile(bodyPattern);
        matcher = pattern.matcher(curlCommand);
        String body = matcher.find() ? matcher.group(1) : "";

        generateClassFile(className, packageName, curlCommand, url, headers, body);
    }

    public static void main(String[] args) {
        String curlCommand = "curl";
        String className = "GeneratedRequest";
        String packageName = "com.example.generated";
        transformCurl(curlCommand, className, packageName);

    }
}