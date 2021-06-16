package com.rs.game.player.content.dialogue.statements;

import com.rs.cache.loaders.NPCDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.HeadE;

public class NPCStatement implements Statement {

    private int npcId;
    private HeadE emote;
    private String[] texts;

    public NPCStatement(int npcId, HeadE emote, String... texts) {
        this.npcId = npcId;
        this.emote = emote;
        this.texts = texts;
    }

    @Override
    public void send(Player player) {
        StringBuilder builder = new StringBuilder();
        for (int line = 0; line < texts.length; line++)
            builder.append(" " + texts[line]);
        String text = builder.toString();
        player.getInterfaceManager().sendChatBoxInterface(1184);
        player.getPackets().setIFText(1184, 17, NPCDefinitions.getDefs(npcId, player.getVars()).getName());
        player.getPackets().setIFText(1184, 13, text);
        player.getPackets().setIFNPCHead(1184, 11, npcId);
        if (emote != null && emote.getEmoteId() != -1)
            player.getPackets().setIFAnimation(emote.getEmoteId(), 1184, 11);
    }

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}
}
