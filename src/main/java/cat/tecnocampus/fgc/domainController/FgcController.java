package cat.tecnocampus.fgc.domainController;

import cat.tecnocampus.fgc.domain.*;
import cat.tecnocampus.fgc.exception.SameOriginDestinationException;
import cat.tecnocampus.fgc.exception.UserDoesNotExistsException;
import cat.tecnocampus.fgc.repositories.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by roure on 07/12/2016.
 */
@Service
public class FgcController {
    private StationRepository stationRepository;
    private UserRepository userRepository;
    private FavoriteJourneyRepository favoriteJourneyRepository;

    public FgcController(StationRepository stationRepository, UserRepository userRepository,
                         FavoriteJourneyRepository favoriteJourneyRepository) {
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
        this.favoriteJourneyRepository = favoriteJourneyRepository;
    }

    public List<Station> getStationsFromRepository() {
        return stationRepository.findAll();
    }

    public void saveStations(List<Station> stations) {
        stationRepository.saveStations(stations);
    }

    public User getUser(String username) {
        User user = userRepository.findOneUser(username);

        user.setFavoriteJourneyList(favoriteJourneyRepository.findFavoriteJourneys(username));

        return user;
    }

    public List<User> getUsers() {
        List<User> users = userRepository.getUsers();
        users.forEach(u -> u.setFavoriteJourneyList(favoriteJourneyRepository.findFavoriteJourneys(u.getUsername())));

        return users;
    }

    public boolean existsUser (String username) {
        return userRepository.existsUser(username);
    }

    public int addUserFavoriteJourney(String username, FavoriteJourney favoriteJourney) {
        if (favoriteJourney.getJourney().sameOriginDestination()) {
            throw new SameOriginDestinationException();
        }
        if (existsUser(username)) {
            favoriteJourneyRepository.saveFavoriteJourney(favoriteJourney, username);
        }
        else {
            UserDoesNotExistsException e = new UserDoesNotExistsException("Non existing resource");
            e.setUsername(username);
            throw e;
        }
        return 0;
    }

    public List<FavoriteJourney> getFavoriteJourneys(String username) {
        if (!existsUser(username)) {
            UserDoesNotExistsException e = new UserDoesNotExistsException("Non existing resource");
            e.setUsername(username);
            throw e;
        }

        return favoriteJourneyRepository.findFavoriteJourneys(username);
    }
}
