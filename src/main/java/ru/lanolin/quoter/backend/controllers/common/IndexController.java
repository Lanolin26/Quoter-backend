package ru.lanolin.quoter.backend.controllers.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {

	@Value("${spring.profiles.active}")
	public String develop;

	@GetMapping
	public String index(Map<String, Object> model) {
		model.put("dev", "develop".equalsIgnoreCase(develop));
		model.put("title", "Quoter's Library");
		return "index";
	}

}
