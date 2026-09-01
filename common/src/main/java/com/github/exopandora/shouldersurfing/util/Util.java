package com.github.exopandora.shouldersurfing.util;

import java.util.function.Predicate;
import java.util.regex.Pattern;

public class Util {
	public static Predicate<String> expressionToMatchPredicate(String expression) {
		try {
			return Pattern.compile(expression).asMatchPredicate();
		} catch (Exception e) {
			return expression::equals;
		}
	}
}
