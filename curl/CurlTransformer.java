package curl;

import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static io.restassured.RestAssured.given;

public class CurlTransformer {
    public static void transformCurl(String curlCommand) {
        String urlPattern = "curl '([^']*)'";
        String methodPattern = "-X ([A-Z]+)";
        String headerPattern = "-H '([^']*)'";
        String bodyPattern = "--data '([^']*)'";

        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(curlCommand);
        String url = matcher.find() ? matcher.group(1) : null;

        pattern = Pattern.compile(methodPattern);
        matcher = pattern.matcher(curlCommand);
        String method = matcher.find() ? matcher.group(1) : null;

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
        String body = matcher.find() ? matcher.group(1) : null;

        Response response = given()
                .urlEncodingEnabled(false)
                .headers(headers)
                .body(body)
                .contentType(ContentType.JSON)
                .request(method, url);

        // Use the response as needed
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();

        System.out.println("Status code: " + statusCode);
        System.out.println("Response body: " + responseBody);
    }

    public static void main(String[] args) {
        String curlCommand = "curlexample";
        transformCurl(curlCommand);
    }
}