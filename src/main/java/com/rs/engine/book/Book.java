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
package com.rs.engine.book;

import com.rs.game.model.entity.player.Player;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public abstract class Book {

	private static final int INTERFACE = 960;

	private static final int[] LEFT_COMPONENTS = { 49, 56, 61, 62, 54, 63, 55, 51, 60, 58, 53, 50, 57, 59, 52 };
	private static final int[] RIGHT_COMPONENTS = { 33, 39, 36, 44, 37, 46, 40, 42, 34, 35, 38, 43, 47, 45, 41 };

	private Player player;
	private int page;

	private String title;
	private BookPage[] pages;

	public Book(String title, BookPage... pages) {
		this.title = title;
		this.pages = pages;
	}

	public static ButtonClickHandler handleInter = new ButtonClickHandler(INTERFACE, e -> {
		if (e.getPlayer().getTempAttribs().getO("currBook") == null)
			return;
		Book book = e.getPlayer().getTempAttribs().getO("currBook");
		if (e.getComponentId() == 72)
			book.prevPage();
		if (e.getComponentId() == 73)
			book.nextPage();
	});

	public void open(Player player) {
		this.player = player;
		this.player.getTempAttribs().setO("currBook", this);
		this.player.setCloseInterfacesEvent(() -> {
			this.player.getTempAttribs().removeO("currBook");
		});
		player.getPackets().setIFText(INTERFACE, 69, title);
		page = 0;
		update();
	}

	public void update() {
		player.getInterfaceManager().sendInterface(INTERFACE);
		if (page == 0) {
			player.getPackets().setIFHidden(INTERFACE, 72, true);
			player.getPackets().setIFText(INTERFACE, 70, "");
		} else {
			player.getPackets().setIFHidden(INTERFACE, 72, false);
			player.getPackets().setIFText(INTERFACE, 70, "Prev");
		}
		if (page >= pages.length-1) {
			player.getPackets().setIFHidden(INTERFACE, 73, true);
			player.getPackets().setIFText(INTERFACE, 71, "");
		} else {
			player.getPackets().setIFHidden(INTERFACE, 73, false);
			player.getPackets().setIFText(INTERFACE, 71, "Next");
		}
		for (int line = 0;line < LEFT_COMPONENTS.length;line++) {
			player.getPackets().setIFText(INTERFACE, LEFT_COMPONENTS[line], pages[page].getLeftLine(line));
			player.getPackets().setIFText(INTERFACE, RIGHT_COMPONENTS[line], pages[page].getRightLine(line));
		}
	}

	public void nextPage() {
		if (page >= pages.length-1)
			return;
		page++;
		update();
	}

	public void prevPage() {
		if (page <= 0)
			return;
		page--;
		update();
	}

}
