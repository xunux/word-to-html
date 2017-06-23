package com.dsky.word2html.controller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dsky.word2html.model.User;

@RestController
@RequestMapping("/test")
public class TestController {

	@RequestMapping("/hello")
	public String hello(){
		return "hello";
	}
	
	/**
	 * 
	 * @param user
		<code>{
		"userId":"123",
		"name":"lois",
		"birth":"2017-06-23 19:33:00"
		}</code>
	 * @return
	 */
	@RequestMapping("/user/add")
	public User add(@RequestBody User user){
		return user;
	}
}
