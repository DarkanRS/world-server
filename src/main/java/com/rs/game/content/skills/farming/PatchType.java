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
package com.rs.game.content.skills.farming;

public enum PatchType {
	ALLOTMENT(2) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			if (patch.location == PatchLocation.Burthorpe_potato_patch)
				switch(patch.growthStage) {
				case 0:
					return 6;
				case 1:
				case 2:
				case 3:
					return 5;
				default:
					return 7;
				}
			int value = patch.seed.varBitPlanted + patch.growthStage;
			if (patch.dead)
				value |= 192;
			else if (patch.diseased)
				value |= 128;
			else if (patch.watered)
				value |= 64;
			return value;
		}
	},
	FLOWER(1) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int value = patch.seed.varBitPlanted + patch.growthStage;
			if (patch.dead)
				value |= 192;
			else if (patch.diseased)
				value |= 128;
			else if (patch.watered)
				value |= 64;
			return value;
		}
	},
	HERB(4) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int value = patch.growthStage + (patch.seed == ProduceType.Wergali ? 60 : 4);
			if (patch.dead)
				value = patch.growthStage + 169;
			else if (patch.diseased)
				value = patch.growthStage + 127;
			return value;
		}
	},
	HOP(2) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int value = patch.seed.varBitPlanted + patch.growthStage;
			if (patch.dead)
				value |= 192;
			else if (patch.diseased)
				value |= 128;
			else if (patch.watered)
				value |= 64;
			return value;
		}
	},
	TREE(8) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int baseValue = patch.growthStage + patch.seed.varBitPlanted;
			if (patch.checkedHealth) {
				if (patch.lives == -1)
					return baseValue + 2;
				return baseValue + 1;
			}
			if (patch.dead)
				return baseValue + 128;
			if (patch.diseased)
				return baseValue + 64;
			return baseValue;
		}
	},
	FRUIT_TREE(32) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int baseValue = patch.growthStage + patch.seed.varBitPlanted;

			if (!patch.checkedHealth && patch.fullyGrown())
				return patch.seed.varBitPlanted + 26;
			if (patch.lives == -1)
				return patch.seed.varBitPlanted + 25;

			if (patch.dead)
				baseValue += 18;
			else if (patch.diseased)
				baseValue += 12;
			return baseValue += patch.lives;
		}
	},
	BUSH(4) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int baseValue = patch.growthStage + patch.seed.varBitPlanted;
			switch(patch.seed) {
			case Redberry:
				if (patch.checkedHealth)
					return patch.lives + baseValue;
				if (patch.dead)
					return 129 + baseValue;
				if (patch.diseased)
					return 65 + baseValue;
				if (!patch.checkedHealth && patch.fullyGrown())
					return (patch.seed.ordinal() - ProduceType.Redberry.ordinal()) + 250;
				return baseValue;
			case Cadavaberry:
				if (patch.checkedHealth)
					return patch.lives + baseValue;
				if (patch.dead)
					return 129 + baseValue;
				if (patch.diseased)
					return 65 + baseValue;
				if (!patch.checkedHealth && patch.fullyGrown())
					return (patch.seed.ordinal() - ProduceType.Redberry.ordinal()) + 250;
				return baseValue;
			case Dwellberry:
				if (patch.checkedHealth)
					return patch.lives + baseValue;
				if (patch.dead)
					return 128 + baseValue;
				if (patch.diseased)
					return 64 + baseValue;
				if (!patch.checkedHealth && patch.fullyGrown())
					return (patch.seed.ordinal() - ProduceType.Redberry.ordinal()) + 250;
				return baseValue;
			case Jangerberry:
				if (patch.checkedHealth)
					return patch.lives + baseValue;
				if (patch.dead)
					return 128 + baseValue;
				if (patch.diseased)
					return 64 + baseValue;
				if (!patch.checkedHealth && patch.fullyGrown())
					return (patch.seed.ordinal() - ProduceType.Redberry.ordinal()) + 250;
				return baseValue;
			case Whiteberry:
				if (patch.checkedHealth)
					return patch.lives + baseValue;
				if (patch.dead)
					return 129 + baseValue;
				if (patch.diseased)
					return 64 + baseValue;
				if (!patch.checkedHealth && patch.fullyGrown())
					return (patch.seed.ordinal() - ProduceType.Redberry.ordinal()) + 250;
				return baseValue;
			case Poison_ivy:
				if (patch.checkedHealth)
					return patch.lives+baseValue;
				if (patch.dead)
					return 20 + baseValue;
				if (patch.diseased)
					return 12 + baseValue;
				if (!patch.checkedHealth && patch.fullyGrown())
					return (patch.seed.ordinal() - ProduceType.Redberry.ordinal()) + 250;
				return baseValue;
			default:
				return baseValue;
			}
		}
	},
	CACTUS(16) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int baseValue = patch.growthStage + patch.seed.varBitPlanted;
			if (patch.checkedHealth)
				baseValue += patch.lives;
			else if (patch.dead)
				return patch.growthStage + 24;
			else if (patch.diseased)
				return patch.growthStage + 18;
			else if (!patch.checkedHealth && patch.fullyGrown())
				return 31;
			return baseValue;
		}
	},
	EVIL_TURNIP(1) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			return patch.seed.varBitPlanted + patch.growthStage;
		}
	},
	MUSHROOM(8) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			if (patch.fullyGrown())
				return patch.seed.varBitPlanted + patch.growthStage + (patch.seed.productId.getAmount() - patch.lives);
			switch(patch.seed) {
			case Bittercap:
				if (patch.dead)
					return patch.seed.varBitPlanted + patch.growthStage + 16;
				if (patch.diseased)
					return patch.seed.varBitPlanted + patch.growthStage + 11;
				return patch.seed.varBitPlanted + patch.growthStage;
			case Morchella:
				if (patch.dead)
					return patch.seed.varBitPlanted + patch.growthStage + 19;
				if (patch.diseased)
					return patch.seed.varBitPlanted + patch.growthStage + 14;
				return patch.seed.varBitPlanted + patch.growthStage;
			default:
				return patch.seed.varBitPlanted + patch.growthStage;
			}
		}
	},
	BELLADONNA(8) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int baseValue = patch.growthStage + patch.seed.varBitPlanted;
			if (patch.dead)
				return baseValue + 7;
			if (patch.diseased)
				return baseValue + 4;
			return baseValue;
		}
	},
	VINE_FLOWER(1) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			switch(patch.seed) {
			case Red_blossom:
				if (patch.dead)
					return 39 + patch.growthStage;
				if (patch.diseased)
					return 30 + patch.growthStage;
				if (patch.watered)
					return 19 + patch.growthStage;
				return patch.growthStage + patch.seed.varBitPlanted;
			case Blue_blossom:
				if (patch.dead)
					return 43 + patch.growthStage;
				if (patch.diseased)
					return 33 + patch.growthStage;
				if (patch.watered)
					return 23 + patch.growthStage;
				return patch.growthStage + patch.seed.varBitPlanted;
			case Green_blossom:
				if (patch.dead)
					return 47 + patch.growthStage;
				if (patch.diseased)
					return 36 + patch.growthStage;
				if (patch.watered)
					return 27 + patch.growthStage;
				return patch.growthStage + patch.seed.varBitPlanted;
			default:
				return patch.growthStage + patch.seed.varBitPlanted;
			}
		}
	},
	VINE_HERB(4) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			if (patch.dead)
				return patch.growthStage + 43;
			if (patch.diseased)
				return patch.growthStage + 31;
			return patch.growthStage + patch.seed.varBitPlanted;
		}
	},
	VINE_BUSH(4) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int baseValue = patch.growthStage + patch.seed.varBitPlanted;
			switch(patch.seed) {
			case Lergberry:
				if (patch.fullyGrown())
					return patch.checkedHealth ? (patch.seed.varBitPlanted + patch.growthStage + patch.lives) : 50;
				if (patch.dead)
					return baseValue + 33;
				if (patch.diseased)
					return baseValue + 21;
				return baseValue;
			case Kalferberry:
				if (patch.fullyGrown())
					return patch.checkedHealth ? (patch.seed.varBitPlanted + patch.growthStage + patch.lives) : 51;
				if (patch.dead)
					return baseValue + 28;
				if (patch.diseased)
					return baseValue + 16;
				return baseValue;
			default:
				return baseValue;
			}
		}
	},
	CALQUAT(16) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int baseValue = patch.growthStage + patch.seed.varBitPlanted;
			if (!patch.checkedHealth && patch.fullyGrown())
				return 34;
			if (patch.fullyGrown())
				return baseValue + patch.lives;
			if (patch.dead)
				return baseValue + 21;
			if (patch.diseased)
				return baseValue + 14;
			return baseValue;
		}
	},
	SPIRIT(64) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			int baseValue = patch.growthStage + patch.seed.varBitPlanted;
			if (patch.dead)
				return baseValue + 24;
			if (patch.diseased)
				return baseValue + 12;
			return baseValue;
		}
	},
	COMPOST(9) {
		@Override
		int getVarBitValue(FarmPatch patch) {
			if (patch.seed == null) {
				if (patch.lives == 0)
					return 0;
				if (patch.lives <= -15)
					return 15;
				return 5;
			}
			switch(patch.seed) {
			case Compost:
			case Supercompost:
				if (patch.lives == 0 || !patch.checkedHealth)
					return 31;
				else if (patch.lives >= 7)
					return patch.seed == ProduceType.Compost ? 30 : 62;
				else
					return patch.seed == ProduceType.Compost ? 29 : 61;
			default:
				return 0;
			}
		}
	};

	private int growthTicksPerStage;
	abstract int getVarBitValue(FarmPatch patch);

	private PatchType(int growthTicksPerStage) {
		this.growthTicksPerStage = growthTicksPerStage;
	}

	public int getGrowthTicksPerStage() {
		return growthTicksPerStage;
	}

	public int getValue(FarmPatch patch) {
		if (patch.location.type == PatchType.COMPOST)
			return getVarBitValue(patch);
		if (patch.weeds > 0)
			return 3 - patch.weeds;
		if (patch.seed == null)
			return 3;
		return getVarBitValue(patch);
	}
}
