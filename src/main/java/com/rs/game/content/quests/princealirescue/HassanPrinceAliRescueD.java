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
package com.rs.game.content.quests.princealirescue;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.engine.quest.Quest;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class HassanPrinceAliRescueD extends Conversation {
	private final static int HASSAN = 923;

	public HassanPrinceAliRescueD(Player player) {
		super(player);
		addNPC(HASSAN, HeadE.HAPPY_TALKING, "Greetings I am Hassan, Chancellor to the Emir of Al- Kharid.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(player.getQuestManager().getStage(Quest.PRINCE_ALI_RESCUE) == PrinceAliRescue.NOT_STARTED)
					option("Can I help you? You must need some help here in the desert.", new Dialogue()
							.addNPC(HASSAN, HeadE.TALKING_ALOT, "I need the services of someone, yes. If you are interested, see the spymaster, Osman. I manage " +
									"the finances here. Come to me when you need payment.")
							.addOptions("Start Prince Ali To The Rescue?", new Options() {
								@Override
								public void create() {
									option("Yes.", new Dialogue()
											.addNPC(HASSAN, HeadE.HAPPY_TALKING, "Speak to Osman outside the palace, he will give you more details...", ()->{
												player.getQuestManager().setStage(Quest.PRINCE_ALI_RESCUE, PrinceAliRescue.STARTED);
											})
											.addPlayer(HeadE.HAPPY_TALKING, "Okay..."));
									option("No.", new Dialogue());
								}
							}));
				option("It's just too hot here. How can you stand it?", new Dialogue()
						.addNPC(HASSAN, HeadE.HAPPY_TALKING, "We manage, in our humble way. We are a wealthy town and we have water. It cures many thirsts."));
				option("Do you mind if I just kill your warriors?", new Dialogue()
						.addNPC(HASSAN, HeadE.HAPPY_TALKING, "You are welcome. They are not expensive. We have them here to stop the elite guard being bothered. " +
								"They are a little harder to kill."));
			}
		});


	}

	public static NPCClickHandler handleHassan = new NPCClickHandler(new Object[] { HASSAN }, e -> e.getPlayer().startConversation(new HassanPrinceAliRescueD(e.getPlayer()).getStart()));
}

