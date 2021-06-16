package com.rs.game.player.content.dialogue.statements;

import com.rs.game.player.Player;

public class ItemStatement implements Statement {

    private int itemId;
    private String text;

    public ItemStatement(int itemId, String text) {
        this.itemId =  itemId;
        this.text = text;
    }

    @Override
    public void send(Player player) {
    	player.getInterfaceManager().sendChatBoxInterface(1189);
        player.getPackets().setIFItem(1189, 1, itemId, 1);
        player.getPackets().setIFText(1189, 4, text);
    }

	@Override
	public int getOptionId(int componentId) {
		return 0;
	}
}
