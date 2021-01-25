package mif.weatheraggregator.providers;

public interface WeatherProvider {

  public String getName();

  public String getData(String location);

}
