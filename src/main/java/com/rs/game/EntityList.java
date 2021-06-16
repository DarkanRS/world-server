package com.rs.game;

import java.util.AbstractCollection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.rs.Settings;

public class EntityList<T extends Entity> extends AbstractCollection<T> {
	public Object[] entities;
	public Set<Integer> indicies = new HashSet<Integer>();
	public int capacity;
	private final Object lock = new Object();

	public EntityList(int capacity) {
		entities = new Object[capacity];
		this.capacity = capacity;
	}

	public int getEmptySlot() {
		for (int i = 1; i < entities.length; i++) {
			if (i >= Settings.NPCS_LIMIT)
				return -1;
			if (entities[i] == null) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean add(T entity) {
		synchronized (lock) {
			int slot = getEmptySlot();
			if (slot == -1) {
				return false;
			}
			add(entity, slot);
			return true;
		}
	}

	public void remove(T entity) {
		synchronized (lock) {
			entities[entity.getIndex()] = null;
			indicies.remove(entity.getIndex());
		}
	}

	@SuppressWarnings("unchecked")
	public T remove(int index) {
		synchronized (lock) {
			Object temp = entities[index];
			entities[index] = null;
			indicies.remove(index);
			return (T) temp;
		}
	}

	@SuppressWarnings("unchecked")
	public T get(int index) {
		synchronized (lock) {
			if (index >= entities.length || index < 0)
				return null;
			return (T) entities[index];
		}
	}

	public void add(T entity, int index) {
		if (entities[index] != null) {
			return;
		}
		entities[index] = entity;
		entity.setIndex(index);
		indicies.add(index);
	}

	@Override
	public Iterator<T> iterator() {
		synchronized (lock) {
			return new EntityListIterator<T>(entities, indicies, this);
		}
	}

	public boolean contains(T entity) {
		return indexOf(entity) > -1;
	}

	public int indexOf(T entity) {
		synchronized (lock) {
			for (int index : indicies) {
				if (entities[index].equals(entity)) {
					return index;
				}
			}
		}
		return -1;
	}

	@Override
	public int size() {
		return indicies.size();
	}
}