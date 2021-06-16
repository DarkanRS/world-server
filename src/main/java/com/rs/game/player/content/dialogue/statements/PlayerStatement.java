package com.rs.game.player.content.dialogue.statements;

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.HeadE;

public class PlayerStatement implements Statement {

    private HeadE emote;
    private String[] texts;

    public PlayerStatement(HeadE emote, String... texts) {
        this.emote = emote;
        this.texts = texts;
    }

    @Override
    public void send(Player player) {
        StringBuilder builder = new StringBuilder();
        for (int line = 0; line < texts.length; line++)
            builder.append(" " + texts[line]);
        String text = builder.toString();
        player.getInterfaceManager().sendChatBoxInterface(1191);
        player.getPackets().setIFText(1191, 8, player.getDisplayName());
        player.getPackets().setIFText(1191, 17, text);
        player.getPackets().setIFPlayerHead(1191, 15);
        if (emote != null && emote.getEmoteId() != -1)
            player.getPackets().setIFAnimation(emote.getEmoteId(), 1191, 15);
    }

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}
}
