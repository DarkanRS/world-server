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
package com.rs.game.content.skills.woodcutting;

import com.rs.game.model.entity.player.Player;
import com.rs.lib.Constants;
import com.rs.lib.game.Animation;

/*
 * 1351 - {-423442303=[6744], -1127713268=[17091], -812799196=[879], 212908826=[4077], 1728453613=[11594], -1191283983=[12282], -2008713161=[12329], -1226375116=[3324], -219949008=[15832], 1068619588=[9999], 249243873=[12345], 1917942796=[12322], -269793388=[10071]}
 * 1349 - {-2008713161=[12330], -1226375116=[3323], -423442303=[6743], -1127713268=[17090], 1068619588=[9998], -812799196=[877], 212908826=[4036], 249243873=[12344], 1728453613=[11600], 1917942796=[2847], -269793388=[10072]}
 * 1353 - {-2008713161=[12331], -1226375116=[3292], -423442303=[6742], -1127713268=[17089], 1068619588=[9997], -812799196=[875], 212908826=[4031], 249243873=[12343], 1728453613=[11599], 1917942796=[880], -269793388=[10073]}
 * 1361 - {-2008713161=[12332], -1226375116=[3284], -423442303=[6741], 2136703583=[6364], -1127713268=[17088], 1068619588=[9996], -812799196=[873], 1728453613=[11598], 1917942796=[878], -269793388=[10074]}
 * 1355 - {-2008713161=[12333], -1226375116=[3263], -954432337=[5366], -423442303=[6740], -1127713268=[17087], 1068619588=[9995], -812799196=[871], 212908826=[4017], 249243873=[12342], 1728453613=[11597], 1917942796=[876], -269793388=[10075]}
 * 1357 - {-2008713161=[12334], -1226375116=[3262], -954432337=[5367], -423442303=[6739], -1127713268=[17086], 1068619588=[9994], -812799196=[869], 212908826=[4012], 249243873=[12341], 1728453613=[11596], 1917942796=[874], -269793388=[10076]}
 * 1359 - {-423442303=[6738], -1127713268=[17085], -812799196=[867], 212908826=[4011], 1728453613=[11595], -2008713161=[12335], -1226375116=[3261], -954432337=[5368], 1068619588=[9993], -1652546089=[5781], 249243873=[12340], 1917942796=[872], -269793388=[10077]}
 * 6739 - {-2008713161=[12336], -1226375116=[3260], -954432337=[5369], -423442303=[6745], -1127713268=[17092], 1068619588=[9992], -812799196=[2846], 212908826=[4008], 249243873=[12346], 1728453613=[11601], -1992899638=[870], -269793388=[10078]}
 * 13661 - {-512925811=[10348], -423442303=[10249], -466056516=[10226], 191151360=[10227], -1127713268=[17093], -1610585784=[10251], 212908826=[4402], 1728453613=[11604], -1153750998=[14366], -2008713161=[12337], -1226375116=[3325], -954432337=[10247], 1312980631=[10224, 10228, 10222], -912685908=[10225], 1068619588=[10250], 249243873=[12347], 2043016414=[16002], -1935819679=[15250], -1636805267=[10341], 1917942796=[12323], -1204678996=[10223], -269793388=[7383]}
 */
public enum Hatchet {
	BRONZE(1351, 1, 0.5, 		12329, 6744, 3324, 17091, 9999, 879, 4077, 12345, 11594, 12322, 10071),
	IRON(1349, 1, 0.6, 			12330, 6743, 3323, 17090, 9998, 877, 4036, 12344, 11600, 2847, 10072),
	STEEL(1353, 6, 0.7, 		12331, 6742, 3292, 17089, 9997, 875, 4031, 12343, 11599, 880, 10073),
	BLACK(1361, 6, 0.7, 		12332, 6741, 3284, 17088, 9996, 873, -1, -1, 11598, 878, 10074),
	MITHRIL(1355, 21, 0.8, 		12333, 6740, 3263, 17087, 9995, 871, 4017, 12342, 11597, 876, 10075),
	ADAMANT(1357, 31, 1.0, 		12334, 6739, 3262, 17086, 9994, 869, 4012, 12341, 11596, 874, 10076),
	RUNE(1359, 41, 1.2, 		12335, 6738, 3261, 17085, 9993, 867, 4011, 12340, 11595, 872, 10077),
	DRAGON(6739, 61, 1.3, 		12336, 6745, 3260, 17092, 9992, 2846, 4008, 12346, 11601, 870, 10078),
	INFERNO(13661, 61, 1.3, 	12337, 10249, 3325, 17093, 10250, 10251, 4402, 12347, 11604, 12323, 7383);

	private int itemId, useLevel;
	private int[] animations;
	private double toolMod;

	private Hatchet(int itemId, int useLevel, double toolMod, int... animations) {
		this.itemId = itemId;
		this.useLevel = useLevel;
		this.toolMod = toolMod;
		this.animations = animations;
	}
	
	/*
	 * 0: OneHandSwingDown
	 * 1: ChopCanoe
	 * 2: IvyFurtherInWall
	 * 3: HighQualityChop
	 * 4: OneSingleOldChop
	 * 5: NormalTree
	 * 6: OneLargeDownwardSwing
	 * 7: ShortCanoeChop
	 * 8: HitGetStuckClearSurface
	 * 9: IvyCutDown
	 * 10: ChopShakeHeadCraft
	 */
	public Animation animNormal() {
		return new Animation(animations[5]);
	}
	
	public Animation animIvy() {
		return new Animation(animations[9]);
	}

	public Animation getAnim(TreeType type) {
		return switch(type) {
		case IVY -> animIvy();
		default -> animNormal();
		};
	}
	
	public int getItemId() {
		return itemId;
	}

	public int getUseLevel() {
		return useLevel;
	}

	public double getToolMod() {
		return toolMod;
	}

	public static Hatchet getBest(Player player) {
		for (int i = Hatchet.values().length-1; i >= 0; i--) {
			Hatchet def = Hatchet.values()[i];
			if (player.getInventory().containsItem(def.itemId) || player.getEquipment().getWeaponId() == def.itemId)
				if (player.getSkills().getLevel(Constants.WOODCUTTING) >= def.useLevel)
					return def;
		}
		return null;
	}
}
