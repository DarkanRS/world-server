package com.rs.game.player.quests.handlers.piratestreasure;

import static com.rs.game.player.content.world.doors.Doors.handleDoor;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.APRON;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.HAS_SMUGGLED_RUM_ATTR;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.RUM;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.RUM_IN_KARAMJA_CRATE_ATTR;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.RUM_IN_SARIM_CRATE_ATTR;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.SMUGGLE_RUM;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.WYDIN;
import static com.rs.game.player.quests.handlers.piratestreasure.PiratesTreasure.WYDIN_EMPLOYMENT_ATTR;

import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.quests.Quest;
import com.rs.lib.game.Item;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class WydinPiratesTreasureD extends Conversation {

	public WydinPiratesTreasureD(Player p) {
		super(p);
		switch (p.getQuestManager().getStage(Quest.PIRATES_TREASURE)) {
		case SMUGGLE_RUM -> {
			addPlayer(HeadE.HAPPY_TALKING, "Can I get a job here?");
			addNPC(WYDIN, HeadE.CALM_TALK, "Well, you're keen, I'll give you that. Okay, I'll give you a go. Have you got your own white apron?");
			if(p.getInventory().containsItem(APRON, 1) || p.getEquipment().getChestId() == APRON) {
				addPlayer(HeadE.HAPPY_TALKING, "Yes, I have one right here.");
				addNPC(WYDIN, HeadE.CALM_TALK, "Wow - you are well prepared! You're hired. Go through to the back and tidy up for me, please.", ()->{
					p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB(WYDIN_EMPLOYMENT_ATTR, true);
				});
			}
			else {
				addPlayer(HeadE.HAPPY_TALKING, "No, I haven't.");
				addNPC(WYDIN, HeadE.CALM_TALK, "Well, you can't work here unless you have a white apron. Health and safety regulations, you understand.");
				addPlayer(HeadE.HAPPY_TALKING, "Where can I get one of those?");
				addNPC(WYDIN, HeadE.CALM_TALK, "Well, I get all of mine over at the clothing shop in Varrock. They sell them cheap there. Oh, and I'm sure " +
						"that I've seen a spare one over in Gerrant's fish store somewhere. It's the little place just north of here.");
			}

		}
		}
	}

	public static ObjectClickHandler handleBackRoom = new ObjectClickHandler(new Object[] { 2069 }) {//backroom of Wydin
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(p.getX() < obj.getX()) {
				handleDoor(p, e.getObject());
				return;
			}
			if(p.getQuestManager().getStage(Quest.PIRATES_TREASURE) == SMUGGLE_RUM) {
				if(!p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB(WYDIN_EMPLOYMENT_ATTR)) {
					p.startConversation(new Conversation(e.getPlayer()) {
						{
							addNPC(WYDIN, HeadE.CALM_TALK, "Hey, you are not employed here!");
							create();
						}
					});
					return;
				}
				if(p.getEquipment().getChestId() != APRON) {
					p.startConversation(new Conversation(e.getPlayer()) {
						{
							addNPC(WYDIN, HeadE.CALM_TALK, "Hey, you need your apron on!");
							create();
						}
					});
					return;
				}
			}
			handleDoor(p, e.getObject());
		}
	};

	public static ObjectClickHandler handleSmuggleCrate = new ObjectClickHandler(new Object[] { 2071 }) {//backroom of Wydin
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			if(p.getQuestManager().getStage(Quest.PIRATES_TREASURE) == SMUGGLE_RUM)
				if(p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB(RUM_IN_SARIM_CRATE_ATTR)) {
					p.getInventory().addItem(new Item(RUM, 1), true);
					p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).removeB(RUM_IN_SARIM_CRATE_ATTR);
					p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).setB(HAS_SMUGGLED_RUM_ATTR, true);
				} else if(p.getQuestManager().getAttribs(Quest.PIRATES_TREASURE).getB(RUM_IN_KARAMJA_CRATE_ATTR))
					p.startConversation(new Conversation(e.getPlayer()) {
						{
							addPlayer(HeadE.HAPPY_TALKING, "Darn, I should probably tell Lathus to send the crate!");
							create();
						}
					});
		}
	};

	public static NPCClickHandler handleWydin = new NPCClickHandler(WYDIN) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new WydinPiratesTreasureD(e.getPlayer()).getStart());
		}
	};
}
