package com.rs.game.player.dialogues;

import com.rs.lib.model.Clan;
import com.rs.net.LobbyCommunicator;

public class ClanInvite extends Dialogue {

	private Clan clan;

	@Override
	public void start() {
		clan = (Clan) parameters[0];
		if (clan == null || player.getSocial().getClanName() != null) {
			end();
			return;
		}
		if (player.getInterfaceManager().containsScreenInter() || player.getControllerManager().getController() != null) {
			end();
			return;
		}
		player.getPackets().sendClanSettings(clan, true);
		player.getInterfaceManager().sendInterface(1095);
		player.getPackets().setIFText(1095, 2, "You have been invited to join " + clan.getName() + "!");
		if (clan.getMottifTop() != 0)
			player.getPackets().setIFGraphic(1095, 44, Clan.getMottifSprite(clan.getMottifTop()));
		if (clan.getMottifBottom() != 0)
			player.getPackets().setIFGraphic(1095, 54, Clan.getMottifSprite(clan.getMottifBottom()));
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == 33)
			LobbyCommunicator.addClanMember(clan, player);
		end();

	}

	@Override
	public void finish() {

	}

}
