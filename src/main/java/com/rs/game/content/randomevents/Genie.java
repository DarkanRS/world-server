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
package com.rs.game.content.randomevents;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Genie extends OwnedNPC {

	private int ticks = 0;
	private boolean claimed = false;

	public Genie(Player owner, Tile tile) {
		super(owner, 3022, tile, false);
		setRun(true);
		setNextFaceEntity(owner);
		setAutoDespawnAtDistance(false);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (getOwner().isDead() || !withinDistance(getOwner(), 16)) {
			finish();
			return;
		}
		entityFollow(getOwner(), false, 0);
		if (!claimed && (getOwner().getInterfaceManager().containsChatBoxInter() || getOwner().getInterfaceManager().containsScreenInter()))
			return;
		ticks++;
		if (ticks == 1) {
			setNextSpotAnim(new SpotAnim(1605));
			forceTalk("Hello " + getOwner().getDisplayName() + "!");
			setNextFaceEntity(getOwner());
		} else if (ticks == 30)
			forceTalk("A wish for " + getOwner().getDisplayName() + ".");
		else if (ticks == 60)
			forceTalk("I came from the desert you know...");
		else if (ticks == 90)
			forceTalk("Not just anyone gets a wish");
		else if (ticks == 120)
			forceTalk("Young " + (getOwner().getAppearance().isMale() ? "sir" : "madam") + " these things are quite rare.");
		else if (ticks == 149)
			forceTalk("So rude!");
		else if (ticks == 150) {
			setNextAnimation(new Animation(3045));
			final Player owner = getOwner();
			owner.lock();
			owner.setNextAnimation(new Animation(836));
			owner.stopAll();
			owner.fadeScreen(() -> {
				Tile tile = RandomEvents.getRandomTile();
				owner.getControllerManager().processMagicTeleport(tile);
				owner.setNextTile(tile);
				owner.setNextAnimation(new Animation(-1));
				owner.unlock();
			});
		} else if (ticks == 153) {
			setNextSpotAnim(new SpotAnim(1605));
			getOwner().setNextAnimation(new Animation(-1));
		} else if (ticks == 155)
			finish();
	}

	public static NPCClickHandler handleTalkTo = new NPCClickHandler(new Object[] { 3022 }, e -> {
		if (e.getNPC() instanceof Genie) {
			Genie npc = (Genie) e.getNPC();
			if (npc.ticks >= 149)
				return;
			if (npc.getOwner() != e.getPlayer()) {
				e.getPlayer().startConversation(new Conversation(new Dialogue()
						.addNPC(3022, HeadE.CALM_TALK, "This wish is for " + npc.getOwner().getDisplayName() + ", not you!")));
				return;
			}
			if (e.getPlayer().inCombat()) {
				if(e.getPlayer().getInventory().hasFreeSlots()) {
					e.getPlayer().sendMessage("The genie gives you a lamp!");
					e.getPlayer().getInventory().addItem(2528, 1);
					npc.forceTalk("Hope that satisfies you!");
					npc.claimed = true;
				} else {
					e.getPlayer().sendMessage("Your inventory is too full for a lamp!");
					npc.claimed = true;
				}
				npc.ticks = 152;
				return;
			}
			e.getPlayer().startConversation(new Conversation(e.getPlayer())
					.addNPC(3022, HeadE.HAPPY_TALKING, "Ah, so you are there master. I'm so glad you summoned me. Please take this lamp and make your with!")
					.addOptions(new Options() {
						@Override
						public void create() {
							option("Take the lamp", () -> {
								if(e.getPlayer().getInventory().hasFreeSlots()) {
									e.getPlayer().sendMessage("The genie gives you a lamp!");
									e.getPlayer().getInventory().addItem(2528, 1);
									npc.forceTalk("I hope you're happy with your wish.");
									npc.claimed = true;
								} else {
									e.getPlayer().sendMessage("Your inventory is too full for a lamp!");
									npc.claimed = true;
								}
								npc.ticks = 152;
							});
							option("Don't take it", () -> {
								npc.claimed = true;
								npc.ticks = 152;
							});
						}
					}));
		}
	});
}
