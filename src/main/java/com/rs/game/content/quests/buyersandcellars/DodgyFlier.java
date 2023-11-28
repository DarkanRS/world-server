package com.rs.game.content.quests.buyersandcellars;

import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
@PluginEventHandler
public class DodgyFlier {

    public static ItemClickHandler read = new ItemClickHandler(new Object[] { 18645 }, new String[] { "Read" }, e -> {
        e.getPlayer().sendMessage("It says to apply at the Lumbridge Guildhouse. Wonder where that is...");
        e.getPlayer().getInterfaceManager().sendInterface(220);//Message interface
        e.getPlayer().getPackets().setIFText(220, 1, "Darren Lightfinger,");
        e.getPlayer().getPackets().setIFText(220, 2, "Chancellor of the Lumbridge Guild of Thieves,");
        e.getPlayer().getPackets().setIFText(220, 3,  "requests your presence at the First Annual Selection of Appointees.");
        e.getPlayer().getPackets().setIFText(220, 4, "If you have ever dreamed of making a name for yourself,");
        e.getPlayer().getPackets().setIFText(220, 5,"and making your fortune at the same time,");
        e.getPlayer().getPackets().setIFText(220, 6,"now is the best opportunity you will ever have!");
        e.getPlayer().getPackets().setIFText(220, 7, "The world-renowned Lumbridge Guild of Thieves");
        e.getPlayer().getPackets().setIFText(220, 8, "is recruiting new members");
        e.getPlayer().getPackets().setIFText(220, 9,"to be shown a new world of profit and derring-do.");
        e.getPlayer().getPackets().setIFText(220, 10, "Commissions, fame and the adulation of your peers");
        e.getPlayer().getPackets().setIFText(220, 11,"and of the opposite sex will be your fate");
        e.getPlayer().getPackets().setIFText(220, 12, "as a successful applicant.");
        e.getPlayer().getPackets().setIFText(220, 13, "and your life will never again be the same!");
    });
}
