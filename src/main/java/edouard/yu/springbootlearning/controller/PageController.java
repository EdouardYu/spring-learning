package edouard.yu.springbootlearning.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PageController {
    @GetMapping("index")
    //si on veut renvoyer un template à la place d'un chemin vers un template (dans ./resources/templates)
    // comme un return "<h1>Hello World!</h1>", on doit mettre l'annotation @ResponseBody
    public String home(@RequestParam(required = false, defaultValue = "World") String name, ModelMap modelMap) {
        modelMap.put("name", name);
        return "page/home";
    }
}
