package com.rs.utils.reflect;

import com.rs.lib.util.reflect.ReflectionCheck;
import com.rs.lib.util.reflect.ReflectionChecks;

import java.util.ArrayList;
import java.util.List;

public class ReflectionAnalysis {
	
	private boolean built = false;
	private ReflectionChecks checks;
	private List<ReflectionTest> tests = new ArrayList<>();
	
	public ReflectionAnalysis() {
		this.checks = new ReflectionChecks();
	}
	
	public ReflectionAnalysis addTest(ReflectionTest test) {
		if (built)
			throw new RuntimeException("Cannot add a test once it has been finalized.");
		tests.add(test);
		return this;
	}
	
	public ReflectionAnalysis build() {
		this.checks.setReflectionChecks(tests.stream().map(test -> test.getCheck()).toArray(ReflectionCheck[]::new));
		built = true;
		return this;
	}

	public int getId() {
		return checks.getId();
	}
	
	public ReflectionChecks getChecks() {
		return checks;
	}

	public boolean isBuilt() {
		return built;
	}

	public List<ReflectionTest> getTests() {
		return tests;
	}
}
