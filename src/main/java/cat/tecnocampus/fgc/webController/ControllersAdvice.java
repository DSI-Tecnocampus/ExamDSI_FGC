package cat.tecnocampus.fgc.webController;

import cat.tecnocampus.fgc.domain.*;
import cat.tecnocampus.fgc.domainController.FgcController;
import cat.tecnocampus.fgc.exception.SameOriginDestinationException;
import cat.tecnocampus.fgc.exception.UserDoesNotExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

@ControllerAdvice
public class ControllersAdvice {
    private static final Logger logger = LoggerFactory.getLogger(ControllersAdvice.class);
    private FgcController fgcController;

    public ControllersAdvice(FgcController fgcController) {
        this.fgcController = fgcController;
    }

    @ExceptionHandler
    public String handleUserDoesNotExist(UserDoesNotExistsException exception, Model model) {
        model.addAttribute("username", exception.getUsername());

        return "/error/userDoesNotExist";
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public String handleUsernameDoesNotExist(Model model, HttpServletRequest request, Exception ex) {
        String url = request.getRequestURL().toString();

        logger.error("Request: " + url + " raised " + ex);

        model.addAttribute("username", url.substring(url.lastIndexOf("/") + 1));
        return "error/usernameDoesNotExist";
    }

    @ExceptionHandler(SameOriginDestinationException.class)
    public String handleSameOriginDestination(Model model, HttpServletRequest request) {
        ArrayList<String> myErrors = new ArrayList<>();
        myErrors.add("Origin and destination must be different");

        model.addAttribute("username", getUserNameFromUrl(request));
        model.addAttribute("stationList", fgcController.getStationsFromRepository());
        model.addAttribute("favoriteJourney", buildFavoriteJourney(request));
        model.addAttribute("myErrors", myErrors);

        return "newFavoriteJourney";
    }

    private String getUserNameFromUrl(HttpServletRequest request){
        String url = request.getRequestURL().toString();
        int end = url.lastIndexOf("/");
        int begin = url.indexOf("8080/") + 5;
        return url.substring(begin,end);
    }

    private FavoriteJourney buildFavoriteJourney(HttpServletRequest request) {
        FavoriteJourney favoriteJourney;
        Station origin, destination;

        origin = new Station();
        origin.setNom(request.getParameter("journey.origin.nom"));

        destination = new Station();
        destination.setNom(request.getParameter("journey.destination.nom"));

        favoriteJourney = new FavoriteJourney();
        favoriteJourney.setJourney(new Journey(origin, destination));

        favoriteJourney.setStartList(buildStartList(request));

        return favoriteJourney;
    }

    private List<DayTimeStart> buildStartList(HttpServletRequest request) {
        ArrayList<DayTimeStart> list = new ArrayList<>();

        Enumeration<String> names = request.getParameterNames();

        //skip origin and destination
        names.nextElement();
        names.nextElement();

        while(names.hasMoreElements()) {
            String paramName = names.nextElement(); //day of week
            String dayOfWeek = request.getParameter(paramName);

            paramName = names.nextElement(); //time
            String start = request.getParameter(paramName);

            System.out.println(dayOfWeek + " ... " + start);

            list.add(new DayTimeStart(dayOfWeek, start));
        }


        return list;
    }

}
