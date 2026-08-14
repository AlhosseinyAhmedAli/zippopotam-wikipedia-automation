package test.api;

import test.utilities.TestData;
import test.api.model.ZippopotamResponse;
import test.api.model.Place;
import io.qameta.allure.Description;
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

        ZippopotamResponse root = response.as(ZippopotamResponse.class);
                Assert.assertEquals(root.getPostCode(), postalCode, "post code");
                assertCountryEquals(root.getCountry(), expectedCountry);
                Assert.assertEquals(root.getCountryAbbreviation(), expectedAbbreviation,
                "country abbreviation");

                List<Place> places = root.getPlaces();
        Assert.assertNotNull(places, "places must exist");
        Assert.assertFalse(places.isEmpty(), "places must not be empty");

                for (Place place : places) {
                    Assert.assertNotNull(place.getPlaceName(), "place name is missing");
                    Assert.assertNotNull(place.getState(), "state is missing");
                    Assert.assertTrue(place.getLatitude().toString().matches("-?\\d+(\\.\\d+)?"),
                    "Invalid latitude");
                    Assert.assertTrue(place.getLongitude().toString().matches("-?\\d+(\\.\\d+)?"),
                    "Invalid longitude");
        }
    }

    @Test
    @Description("Verify country code is accepted in lowercase using CSV data.")
    public void countryCodeIsCaseInsensitive() {
        Map<String, String> row = TestData.api().stream()
                .filter(r -> "CASE_INSENSITIVE".equals(r.get("testCase")))
                .findFirst()
                .orElseThrow();

        Response response = client.getByPostalCode(
                row.get("country").toLowerCase(), row.get("postalCode"));

        Assert.assertEquals(response.statusCode(), Integer.parseInt(row.get("expectedStatus")));
        ZippopotamResponse root = response.as(ZippopotamResponse.class);
                assertCountryEquals(root.getCountry(), row.get("expectedCountry"));
                Assert.assertEquals(root.getCountryAbbreviation(), row.get("expectedAbbreviation"));
    }

    @Test
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
