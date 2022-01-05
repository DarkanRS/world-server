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
package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ResumeTextDialogue;
import com.rs.plugin.events.InputStringEvent;

public class ResumeTextDialoguesHandler implements PacketHandler<Player, ResumeTextDialogue> {

	@Override
	public void handle(Player player, ResumeTextDialogue packet) {
		if (!player.isRunning() || player.isDead())
			return;
		if (packet.getText().equals(""))
			return;
		switch (packet.getOpcode()) {
			case RESUME_CLANFORUMQFCDIALOG -> {
				if (player.getTempAttribs().getO("pluginQFCD") != null && player.getTempAttribs().removeO("pluginQFCD") instanceof InputStringEvent ise)
					ise.run(packet.getText());
			}
			case RESUME_NAMEDIALOG -> {
				if (player.getTempAttribs().getO("pluginEnterName") != null && player.getTempAttribs().removeO("pluginEnterName") instanceof InputStringEvent ise)
					ise.run(packet.getText());
			}
			case RESUME_TEXTDIALOG -> {
				if (player.getTempAttribs().getO("pluginEnterLongText") != null && player.getTempAttribs().removeO("pluginEnterLongText") instanceof InputStringEvent ise)
					ise.run(packet.getText());
			}
			default -> {}
		}
	}

}
