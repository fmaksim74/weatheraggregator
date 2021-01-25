package mif.weatheraggregator.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

@Entity
public class WeatherRecord {

  @EmbeddedId
  private WeatherRecordPK Id;

  public WeatherRecordPK getId() {
    return Id;
  }

  public void setId(WeatherRecordPK id) {
    Id = id;
  }

  private BigDecimal value;

  public BigDecimal getValue() {
    return value;
  }

  public void setValue(BigDecimal value) {
    this.value = value;
  }

  public WeatherRecord(WeatherRecordPK id, BigDecimal value) {
    super();
    this.Id = id;
    this.value = value;
  }
  
  public WeatherRecord(String location, String provider, String parameter, LocalDateTime datetime, BigDecimal value) {
    super();
    this.Id = new WeatherRecordPK(location, provider, parameter, datetime);
    this.value = value;
  }

  @Override
  public int hashCode() {
    return Objects.hash(Id, value);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof WeatherRecord)) {
      return false;
    }
    WeatherRecord other = (WeatherRecord) obj;
    return Objects.equals(Id, other.Id) && Objects.equals(value, other.value);
  }

  @Override
  public String toString() {
    StringBuffer builder = new StringBuffer();
    builder.append("WeatherRecord [Id=").append(Id).append(", value=").append(value).append("]");
    return builder.toString();
  }

}
