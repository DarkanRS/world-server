package com.rs.utils.reflect;

import java.util.function.Function;

import com.rs.lib.util.reflect.ReflectionCheck;

public class ReflectionTest {
	
	private String name;
	private String description;
	private ReflectionCheck check;
	private Function<ReflectionCheck, Boolean> validation;
	
	public ReflectionTest(String name, String description, ReflectionCheck check, Function<ReflectionCheck, Boolean> validation) {
		this.name = name;
		this.description = description;
		this.check = check;
		this.validation = validation;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public ReflectionCheck getCheck() {
		return check;
	}

	public Function<ReflectionCheck, Boolean> getValidation() {
		return validation;
	}
}
