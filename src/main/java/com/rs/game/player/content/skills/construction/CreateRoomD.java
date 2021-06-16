package com.rs.game.player.content.skills.construction;

import com.rs.game.player.content.skills.construction.House.RoomReference;
import com.rs.game.player.dialogues.Dialogue;

public class CreateRoomD extends Dialogue {

	private RoomReference room;

	@Override
	public void start() {
		this.room = (RoomReference) parameters[0];
		sendPreview();
	}

	public void sendPreview() {
		sendOptionsDialogue("Select an Option", "Rotate clockwise", "Rotate anticlockwise.", "Build.", "Cancel");
		player.getHouse().previewRoom(room, false);
	}

	@Override
	public void run(int interfaceId, int componentId) {
		if (componentId == OPTION_4) {
			end();
			return;
		}
		if (componentId == OPTION_3) {
			end();
			player.getHouse().createRoom(room);
			return;
		}
		player.getHouse().previewRoom(room, true);
		room.setRotation((room.getRotation() + (componentId == OPTION_1 ? 1 : -1)) & 0x3);
		sendPreview();
	}

	@Override
	public void finish() {
		player.getHouse().previewRoom(room, true);
	}

}