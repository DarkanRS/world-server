package com.rs.game.content.skills.farming;

import java.util.HashMap;

public enum ProduceContainer {
	COOKING_APPLES(5376, 5, 1955, 5378, 5386),
	ORANGES(5376, 5, 2108, 5388, 5396),
	STRAWBERRIES(5376, 5, 5504, 5398, 5406),
	BANANAS(5376, 5, 1963, 5408, 5416),
	POTATO(5418, 10, 1942, 5420, 5438),
	ONION(5418, 10, 1957, 5440, 5458),
	CABBAGE(5418, 10, 1965, 5460, 5478),
	TOMATOES(5376, 5, 1982, 5960, 5968),
	;

	private final static HashMap<Integer, ProduceContainer> BY_FILL_ID = new HashMap<>();

	static {
		for (ProduceContainer container : values())
			BY_FILL_ID.put(container.fillId, container);
	}

	private final static int[] SACK_FILL_IDS_BACKWARDS = new int[] { 1965, 1957, 1942 };
	private final static int[] BASKET_FILL_IDS_BACKWARDS = new int[] { 1982, 5504, 2108, 1963, 1955 };

	public static int[] getReverseFillIdsForContainerId(int containerId) {
		if (containerId == 5418)
			return SACK_FILL_IDS_BACKWARDS;
		return BASKET_FILL_IDS_BACKWARDS;
	}

	public static ProduceContainer forFillId(int fillId) {
		return BY_FILL_ID.get(fillId);
	}

	public static ProduceContainer forId(int containerId) {
		for (ProduceContainer container : values()) {
			if (containerId >= container.baseId && containerId <= container.fullId)
				return container;
		}
		return null;
	}

	public static boolean isEmptyContainer(int itemId) {
		return itemId == 5376 || itemId == 5418;
	}

	private final int containerId;
	private final int containerSize;
	private final int fillId;
	private final int baseId;
	private final int fullId;

	ProduceContainer(int containerId, int containerSize, int fillId, int baseId, int fullId) {
		this.containerId = containerId;
		this.containerSize = containerSize;
		this.fillId = fillId;
		this.baseId = baseId;
		this.fullId = fullId;
	}

	public int getCountForItemId(int itemId) {
		if (itemId < baseId || itemId > fullId)
			return 0;
		return ((itemId - baseId) / 2) + 1;
	}

	public int getItemIdForCount(int count) {
		if (count >= 10)
			return fullId;
		if (count < 0)
			return baseId;
		return baseId + (count * 2) - 2;
	}

	public int getContainerId() {
		return containerId;
	}

	public int getContainerSize() {
		return containerSize;
	}

	public int getFillId() {
		return fillId;
	}

	public int getBaseId() {
		return baseId;
	}

	public int getFullId() {
		return fullId;
	}

}
