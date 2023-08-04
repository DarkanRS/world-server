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
package com.rs.game.model.item;

import com.rs.lib.game.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Container class.
 *
 * @author Graham / edited by Dragonkk(Alex)
 * @param <T>
 */
public final class ItemsContainer<T extends Item> {

	private Item[] data;
	private boolean alwaysStackable = false;

	public ItemsContainer(int size, boolean alwaysStackable) {
		data = new Item[size];
		this.alwaysStackable = alwaysStackable;
	}

	public void shift() {
		Item[] oldData = data;
		data = new Item[oldData.length];
		int ptr = 0;
		for (int i = 0; i < data.length; i++)
			if (oldData[i] != null)
				data[ptr++] = oldData[i];
	}

	@SuppressWarnings("unchecked")
	public T get(int slot) {
		if (slot < 0 || slot >= data.length)
			return null;
		return (T) data[slot];
	}

	public void set(int slot, T item) {
		if (slot < 0 || slot >= data.length)
			return;
		data[slot] = item;
		if (data[slot] != null)
			data[slot].setSlot(slot);
	}

	public void set2(int slot, Item item) {
		if (slot < 0 || slot >= data.length)
			return;
		data[slot] = item;
	}

	public boolean forceAdd(T item) {
		for (int i = 0; i < data.length; i++)
			if (data[i] == null) {
				data[i] = item;
				return true;
			}
		return false;
	}

	public boolean add(T item) {
		if ((alwaysStackable || item.getDefinitions().isStackable() || item.getDefinitions().isNoted()) && item.getMetaData() == null) {
			for (int i = 0; i < data.length; i++)
				if (data[i] != null)
					if (data[i].getId() == item.getId()) {
						if (data[i].getAmount() + item.getAmount() <= 0)
							return false;
						data[i] = new Item(data[i].getId(), data[i].getAmount() + item.getAmount(), item.getMetaData());
						data[i].setSlot(i);
						return true;
					}
		} else if (item.getAmount() > 1) {
			if (freeSlots() < item.getAmount())
				return false;
			for (int i = 0; i < item.getAmount(); i++) {
				int index = freeSlot();
				data[index] = new Item(item.getId(), 1);
				data[index].setSlot(index);
			}
			return true;
		}
		int index = freeSlot();
		if (index == -1)
			return false;
		data[index] = item;
		data[index].setSlot(index);
		return true;
	}

	public int getThisItemSlot(int itemId) {
		for (int i = 0; i < data.length; i++)
			if (data[i] != null)
				if (data[i].getId() == itemId)
					return i;
		return -1;
	}

	public int freeSlots() {
		int j = 0;
		for (Item aData : data)
			if (aData == null)
				j++;
		return j;
	}

	public int remove(T item) {
		int removed = 0, toRemove = item.getAmount();
		for (int i = 0; i < data.length; i++)
			if (data[i] != null)
				if (data[i].getId() == item.getId()) {
					int amt = data[i].getAmount();
					if (amt > toRemove) {
						removed += toRemove;
						amt -= toRemove;
						toRemove = 0;
						data[i] = new Item(data[i].getId(), amt);
						return removed;
					}
					removed += amt;
					toRemove -= amt;
					data[i] = null;
				}
		return removed;
	}

	public void removeAll(T item) {
		for (int i = 0; i < data.length; i++)
			if (data[i] != null)
				if (data[i].getId() == item.getId())
					data[i] = null;
	}

	public void removeAll(int item) {
		for (int i = 0; i < data.length; i++)
			if (data[i] != null)
				if (data[i].getId() == item)
					data[i] = null;
	}

	public boolean containsOne(T item) {
		for (Item aData : data)
			if (aData != null)
				if (aData.getId() == item.getId())
					return true;
		return false;
	}

	public boolean contains(T item) {
		int amtOf = 0;
		if (item == null)
			return false;
		for (Item aData : data)
			if (aData != null)
				if (aData.getId() == item.getId())
					amtOf += aData.getAmount();
		return amtOf >= item.getAmount();
	}

	public int freeSlot() {
		for (int i = 0; i < data.length; i++)
			if (data[i] == null)
				return i;
		return -1;
	}

	public void clear() {
		Arrays.fill(data, null);
	}

	public int getSize() {
		return data.length;
	}

	public int getFreeSlots() {
		int s = 0;
		for (Item aData : data)
			if (aData == null)
				s++;
		return s;
	}

	public int getUsedSlots() {
		int s = 0;
		for (Item aData : data)
			if (aData != null)
				s++;
		return s;
	}

	public int getNumberOf(Item item) {
		if (item.getMetaData() != null)
			return 1;
		int count = 0;
		for (Item aData : data)
			if (aData != null)
				if (aData.getId() == item.getId() && aData.getMetaData() == item.getMetaData())
					count += aData.getAmount();
		return count;
	}

