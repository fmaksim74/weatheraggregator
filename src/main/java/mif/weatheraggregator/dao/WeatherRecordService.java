package mif.weatheraggregator.dao;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeatherRecordService {

    @Autowired
    private EntityManager manager;

    public WeatherRecord getById(WeatherRecordPK id) {
	String jpql = "SELECT r FROM WeatherRecord r WHERE r.Id = :Id";
	return manager
		.createQuery(jpql, WeatherRecord.class)
		.setParameter("Id", id)
		.getSingleResult();
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
    
    public BigDecimal getAvgByLocationAndDate(String location, String parameter, LocalDateTime date) {

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
		
	return manager
		.createQuery(jpql, BigDecimal.class)
		.setParameter("location", location)
		.setParameter("parameter", parameter)
		.setParameter("begin", begin)
		.setParameter("end", end)
		.getSingleResult();
    }
    
    @Transactional
    public void save(WeatherRecord record) {
	manager.persist(record);
	manager.flush();
    }
}
