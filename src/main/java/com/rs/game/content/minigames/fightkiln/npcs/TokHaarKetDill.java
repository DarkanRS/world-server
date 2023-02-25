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
package com.rs.game.content.minigames.fightkiln.npcs;

import com.rs.game.content.minigames.fightkiln.FightKilnController;
import com.rs.game.model.entity.Entity;
import com.rs.game.model.entity.Hit;
import com.rs.game.model.entity.Hit.HitLook;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

public class TokHaarKetDill extends FightKilnNPC {

	private int receivedHits;

	public TokHaarKetDill(int id, Tile tile, FightKilnController controller) {
		super(id, tile, controller);
	}

	@Override
	public void handlePreHit(final Hit hit) {
		if (hit.getLook() != HitLook.MELEE_DAMAGE && hit.getLook() != HitLook.RANGE_DAMAGE && hit.getLook() != HitLook.MAGIC_DAMAGE)
			return;
		if (receivedHits != -1) {
			Entity source = hit.getSource();
			if (source == null || !(source instanceof Player))
				return;
			hit.setDamage(0);
			Player playerSource = (Player) source;
			int weaponId = playerSource.getEquipment().getWeaponId();
			if (weaponId == 1275 || weaponId == 13661 || weaponId == 15259) {
				receivedHits++;
				if ((weaponId == 1275 && receivedHits >= 5) || ((weaponId == 13661 || weaponId == 15259) && receivedHits >= 3)) {
					receivedHits = -1;
					transformIntoNPC(getId() + 1);
					playerSource.sendMessage("Your pickaxe breaks the TokHaar-Ket-Dill's thick armour!");
				} else
					playerSource.sendMessage("Your pickaxe slowy  cracks its way through the TokHaar-Ket-Dill's armour.");
			}
		}

	}

}
