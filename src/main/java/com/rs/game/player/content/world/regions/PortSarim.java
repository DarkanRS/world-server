// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.  If not, see <http://www.gnu.org/licenses/>.
//
//  Copyright © 2021 Trenton Kress
//  This file is part of project: Darkan
//
package com.rs.game.player.content.world.regions;

import com.rs.game.World;
import com.rs.game.npc.NPC;
import com.rs.game.object.GameObject;
import com.rs.game.player.Player;
import com.rs.game.player.content.Skillcapes;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.Options;
import com.rs.game.player.content.dialogue.impl.skillmasters.GenericSkillcapeOwnerD;
import com.rs.game.player.quests.Quest;
import com.rs.game.player.quests.handlers.knightssword.KnightsSword;
import com.rs.game.player.quests.handlers.knightssword.ThurgoKnightsSwordD;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.*;
import com.rs.plugin.handlers.ItemAddedToInventoryHandler;
import com.rs.plugin.handlers.ItemOnNPCHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

import java.util.List;

@PluginEventHandler
public class PortSarim {

	public static ItemOnNPCHandler handleThurgoItem = new ItemOnNPCHandler(604) {
		@Override
		public void handle(ItemOnNPCEvent e) {
			if (e.getItem().getId() == 24303 || e.getItem().getId() == 24339)
				e.getPlayer().sendOptionDialogue("Would you like Thurgo to "+(e.getItem().getId() == 24339 ? "repair" : "forge")+" your Royal Crossbow?", new String[] { "Yes, please (Requires a stabilizer, frame, sight, and spring)", "No, thanks." }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							if (player.getInventory().containsItems(new Item(24340), new Item(24342), new Item(24344), new Item(24346))) {
								player.getInventory().deleteItem(e.getItem().getId(), 1);
								player.getInventory().deleteItem(24340, 1);
								player.getInventory().deleteItem(24342, 1);
								player.getInventory().deleteItem(24344, 1);
								player.getInventory().deleteItem(24346, 1);
								player.getInventory().addItem(e.getItem().getId() == 24339 ? 24338 : 24337, 1);
								player.sendMessage("Thurgo "+(e.getItem().getId() == 24339 ? "repairs" : "forges")+" your Royal crossbow.");
							}
					}
				});
		}
	};

    public static ItemAddedToInventoryHandler handlePortSarimApron= new ItemAddedToInventoryHandler(7957) { //Apron in port sarim fishing shop
        @Override
        public void handle(ItemAddedToInventoryEvent e) {
            Player p = e.getPlayer();
            p.getInventory().removeItems(e.getItem());
            p.getInventory().addItem(1005, 1);
        }
    };

	public static NPCClickHandler handleThurgo = new NPCClickHandler(604) {
		@Override
		public void handle(NPCClickEvent e) {
			e.getPlayer().startConversation(new Conversation(e.getPlayer()) {
				{
					addOptions("What would you like to say?", new Options() {
						@Override
						public void create() {
							if(player.getQuestManager().getStage(Quest.KNIGHTS_SWORD) >= KnightsSword.FIND_DWARF)
								option("About Knight's Sword.", new Dialogue()
										.addNext(()->{e.getPlayer().startConversation(new ThurgoKnightsSwordD(e.getPlayer()).getStart());}));
							option("About that skill cape...", new Dialogue()
									.addNext(()->{player.startConversation(new GenericSkillcapeOwnerD(player, 604,Skillcapes.Smithing));})
									);
						}
					});
					create();
				}
			});
		}
	};

	public static ObjectClickHandler handleEnterIceDungeon = new ObjectClickHandler(new Object[] { 9472 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3007, 9550, 0));
		}
	};

	public static ObjectClickHandler handleExitIceDungeon = new ObjectClickHandler(new Object[] { 32015 }, new WorldTile(3008, 9550, 0)) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3008, 3149, 0));
		}
	};

	public static ObjectClickHandler handleEnterWyvern = new ObjectClickHandler(new Object[] { 33173 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3056, 9555, 0));
		}
	};

	public static ObjectClickHandler handleExitWyvern = new ObjectClickHandler(new Object[] { 33174 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3056, 9562, 0));
		}
	};

	public static ObjectClickHandler handleEnterLadyLumbridgeBoat = new ObjectClickHandler(new Object[] { 2594, 2593 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(p.getY() > obj.getY())
				e.getPlayer().setNextWorldTile(new WorldTile(3047, 3204, 0));
			if(p.getY() < obj.getY())
				e.getPlayer().setNextWorldTile(new WorldTile(3047, 3207, 1));
		}
	};

	public static ObjectClickHandler handleEnterLadyLumbridgeBoatUpperLadder = new ObjectClickHandler(new Object[] { 2590 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			p.useStairs(828, new WorldTile(obj.getX()-1, obj.getY(), obj.getPlane() - 1), 1, 2);
		}
	};

	public static ObjectClickHandler handleLowerBoatLadder = new ObjectClickHandler(new Object[] { 272 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			Player p = e.getPlayer();
			GameObject obj = e.getObject();
			if(obj.getRotation() == 0)
				p.useStairs(828, new WorldTile(obj.getX(), obj.getY()-1, obj.getPlane() + 1), 1, 2);
			if(obj.getRotation() == 1)
				p.useStairs(828, new WorldTile(obj.getX()-1, obj.getY(), obj.getPlane() + 1), 1, 2);
			if(obj.getRotation() == 2)
				p.useStairs(828, new WorldTile(obj.getX(), obj.getY()+1, obj.getPlane() + 1), 1, 2);
			if(obj.getRotation() == 3)
				p.useStairs(828, new WorldTile(obj.getX()+1, obj.getY(), obj.getPlane() + 1), 1, 2);
		}
	};
}
