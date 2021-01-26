package mif.weatheraggregator.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mif.weatheraggregator.WeatherAggregatorApplication;

@Service
public class WeatherRecordService {
    
    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(WeatherAggregatorApplication.class); 

    @Autowired
    private EntityManager manager;

    public WeatherRecord getById(String location, String provider, String parameter, LocalDateTime datetime) {
	String jpql = "SELECT r "
		    + "  FROM WeatherRecord r "
		    + " WHERE r.Id.location = :location"
		    + "   AND r.Id.provider = :provider"
		    + "   AND r.Id.parameter = :parameter"
		    + "   AND r.Id.date = :datetime";
	
	return manager
		.createQuery(jpql, WeatherRecord.class)
		.setParameter("location", location)
		.setParameter("provider", provider)
		.setParameter("parameter", parameter)
		.setParameter("datetime", datetime)
		.getSingleResult();
    }
    
    public WeatherRecord getById(WeatherRecordPK id) {
	return this.getById(id.getLocation(), id.getProvider(), id.getParameter(), id.getDate());
    }
    
    public List<WeatherRecord> getAll() {
	String jpql = "SELECT r FROM WeatherRecord r";
	return manager
		.createQuery(jpql, WeatherRecord.class)
		.getResultList();
    }

    public List<WeatherRecord> getByLocationAndDate(String location, LocalDateTime date) {
	String jpql = "SELECT r "
		    + "  FROM WeatherRecord r "
		    + " WHERE r.Id.location = :location "
		    + "   AND r.Id.date >= :begin "
		    + "   AND r.Id.date < :end";


	return manager
		.createQuery(jpql, WeatherRecord.class)
		.setParameter("location", location)
		.setParameter("begin", date.truncatedTo(ChronoUnit.DAYS))
		.setParameter("end", date.truncatedTo(ChronoUnit.DAYS).plusDays(1))
		.getResultList();
    }
    
    public BigDecimal getAvgByLocationAndParameterAndDate(String location, String parameter, LocalDateTime date) {

	if (Strings.isBlank(location))
	    return null;
	
	if (Strings.isBlank(parameter))
	    return null;
	
	if (Objects.isNull(date))
	    date = LocalDateTime.now();
	
	LocalDateTime begin = date.truncatedTo(ChronoUnit.DAYS);
	LocalDateTime end = begin.plusDays(1);

	String jpql = "SELECT AVG(r.value) " 
		    + "  FROM WeatherRecord r "
		    + " WHERE r.Id.location = :location "
		    + "   AND r.Id.parameter = :parameter "
		    + "   AND r.Id.date >= :begin "
		    + "   AND r.Id.date < :end";
	Double result = manager
		.createQuery(jpql, Double.class)
		.setParameter("location", location)
		.setParameter("parameter", parameter)
		.setParameter("begin", begin)
		.setParameter("end", end)
		.getSingleResult();
	
	return BigDecimal.valueOf(result);

    }
    
    @Transactional
    public WeatherRecord save(WeatherRecord record) {
	manager.persist(record);
	manager.flush();
	return record;
    }
    
    public Long getCount() {
	String jpql = "SELECT COUNT(r) FROM WeatherRecord r";
	return manager.createQuery(jpql, Long.class).getSingleResult().longValue();
    }
}
