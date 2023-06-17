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
package com.rs.game.content.tutorialisland;

import com.rs.Settings;
import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.statements.NPCStatement;
import com.rs.game.content.tutorialisland.TutorialIslandController.Stage;
import com.rs.game.model.entity.npc.NPC;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.NPCInstanceHandler;

@PluginEventHandler
public class RuneScapeGuide extends Conversation {

	public RuneScapeGuide(Player player, NPC npc, TutorialIslandController ctrl) {
		super(player);
		npc.faceEntity(player);
		npc.resetWalkSteps();

		create("Intro", new Dialogue(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Greetings! I see you are a new arrival to this land. My", "job is to welcome all new visitors. So welcome!")))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "You have already learned the first thing needed to", "succeed in this world: talking to other people!"))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "You will find many inhabitants of this world have useful", "things to say to you. By clicking on them with your", "mouse you can talk to them."))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "I would also suggest reading through some of the", "supporting information on the website. There you can", "find the Knowledge Base, which contains all the", "additional information you're ever likely to need. It also"))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "contains maps and helpful tips to help you on your", "journey."))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "You will notice a flashing icon of a spanner; please click", "on this to continue the tutorial."))
		.addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.OPEN_SETTINGS)));

		create("Outro", new Dialogue(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "I'm glad you're making progress!")))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "To continue the tutorial go through that door over", "there and speak to your first instructor!"))
		.addNext(new Dialogue().setFunc(() -> ctrl.nextStage(Stage.LEAVE_GUIDE_ROOM)));

		create("Recap", new Dialogue(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "Welcome back.")))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "You have already learned the first thing needed to", "succeed in this world: talking to other people!"))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "You will find many inhabitants of this world have useful", "things to say to you. By clicking on them with your", "mouse you can talk to them."))
		.addNext(new NPCStatement(npc.getId(), HeadE.CHEERFUL, "To continue the tutorial go through that door over", "there and speak to your first instructor!"));

		if (ctrl.inSection(Stage.TALK_TO_GUIDE, Stage.TALK_TO_GUIDE_2))
			create("Intro");
		else if (ctrl.inSection(Stage.TALK_TO_GUIDE_2, Stage.LEAVE_GUIDE_ROOM))
			create("Outro");
		else
			create("Recap");
	}

	public static NPCInstanceHandler toFunc = new NPCInstanceHandler(new Object[] { 945 }, (npcId, tile) -> {
		NPC n = new NPC(npcId, tile);
		n.setPermName(Settings.getConfig().getServerName()+" Guide");
		return n;
	});
}