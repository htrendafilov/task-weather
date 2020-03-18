package com.htrendafilov.weather.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("weather")
public class WeatherController {
	
	@RequestMapping(value = "/{city}", method = RequestMethod.GET)
	public String root(@PathVariable String city) {
		return "Weather-App is up and running for "+ city;
	}

}
