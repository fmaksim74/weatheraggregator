package mif.weatheraggregator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import mif.weatheraggregator.dao.WeatherRecord;
import mif.weatheraggregator.dao.WeatherRecordService;
import mif.weatheraggregator.providers.WeatherProvider;

@SpringBootApplication
@EnableScheduling
@RestController
public class WeatherAggregatorApplication {

    private static final Logger log = LoggerFactory.getLogger(WeatherAggregatorApplication.class);

    @Value("${locations:Moscow,Chelyabinsk}")
    private List<String> locations;

    @Autowired
    private List<? extends WeatherProvider> providers;

    @Autowired
    private WeatherRecordService recordService;

    public static void main(String[] args) {
	SpringApplication.run(WeatherAggregatorApplication.class, args);
    }

    @PostConstruct
    public void initStorage() {
	if (Objects.nonNull(locations)) {
	    log.info("Requesting weather data for {}", locations.stream().collect(Collectors.joining(",")));
	}
    }

    @Scheduled(initialDelayString = "PT10S", fixedRateString = "${aggregationRate:PT1M}")
    @Transactional
    public void aggregateWearther() {
	log.info("Requesting weather data...");
	LocalDateTime now = LocalDateTime.now();
	if (Objects.nonNull(providers)) {
	    locations.forEach((l) -> providers.forEach((p) -> {
		try {
		    var svalue = p.getValue(l, "temp_c");
		    if (!Strings.isEmpty(svalue)) {
			var value = new BigDecimal(svalue);
			WeatherRecord wr = new WeatherRecord(l, p.getName(), "temp_c", now, value);
			recordService.save(wr);
			log.debug(wr.toString());
		    }
		} catch (Exception e) {
		    log.error("ERROR {}", e.getMessage());
		}
	    }));
	} else {
	    log.warn("No weather providers!");
	}
    }

    @GetMapping(path = "/avgTempC/{location}")
    public ResponseEntity<String> getAvgTempC(@PathVariable("location") String location,
	    @RequestParam(name = "dt", required = false) LocalDate date) {
	log.info("Requested AVG for {}", location);

	var result = recordService
		.getAvgByLocationAndParameterAndDate(location, "temp_c",
			Objects.nonNull(date) ? LocalDateTime.of(date, LocalTime.MIN) : null);

	return Objects.nonNull(result) ? ResponseEntity.ok(result.toString()) : ResponseEntity.noContent().build();
    }
}
