package com.rs.utils.reflect;

import com.rs.lib.util.reflect.ReflectionCheck;

import java.util.function.Function;

public class ReflectionTest {
	
	private final String name;
	private final String description;
	private final ReflectionCheck check;
	private final Function<ReflectionCheck, Boolean> validation;
	
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
