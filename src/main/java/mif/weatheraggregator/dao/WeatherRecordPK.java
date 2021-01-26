package mif.weatheraggregator.dao;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class WeatherRecordPK implements Serializable {

  private static final long serialVersionUID = 1L;

  private String location;

  public String getLocation() {
    return this.location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  private String provider;

  public String getProvider() {
    return this.provider;
  }

  public void setProvider(String provider) {
    this.provider = provider;
  }

  private String parameter;

  public String getParameter() {
    return this.parameter;
  }

  @Basic
  public void setParameter(String parameter) {
    this.parameter = parameter;
  }

  // For equal precision in java.time.LocaDateTime and H2 TIMESTAMP
  // Different precision causes comparison fails.
  @Column(columnDefinition = "TIMESTAMP(9)")
  private LocalDateTime date;

  public LocalDateTime getDate() {
    return this.date;
  }

  public void setDate(LocalDateTime date) {
    this.date = date;
  }

  public WeatherRecordPK() {
      
  }
  
  public WeatherRecordPK(String location, String provider, String parameter, LocalDateTime datetime) {
    super();
    this.location = location;
    this.provider = provider;
    this.parameter = parameter;
    this.date = datetime;
  }

  @Override
  public int hashCode() {
    return Objects.hash(date, location, parameter, provider);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof WeatherRecordPK)) {
      return false;
    }
    WeatherRecordPK other = (WeatherRecordPK) obj;
    return Objects.equals(date, other.date) && Objects.equals(location, other.location)
        && Objects.equals(parameter, other.parameter) && Objects.equals(provider, other.provider);
  }

  @Override
  public String toString() {
    StringBuffer builder = new StringBuffer();
    builder.append("WeatherRecordPK [location=").append(location).append(", provider=").append(provider)
        .append(", parameter=").append(parameter).append(", date=").append(date).append("]");
    return builder.toString();
  }

}
