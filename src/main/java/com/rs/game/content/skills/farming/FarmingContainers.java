package com.rs.game.content.skills.farming;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class FarmingContainers {

	private static void fillUp(Player player, Item item, ProduceContainer container, boolean empty) {
		if (empty) {
			Item next = player.getInventory().findItemByIds(ProduceContainer.getReverseFillIdsForContainerId(item.getId()));
			if (next == null) {
				player.sendMessage("You don't have any produce to add!");
				return;
			}
			container = ProduceContainer.forFillId(next.getId());
		}
		if (container == null)
			return;

		int inventoryCount = player.getInventory().getAmountOf(container.getFillId());
		if (inventoryCount == 0) {
			player.sendMessage("You don't have any produce to add!");
			return;
		}

		int currentCount = container.getCountForItemId(item.getId());
		if (currentCount == container.getContainerSize()) {
			return;
		}

		int maxProduceToFill = Math.min(container.getContainerSize(), currentCount + inventoryCount);
		int nextId = container.getItemIdForCount(maxProduceToFill);
		player.getInventory().replace(item, new Item(nextId));
		player.getInventory().deleteItem(container.getFillId(), container.getContainerSize() - currentCount);
		player.getInventory().refresh();
	}

	private static void removeOne(Player player, Item item) {
		ProduceContainer container = ProduceContainer.forId(item.getId());
		if (container == null)
			return;

		int currentCount = container.getCountForItemId(item.getId());
		if (currentCount == 1) {
			player.getInventory().replace(item, new Item(container.getContainerId(), 1));
		} else {
			int newCount = currentCount - 1;
			player.getInventory().replace(item, new Item(container.getItemIdForCount(newCount)));
		}
		player.getInventory().addItemDrop(new Item(container.getFillId(), 1));
		player.getInventory().refresh();
	}

	private static void empty(Player player, Item item) {
		ProduceContainer container = ProduceContainer.forId(item.getId());
		if (container == null)
			return;

		int currentCount = container.getCountForItemId(item.getId());
		player.getInventory().replace(item, new Item(container.getContainerId(), 1));
		player.getInventory().addItemDrop(new Item(container.getFillId(), currentCount));
		player.getInventory().refresh();
	}

	public static ItemOnItemHandler fillFarmingContainer = new ItemOnItemHandler(new int[] {
			5376,
			5378, 5380, 5382, 5384, 5386,
			5388, 5390, 5392, 5394, 5396,
			5398, 5400, 5402, 5404, 5406,
			5408, 5410, 5412, 5414, 5416,
			5960, 5962, 5964, 5966, 5968,
			5418,
			5420, 5422, 5424, 5426, 5428, 5430, 5432, 5434, 5436, 5438,
			5440, 5442, 5444, 5446, 5448, 5450, 5452, 5454, 5456, 5458,
			5460, 5462, 5464, 5466, 5468, 5470, 5472, 5474, 5476, 5478,
	}, new int[] { 1942, 1957, 1965, 1955, 2108, 5504, 1963, 1982 }, e -> {
		ProduceContainer container = ProduceContainer.forId(e.getItem1().getId());
		Item item = e.getItem1();
		boolean empty = ProduceContainer.isEmptyContainer(e.getItem1().getId());
		if (!empty && container == null) {
			container = ProduceContainer.forId(e.getItem2().getId());
			item = e.getItem2();
			empty = ProduceContainer.isEmptyContainer(e.getItem2().getId());
		}
		fillUp(e.getPlayer(), item, container, empty);
	});

	public static ItemClickHandler clickFarmingContainer = new ItemClickHandler(new Integer[] {
			5376,
			5378, 5380, 5382, 5384, 5386,
			5388, 5390, 5392, 5394, 5396,
			5398, 5400, 5402, 5404, 5406,
			5408, 5410, 5412, 5414, 5416,
			5960, 5962, 5964, 5966, 5968,
			5418,
			5420, 5422, 5424, 5426, 5428, 5430, 5432, 5434, 5436, 5438,
			5440, 5442, 5444, 5446, 5448, 5450, 5452, 5454, 5456, 5458,
			5460, 5462, 5464, 5466, 5468, 5470, 5472, 5474, 5476, 5478,
	}, new String[] { "Fill", "Remove-one", "Empty" }, e -> {
		switch (e.getOption()) {
			case "Fill" -> {
				ProduceContainer container = ProduceContainer.forId(e.getItem().getId());
				boolean empty = ProduceContainer.isEmptyContainer(e.getItem().getId());
				fillUp(e.getPlayer(), e.getItem(), container, empty);
			}
			case "Remove-one" -> removeOne(e.getPlayer(), e.getItem());
			case "Empty" -> empty(e.getPlayer(), e.getItem());
		}
	});

}
