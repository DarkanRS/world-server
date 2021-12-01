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
package com.rs.net.decoders.handlers.impl.chat;

import com.rs.cache.loaders.QCMesDefinitions;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.game.QuickChatMessage;
import com.rs.lib.io.OutputStream;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.chat.QCPrivate;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;

public class QCPrivateHandler implements PacketHandler<Player, QCPrivate> {

	@Override
	public void handle(Player player, QCPrivate packet) {
		if (!player.hasStarted())
			return;
		if (player.getLastPublicMessage() > System.currentTimeMillis())
			return;
		player.setLastPublicMessage(System.currentTimeMillis() + 300);

		if (!Utils.isQCValid(packet.getQcId()))
			return;
		byte[] data = completeQuickMessage(player, packet.getQcId(), packet.getMessageData());
		Player p2 = World.getPlayer(packet.getToUsername());
		if (p2 == null)
			return;
		LobbyCommunicator.sendPMQuickChat(player, packet.getToUsername(), new QuickChatMessage(packet.getQcId(), data));
	}
	
	public static byte[] completeQuickMessage(Player player, int fileId, byte[] data) {
		QCMesDefinitions defs = QCMesDefinitions.getDefs(fileId);
		if (defs == null || defs.types == null)
			return null;
		
		OutputStream stream = new OutputStream();
		
		for (int i = 0;i < defs.types.length;i++) {
			switch(defs.types[i]) {
			case STAT_BASE:
				stream.writeByte(player.getSkills().getLevelForXp(defs.configs[i][0]));
				break;
			case TOSTRING_VARP:
				stream.writeInt(player.getVars().getVar(defs.configs[i][0]));
				break;
			case TOSTRING_VARBIT:
				stream.writeInt(player.getVars().getVarBit(defs.configs[i][0]));
				break;
			case ENUM_STRING:
				stream.writeInt(player.getVars().getVar(defs.configs[i][1]-1));
				break;
			case ENUM_STRING_STATBASE:
				stream.writeByte(player.getSkills().getLevelForXp(defs.configs[i][1]));
				break;
			case OBJTRADEDIALOG:
			case OBJDIALOG:
			case LISTDIALOG:
				if (data != null && data.length >= 2)
					return data;
				break;
			case ACTIVECOMBATLEVEL:
				stream.writeByte(player.getSkills().getCombatLevelWithSummoning());
				break;
			case ACC_GETMEANCOMBATLEVEL:
				stream.writeByte(0); //TODO Avg combat level in FC
				break;
			case ACC_GETCOUNT_WORLD:
				stream.writeByte(0); //TODO Count players in FC
				break;
			default:
				System.out.println("Unhandled quickchat type: " + defs);
				break;
			}
		}
		
		return stream.toByteArray();
	}

}
