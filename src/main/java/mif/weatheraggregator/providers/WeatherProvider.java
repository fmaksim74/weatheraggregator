package mif.weatheraggregator.providers;

public interface WeatherProvider {

    public String getName();

    public String getValue(String location, String parameter);

    public String getHomeUrl();

}
