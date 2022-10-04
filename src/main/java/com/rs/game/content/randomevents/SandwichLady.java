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

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.utils.Ticks;

@PluginEventHandler
public class SandwichLady extends OwnedNPC {

	private static final int DURATION = Ticks.fromMinutes(10);
	private int ticks = 0;
	private boolean claimed = false;

	public SandwichLady(Player owner, WorldTile tile) {
		super(owner, 8629, tile, false);
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
			forceTalk("Sandwich delivery for " + getOwner().getDisplayName() + "!");
			setNextFaceEntity(getOwner());
		} else if (ticks == DURATION-1)
			forceTalk("Let's see how you like this!");
		else if (ticks == DURATION) {
			setNextAnimation(new Animation(3045));
			final Player owner = getOwner();
			owner.lock();
			owner.setNextAnimation(new Animation(836));
			owner.stopAll();
			owner.fadeScreen(() -> {
				WorldTile tile = RandomEvents.getRandomTile();
				owner.getControllerManager().processMagicTeleport(tile);
				owner.setNextWorldTile(tile);
				owner.setNextAnimation(new Animation(-1));
				owner.unlock();
			});
		} else if (ticks == DURATION+3) {
			setNextSpotAnim(new SpotAnim(1605));
			getOwner().setNextAnimation(new Animation(-1));
		} else if (ticks == DURATION+5)
			finish();
		else if (ticks % 30 == 0)
			forceTalk(randomQuote(getOwner()));
	}
	
	private static final String randomQuote(Player player) {
		return switch(Utils.randomInclusive(0, 8)) {
		case 0 -> "All types of sandwiches, " + player.getDisplayName() + ".";
		case 1 -> "Come on " + player.getDisplayName() + ", I made these specifically!!";
		case 2 -> "You better start showing some manners young " + (player.getAppearance().isMale() ? "man" : "lady") + "!!";
		default -> "You think I made these just for fun?!!?";
		};
	}

	public static NPCClickHandler handleTalkTo = new NPCClickHandler(new Object[] { 8629 }) {
		@Override
		public void handle(NPCClickEvent e) {
			if (e.getNPC() instanceof SandwichLady) {
				SandwichLady npc = (SandwichLady) e.getNPC();
				if (npc.ticks >= DURATION)
					return;
				if (npc.getOwner() != e.getPlayer()) {
					e.getPlayer().startConversation(new Conversation(new Dialogue()
							.addNPC(8629, HeadE.CALM_TALK, "This is for " + npc.getOwner().getDisplayName() + ", not you!")));
					return;
				}
				if (e.getPlayer().inCombat()) {
					e.getPlayer().sendMessage("The sandwich lady gives you a chocolate bar!");
					e.getPlayer().getInventory().addItemDrop(1973, 1);
					npc.forceTalk("Hope that fills you up!");
					npc.ticks = DURATION+4;
					return;
				}
				e.getPlayer().startConversation(new Conversation(e.getPlayer())
						.addNPC(8629, HeadE.HAPPY_TALKING, "You look hungry to me. I tell you what - have a chocolate bar on me.")
						.addNext(() -> {
							e.getPlayer().getTempAttribs().setO("sandwichLady", e.getNPC());
							e.getPlayer().getInterfaceManager().sendInterface(297);
						}));
			}
		}
	};

	public static ButtonClickHandler handleSandwichInterface = new ButtonClickHandler(297) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 10 && e.getComponentId() <= 22) {
				SandwichLady lady = e.getPlayer().getTempAttribs().getO("sandwichLady");
				e.getPlayer().closeInterfaces();
				if (lady == null) {
					e.getPlayer().sendMessage("An error has ocurred.");
					return;
				}
				if (e.getComponentId() == 22) {
					e.getPlayer().sendMessage("The sandwich lady gives you a chocolate bar!");
					e.getPlayer().getInventory().addItemDrop(1973, 1);
					lady.forceTalk("Hope that fills you up!");
					lady.ticks = DURATION+4;
				} else {
					e.getPlayer().sendMessage("The sandwich lady knocks you out and you wake up somewhere.. different.");
					lady.forceTalk("Hey, I didn't say you could have that!");
					lady.ticks = DURATION-1;
				}
				lady.claimed = true;
			}
		}
	};
}
