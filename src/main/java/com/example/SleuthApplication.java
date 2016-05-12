package com.example;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.cloud.sleuth.SpanAccessor;
import org.springframework.cloud.sleuth.Tracer;
import org.springframework.cloud.sleuth.sampler.AlwaysSampler;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@RestController
public class SleuthApplication implements
ApplicationListener<EmbeddedServletContainerInitializedEvent> {
	
	@Autowired
	private RestTemplate restTemplate;
//	@Autowired
//	private Tracer tracer;
//	@Autowired
//	private SpanAccessor accessor;

	@Autowired
	private Random random;
	private int port;

	
	private final Logger slf4jLogger = LoggerFactory.getLogger(SleuthApplication.class);
	
	@Bean
	public AlwaysSampler defaultSampler() {
		return new AlwaysSampler();
	}
	
	@RequestMapping("/hop")
	public String service2() {
		slf4jLogger.info("Hi, {}", "service2");
		return "Service2";
	}
	
	@RequestMapping("/")
	public String service1() {
		// slf4jLogger.info("Hi, {}", "service1");
		String s = this.restTemplate.getForObject("http://localhost:" + this.port
				+ "/hop", String.class);
		return "Service1" + s;
		// return "Service1";
	}

	
	public static void main(String[] args) {
		SpringApplication.run(SleuthApplication.class, args);
	}
	
	@Override
	public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
		this.port = event.getEmbeddedServletContainer().getPort();
	}
}
