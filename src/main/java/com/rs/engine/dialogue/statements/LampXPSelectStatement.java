package com.rs.engine.dialogue.statements;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.interfaces.IFEvents;
import com.rs.game.content.Lamp;
import com.rs.game.model.entity.player.Player;

public class LampXPSelectStatement implements Statement {
	
	private Lamp lamp;
	
	public LampXPSelectStatement(Lamp lamp) {
		this.lamp = lamp;
	}

	@Override
	public void send(Player player) {
		if (lamp.getId() == 12628)
            lamp.setXp(500);
        player.getTempAttribs().setO("lampInstance", lamp);
        player.getVars().setVar(738, 1); //has house
        player.getVars().setVarBit(2187, 1);
        player.getInterfaceManager().sendInterface(1263);
        player.getPackets().sendVarcString(358, "What skill would you like XP in?");
        sendSelectedSkill(player);
        player.getPackets().sendVarc(1797, 0);
        player.getPackets().sendVarc(1798, lamp.getReq()); //level required to use lamp
        player.getPackets().sendVarc(1799, getVarCValueForLamp(lamp.getId()));
        for (int i = 13; i < 38; i++)
            player.getPackets().setIFRightClickOps(1263, i, -1, 0, 0);
        player.getPackets().setIFEvents(new IFEvents(1263, 39, 1, 26).enableContinueButton());
	}

	@Override
	public int getOptionId(int componentId) {
		return switch(componentId) {
			case 39 -> 0;
			default -> 1;
		};
	}

    private static void sendSelectedSkill(Player player) {
        if (player.getTempAttribs().getO("lampInstance") == null)
            return;
        Lamp lamp = player.getTempAttribs().getO("lampInstance");
        EnumDefinitions map = EnumDefinitions.getEnum(681);
        if (lamp.getSelectedSkill() == map.getDefaultIntValue()) {
            player.getPackets().sendVarc(1796, map.getDefaultIntValue());
            return;
        }

        long key = map.getKeyForValue(lamp.getSelectedSkill());
        player.getPackets().sendVarc(1796, (int) key);
    }
    
    private int getVarCValueForLamp(int id) {
        switch (id) {
            case 4447://Shield of Arrav
                return 23713;
        }
        return id;
    }

	@Override
	public void close(Player player) {
		player.closeInterfaces();
	}
}
