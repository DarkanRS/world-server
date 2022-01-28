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
package com.rs.game.player.quests.handlers.princealirescue;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class HassanPrinceAliRescueD extends Conversation {
	Player p;
	public final static int HASSAN = 923;

	public HassanPrinceAliRescueD(Player p) {
		super(p);
		this.p = p;
		addNPC(HASSAN, HeadE.HAPPY_TALKING, "Greetings I am Hassan, Chancellor to the Emir of Al- Kharid.");
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				if(p.getQuestManager().getStage(Quest.PRINCE_ALI_RESCUE) == PrinceAliRescue.NOT_STARTED)
					option("Can I help you? You must need some help here in the desert.", new Dialogue()
							.addNPC(HASSAN, HeadE.TALKING_ALOT, "I need the services of someone, yes. If you are interested, see the spymaster, Osman. I manage " +
									"the finances here. Come to me when you need payment.")
							.addOptions("Start Prince Ali To The Rescue?", new Options() {
								@Override
								public void create() {
									option("Yes.", new Dialogue()
											.addNext(()->{p.getQuestManager().setStage(Quest.PRINCE_ALI_RESCUE, PrinceAliRescue.STARTED);}));
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

	public static NPCClickHandler handleHassan = new NPCClickHandler(HASSAN) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new HassanPrinceAliRescueD(e.getPlayer()).getStart());
		}
	};
}

