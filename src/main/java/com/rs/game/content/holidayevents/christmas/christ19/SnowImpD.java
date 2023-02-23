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
package com.rs.game.content.holidayevents.christmas.christ19;

import com.rs.game.content.holidayevents.christmas.christ19.Christmas2019.Imp;
import com.rs.game.content.holidayevents.christmas.christ19.Christmas2019.Location;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.HeadE;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCClickHandler;

@PluginEventHandler
public class SnowImpD extends Conversation {

	private static final int IMP = 8536, IMP_HEAD = 9364;

	public static NPCClickHandler handleSnowImpTalk = new NPCClickHandler(new Object[] { IMP }, e -> {
		if (e.getPlayer().getPet() == e.getNPC())
			e.getPlayer().startConversation(new SnowImpD(e.getPlayer()));
		else
			e.getPlayer().startConversation(new Conversation(e.getPlayer()).addNPC(IMP_HEAD, HeadE.CONFUSED, "Whatchu want, guv?"));
	});

	public SnowImpD(Player player) {
		super(player);

		switch(player.getI(Christmas2019.STAGE_KEY)) {
		case 1:
			addPlayer(HeadE.CALM_TALK, "Hey, you're Rasmus right?");
			addNPC(IMP_HEAD, HeadE.CHEERFUL, "Yep, dats me. Heard ya need help findin summa dem rogue imps.");
			addPlayer(HeadE.UPSET, "Yeah, I do. They've stolen some items from the feast!");
			addNPC(IMP_HEAD, HeadE.UPSET, "Ya, it was sum potatoes, turkey, wine, and yule logs! Nasties.");
			addPlayer(HeadE.CHEERFUL, "Do you know where any of them went?");
			addNPC(IMP_HEAD, HeadE.CHEERFUL, "Aight, guv. Gimme a sec.");
			addSimple("The imp closes his eyes briefly and makes a grunting sound.");
			addNPC(IMP_HEAD, HeadE.LAUGH, "I've located da potatoes! Start walkin' and I'll let ya know if ya getting closer!");
			addPlayer(HeadE.CHEERFUL, "Alright, thanks!", () -> {
				player.save(Christmas2019.STAGE_KEY, 2);
				player.setChrist19Loc(Imp.POTATOES.randomLoc());
			});
			break;
		case 3:
			addPlayer(HeadE.CALM_TALK, "Hey Rasmus, where's the next location?");
			addNPC(IMP_HEAD, HeadE.CHEERFUL, "Aight, guv. Gimme a sec.");
			addSimple("The imp closes his eyes briefly and makes a grunting sound.");
			addNPC(IMP_HEAD, HeadE.LAUGH, "I've located da wine! Start walkin' and I'll let ya know if ya getting closer!");
			addPlayer(HeadE.CHEERFUL, "Alright, thanks!", () -> {
				player.save(Christmas2019.STAGE_KEY, 4);
				player.setChrist19Loc(Imp.WINE.randomLoc());
			});
			break;
		case 5:
			addPlayer(HeadE.CALM_TALK, "Hey Rasmus, where's the next location?");
			addNPC(IMP_HEAD, HeadE.CHEERFUL, "Aight, guv. Gimme a sec.");
			addSimple("The imp closes his eyes briefly and makes a grunting sound.");
			addNPC(IMP_HEAD, HeadE.LAUGH, "I've located da turkeys! Start walkin' and I'll let ya know if ya getting closer!");
			addPlayer(HeadE.CHEERFUL, "Alright, thanks!", () -> {
				player.save(Christmas2019.STAGE_KEY, 6);
				player.setChrist19Loc(Imp.TURKEY.randomLoc());
			});
			break;
		case 7:
			addPlayer(HeadE.CALM_TALK, "Hey Rasmus, where's the next location?");
			addNPC(IMP_HEAD, HeadE.CHEERFUL, "Aight, guv. Gimme a sec.");
			addSimple("The imp closes his eyes briefly and makes a grunting sound.");
			addNPC(IMP_HEAD, HeadE.LAUGH, "I've located da yule logs! Start walkin' and I'll let ya know if ya getting closer!");
			addPlayer(HeadE.CHEERFUL, "Alright, thanks!", () -> {
				player.save(Christmas2019.STAGE_KEY, 8);
				player.setChrist19Loc(Imp.YULE_LOG.randomLoc());
			});
			break;
		case 2:
		case 4:
		case 6:
		case 8:
			Location loc = player.getChrist19Loc();
			addPlayer(HeadE.CONFUSED, "Any hints as to where to go?");
			addNPC(IMP_HEAD, HeadE.CHEERFUL, loc.getHint());
			addPlayer(HeadE.CHEERFUL, "Thanks!");
			break;
		case 9:
			addPlayer(HeadE.CHEERFUL, "We found them all!");
			addNPC(IMP_HEAD, HeadE.CHEERFUL, "Yeah we did! Let's head back to Santa and let em know!");
			addPlayer(HeadE.CHEERFUL, "Alright, try to keep up!");
			break;
		case 10:
			addNPC(IMP_HEAD, HeadE.CHEERFUL, "'Ello, " + player.getDisplayName() + "!");
			addPlayer(HeadE.CHEERFUL, "Hello again Rasmus. Merry Christmas!");
			addNPC(IMP_HEAD, HeadE.LAUGH, "Merry Christmas " + player.getDisplayName() + ", thanks for bringing me to da feastie!");
			addPlayer(HeadE.CHEERFUL, "No problem! It would't be Christmas without Rasmus, would it?");
			addNPC(IMP_HEAD, HeadE.CHEERFUL, "Certainly not " + player.getDisplayName() + ".");
			break;
		}

		create();
	}

}
