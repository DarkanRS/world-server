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
		if (player.getTempAttribs().get("SkillcapeCustomize") != null)
			SkillCapeCustomizer.handleSkillCapeCustomizerColor(player, packet.getColorId());
		else if (player.getTempAttribs().get("MottifCustomize") != null) {
			player.getClan().setMottifColour(player.getTempI("cMottifColorIndexSet"), packet.getColorId()); //TODO
			LobbyCommunicator.updateClan(player.getClan());
		}
	}

}
