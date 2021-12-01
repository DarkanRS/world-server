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
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content;

import java.util.ArrayList;
import java.util.List;

import com.rs.cache.loaders.interfaces.IFTargetParams;
import com.rs.game.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public final class Notes {

	private List<Note> notes;
	private transient Player player;

	public Notes() {
		notes = new ArrayList<Note>(30);
	}

	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public static ButtonClickHandler handleButtons = new ButtonClickHandler(34) {
		@Override
		public void handle(ButtonClickEvent e) {
			switch (e.getComponentId()) {
			case 35:
			case 37:
			case 39:
			case 41:
				e.getPlayer().getNotes().colour((e.getComponentId() - 35) / 2);
				e.getPlayer().getPackets().setIFHidden(34, 16, true);
				break;
			case 3:
				e.getPlayer().getPackets().sendInputLongTextScript("Add note:");
				e.getPlayer().getTempAttribs().setB("entering_note", true);
				break;
			case 9:
				switch (e.getPacket()) {
				case IF_OP1:
					if (e.getPlayer().getNotes().getCurrentNote() == e.getSlotId())
						e.getPlayer().getNotes().removeCurrentNote();
					else
						e.getPlayer().getNotes().setCurrentNote(e.getSlotId());
					break;
				case IF_OP2:
					e.getPlayer().getPackets().sendInputLongTextScript("Edit note:");
					e.getPlayer().getNotes().setCurrentNote(e.getSlotId());
					e.getPlayer().getTempAttribs().setB("editing_note", true);
					break;
				case IF_OP3:
					e.getPlayer().getNotes().setCurrentNote(e.getSlotId());
					e.getPlayer().getPackets().setIFHidden(34, 16, false);
					break;
				case IF_OP4:
					e.getPlayer().getNotes().delete(e.getSlotId());
					break;
				default:
					break;
				}
				break;
			case 11:
				switch (e.getPacket()) {
				case IF_OP1:
					e.getPlayer().getNotes().delete();
					break;
				case IF_OP2:
					e.getPlayer().getNotes().deleteAll();
					break;
				default:
					break;
				}
				break;
			}
		}
	};

	public void init() {
		player.getPackets().setIFTargetParams(new IFTargetParams(34, 9, 0, 30)
				.enableRightClickOptions(0,1,2,3)
				.setDepth(2)
				.enableDrag());
		player.getPackets().setIFHidden(34, 3, false);
		player.getPackets().setIFHidden(34, 44, false);
		player.getVars().setVar(1437, 1); // unlocks add notes
		player.getVars().setVar(1439, -1);
		refresh();
	}

	private void refresh() {
		for (int i = 0; i < 30; i++)
			player.getPackets().sendVarcString(149 + i, notes.size() <= i ? "" : notes.get(i).text);
		player.getVars().setVar(1440, getPrimaryColour(this));
		player.getVars().setVar(1441, getSecondaryColour(this));
	}

	public int getCurrentNote() {
		return player.getTempAttribs().getI("CURRENT_NOTE");
	}

	public void setCurrentNote(int id) {
		if (id >= 30)
			return;
		player.getTempAttribs().setI("CURRENT_NOTE", id);
		player.getVars().setVar(1439, id);
	}

	public void removeCurrentNote() {
		player.getTempAttribs().removeI("CURRENT_NOTE");
		player.getVars().setVar(1439, -1);
	}

	public boolean add(String text) {
		if (notes.size() >= 30) {
			player.sendMessage("You may only have 30 notes!");
			return false;
		}
		if (text.length() > 50) {
			player.sendMessage("You can only enter notes up to 50 characters!");
			return false;
		}
		player.getPackets().sendVarcString(149 + notes.size(), text);
		setCurrentNote(notes.size());
		return notes.add(new Note(text));
	}

	public boolean edit(String text) {
		if (text.length() > 50) {
			player.sendMessage("You can only enter notes up to 50 characters!");
			return false;
		}
		int id = getCurrentNote();
		if (id == -1 || notes.size() <= id)
			return false;
		notes.get(id).setText(text);
		player.getPackets().sendVarcString(149 + id, text);
		return true;
	}

	public boolean colour(int colour) {
		int id = getCurrentNote();
		if (id == -1 || notes.size() <= id)
			return false;
		notes.get(id).setColour(colour);
		if (id < 16)
			player.getVars().setVar(1440, getPrimaryColour(this));
		else
			player.getVars().setVar(1441, getSecondaryColour(this));
		return true;
	}

	public void switchNotes(int from, int to) {
		if (notes.size() <= from || notes.size() <= to)
			return;
		notes.set(to, notes.set(from, notes.get(to)));
		refresh();
	}

	public void delete() {
		delete(getCurrentNote());
	}

	public void delete(int id) {
		if (id == -1 || notes.size() <= id)
			return;
		notes.remove(id);
		removeCurrentNote();
		refresh();
	}

	public void deleteAll() {
		notes.clear();
		removeCurrentNote();
		refresh();
	}

	/**
	 * Gets the primary colour of the notes.
	 * 
	 * @param notes
	 *            The notes.
	 * @return
	 */
	public static int getPrimaryColour(Notes notes) {
		int color = 0;
		for (int i = 0; i < 16; i++) {
			if (notes.notes.size() <= i)
				break;
			color += colourize(notes.notes.get(i).colour, i);
		}
		return color;
	}

	/**
	 * Gets the secondary colour of the notes.
	 * 
	 * @param notes
	 *            The notes.
	 * @return
	 */
	public static int getSecondaryColour(Notes notes) {
		int color = 0;
		for (int i = 0; i < 14; i++) {
			if (notes.notes.size() - 16 <= i)
				break;
			color += colourize(notes.notes.get(i + 16).colour, i);
		}
		return color;
	}

	public static int colourize(int colour, int noteId) {
		return (int) (Math.pow(4, noteId) * colour);
	}

	public List<Note> getNotes() {
		return notes;
	}

	public static final class Note {

		private String text;
		private int colour;

		public Note(String text) {
			this.text = text;
		}

		public String getText() {
			return text;
		}

		public int getColour() {
			return colour;
		}

		public void setText(String text) {
			this.text = text;
		}

		public void setColour(int colour) {
			this.colour = colour;
		}
	}
}
