package com.rs.net.decoders.handlers.impl;

import com.rs.game.player.Player;
import com.rs.game.player.content.SkillCapeCustomizer;
import com.rs.lib.net.packets.PacketHandler;
import com.rs.lib.net.packets.decoders.ResumeHSLDialogue;
import com.rs.net.LobbyCommunicator;

public class ResumeHSLDialogueHandler implements PacketHandler<Player, ResumeHSLDialogue> {

	@Override
	public void handle(Player player, ResumeHSLDialogue packet) {
		if (!player.hasStarted())
			return;
		if (player.getTempAttribs().getB("SkillcapeCustomize"))
			SkillCapeCustomizer.handleSkillCapeCustomizerColor(player, packet.getColorId());
		else if (player.getTempAttribs().getB("MottifCustomize")) {
			player.getClan().setMottifColour(player.getTempAttribs().getI("cMottifColorIndexSet", 0), packet.getColorId()); //TODO
			LobbyCommunicator.updateClan(player.getClan());
		}
	}

}
