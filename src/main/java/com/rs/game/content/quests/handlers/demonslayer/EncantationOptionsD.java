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
package com.rs.game.content.quests.handlers.demonslayer;

import com.rs.game.content.dialogue.Conversation;
import com.rs.game.content.dialogue.Dialogue;
import com.rs.game.content.dialogue.HeadE;
import com.rs.game.content.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class EncantationOptionsD  extends Conversation {
	Player p;
	DelrithBoss boss;
	int chantCount;
	String[] chantOrder = {
			"Aber",
			"Gabindo",
			"Purchai",
			"Camerinthum",
			"Carlem"
	};

	public EncantationOptionsD(Player p, DelrithBoss boss) {
		super(p);
		this.p = p;
		this.boss = boss;
		chantCount = 0;
		addPlayer(HeadE.SKEPTICAL_THINKING, "Now what was that incantation again?");
		addNext(()-> {p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());});
	}

	public EncantationOptionsD(Player p, DelrithBoss boss, int chantCount, int convoID) {
		super(p);
		this.p = p;
		this.boss = boss;
		this.chantCount = chantCount;
		switch(convoID) {
		case 0:
			chant();
			break;
		default:
			break;
		}


	}

	private void chant() {
		addOptions("Choose an option:", new Options() {
			@Override
			public void create() {
				option("Carlem...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Carlem...", () -> {
							p.forceTalk("Carlem...");
							if(chantOrder[chantCount].contains("Carlem"))
								chantCount++;
							else {
								chantCount = 0;
								boss.forceTalk("Wrong! Ha ha!");
							}
						})
						.addNext(()->{
							if(chantCount == 5)
								boss.die();
							else
								p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
						}));
				option("Aber...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Aber...", () -> {
							p.forceTalk("Aber...");
							if(chantOrder[chantCount].contains("Aber"))
								chantCount++;
							else {
								chantCount = 0;
								boss.forceTalk("Wrong! Ha ha!");
							}
						})
						.addNext(()->{
							if(chantCount == 5)
								boss.die();
							else
								p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
						}));
				option("Camerinthum...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Camerinthum...", () -> {
							p.forceTalk("Camerinthum...");
							if(chantOrder[chantCount].contains("Camerinthum"))
								chantCount++;
							else {
								chantCount = 0;
								boss.forceTalk("Wrong! Ha ha!");
							}
						})
						.addNext(()->{
							if(chantCount == 5)
								boss.die();
							else
								p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
						}));
				option("Purchai...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Purchai...", () -> {
							p.forceTalk("Purchai...");
							if(chantOrder[chantCount].contains("Purchai"))
								chantCount++;
							else {
								chantCount = 0;
								boss.forceTalk("Wrong! Ha ha!");
							}
						})
						.addNext(()->{
							if(chantCount == 5)
								boss.die();
							else
								p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
						}));
				option("Gabindo...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Gabindo...", () -> {
							p.forceTalk("Gabindo...");
							if(chantOrder[chantCount].contains("Gabindo"))
								chantCount++;
							else {
								chantCount = 0;
								boss.forceTalk("Wrong! Ha ha!");
							}
						})
						.addNext(()->{
							if(chantCount == 5)
								boss.die();
							else
								p.startConversation(new EncantationOptionsD(p, boss, chantCount, 0).getStart());
						}));
			}
		});
	}
}
