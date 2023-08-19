package RequestSpec;


import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;


public class RequestSpec {

    public static RequestSpecification ltsLogin() {
        return new RequestSpecBuilder().
                setBaseUri(ltsBaseUrl).
                setContentType(ContentType.JSON).
                build();
    }
    public static RequestSpecification cfLogin() {
        return new RequestSpecBuilder().
                setBaseUri(ltsBaseUrl).
                setContentType(ContentType.JSON).
                build();
    }

    static String ltsBaseUrl ="https://mohassel.test.halan.io/";
}