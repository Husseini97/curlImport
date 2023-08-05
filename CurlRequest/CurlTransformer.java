package CurlRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
public class CurlTransformer {
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
    public static class CurlRequest {
        public String url;
        public String method;
        public String headers;
        public String body;
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

        String url = curlParts[1].replace("'", "");

        String method = "GET"; // Default method is GET
        String body = ""; // Default body is empty

        // Find the -X or --request flag to extract the method
        for (int i = 0; i < curlParts.length; i++) {
            if ((curlParts[i].equals("-X") || curlParts[i].equals("--request")) && i + 1 < curlParts.length) {
                String potentialMethod = curlParts[i + 1].toUpperCase();
                if (isValidMethod(potentialMethod)) {
                    method = potentialMethod;
                    break;
                }
            }
        }

        // Find the --data or -d flag to extract the body
        for (int i = 0; i < curlParts.length; i++) {
            if ((curlParts[i].equals("--data-raw") || curlParts[i].equals("-d")) && i + 1 < curlParts.length) {
                body = curlParts[i + 1].replace("'", "");
                break;
            }
        }

        String headers = "";
        for (int i = 0; i < curlParts.length; i++) {
            if (curlParts[i].equals("-H") && i + 1 < curlParts.length) {
                headers += curlParts[i + 1] + " ";
            }
        }

        return new CurlRequest(url, method, headers, body);
    }

    private static boolean isValidMethod(String method) {
        return method.equals("GET") || method.equals("POST") || method.equals("PUT") || method.equals("DELETE");


    }
    public static void main(String[] args) {
        String curlCommand = "curl ";
        CurlRequest request = transformCurl(curlCommand);

        GeneratedRequest1 generatedRequest = new GeneratedRequest1(
                request.getUrl(),
                request.getMethod(),
                request.getHeaders(),
                request.getBody()
        );

        // Now you can use the generatedRequest object as needed
        System.out.println("URL: " + generatedRequest.getUrl());
        System.out.println("Method: " + generatedRequest.getMethod());
        System.out.println("Headers: " + generatedRequest.getHeaders());
        System.out.println("Body: " + generatedRequest.getBody());

        // Generate a new Java class
        generateJavaClass(generatedRequest);
    }

    private static void generateJavaClass(GeneratedRequest1 generatedRequest) {
        String className = "GeneratedRequest" + System.currentTimeMillis();

        try (PrintWriter writer = new PrintWriter(new FileWriter(className + ".java"))) {
            // Write the package statement
            writer.println("package com.example.generated;");
            writer.println();

            // Write the class declaration
            writer.println("public class " + className + " {");
            writer.println();

            // Write the fields
            writer.println("    private String url;");
            writer.println("    private String method;");
            writer.println("    private String headers;");
            writer.println("    private String body;");
            writer.println();

            // Write the constructor
            writer.println("    public " + className + "(String url, String method, String headers, String body) {");
            writer.println("        this.url = url;");
            writer.println("        this.method = method;");
            writer.println("        this.headers = headers;");
            writer.println("        this.body = body;");
            writer.println("    }");
            writer.println();

            // Write the getter methods
            writer.println("    public String getUrl() {");
            writer.println("        return url;");
            writer.println("    }");
            writer.println();
            writer.println("    public String getMethod() {");
            writer.println("        return method;");
            writer.println("    }");
            writer.println();
            writer.println("    public String getHeaders() {");
            writer.println("        return headers;");
            writer.println("    }");
            writer.println();
            writer.println("    public String getBody() {");
            writer.println("        return body;");
            writer.println("    }");
            writer.println("}");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    }

