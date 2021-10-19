package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ResumeTextDialogue;
import com.rs.lib.util.Utils;
import com.rs.net.LobbyCommunicator;
import com.rs.plugin.events.InputStringEvent;

public class ResumeTextDialoguesHandler implements PacketHandler<Player, ResumeTextDialogue> {

	@Override
	public void handle(Player player, ResumeTextDialogue packet) {
		if (!player.isRunning() || player.isDead())
			return;
		if (packet.getText().equals(""))
			return;

		switch (packet.getOpcode()) {
		case RESUME_CLANFORUMQFCDIALOG:
			break;
		case RESUME_NAMEDIALOG:
			if (player.getTemporaryAttributes().remove("setclan") != null)
				LobbyCommunicator.createClan(player, packet.getText());
			else if (player.getTemporaryAttributes().remove("joinguestclan") != null)
				LobbyCommunicator.connectToClan(player, packet.getText(), true);
			else if (player.getTemporaryAttributes().remove("banclanplayer") != null)
				LobbyCommunicator.banClanPlayer(player, packet.getText());
			else if (player.getTemporaryAttributes().remove("unbanclanplayer") != null)
				LobbyCommunicator.unbanClanPlayer(player, packet.getText());
			else if (player.getTemporaryAttributes().remove("DUNGEON_INVITE") != null)
				player.getDungManager().invite(packet.getText());
			else if (player.getTemporaryAttributes().get("yellcolor") == Boolean.TRUE) {
				if (packet.getText().length() != 6) {
					player.getDialogueManager().execute(new SimpleMessage(), "The HEX yell color you wanted to pick cannot be longer and shorter then 6.");
				} else if (Utils.containsInvalidCharacter(packet.getText().toLowerCase()) || packet.getText().contains("_")) {
					player.getDialogueManager().execute(new SimpleMessage(), "The requested yell color can only contain numeric and regular characters.");
				} else {
					player.setYellColor(packet.getText());
					player.getDialogueManager().execute(new SimpleMessage(), "Your yell color has been changed to <col=" + player.getYellColor() + ">" + player.getYellColor() + "</col>.");
				}
				player.getTemporaryAttributes().put("yellcolor", Boolean.FALSE);
			} else if (player.getTemporaryAttributes().get("yelltitle") == Boolean.TRUE) {
				if (packet.getText().length() > 20) {
					player.getDialogueManager().execute(new SimpleMessage(), "Your yell title cannot be longer than 20 characters.");
				} else if (Utils.containsBadCharacter(packet.getText())) {
					player.getDialogueManager().execute(new SimpleMessage(), "Keep your title as only letters and numbers..");
				} else {
					player.setYellTitle(packet.getText());
					player.getAppearance().generateAppearanceData();
					player.getDialogueManager().execute(new SimpleMessage(), "Your title has been changed to " + player.getYellTitle() + ".");
				}
				player.getTemporaryAttributes().put("yelltitle", Boolean.FALSE);
			} else
			// START CUSTOM TITLES
			if (player.getTemporaryAttributes().get("settitle") == Boolean.TRUE) {
				if (packet.getText().length() > 20) {
					player.getDialogueManager().execute(new SimpleMessage(), "Your title cannot be longer than 20 characters.");
				} else if (Utils.containsBadCharacter(packet.getText())) {
					player.getDialogueManager().execute(new SimpleMessage(), "Keep your title as only letters and numbers..");
				} else if (packet.getText().toLowerCase().contains("ironman") || packet.getText().toLowerCase().contains("hard")) {
					player.getDialogueManager().execute(new SimpleMessage(), "That title is reserved for special account types.");
				} else {
					player.setTitle(packet.getText());
					player.getAppearance().generateAppearanceData();
					player.getDialogueManager().execute(new SimpleMessage(), "Your title has been changed to " + player.getTitle() + ".");
				}
				player.getTemporaryAttributes().put("settitle", Boolean.FALSE);
			} else if (player.getTemporaryAttributes().get("titlecolor") == Boolean.TRUE) {
				if (packet.getText().length() != 6) {
					player.getDialogueManager().execute(new SimpleMessage(), "HEX colors are 6 characters long bud.");
				} else if (Utils.containsInvalidCharacter(packet.getText().toLowerCase()) || packet.getText().contains("_")) {
					player.getDialogueManager().execute(new SimpleMessage(), "HEX colors just contain letters and numbers bud.");
				} else {
					player.setTitleColor(packet.getText());
					player.getAppearance().generateAppearanceData();
					player.getDialogueManager().execute(new SimpleMessage(), "Your title has been changed to " + player.getTitle() + ".");
				}
				player.getTemporaryAttributes().put("titlecolor", Boolean.FALSE);
			} else if (player.getTemporaryAttributes().get("titleshade") == Boolean.TRUE) {
				if (packet.getText().length() != 6) {
					player.getDialogueManager().execute(new SimpleMessage(), "HEX colors are 6 characters long bud.");
				} else if (Utils.containsInvalidCharacter(packet.getText().toLowerCase()) || packet.getText().contains("_")) {
					player.getDialogueManager().execute(new SimpleMessage(), "HEX colors just contain letters and numbers bud.");
				} else {
					player.setTitleShading(packet.getText());
					player.getAppearance().generateAppearanceData();
					player.getDialogueManager().execute(new SimpleMessage(), "Your title has been changed to " + player.getTitle() + ".");
				}
				player.getTemporaryAttributes().put("titleshade", Boolean.FALSE);
			}
			// END CUSTOM TITLES
			else {
				if (player.getTemporaryAttributes().get("pluginString") != null && player.getTemporaryAttributes().remove("pluginString") instanceof InputStringEvent ise)
					ise.run(packet.getText());
				return;
			}
		case RESUME_TEXTDIALOG:
			if (player.getTemporaryAttributes().remove("entering_note") == Boolean.TRUE)
				player.getNotes().add(packet.getText());
			else if (player.getTemporaryAttributes().remove("editing_note") == Boolean.TRUE)
				player.getNotes().edit(packet.getText());
			if (player.getInterfaceManager().containsInterface(1103))
				LobbyCommunicator.setClanMotto(player, packet.getText());
			break;
		default:
			break;
		}
	}

}
