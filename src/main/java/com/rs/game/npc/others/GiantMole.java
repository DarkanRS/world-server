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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.npc.others;

import com.rs.game.Entity;
import com.rs.game.Hit;
import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.player.Player;
import com.rs.game.tasks.WorldTask;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class GiantMole extends NPC {

	private static final WorldTile[] COORDS = { new WorldTile(1737, 5228, 0), new WorldTile(1751, 5233, 0), new WorldTile(1778, 5237, 0), new WorldTile(1736, 5227, 0), new WorldTile(1780, 5152, 0), new WorldTile(1758, 5162, 0),
			new WorldTile(1745, 5169, 0), new WorldTile(1760, 5183, 0) };

	public GiantMole(int id, WorldTile tile, boolean spawned) {
		super(id, tile, spawned);
	}

	@Override
	public void handlePostHit(Hit hit) {
		if (getHPPerc() > 5.0 && getHPPerc() < 50.0 && Utils.random(4) == 0)
			move(hit);
		super.handlePostHit(hit);
	}

	private double getHPPerc() {
		return ((double) getHitpoints() / (double) getMaxHitpoints()) * 100.0;
	}

	public void move(Hit hit) {
		setNextAnimation(new Animation(3314));
		setCantInteract(true);
		getCombat().removeTarget();
		Entity source = hit.getSource();
		final Player player = source == null ? null : (Player) (source instanceof Player ? source : null);
		if (player != null)
			player.getInterfaceManager().setOverlay(226);
		final WorldTile middle = getMiddleWorldTile();
		WorldTasks.schedule(new WorldTask() {
			@Override
			public void run() {
				if (player != null)
					player.getInterfaceManager().removeOverlay();
				setCantInteract(false);
				if (isDead())
					return;
				World.sendSpotAnim(GiantMole.this, new SpotAnim(572), middle);
				World.sendSpotAnim(GiantMole.this, new SpotAnim(571), new WorldTile(middle.getX(), middle.getY() - 1, middle.getPlane()));
				World.sendSpotAnim(GiantMole.this, new SpotAnim(571), new WorldTile(middle.getX(), middle.getY() + 1, middle.getPlane()));
				World.sendSpotAnim(GiantMole.this, new SpotAnim(571), new WorldTile(middle.getX() - 1, middle.getY() - 1, middle.getPlane()));
				World.sendSpotAnim(GiantMole.this, new SpotAnim(571), new WorldTile(middle.getX() - 1, middle.getY() + 1, middle.getPlane()));
				World.sendSpotAnim(GiantMole.this, new SpotAnim(571), new WorldTile(middle.getX() + 1, middle.getY() - 1, middle.getPlane()));
				World.sendSpotAnim(GiantMole.this, new SpotAnim(571), new WorldTile(middle.getX() + 1, middle.getY() + 1, middle.getPlane()));
				World.sendSpotAnim(GiantMole.this, new SpotAnim(571), new WorldTile(middle.getX() - 1, middle.getY(), middle.getPlane()));
				World.sendSpotAnim(GiantMole.this, new SpotAnim(571), new WorldTile(middle.getX() + 1, middle.getY(), middle.getPlane()));
				setNextWorldTile(new WorldTile(COORDS[Utils.random(COORDS.length)]));
				setNextAnimation(new Animation(3315));

			}
		}, 2);

	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(3340) {
		@Override
		public NPC getNPC(int npcId, WorldTile tile) {
			return new GiantMole(npcId, tile, false);
		}
	};
}
