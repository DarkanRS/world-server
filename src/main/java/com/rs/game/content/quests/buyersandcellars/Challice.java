package com.rs.game.content.quests.buyersandcellars;

import com.rs.engine.quest.Quest;
import com.rs.game.content.world.doors.Doors;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
@PluginEventHandler
public class Challice {

	public static ObjectClickHandler handleShackDoor = new ObjectClickHandler(new Object[] { 45539, 45540 }, e -> {
		Doors.handleDoor(e.getPlayer(), e.getObject());
	});

	public static ObjectClickHandler handleCase = new ObjectClickHandler(new Object[] { 51653 }, e -> {
		if(e.getPlayer().getQuestStage(Quest.BUYERS_AND_CELLARS) != 7){
			e.getPlayer().sendMessage("The golden chalice is firmly locked inside this sturdy display case.");
		}
		else
		if(e.getPlayer().getInventory().containsItem(18647)){
			e.getPlayer().getInventory().deleteItem(18647,1);
			e.getPlayer().getInventory().addItem(18648);
			e.getPlayer().sendMessage("You quietly unlock the display case and remove the golden chalice from it.");
			e.getPlayer().setQuestStage(Quest.BUYERS_AND_CELLARS,8);
		}
		else
			e.getPlayer().sendMessage("The golden chalice is firmly locked inside this sturdy display case.");
	});

}