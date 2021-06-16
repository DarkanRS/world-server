package com.rs.game.player.content.dialogue.statements;

import com.rs.game.player.Player;

public class SimpleStatement implements Statement {

    private String[] texts;

    public SimpleStatement(String... texts) {
        this.texts = texts;
    }

    @Override
    public void send(Player player) {
        StringBuilder builder = new StringBuilder();
        for (int line = 0; line < texts.length; line++)
            builder.append((line == 0 ? "<p=" + 1 + ">" : "<br>") + texts[line]);
        String text = builder.toString();
        player.getInterfaceManager().sendChatBoxInterface(1186);
        player.getPackets().setIFText(1186, 1, text);
    }

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}
}
