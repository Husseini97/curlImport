package CurlRequest;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
public class CurlTransformer {
    public static void generateClassFile(String className, String packageName, CurlRequest curlCommand) {
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
        String curlCommand = "curl 'https://api-vendor-test.halan.io/api/v1/vendor-bff/calculate-installment' \\\n" +
                "  -H 'authority: api-vendor-test.halan.io' \\\n" +
                "  -H 'accept: application/json, text/plain, */*' \\\n" +
                "  -H 'accept-language: en-US,en;q=0.9' \\\n" +
                "  -H 'authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6IjYxYWRlN2IxNTc3NWQ2YmNkYjBlZDRjNCIsInVzZXJuYW1lIjoiYWtva28iLCJicmFuY2hfaWQiOiI2MWFkZTY1ZjU3NzVkNmJjZGIwZWQ0YzMiLCJtZXJjaGFudF9pZCI6IjYxYWRlMzk1NTc3NWQ2YmNkYjBlZDRiYiIsInBlcm1pc3Npb24iOjE5MiwiZXhwIjoxNjkzOTk5MTMwfQ.GZDbyILO082hcUVoO5RRDpFPElRi3J9lTzJzZqVIRCo' \\\n" +
                "  -H 'content-type: application/json;charset=UTF-8' \\\n" +
                "  -H 'language: en' \\\n" +
                "  -H 'origin: https://consumer-vendor-test.halan.io' \\\n" +
                "  -H 'referer: https://consumer-vendor-test.halan.io/' \\\n" +
                "  -H 'sec-ch-ua: \"Google Chrome\";v=\"113\", \"Chromium\";v=\"113\", \"Not-A.Brand\";v=\"24\"' \\\n" +
                "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                "  -H 'sec-ch-ua-platform: \"Linux\"' \\\n" +
                "  -H 'sec-fetch-dest: empty' \\\n" +
                "  -H 'sec-fetch-mode: cors' \\\n" +
                "  -H 'sec-fetch-site: same-site' \\\n" +
                "  -H 'user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36' \\\n" +
                "  --data-raw '{\"catId\":\"61421bc9d1c9258d373b67a9\",\"subCat\":\"61421bcfd1c9258d373b67d2\",\"price\":3000,\"downPayment\":0,\"periodId\":\"64c90967fe54e84fe0f7dbd4\",\"userId\":\"64cf736168c2113ceac9a6b6\"}' \\\n" +
                "  --compressed ";
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

