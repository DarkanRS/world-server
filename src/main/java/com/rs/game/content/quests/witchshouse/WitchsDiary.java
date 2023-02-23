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
package com.rs.game.content.quests.witchshouse;

import com.rs.engine.book.Book;
import com.rs.engine.book.BookPage;

public class WitchsDiary extends Book {

	public WitchsDiary() {
		super("Witches' Diary",
				new BookPage(
						new String[] {
								"<col=FF0000>2nd of Pentember</col>",
								"Experiment is growing",
								"larger daily. Making",
								"excellent progress",
								"now. I am currently",
								"feeding it on a mixture",
								"of fungus, tar and clay.",
								"It seems to like this",
								"combination a lot!",
								"",
								"",
						},
						new String[] {
								"<col=FF0000>3rd of Pentember</col>",
								"Experiment still going",
								"extremely well. Moved it",
								"to the wooden garden",
								"shed; it does too much",
								"damage to the house! It",
								"is getting very strong",
								"now, but unfortunately",
								"is not too intelligent yet.",
								"It has a really mean stare",
								"too!",
						}),
				new BookPage(
						new String[] {
								"<col=FF0000>4th of Pentember</col>",
								"Sausages for dinner",
								"tonight! Lovely!",
								"",
								"<col=FF0000>5th of Pentember</col>",
								"A guy called Professor",
								"Oddenstein installed a",
								"new security system for",
								"me in the basement. He",
								"seems to have a lot of",
								"good security ideas.",
						},
						new String[] {
								"<col=FF0000>6th of Pentember</col>",
								"Don't want people getting ",
								"into my back garden to see ",
								"the experiment. Professor ",
								"Oddenstein is fitting me a ",
								"new security system ",
								"after his successful ",
								"installation in the cellar.",
								"",
								"",
								"",
						}),
				new BookPage(
						new String[] {
								"<col=FF0000>7th of Pentember</col>",
								"That pesky kid keeps",
								"kicking his ball into my ",
								"garden. I swear, if he ",
								"does it AGAIN, I'm going",
								"to lock his ball away in ",
								"the shed.",
								"",
								"<col=FF0000>8th of Pentember</col>",
								"The security system is ",
								"done. By Zamorak! Wow, ",
						},
						new String[] {
								"is it contrived! Now, to ",
								"open my own back door, ",
								"I lure a mouse out of a ",
								"hole in the back porch, I ",
								"fit a magic curved piece ",
								"of metal to the harness ",
								"on its back, the mouse ",
								"goes back in the hole, and",
								" the door unlocks! The",
								"prof tells me that this is ",
								"cutting edge technology!",
						}),
				new BookPage(
						new String[] {
								"As an added precaution I",
								"have hidden the key to ",
								"the shed in a secret ",
								"compartment of the ",
								"fountain in the garden. ",
								"No one will ever look ",
								"there!",
								"",
								"<col=FF0000>9th of Pentember</col>",
								"Still cant think of a good",
								" name for 'The ",
						},
						new String[] {
								"Experiment'. Leaning",
								" towards 'Fritz'... Although",
								"am considering Lucy as",
								"it reminds me of my ",
								"mother!",
								"",
								"",
								"",
								"",
								"",
								"",
						})
				);
	}

}
