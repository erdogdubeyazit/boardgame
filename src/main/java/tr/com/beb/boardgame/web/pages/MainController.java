package tr.com.beb.boardgame.web.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping(value = { "/", "/login", "/register", "game-scene" })
    public String index() {
        return "index";
    }

}
