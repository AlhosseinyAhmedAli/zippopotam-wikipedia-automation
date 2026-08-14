package test.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Place {
    @JsonProperty("place name")
    private String placeName;
    @JsonProperty("longitude")
    private String longitude;
    @JsonProperty("state")
    private String state;
    @JsonProperty("state abbreviation")
    private String stateAbbreviation;
    @JsonProperty("latitude")
    private String latitude;

    public String getPlaceName() { return placeName; }
    public void setPlaceName(String placeName) { this.placeName = placeName; }
    public String getLongitude() { return longitude; }
    public void setLongitude(String longitude) { this.longitude = longitude; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getStateAbbreviation() { return stateAbbreviation; }
    public void setStateAbbreviation(String stateAbbreviation) { this.stateAbbreviation = stateAbbreviation; }
    public String getLatitude() { return latitude; }
    public void setLatitude(String latitude) { this.latitude = latitude; }
}