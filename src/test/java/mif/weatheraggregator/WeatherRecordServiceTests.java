package mif.weatheraggregator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import mif.weatheraggregator.dao.WeatherRecord;
import mif.weatheraggregator.dao.WeatherRecordPK;
import mif.weatheraggregator.dao.WeatherRecordService;

@DataJpaTest
@ContextConfiguration(classes = {WeatherRecordService.class})
@EnableAutoConfiguration
public class WeatherRecordServiceTests {
    
    @Autowired
    WeatherRecordService service;
    
    @Test
    void save_method_should_save_entity() {
	
	service.save(new WeatherRecord("Location", "Provider", "Parameter", LocalDateTime.now(), new BigDecimal(0)));
	assert(service.getCount() == 1);
    }
    
    @Test
    void getById_should_return_an_entity_simple() {

	LocalDateTime ldt = LocalDateTime.now();
	service.save(new WeatherRecord("Location", "Provider", "Parameter", ldt, new BigDecimal(0)));
	assert(Objects.nonNull(service.getById("Location", "Provider", "Parameter", ldt)));
    }

    @Test
    void getById_should_return_an_entity_class() {
	
	LocalDateTime ldt = LocalDateTime.now();
	service.save(new WeatherRecord("Location", "Provider", "Parameter", ldt, new BigDecimal(0)));
	assert(Objects.nonNull(service.getById(new WeatherRecordPK("Location", "Provider", "Parameter", ldt))));
    }

    @Test
    void getAll_should_return_all_entities() {

	LocalDateTime ldt = LocalDateTime.now();
	
	int providerCount = 5;
	int parameterCount = 5;
	for (int i = 0; i < providerCount; i++) {
	    for (int j = 0; j < parameterCount; j++) {
		service.save(new WeatherRecord("Location", "Provider%d".formatted(i), "Parameter%d".formatted(j), ldt, new BigDecimal(i+j)));
	    }
	}

	List<WeatherRecord> lwr = service.getAll();
	assert(Objects.nonNull(lwr));
	assert(lwr.size() == providerCount * parameterCount);
    }
    
    @Test
    void getByLocationAndDate_should_return_not_all_entities() {
	
	LocalDateTime ldt = LocalDateTime.now();
	
	int providerCount = 5;
	int parameterCount = 5;
	int dayShifts = 5;
	for (int i = 0; i < providerCount; i++) {
	    for (int j = 0; j < parameterCount; j++) {
		for (int k = 0; k < dayShifts; k++) {
		    service.save(new WeatherRecord("Location", "Provider%d".formatted(i), "Parameter%d".formatted(j), ldt.minusDays(k), new BigDecimal(i+j+k)));
		}
	    }
	}

	List<WeatherRecord> lwr = service.getByLocationAndDate("Location", ldt);
	
	assert(Objects.nonNull(lwr));
	assert(lwr.size() == providerCount * parameterCount);
    }

    @Test
    void getAvgByLocationAndDate_should_correct_filter_records_and_return_right_value() {
	
	LocalDateTime ldt = LocalDateTime.now();
	
	int providerCount = 5;
	int parameterCount = 5;
	int dayShifts = 5;
	int valueSumm = 0;
	for (int i = 0; i < providerCount; i++) {
	    for (int j = 0; j < parameterCount; j++) {
		for (int k = 0; k < dayShifts; k++) {
		    service.save(new WeatherRecord("Location", "Provider%d".formatted(i), "Parameter%d".formatted(j), ldt.minusDays(k), new BigDecimal(i+j+k)));
		    if (j ==0 && k == 0) valueSumm += i+j+k;
		}
	    }
	}

	BigDecimal avg = service.getAvgByLocationAndParameterAndDate("Location", "Parameter0", ldt);
	
	assert(Objects.nonNull(avg));
	assert(avg.compareTo(new BigDecimal(Double.valueOf(valueSumm) / Double.valueOf(dayShifts))) == 0);
    }

}
