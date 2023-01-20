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
package com.rs.game.content.skills.prayer;

import com.rs.game.model.entity.ForceTalk;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Animation;
import com.rs.lib.util.Utils;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class PrayerBooks {

	public static final int[] BOOKS = { 3839, 3841, 3843, 19612, 19614, 19616 };
	private static final int[] ANIMATIONS = { 1335, 1336, 1337, 14308, 14309, 14310 };
	private static final int[] PAGE_BASE = { 3827, 3831, 3835, 19600, 19604, 19608 };

	private static final String[][] CHANTS = {
			{ "In the name of Saradomin, protector of us all, I now join you in the eyes of Saradomin.", "Thy cause was false, thy skills did lack; see you in Lumbridge when you get back.", "Go in peace in the name of Saradomin; may his glory shine upon you like the sun.", "Protect your self, protect your friends. Mine is the glory that never ends. This is Saradomin's wisdom.", "The darkness in life may be avoided, by the light of wisdom shining. This is Saradomin's wisdom.", "Show love to your friends, and mercy to your enemies, and know that the wisdom of Saradomin will follow. This is Saradomin's wisdom.", "A fight begun, when the cause is just, will prevail over all others. This is Saradomin's wisdom." },

			{ "Two great warriors, joined by hand, to spread destruction across the land. In Zamorak's name, now two are one.", "The weak deserve to die, so the strong may flourish. This is the will of Zamorak.", "May your bloodthirst never be sated, and may all your battles be glorious. Zamorak bring you strength.", "Battles are not lost and won; They simply remove the weak from the equation. Zamorak give me strength!", "Those who fight, then run away, shame Zamorak with their cowardice. Zamorak give me strength!", "Battle is my calling, and death shall be my rest. Zamorak give me strength!", "Strike fast, strike hard, strike true: The strength of Zamorak will be with you. Zamorak give me strength!", "There is no opinion that cannot be proven true, by crushing those who choose to disagree with it. Zamorak give me strength!", },

			{ "Light and dark, day and night, Balance arises from contrast. I unify thee in the name of Guthix.", "Thy death was not in vain, for it brought some balance to the world. May Guthix bring you rest.", "May you walk the path, and never fall, For Guthix walks beside thee on thy journey. May Guthix bring you peace.", "The trees, the earth, the sky, the waters: All play their part upon this land. May Guthix bring you balance." },

			{ "Big High War God want Great Warriors. Because you can make more, I bind you in Big High War God name.", "You not worthy of Big High War God, you die too easy.", "Big High War God make you strong, so you smash enemies.", "Big High War God say: You not run away from battle, or Big High War God strike you.", "Big High War God say: Good warrior kill enemies, before he get squished.", "Big High War God say: Follow and obey your commander, unless they run away.", "Big High War God say: If you not worthy of Big High War God, you get made dead soon.", "Big High War God say: If your friend get killed, be happy; they were too weak for Big High War God.", "Big High War God say: War is best, Peace is for weak.", },

			{ "As ye vow to be at peace with each other, And to uphold high values of morality and friendship, I now pronounce you united in the law of Armadyl.", "Thou didst fight true, but the foe was too great. May thy return be as swift as the flight of Armadyl.", "For thy task is lawful, May the blessing of Armadyl be upon thee.", "Do not let thy vision be clouded by evil, Look up, for the truth cometh from the skies. This is the law of Armadyl.", "It is honourable to resist, And fight for what thou believe is just. This is the law of Armadyl.", "Peace shall bring thee wisdom; Wisdom shall bring thee peace. This is the law of Armadyl.", "Thou shalt avoid war; but, if thou must fight, Believe, and thou shalt strike true. This is the law of Armadyl.", "Thou shalt fly like the bird, Not like the rock. This is the law of Armadyl.", "To those cursed by war and pest, Come into the light of Armadyl and rest. This is the law of Armadyl.", }, { "Ye faithful and loyal to the Great Lord, May ye together succeed in your deeds, Ye are now joined by the greatest power.", "Thy faith faltered, no power could save thee. Like the Great Lord, one day you shall rise again.", "By day or night, in defeat or victory, The power of the Great Lord be with thee.", "Though your enemies wish to silence thee, Do not falter, defy them to the end. Power to the Great Lord!", "The followers of the Great Lord are few, But they are powerful and mighty. Power to the Great Lord!", "Follower of the Great Lord be relieved: One day your loyalty will be rewarded. Power to the Great Lord!", "Pray for the day that the Great Lord rises; It is that day thou shalt be rewarded. Power to the Great Lord!", "Oppressed thou art, but fear not: The day will come when the Great Lord rises. Power to the Great Lord!", "Fighting oppression is the wisest way, To prove your worth to the Great Lord. Power to the Great Lord!", } };

	public static boolean isGodBook(int bookId, boolean complete) {
		for (int book : BOOKS)
			if (book + (complete ? 1 : 0) == bookId)
				return true;
		return false;
	}

	public static ItemClickHandler handleGodBooks = new ItemClickHandler(new Object[] { BOOKS }, new String[] { "Preach" }, e -> PrayerBooks.handleSermon(e.getPlayer(), e.getItem().getId()));

	public static void bindPages(Player player, int bookId) {
		int god = -1;
		for (int i = 0;i < BOOKS.length;i++)
			if (BOOKS[i] == bookId)
				god = i;
		boolean containsGodPages = true;

		for (int pageIndex = 0; pageIndex < 4; pageIndex++)
			if (!player.getInventory().containsItem(PAGE_BASE[god] + pageIndex, 1)) {
				containsGodPages = false;
				break;
			}

		if (containsGodPages) {
			for (int pageIndex = 0; pageIndex < 4; pageIndex++)
				player.getInventory().deleteItem(PAGE_BASE[god] + pageIndex, 1);
			player.getInventory().deleteItem(bookId, 1);
			player.getInventory().addItem(BOOKS[god] + 1, 1);
			player.sendMessage("You bind all four pages into the book.");
			player.getPrayerBook()[god] = true;
		} else
			player.sendMessage("You need all four pages to create this book.");

	}

	public static void handleSermon(Player player, final int bookId) {
		final int god = (bookId - (bookId > 5000 ? BOOKS[3] - 6 : BOOKS[0])) / 2;
		final String[] passages = CHANTS[god];
		player.sendOptionDialogue("Select a relevant passage.", ops -> {
			ops.add("Wedding Ceremony", () -> {
				String message = passages[0];
				int animation = ANIMATIONS[god];
				if (animation != -1)
					player.setNextAnimation(new Animation(animation));
				player.setNextForceTalk(new ForceTalk(message));
			});
			ops.add("Last Rites", () -> {
				String message = passages[0];
				int animation = ANIMATIONS[god];
				if (animation != -1)
					player.setNextAnimation(new Animation(animation));
				player.setNextForceTalk(new ForceTalk(message));
			});
			ops.add("Blessings", () -> {
				String message = passages[0];
				int animation = ANIMATIONS[god];
				if (animation != -1)
					player.setNextAnimation(new Animation(animation));
				player.setNextForceTalk(new ForceTalk(message));
			});
			ops.add("Preach", () -> {
				String message = passages[0];
				if (passages.length > 3)
					message = passages[3 + Utils.random(passages.length - 3)];
				int animation = ANIMATIONS[god];
				if (animation != -1)
					player.setNextAnimation(new Animation(animation));
				player.setNextForceTalk(new ForceTalk(message));
			});
		});
	}
}
