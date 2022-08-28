package com.rs.game.content.clans;

import com.rs.game.model.entity.npc.OwnedNPC;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.WorldTile;

public class ClanVexillum extends OwnedNPC {

	public ClanVexillum(Player owner, WorldTile tile) {
		super(owner, 13634, tile, false);
		setAutoDespawnAtDistance(false);
		if (owner.getClan() == null)
			return;
		modifyMesh()
			.setModel(0, 64928) //t5 citadel
			.addColors(owner.getClan().getMottifColors()[0], owner.getClan().getMottifColors()[1], owner.getClan().getMottifColors()[2]-20, owner.getClan().getMottifColors()[2]-16, owner.getClan().getMottifColors()[2]-12, owner.getClan().getMottifColors()[2]-8, owner.getClan().getMottifColors()[2]-4, owner.getClan().getMottifColors()[2], owner.getClan().getMottifColors()[3]-20, owner.getClan().getMottifColors()[3]-16, owner.getClan().getMottifColors()[3]-12, owner.getClan().getMottifColors()[3]-8, owner.getClan().getMottifColors()[3]-4, owner.getClan().getMottifColors()[3])
			.addTextures(owner.getClan().getMottifTextures()[0], owner.getClan().getMottifTextures()[1]);
	}

}
