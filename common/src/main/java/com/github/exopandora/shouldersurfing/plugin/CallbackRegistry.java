package com.github.exopandora.shouldersurfing.plugin;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

class CallbackRegistry<T> {
	private final TreeMap<Integer, List<T>> entries = new TreeMap<Integer, List<T>>();
	private List<T> orderedCallbacks;
	
	public void register(int priority, T entry) {
		this.entries.computeIfAbsent(priority, (_) -> new LinkedList<T>()).add(entry);
		this.orderedCallbacks = null;
	}
	
	public List<T> getCallbacks() {
		if (this.orderedCallbacks == null) {
			this.orderedCallbacks = this.entries.entrySet().stream()
				.flatMap(entry -> entry.getValue().stream())
				.toList();
		}
		return this.orderedCallbacks;
	}
}
