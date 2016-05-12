package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.sleuth.SpanAccessor;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
	
	private final Logger slf4jLogger = LoggerFactory.getLogger(SleuthApplication.class);
	
	@Autowired
	HelloProperties props;
	@Autowired
	private Tracer tracer;
	@Autowired
	private SpanAccessor accessor;
	
	
	@RequestMapping("/hello")
	public String hello(@RequestParam String name) {
		slf4jLogger.info("Hi, {}", "here");
		return props.getGreeting() + name;
	}
}
