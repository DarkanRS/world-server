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
package com.rs.game.content.quests.demonslayer;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class EncantationOptionsD  extends Conversation {
	private DelrithBoss boss;
	private int chantCount;
	private String[] chantOrder = {
			"Aber",
			"Gabindo",
			"Purchai",
			"Camerinthum",
			"Carlem"
	};

	public EncantationOptionsD(Player player, DelrithBoss boss) {
		super(player);
		this.boss = boss;
		chantCount = 0;
		addPlayer(HeadE.SKEPTICAL_THINKING, "Now what was that incantation again?");
		addNext(()-> {
			player.startConversation(new EncantationOptionsD(player, boss, chantCount, 0).getStart());});
	}

	public EncantationOptionsD(Player player, DelrithBoss boss, int chantCount, int convoID) {
		super(player);
		this.player = player;
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
							player.forceTalk("Carlem...");
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
								player.startConversation(new EncantationOptionsD(player, boss, chantCount, 0).getStart());
						}));
				option("Aber...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Aber...", () -> {
							player.forceTalk("Aber...");
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
								player.startConversation(new EncantationOptionsD(player, boss, chantCount, 0).getStart());
						}));
				option("Camerinthum...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Camerinthum...", () -> {
							player.forceTalk("Camerinthum...");
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
								player.startConversation(new EncantationOptionsD(player, boss, chantCount, 0).getStart());
						}));
				option("Purchai...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Purchai...", () -> {
							player.forceTalk("Purchai...");
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
								player.startConversation(new EncantationOptionsD(player, boss, chantCount, 0).getStart());
						}));
				option("Gabindo...", new Dialogue()
						.addPlayer(HeadE.FRUSTRATED, "Gabindo...", () -> {
							player.forceTalk("Gabindo...");
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
								player.startConversation(new EncantationOptionsD(player, boss, chantCount, 0).getStart());
						}));
			}
		});
	}
}
