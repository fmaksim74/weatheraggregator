package mif.weatheraggregator;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import mif.weatheraggregator.dao.*;

@DataJpaTest
@ContextConfiguration(classes = {WeatherRecordService.class})
@EnableAutoConfiguration
public class WeatherRecordServiceTests {
    
    @Autowired
    WeatherRecordService service;
    
    @Test
    void save_method_should_save_entity() {
	WeatherRecord r = new WeatherRecord("Somewhere", "Someapi", "Someparam", LocalDateTime.now(), new BigDecimal(0));
	service.save(r);
    }
    
}
