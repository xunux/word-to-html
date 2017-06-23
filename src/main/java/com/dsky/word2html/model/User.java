package com.dsky.word2html.model;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * spring mvc 中传 json日期的转换使用 jsonFormat
 * 
 * 参考：
 * 	http://blog.csdn.net/zhanngle/article/details/24123659
 * 
 * @author lois.xiao
 *
 */
public class User {
	private String userId;
	private String name;
	
//	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date birth;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getBirth() {
		return birth;
	}
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	
	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", birth=" + birth + "]";
	}
	
}
