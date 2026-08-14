package test.api;

import test.core.Config;
import io.restassured.response.Response;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static io.restassured.RestAssured.given;

public class ZippopotamApiClient {
    public Response getByPostalCode(String country, String postalCode) {
        String base = Config.get("api.base.url");
        if (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        String encodedCountry = URLEncoder.encode(country, StandardCharsets.UTF_8).replace("+", "%20");
        String encodedPostal = URLEncoder.encode(postalCode, StandardCharsets.UTF_8).replace("+", "%20");
        String url = base + "/" + encodedCountry + "/" + encodedPostal;

        return given()
                .header("Accept", "application/json")
                .when()
                .get(url);
    }
}
