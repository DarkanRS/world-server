// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright (C) 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.model.entity;

import java.util.AbstractCollection;
import java.util.Iterator;

import java.lang.SuppressWarnings;
import it.unimi.dsi.fastutil.ints.IntHeapPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import it.unimi.dsi.fastutil.ints.IntSet;

public class EntityList<T extends Entity> extends AbstractCollection<T> {
	public Object[] entities;
	private IntSet usedIndices = new IntOpenHashSet();
	private IntPriorityQueue freeIndices = new IntHeapPriorityQueue();
	private IntSet toFreeUp = new IntOpenHashSet();
	private int freeCap = 1;
	private final Object lock = new Object();

	public EntityList(int capacity) {
		entities = new Object[capacity];
	}

	@Override
	public boolean add(T entity) {
		synchronized (lock) {
			int slot = getEmptySlot();
			if (slot == -1)
				return false;
			if (entities[slot] != null)
				return false;
			entities[slot] = entity;
			entity.setIndex(slot);
			usedIndices.add(slot);
			return true;
		}
	}
	
	private int getEmptySlot() {
		if (freeIndices.isEmpty()) {
			if (freeCap < entities.length)
				freeIndices.enqueue(freeCap++);
			else
				return -1;
		}
		return freeIndices.dequeueInt();
	}

	public void remove(T entity) {
		synchronized (lock) {
			entities[entity.getIndex()] = null;
			usedIndices.remove(entity.getIndex());
			toFreeUp.add(entity.getIndex());
		}
	}

	@SuppressWarnings("unchecked")
	public T remove(int index) {
		synchronized (lock) {
			Object temp = entities[index];
			entities[index] = null;
			usedIndices.remove(index);
			toFreeUp.add(index);
			return (T) temp;
		}
	}
	
	public void processPostTick() {
		synchronized (lock) {
			for (int toFree : toFreeUp) {
				if (toFree == 0)
					continue;
				freeIndices.enqueue(toFree);
			}
			toFreeUp.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public T get(int index) {
		synchronized (lock) {
			if (index >= entities.length || index < 1)
				return null;
			return (T) entities[index];
		}
	}

	@Override
	public Iterator<T> iterator() {
		synchronized (lock) {
			return new EntityListIterator<>(entities, usedIndices, this);
		}
	}

	public boolean contains(T entity) {
		return indexOf(entity) > -1;
	}

	public int indexOf(T entity) {
		synchronized (lock) {
			for (int index : usedIndices)
				if (entities[index].equals(entity))
					return index;
		}
		return -1;
	}

	@Override
	public int size() {
		return usedIndices.size();
	}
}