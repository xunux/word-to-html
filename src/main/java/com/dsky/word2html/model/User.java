package com.dsky.word2html.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * spring mvc 中传 json日期的转换使用 jsonFormat
 * 
 * 参考：
 * 	http://blog.csdn.net/zhanngle/article/details/24123659
 * 
 * JsonIgnoreProperties 参考 
 * 	https://github.com/FasterXML/jackson-databind/issues/95
 * 
 * @author lois.xiao
 *
 */

@JsonIgnoreProperties(value={"birth"}, allowSetters=true)
public class User {
	private String userId;
	private String name;
	
//	@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
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
	
	@JsonProperty(value="birth_day")
	@JsonFormat(with={JsonFormat.Feature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS})
	public Date getBirthDay() {
		return birth;
	}
	
	@JsonGetter("birth")
	public Date getBirthDay2() {
		return birth;
	}
	
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	public void setBirth(Date birth) {
		this.birth = birth;
	}
	
	@Override
	public String toString() {
		return "User [userId=" + userId + ", name=" + name + ", birth=" + birth + "]";
	}
	
}
