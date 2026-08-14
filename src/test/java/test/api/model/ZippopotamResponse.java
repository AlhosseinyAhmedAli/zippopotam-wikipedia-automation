package test.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class ZippopotamResponse {
    @JsonProperty("post code")
    private String postCode;
    @JsonProperty("country")
    private String country;
    @JsonProperty("country abbreviation")
    private String countryAbbreviation;
    @JsonProperty("places")
    private List<Place> places;

    public String getPostCode() { return postCode; }
    public void setPostCode(String postCode) { this.postCode = postCode; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    public String getCountryAbbreviation() { return countryAbbreviation; }
    public void setCountryAbbreviation(String countryAbbreviation) { this.countryAbbreviation = countryAbbreviation; }
    public List<Place> getPlaces() { return places; }
    public void setPlaces(List<Place> places) { this.places = places; }
}