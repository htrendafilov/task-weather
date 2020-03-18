package com.htrendafilov.weather.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.net.URL;
import java.util.Scanner;

@RestController
@RequestMapping("weather")
public class WeatherController {

	@RequestMapping(value = "/{city}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public String root(@PathVariable String city) throws Exception{
		System.out.println("Weather-App is up and running for "+ city);
		String out = new Scanner(new URL("http://api.openweathermap.org/data/2.5/forecast?q="+city+"&appid=83e188e2f9e71ab01c336a288212cead&units=metric")
				.openStream(), "UTF-8")
				.useDelimiter("\\A").next();
		return out;
	}

}
