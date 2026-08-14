package test.api;

import test.utilities.TestData;
import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.Matchers.lessThan;

public class ZippopotamApiTest {
    private final ZippopotamApiClient client = new ZippopotamApiClient();


    private static void assertCountryEquals(String actualCountry, String expectedCountry) {;
        Assert.assertEquals(actualCountry, expectedCountry,
                "country name mismatch: expected " + expectedCountry + " but found " + actualCountry);
    }

    @DataProvider(name = "apiData")
    public Object[][] apiData() {
        List<Map<String, String>> rows = TestData.api();
        List<Object[]> data = new ArrayList<>();
        for (Map<String, String> row : rows) {
            data.add(new Object[]{
                    row.get("testCase"),
                    row.get("country"),
                    row.get("postalCode"),
                    Integer.parseInt(row.get("expectedStatus")),
                    row.get("expectedCountry"),
                    row.get("expectedAbbreviation")
            });
        }
        return data.toArray(new Object[0][]);
    }

    @Test(dataProvider = "apiData")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validate Zippopotam.us behavior using external CSV test data.")
    public void postalCodeApiContract(String testCase, String country, String postalCode,
                                      int expectedStatus, String expectedCountry,
                                      String expectedAbbreviation) {
        Response response = client.getByPostalCode(country, postalCode);

        Assert.assertEquals(response.statusCode(), expectedStatus,
                "Unexpected status for " + country + "/" + postalCode +
                        ". Response: " + response.asString());

        if (expectedStatus != 200) {
            return;
        }

        Map<String, Object> root = response.as(Map.class);
        Assert.assertEquals(root.get("post code"), postalCode, "post code");
        assertCountryEquals(String.valueOf(root.get("country")), expectedCountry);
        Assert.assertEquals(root.get("country abbreviation"), expectedAbbreviation,
                "country abbreviation");

        List<Map<String, Object>> places = (List<Map<String, Object>>) root.get("places");
        Assert.assertNotNull(places, "places must exist");
        Assert.assertFalse(places.isEmpty(), "places must not be empty");

        for (Map<String, Object> place : places) {
            Assert.assertNotNull(place.get("place name"), "place name is missing");
            Assert.assertNotNull(place.get("state"), "state is missing");
            Assert.assertTrue(place.get("latitude").toString().matches("-?\\d+(\\.\\d+)?"),
                    "Invalid latitude");
            Assert.assertTrue(place.get("longitude").toString().matches("-?\\d+(\\.\\d+)?"),
                    "Invalid longitude");
        }
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify country code is accepted in lowercase using CSV data.")
    public void countryCodeIsCaseInsensitive() {
        Map<String, String> row = TestData.api().stream()
                .filter(r -> "CASE_INSENSITIVE".equals(r.get("testCase")))
                .findFirst()
                .orElseThrow();

        Response response = client.getByPostalCode(
                row.get("country").toLowerCase(), row.get("postalCode"));

        Assert.assertEquals(response.statusCode(), Integer.parseInt(row.get("expectedStatus")));
        Map<String, Object> root = response.as(Map.class);
        assertCountryEquals(String.valueOf(root.get("country")), row.get("expectedCountry"));
        Assert.assertEquals(root.get("country abbreviation"), row.get("expectedAbbreviation"));
    }

    @Test
    @Severity(SeverityLevel.NORMAL)
    @Description("Verify the valid API responds within the agreed automation threshold.")
    public void responseTimeIsWithinThreshold() {
        Map<String, String> row = TestData.api().stream()
                .filter(r -> "VALID".equals(r.get("testCase")))
                .findFirst()
                .orElseThrow();

        client.getByPostalCode(row.get("country"), row.get("postalCode"))
                .then()
                .statusCode(Integer.parseInt(row.get("expectedStatus")))
                .time(lessThan(5000L));
    }
}
