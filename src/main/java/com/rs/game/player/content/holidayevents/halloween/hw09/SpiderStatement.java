package com.rs.game.player.content.holidayevents.halloween.hw09;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.statements.Statement;

public class SpiderStatement implements Statement {
	
	private String[] texts;
	
	public SpiderStatement(String... texts) {
		this.texts = texts;
	}

	@Override
	public void send(Player player) {
        StringBuilder builder = new StringBuilder();
        for (int line = 0; line < texts.length; line++)
            builder.append(" " + texts[line]);
        String text = builder.toString();
        player.getInterfaceManager().sendChatBoxInterface(1184);
        player.getPackets().setIFText(1184, 17, NPCDefinitions.getDefs(8985, player.getVars()).getName());
        player.getPackets().setIFText(1184, 13, text);
        player.getPackets().setIFModel(1184, 11, 24613);
		player.getPackets().setIFAngle(1184, 11, 100, 1900, 500);
		player.getPackets().setIFAnimation(6247, 1184, 11);
	}

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}

}
