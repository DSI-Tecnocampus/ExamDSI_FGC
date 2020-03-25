package cat.tecnocampus.fgc.webController;

import cat.tecnocampus.fgc.domain.FavoriteJourney;
import cat.tecnocampus.fgc.domainController.FgcController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
public class WebController {

    private FgcController fgcController;

    public WebController(FgcController fgcController) {
        this.fgcController = fgcController;
    }

    @GetMapping("/stations")
    public String getStations(Model model) {

        model.addAttribute("stationList", fgcController.getStationsFromRepository());

        return "stations";
    }

    @GetMapping("/{username}/favoriteJourney")
    public String getAddFavoriteJourney(@PathVariable String username, Model model) {

        fillModelForNewFavoriteJourney(model, username, new FavoriteJourney());
        return "newFavoriteJourney";
    }

    @PostMapping("/{username}/favoriteJourney")
    public String postAddFavoriteJourney(@PathVariable String username, @Valid FavoriteJourney favoriteJourney, Errors errors, Model model) {

        if (errors.hasErrors()) {
            fillModelForNewFavoriteJourney(model, username, favoriteJourney);
            return "newFavoriteJourney";
        }

        fgcController.addUserFavoriteJourney(username, favoriteJourney);

        return "redirect:/" + username + "/favoriteJourneys";
    }

    private void fillModelForNewFavoriteJourney(Model model, String username, FavoriteJourney favoriteJourney) {
        model.addAttribute("username", username);
        model.addAttribute("stationList", fgcController.getStationsFromRepository());
        model.addAttribute("favoriteJourney", favoriteJourney);
        model.addAttribute("myErrors", new ArrayList<String>());
    }

    @GetMapping("/{username}/favoriteJourneys")
    public String getFavoriteJourneys(@PathVariable String username, Model model) {
        model.addAttribute("username", username);
        model.addAttribute("favoriteJourneys", fgcController.getFavoriteJourneys(username));
        return "favoriteJourneys";
    }

    @GetMapping("/byebye")
    public String byebye() {

        return "byebye";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", fgcController.getUsers());

        return "users";
    }

    @GetMapping("/users/{username}")
    public String user(@PathVariable String username, Model model) {
        model.addAttribute("tuser", fgcController.getUser(username));

        return "user";
    }
}
