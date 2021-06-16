package com.rs.utils;

public class GenericsUtils {

	
	public static boolean isi(Object cst) {
		return cst instanceof Integer;
	}
	
	public static boolean iss(Object cst) {
		return cst instanceof String;
	}
	
	public static boolean isl(Object cst) {
		return cst instanceof Long;
	}
	
	public static Integer asi(Object cst) {
		return as(cst);
	}
	
	public static String ass(Object cst) {
		return as(cst);
	}
	
	public static Long asl(Object cst) {
		return as(cst);
	}
	
	
	public static int shrn(int val, int shr, int n) {
		return (val >>> shr) & n;
	}
	
	public static long shrn(long val, int shr, long n) {
		return (val >>> shr) & n;
	}
	
	
	@SuppressWarnings("unchecked")
	public static <T> T as(Class<T> t, Object cst) {
		return (T)cst;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T as(Object cst) {
		return (T)cst;
	}
}
