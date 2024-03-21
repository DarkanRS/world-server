package com.rs.game.content.skills.hunter;

import java.util.Collection;
import java.util.HashMap;

public class OrderedEnum<T, V extends Enum<V>> extends HashMap<T, V> {

	public OrderedEnum(V[] values, OrderFunction<T, V> orderBy) {
		for (V t : values)
			put(orderBy.apply(t), t);
	}

	@Override
	public V put(T index, V value) {
		throw new IllegalCallerException();
	}

	@FunctionalInterface
	public interface OrderFunction<T, V extends Enum<V>> {
		T apply(V value);
	}
}
