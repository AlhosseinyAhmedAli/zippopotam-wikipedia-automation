package test.api;

import test.utilities.TestData;
import test.api.model.ZippopotamResponse;
import test.api.model.Place;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.lessThan;

public class ZippopotamApiTest {
    private final ZippopotamApiClient client = new ZippopotamApiClient();


    private static void assertCountryEquals(String actualCountry, String expectedCountry) {;
        Assert.assertEquals(actualCountry, expectedCountry,
                "country name mismatch: expected " + expectedCountry + " but found " + actualCountry);
    }

    @DataProvider(name = "rows")
    public static Object[][] rowsProvider() {
        List<Map<String, String>> rows = TestData.api();
        Object[][] out = new Object[rows.size()][1];
        for (int i = 0; i < rows.size(); i++) {
            out[i][0] = rows.get(i);
        }
        return out;
    }

    @Test(dataProvider = "rows")
    @Description("Validate Zippopotam.us behavior using external CSV test data.")
    public void postalCodeApiContract(Map<String, String> row) {
        String testCase = row.get("testCase");
        String country = row.get("country");
        String postalCode = row.get("postalCode");
        int expectedStatus = Integer.parseInt(row.get("expectedStatus"));
        String expectedCountry = row.get("expectedCountry");
        String expectedAbbreviation = row.get("expectedAbbreviation");

        Response response = client.getByPostalCode(country, postalCode);

        Assert.assertEquals(response.statusCode(), expectedStatus,
                "Unexpected status for " + country + "/" + postalCode + " (" + testCase + "). Response: " + response.asString());

        if (expectedStatus != 200) {
            return;
        }

        ZippopotamResponse root = response.as(ZippopotamResponse.class);
        Assert.assertEquals(root.getPostCode(), postalCode, "post code (" + testCase + ")");
        assertCountryEquals(root.getCountry(), expectedCountry);
        Assert.assertEquals(root.getCountryAbbreviation(), expectedAbbreviation, "country abbreviation (" + testCase + ")");

        List<Place> places = root.getPlaces();
        Assert.assertNotNull(places, "places must exist (" + testCase + ")");
        Assert.assertFalse(places.isEmpty(), "places must not be empty (" + testCase + ")");

        for (Place place : places) {
            Assert.assertNotNull(place.getPlaceName(), "place name is missing (" + testCase + ")");
            Assert.assertNotNull(place.getState(), "state is missing (" + testCase + ")");
            Assert.assertTrue(place.getLatitude().toString().matches("-?\\d+(\\.\\d+)?"),
                    "Invalid latitude (" + testCase + ")");
            Assert.assertTrue(place.getLongitude().toString().matches("-?\\d+(\\.\\d+)?"),
                    "Invalid longitude (" + testCase + ")");
        }
    }

    @Test(dataProvider = "rows")
    @Description("Verify country code is accepted in lowercase using CSV data.")
    public void countryCodeIsCaseInsensitive(Map<String, String> row) {
        String testCase = row.get("testCase");
        String country = row.get("country");
        String postalCode = row.get("postalCode");
        int expectedStatus = Integer.parseInt(row.get("expectedStatus"));
        String expectedCountry = row.get("expectedCountry");
        String expectedAbbreviation = row.get("expectedAbbreviation");
        Response response = client.getByPostalCode(country.toLowerCase(), postalCode);
        Assert.assertEquals(response.statusCode(), expectedStatus,
                "Lowercase acceptance failed for " + country + "/" + postalCode + " (" + testCase + ")");

        if (expectedStatus != 200) {
            return;
        }

        ZippopotamResponse root = response.as(ZippopotamResponse.class);
        assertCountryEquals(root.getCountry(), expectedCountry);
        Assert.assertEquals(root.getCountryAbbreviation(), expectedAbbreviation);
    }

    @Test(dataProvider = "rows")
    @Description("Verify the API responds within the agreed automation threshold.")
    public void responseTimeIsWithinThreshold(Map<String, String> row) {
        String country = row.get("country");
        String postalCode = row.get("postalCode");
        int expectedStatus = Integer.parseInt(row.get("expectedStatus"));

        client.getByPostalCode(country, postalCode)
                .then()
                .statusCode(expectedStatus)
                .time(lessThan(5000L));
    }
}
