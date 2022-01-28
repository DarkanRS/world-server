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
package com.rs.game.player.quests.handlers.shieldofarrav;

import com.rs.game.player.Player;
import com.rs.game.player.quests.Quest;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public class BookShieldOfArrav {
	final static int BOOKINTERFACE = 937;//interface
	final static int LEFTARROWCOMP = 66;
	final static int RIGHTARROWCOMP = 67;


	public static void openBook(Player p) {
		set1stPage(p);
	}

	public static void setTitle(Player p, String title) {
		p.getPackets().setIFText(BOOKINTERFACE, 43, title);
	}

	public static void set1stPage(Player p) {
		p.getInterfaceManager().sendInterface(BOOKINTERFACE);
		setTitle(p, "Shield Of Arrav");

		int lineNum = 0;
		lineNum = setParagraph(p, "Arrav is probably the best known hero of the 4th Age.Many legends are told of his heroics. One surviving artefact from the 4th Age is a " +
				"fabulous shield", lineNum);
		lineNum = setParagraph(p, "This shield is believed to have once belonged to Arrav and is now indeed known as the Shield of Arrav. For over 150 years it was the prize " +
				"piece in the royal museum of Varrock.", lineNum);
		lineNum = setParagraph(p, "However, in the year 143 of the fifth age a gang of thieves called the Phoenix Gang broke into the museum and stole the", lineNum);
		showLeftArrow(p, false);
		showRightArrow(p, true);
		p.getPackets().setIFText(BOOKINTERFACE, 92, "Page 2");
	}

	public static void set2ndPage(Player p) {
		p.getInterfaceManager().sendInterface(BOOKINTERFACE);
		setTitle(p, "Shield Of Arrav");

		int lineNum = 0;
		lineNum = setParagraph(p, "shield in a daring raid.", lineNum);
		lineNum = setParagraph(p, "As a result, the current ruler, King Roald, put a 1200 gold bounty (a massive sum in those days) on the return of " +
				"the shield, hoping that one of the culprits would betray his fellows out of greed.", lineNum);
		lineNum = setParagraph(p, "This tactic did not work however, and the thieves who stole the shield have since gone on to become the most powerful crime " +
				"gang in Varrock, despite making an enemy of the royal family many years ago.", lineNum);

		clearRestOfPageLines(p, lineNum);

		showLeftArrow(p, true);
		showRightArrow(p, false);
		p.getPackets().setIFText(BOOKINTERFACE, 91, "Page 1");
	}

	public static int setParagraph(Player p, String text, int lineNum) {
		String[] words = text.split(" ");
		String line = "";

		for(int i = 0; i < words.length;) {
			while ((line + words[i]).length() < 26) {
				line = line + words[i++] + " ";
				if(i >= words.length)
					break;
			}

			p.getPackets().setIFText(BOOKINTERFACE, 69 + lineNum, line);
			line = "";
			lineNum++;
		}

		//Beginning of page can't be new paragraph line
		if(69+lineNum != 80) {
			p.getPackets().setIFText(BOOKINTERFACE, 69 + lineNum, "");
			lineNum++;
		}
		return lineNum;
	}

	public static void clearRestOfPageLines(Player p, int lineNum) {
		while(69+lineNum <= 90)
			p.getPackets().setIFText(BOOKINTERFACE, 69 + lineNum++, "");
	}

	public static void showLeftArrow(Player p, boolean isShown) {
		p.getPackets().setIFHidden(BOOKINTERFACE, LEFTARROWCOMP, !isShown);
		p.getPackets().setIFHidden(BOOKINTERFACE, 91, !isShown);
	}

	public static void showRightArrow(Player p, boolean isShown) {
		p.getPackets().setIFHidden(BOOKINTERFACE, RIGHTARROWCOMP, !isShown);
		p.getPackets().setIFHidden(BOOKINTERFACE, 92, !isShown);
	}

	public static ButtonClickHandler handleBookButtons = new ButtonClickHandler(937) {
		int page = 1;
		@Override
		public void handle(ButtonClickEvent e) {
			if(e.getComponentId() == 41)
				page = 1;
			if(e.getComponentId() == 67)
				page++;
			if(e.getComponentId() == 66)
				page--;
			if(page>2)
				page = 2;

			if(page == 1)
				set1stPage(e.getPlayer());
			if(page == 2) {
				set2ndPage(e.getPlayer());
				if(e.getPlayer().getQuestManager().getStage(Quest.SHIELD_OF_ARRAV) == ShieldOfArrav.FIND_BOOK_STAGE)
					ShieldOfArrav.setStage(e.getPlayer(), ShieldOfArrav.BOOK_IS_READ_STAGE);
			}



		}
	};
}
