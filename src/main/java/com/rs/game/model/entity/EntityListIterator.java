/*
 * Copyright (C) 2008  RS2DBase Development team
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.rs.game.model.entity;

import java.util.Iterator;
import java.util.Set;

public class EntityListIterator<E extends Entity> implements Iterator<E> {
	private Integer[] indicies;
	private Object[] entities;
	@SuppressWarnings("rawtypes")
	private EntityList entityList;
	private int curIndex = 0;

	public EntityListIterator(Object[] entities, Set<Integer> indicies, @SuppressWarnings("rawtypes") EntityList entityList) {
		this.entities = entities;
		this.indicies = indicies.toArray(new Integer[indicies.size()]);
		this.entityList = entityList;
	}

	@Override
	public boolean hasNext() {
		return indicies.length != curIndex;
	}

	@Override
	@SuppressWarnings("unchecked")
	public E next() {
		Object temp = entities[indicies[curIndex]];
		curIndex++;
		return (E) temp;
	}

	@Override
	public void remove() {
		if (curIndex >= 1)
			entityList.remove(indicies[curIndex - 1]);
	}
}
