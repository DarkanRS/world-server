package com.rs.game.content.skills.farming;

import java.util.HashMap;

public enum ProduceSack {
	POTATO(1942, 5420, 5438),
	ONION(1957, 5440, 5458),
	CABBAGE(1965, 5460, 5478)
	;

	public static final int EMPTY_SACK = 5418;

	private final static HashMap<Integer, ProduceSack> BY_FILL_ID = new HashMap<>();
	private final static int[] FILL_IDS_BACKWARDS = new int[ProduceSack.values().length];

	static {
		for (ProduceSack sack : values())
			BY_FILL_ID.put(sack.fillId, sack);
		for (int i=values().length-1; i>0; i--)
			FILL_IDS_BACKWARDS[i] = values()[i].fillId;
	}

	public static int[] getReverseFillIds() {
		return FILL_IDS_BACKWARDS;
	}

	public static ProduceSack forFillId(int fillId) {
		return BY_FILL_ID.get(fillId);
	}

	public static ProduceSack forSackId(int sackId) {
		for (ProduceSack sack : values())
			if (sackId >= sack.baseId && sackId <= sack.fullId)
				return sack;
		return null;
	}

	private final int fillId;
	private final int baseId;
	private final int fullId;

	ProduceSack(int fillId, int baseId, int fullId) {
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