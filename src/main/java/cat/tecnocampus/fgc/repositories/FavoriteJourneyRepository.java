package cat.tecnocampus.fgc.repositories;

import cat.tecnocampus.fgc.domain.DayTimeStart;
import cat.tecnocampus.fgc.domain.FavoriteJourney;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class FavoriteJourneyRepository {
    JdbcTemplate jdbcTemplate;
    JourneyRepository journeyRepository;

    public FavoriteJourneyRepository(JdbcTemplate jdbcTemplate, JourneyRepository journeyRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.journeyRepository = journeyRepository;
    }

    public void saveFavoriteJourney (FavoriteJourney favoriteJourney, String userId) {
        long journeyId = journeyRepository.getJourneyId(favoriteJourney.getJourney());
        if (journeyId == -1) {
            journeyRepository.saveJourney(favoriteJourney.getJourney());
            journeyId = journeyRepository.getJourneyId(favoriteJourney.getJourney());
        }

        long favoriteJourneyId = getFavoriteJourneyId(journeyId, userId);
        if (favoriteJourneyId == -1) {
            jdbcTemplate.update("INSERT INTO favorite_journey(journey, user_id) VALUES (?, ?)", journeyId, userId);
            favoriteJourneyId = getFavoriteJourneyId(journeyId, userId);
        }

        saveDayTimeStart(favoriteJourney.getStartList(), favoriteJourneyId);
    }

    public long getFavoriteJourneyId (long journeyId, String user) {
        try {
            return jdbcTemplate.queryForObject("select favorite_journey_id from favorite_journey where journey = ? and user_id = ?",
                    Long.class, journeyId, user).longValue();
        }
        catch (EmptyResultDataAccessException e) {
            return -1;
        }
    }

    public List<FavoriteJourney> findFavoriteJourneys (String username) {
        return jdbcTemplate.query("select * from favorite_journey where user_id = ?", new FavoriteJourneyMapper(), username);
    }

    private int[] saveDayTimeStart(List<DayTimeStart> start, long favoriteJourneyId) {
        return jdbcTemplate.batchUpdate("INSERT INTO day_time_start(timeStart, day_of_week, favorite_journey_id) values(?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        DayTimeStart dayTimeStart = start.get(i);
                        preparedStatement.setString(1, dayTimeStart.getTimeStart());
                        preparedStatement.setString(2, dayTimeStart.getDayOfWeek());
                        preparedStatement.setLong(3, favoriteJourneyId);
                    }

                    @Override
                    public int getBatchSize() {
                        return start.size();
                    }
        });
    }

    private List<DayTimeStart> findDayTimeStart(long favoriteJourneyId) {
        return jdbcTemplate.query("select * from day_time_start where favorite_journey_id = ?", new DayTimeStartMapper(), favoriteJourneyId);
    }

    public final class FavoriteJourneyMapper implements RowMapper<FavoriteJourney> {
        @Override
        public FavoriteJourney mapRow(ResultSet resultSet, int i) throws SQLException {
            FavoriteJourney favoriteJourney = new FavoriteJourney();

            favoriteJourney.setId(resultSet.getLong("favorite_journey_id"));
            favoriteJourney.setJourney(journeyRepository.findJourney(resultSet.getLong("journey")));
            favoriteJourney.setStartList(findDayTimeStart(favoriteJourney.getId()));
            return favoriteJourney;
        }
    }

    public final class DayTimeStartMapper implements RowMapper<DayTimeStart> {
        @Override
        public DayTimeStart mapRow(ResultSet resultSet, int i) throws SQLException {
            DayTimeStart dayTimeStart = new DayTimeStart();

            dayTimeStart.setId(resultSet.getLong("daytime_id"));
            dayTimeStart.setTimeStart(resultSet.getString("timeStart"));
            dayTimeStart.setDayOfWeek(resultSet.getString("day_of_week"));

            return dayTimeStart;
        }
    }
}