	public int getNumberOf(int item) {
		int count = 0;
		for (Item aData : data)
			if (aData != null)
				if (aData.getId() == item)
					count += aData.getAmount();
		return count;
	}

	public Item[] array() {
		return data;
	}

	public Item[] getItemsCopy() {
		Item[] newData = new Item[data.length];
		System.arraycopy(data, 0, newData, 0, newData.length);
		return newData;
	}

	public List<Item> asList() {
		List<Item> list = new ArrayList<>();
		for (Item i : getItemsCopy())
			if (i != null)
				list.add(i);
		return list;
	}

	public ItemsContainer<Item> asItemContainer() {
		ItemsContainer<Item> c = new ItemsContainer<>(data.length, this.alwaysStackable);
		System.arraycopy(data, 0, c.data, 0, data.length);
		return c;
	}

	public int getFreeSlot() {
		for (int i = 0; i < data.length; i++)
			if (data[i] == null)
				return i;
		return -1;
	}

	public int getThisItemSlot(T item) {
		for (int i = 0; i < data.length; i++)
			if (data[i] != null)
				if (data[i].getId() == item.getId())
					return i;
		return getFreeSlot();
	}

	public Item lookup(int id) {
		for (Item aData : data) {
			if (aData == null)
				continue;
			if (aData.getId() == id)
				return aData;
		}
		return null;
	}

	public int lookupSlot(int id) {
		for (int i = 0; i < data.length; i++) {
			if (data[i] == null)
				continue;
			if (data[i].getId() == id)
				return i;
		}
		return -1;
	}

	public void reset() {
		data = new Item[data.length];
	}

	public int remove(int preferredSlot, Item item) {
		int removed = 0, toRemove = item.getAmount();
		if (data[preferredSlot] != null)
			if (data[preferredSlot].getId() == item.getId() && data[preferredSlot].getMetaData() == item.getMetaData()) {
				int amt = data[preferredSlot].getAmount();
				if (amt > toRemove) {
					removed += toRemove;
					amt -= toRemove;
					toRemove = 0;
					set2(preferredSlot, new Item(data[preferredSlot].getId(), amt, data[preferredSlot].getMetaData()));
					return removed;
				}
				removed += amt;
				toRemove -= amt;
				set(preferredSlot, null);
			}
		for (int i = 0; i < data.length; i++)
			if (data[i] != null)
				if (data[i].getId() == item.getId() && item.getMetaData() == data[i].getMetaData()) {
					int amt = data[i].getAmount();
					if (amt > toRemove) {
						removed += toRemove;
						amt -= toRemove;
						toRemove = 0;
						set2(i, new Item(data[i].getId(), amt, data[i].getMetaData()));
						return removed;
					}
					removed += amt;
					toRemove -= amt;
					set(i, null);
				}
		return removed;
	}

	public void addAll(ItemsContainer<T> container) {
		for (int i = 0; i < container.getSize(); i++) {
			T item = container.get(i);
			if (item != null)
				this.add(item);
		}
	}

	@SuppressWarnings("unchecked")
	public void addAll(Item[] container) {
		for (Item item : container)
			if (item != null)
				this.add((T) item);
	}

	public boolean hasSpaceFor(ItemsContainer<T> container) {
		for (int i = 0; i < container.getSize(); i++) {
			T item = container.get(i);
			if (item != null)
				if (!this.hasSpaceForItem(item))
					return false;
		}
		return true;
	}

	private boolean hasSpaceForItem(T item) {
		if (alwaysStackable || item.getDefinitions().isStackable() || item.getDefinitions().isNoted()) {
			for (Item aData : data)
				if (aData != null)
					if (aData.getId() == item.getId())
						return true;
		} else if (item.getAmount() > 1)
			return freeSlots() >= item.getAmount();
			int index = freeSlot();
			return index != -1;
	}

	public Item[] toArray() {
		return data;
	}

	public void initSlots() {
		for (int i = 0;i < data.length;i++)
			if (data[i] != null)
				data[i].setSlot(i);
	}

	public void addAll(List<T> list) {
		for (T item : list)
			add(item);
	}

	public boolean isEmpty() {
		for (Item element : data)
			if (element != null)
				return false;
		return true;
	}

	public ItemsContainer<T> sortByItemId() {
		Arrays.sort(data, (o1, o2) -> {
			int o1Id = o1 == null ? Integer.MAX_VALUE : o1.getId();
			int o2Id = o2 == null ? Integer.MAX_VALUE : o2.getId();
			return o1Id - o2Id;
		});
		return this;
	}

	public Item[] getItemsNoNull() {
		List<Item> items = new ArrayList<>();
		for (Item i : data)
			if (i != null)
				items.add(i);
		return items.toArray(new Item[items.size()]);
	}

}
