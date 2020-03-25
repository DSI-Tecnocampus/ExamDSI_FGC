package cat.tecnocampus.fgc.repositories;

import cat.tecnocampus.fgc.domain.Journey;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class JourneyRepository {
    JdbcTemplate jdbcTemplate;
    StationRepository stationRepository;

    public JourneyRepository(JdbcTemplate jdbcTemplate, StationRepository stationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.stationRepository = stationRepository;
    }

    public int saveJourney(Journey journey) {
        return jdbcTemplate.update("INSERT INTO journey(origin, destination) VALUES (?, ?)",
                journey.getOrigin().getNom(), journey.getDestination().getNom());
    }

    public long getJourneyId(Journey journey) {
        try {
            Long id = jdbcTemplate.queryForObject("SELECT journey_id FROM journey WHERE origin = ? AND destination = ?",
                    Long.class, journey.getOrigin().getNom(), journey.getDestination().getNom());
            return id;
        }
        catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    public Journey findJourney(long journeyId) {
        return jdbcTemplate.queryForObject("select * from journey where journey_id = ?", new JourneyMapper(), journeyId);
    }

    public final class JourneyMapper implements RowMapper<Journey> {
        @Override
        public Journey mapRow(ResultSet resultSet, int i) throws SQLException {
            Journey journey = new Journey();

            journey.setId(resultSet.getLong("journey_id"));
            journey.setOrigin(stationRepository.findOne(resultSet.getString("origin")));
            journey.setDestination(stationRepository.findOne(resultSet.getString("destination")));

            return journey;
        }
    }
}
