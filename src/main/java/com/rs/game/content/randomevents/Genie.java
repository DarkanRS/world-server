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
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class Genie extends OwnedNPC {

	private int tickCounter = 0;
	private boolean isClaimed = false;
	private static final int
			GENIE_ID = 3022,
	WAVE_ANIM_ID = 863,
	PUFF_SMOKE_ANIM_ID = 1605,
	GENIE_HIT_ANIM_ID = 3045,
	PLAYER_DIE_ANIM_ID = 836,
	XP_LAMP_ITEM_ID = 2528;

	public Genie(Player owner, Tile tile) {
		super(owner, GENIE_ID, tile, false);
		setRun(true);
		setNextFaceEntity(owner);
		setAutoDespawnAtDistance(false);
	}

	@Override
	public void processNPC() {
		super.processNPC();
		if (isOwnerDeadOrOutOfRange()) {
			finish();
			return;
		}
		entityFollow(getOwner(), false, 0);
		if (!isClaimed && (getOwner().getInterfaceManager().containsChatBoxInter() || getOwner().getInterfaceManager().containsScreenInter()))
			return;
		tickCounter++;
		handleGenieActions();
	}

	private boolean isOwnerDeadOrOutOfRange() {
		return getOwner().isDead() || !withinDistance(getOwner(), 16);
	}

	private void handleGenieActions() {
		switch (tickCounter) {
			case 1:
			startGenieIntroduction();
			break;
			case 30:
			case 60:
			case 90:
			case 120:
			case 150:
			continueGenieDialogue();
			break;
			case 189:
			forceTalk("So rude!");
			break;
			case 190:
			handlePlayerIgnore();
			break;
			case 194:
			if (isClaimed) setNextAnimation(new Animation(WAVE_ANIM_ID));
			break;
			case 196:
			spotAnim(PUFF_SMOKE_ANIM_ID);
			getOwner().setNextAnimation(new Animation(-1));
			break;
			case 197:
			finish();
			break;
		}
	}

	private void startGenieIntroduction() {
		setNextSpotAnim(new SpotAnim(PUFF_SMOKE_ANIM_ID));
		setNextAnimation(new Animation(WAVE_ANIM_ID));
		forceTalk("Hello, Master " + getOwner().getDisplayName() + "!");
		setNextFaceEntity(getOwner());
	}

	private void continueGenieDialogue() {
		setNextAnimation(new Animation(WAVE_ANIM_ID));
		forceTalk(getGenieDialogue());
	}

	private String getGenieDialogue() {
		String[] dialogues = {"A wish for ", "I came from the desert, you know, ", "Not just anyone gets a wish, ", "Young " + (getOwner().getAppearance().isMale() ? "Sir" : "Madam") + ", these things are quite rare!", "Last chance, "};
		int index = tickCounter / 30 - 1;
		if (index < dialogues.length - 2) {
			return dialogues[index] + getOwner().getDisplayName() + ".";
		} else if (index == dialogues.length - 2) {
			return dialogues[index];
		} else {
			return dialogues[index] + getOwner().getDisplayName() + ".";
		}
	}



	private void handlePlayerIgnore() {
		final Player owner = getOwner();
		if (!owner.inCombat()) {
			setNextAnimation(new Animation(GENIE_HIT_ANIM_ID));
			owner.lock();
			owner.setNextAnimation(new Animation(PLAYER_DIE_ANIM_ID));
			owner.stopAll();
			owner.fadeScreen(() -> {
				spotAnim(PUFF_SMOKE_ANIM_ID);
				owner.tele(RandomEvents.getRandomTile());
				owner.setNextAnimation(new Animation(-1));
				owner.unlock();
			});
		}
	}

	public static NPCClickHandler handleTalkTo = new NPCClickHandler(new Object[]{GENIE_ID}, e -> {
		if (e.getNPC() instanceof Genie npc) {
			if (npc.tickCounter >= 189)
				return;
			if (npc.getOwner() != e.getPlayer()) {
				e.getPlayer().startConversation(new Conversation(new Dialogue()
					.addNPC(GENIE_ID, HeadE.CALM_TALK, "This wish is for " + npc.getOwner().getDisplayName() + ", not you!")));
				return;
			}
			handleGenieInteraction(e, npc);
		}
	});

	private static void handleGenieInteraction(NPCClickEvent e, Genie npc) {
		if (e.getPlayer().inCombat()) {
			handleCombatInteraction(e, npc);
		} else {
			handleNormalInteraction(e, npc);
		}
	}

	private static void handleCombatInteraction(NPCClickEvent e, Genie npc) {
		Player player = e.getPlayer();
		player.sendMessage("The genie gives you a lamp!");
		player.getInventory().addItemDrop(XP_LAMP_ITEM_ID, 1);
		if (!player.getInventory().hasFreeSlots()) {
			player.sendMessage("The lamp has been placed on the ground.");
		}
		npc.forceTalk("Hope that satisfies you!");
		npc.isClaimed = true;
		npc.tickCounter = 191;
	}

	private static void handleNormalInteraction(NPCClickEvent e, Genie npc) {
		Conversation conversation = new Conversation(new Dialogue());

		if (!npc.isClaimed) {
			conversation.addNPC(GENIE_ID, HeadE.HAPPY_TALKING, "Ah, so you are there master. I'm so glad you summoned me. Please take this lamp and make your wish!");
			e.getPlayer().getInventory().addItemDrop(XP_LAMP_ITEM_ID, 1);
			if (!e.getPlayer().getInventory().hasFreeSlots()) {
				e.getPlayer().sendMessage("The lamp has been placed on the ground.");
			}
			npc.isClaimed = true;
			npc.tickCounter = 191;
		}

		e.getPlayer().startConversation(conversation);
	}

}