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

import com.rs.game.player.Player;
import com.rs.game.player.content.dialogue.Conversation;
import com.rs.game.player.content.dialogue.Dialogue;
import com.rs.game.player.content.dialogue.HeadE;
import com.rs.game.player.content.dialogue.Options;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Item;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.DialogueOptionEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.NPCClickEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.NPCClickHandler;
import com.rs.plugin.handlers.ObjectClickHandler;
import com.rs.utils.shop.ShopsHandler;

@PluginEventHandler
public class Desert  {

	private enum CarpetLocation {
		SHANTAY_PASS(2291, new WorldTile(3308, 3109, 0)),
		BEDABIN_CAMP(2292, new WorldTile(3180, 3045, 0)),
		S_POLLNIVNEACH(2293, new WorldTile(3351, 2942, 0)),
		N_POLLNIVNEACH(2294, new WorldTile(3349, 3003, 0)),
		UZER(2295, new WorldTile(3469, 3113, 0)),
		SOPHANEM(2297, new WorldTile(3285, 2813, 0)),
		MENAPHOS(2299, new WorldTile(3245, 2813, 0)),
		NARDAH(3020, new WorldTile(3401, 2916, 0)),
		MONKEY_COLONY(13237, new WorldTile(3227, 2988, 0));

		private int npcId;
		private WorldTile tile;

		public static CarpetLocation forId(int npcId) {
			for (CarpetLocation loc : CarpetLocation.values())
				if (loc.npcId == npcId)
					return loc;
			return null;
		}

		private CarpetLocation(int npcId, WorldTile tile) {
			this.npcId = npcId;
			this.tile = tile;
		}
	}

	public static NPCClickHandler handleCarpetMerchants = new NPCClickHandler(2291, 2292, 2293, 2294, 2295, 2297, 2299, 3020, 13237) {
		@Override
		public void handle(NPCClickEvent e) {
			CarpetLocation loc = CarpetLocation.forId(e.getNPC().getId());
			switch(loc) {
			case SHANTAY_PASS:
				e.getPlayer().sendOptionDialogue("Where would you like to travel?", new String[] { "Pollnivneach", "Bedabin Camp", "Uzer", "Monkey Colony", "Nevermind" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(CarpetLocation.N_POLLNIVNEACH.tile);
						else if (option == 2)
							player.setNextWorldTile(CarpetLocation.BEDABIN_CAMP.tile);
						else if (option == 3)
							player.setNextWorldTile(CarpetLocation.UZER.tile);
						else if (option == 4)
							player.setNextWorldTile(CarpetLocation.MONKEY_COLONY.tile);
					}
				});
				break;
			case N_POLLNIVNEACH:
			case UZER:
			case BEDABIN_CAMP:
			case MONKEY_COLONY:
				e.getPlayer().sendOptionDialogue("Where would you like to travel?", new String[] { "Shantay Pass", "Nevermind" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(CarpetLocation.SHANTAY_PASS.tile);
					}
				});
				break;
			case S_POLLNIVNEACH:
				e.getPlayer().sendOptionDialogue("Where would you like to travel?", new String[] { "Nardah", "Sophanem", "Menaphos", "Nevermind" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(CarpetLocation.NARDAH.tile);
						else if (option == 2)
							player.setNextWorldTile(CarpetLocation.SOPHANEM.tile);
						else if (option == 3)
							player.setNextWorldTile(CarpetLocation.MENAPHOS.tile);
					}
				});
				break;
			case NARDAH:
			case SOPHANEM:
			case MENAPHOS:
				e.getPlayer().sendOptionDialogue("Where would you like to travel?", new String[] { "Pollnivneach", "Nevermind" }, new DialogueOptionEvent() {
					@Override
					public void run(Player player) {
						if (option == 1)
							player.setNextWorldTile(CarpetLocation.S_POLLNIVNEACH.tile);
					}
				});
				break;
			default:
				break;
			}
		}
	};

	public static ObjectClickHandler handleSpiritWaterfall = new ObjectClickHandler(new Object[] { 10417, 63173 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObjectId() == 63173)
				e.getPlayer().useStairs(new WorldTile(3348, 9535, 0));
			else
				e.getPlayer().useStairs(new WorldTile(3370, 3129, 0));
		}
	};

	public static LoginHandler unlockMonkeyColonyRugMerchant = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().getVars().setVarBit(8628, 1);
			e.getPlayer().getVars().setVarBit(8628, 1);
			e.getPlayer().getVars().setVarBit(8628, 1);
			e.getPlayer().getVars().setVarBit(395, 1); //unlock menaphos rug
		}
	};

	public static ObjectClickHandler handleCurtainDoors = new ObjectClickHandler(new Object[] { 1528 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().getRotation() == 2 || e.getObject().getRotation() == 0)
				e.getPlayer().walkOneStep(e.getPlayer().getX() > e.getObject().getX() ? -1 : 1, 0, false);
			else
				e.getPlayer().walkOneStep(0, e.getPlayer().getY() == e.getObject().getY() ? -1 : 1, false);
		}
	};

	public static ObjectClickHandler handleEnterTTMine = new ObjectClickHandler(new Object[] { 2675, 2676 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(new WorldTile(3279, 9427, 0));
		}
	};

	public static ObjectClickHandler handleExitTTMine = new ObjectClickHandler(new Object[] { 2690, 2691 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().useStairs(new WorldTile(3301, 3036, 0));
		}
	};

	public static NPCClickHandler handleBanditBartender = new NPCClickHandler(1921) {
		@Override
		public void handle(NPCClickEvent e) {
			ShopsHandler.openShop(e.getPlayer(), "the_big_heist_lodge");
		}
	};

    public static NPCClickHandler aliSnakeCharmer = new NPCClickHandler(1872) {
        @Override
        public void handle(NPCClickEvent e) {
            Player p = e.getPlayer();
            p.startConversation(new Conversation(p) {
                {
                    addPlayer(HeadE.HAPPY_TALKING, "Hello...");
                    addNPC(1872, HeadE.FRUSTRATED, "What do you want " + p.getPronoun("sir", "m'am") + "?");
                    addOptions("Choose an option:", new Options() {
                        @Override
                        public void create() {
                            option("Would you like some money?", new Dialogue()
                                    .addPlayer(HeadE.CHEERFUL, "Would you like some money?")
                                    .addNPC(1872, HeadE.FRUSTRATED, "Why else would I sit here with a dangerous snake?")
                                    .addOptions("Give money?", new Options() {
                                        @Override
                                        public void create() {
                                            if(p.getInventory().containsItem(995, 1))
                                                option("Yes.", new Dialogue()
                                                        .addItem(995, "You give the charmer 1 coin", ()->{
                                                            p.getInventory().removeItems(new Item(995, 1));
                                                        })
                                                        .addNPC(1872, HeadE.CHEERFUL, "Oh thank you so much! Please please come again")
                                                        .addSimple("You feel swindled...")
                                                );
                                            else
                                                option("I would give you if I had some...", new Dialogue()
                                                        .addPlayer(HeadE.CHEERFUL, "I would give you money if I had it.")
                                                        .addNPC(1872, HeadE.FRUSTRATED, "Leave me alone.")
                                                );
                                            option("No.", new Dialogue());
                                        }
                                    })
                            );
                            option("Does the snake ever bite?", new Dialogue()
                                    .addPlayer(HeadE.CHEERFUL, "Does the snake ever bite?")
                                    .addNPC(1872, HeadE.SECRETIVE, "It's trained not to.")
                                    .addPlayer(HeadE.AMAZED, "That's cheating isn't it?")
                                    .addNPC(1872, HeadE.CALM_TALK, "Please, leave me alone.")
                            );
                            option("Can I try your flute?", new Dialogue()
                                    .addPlayer(HeadE.CHEERFUL, "Can I try your flute?")
                                    .addSimple("He looks upset...")
                                    .addNPC(1872, HeadE.FRUSTRATED, "Will it get you off my back?")
                                    .addPlayer(HeadE.SECRETIVE, "Umm, sure.")
                                    .addSimple("He pulls out a set of flutes...")
                                    .addNPC(1872, HeadE.FRUSTRATED, "I will give you one, just leave me alone")
                                    .addItem(4605, "He gives you a flute", ()-> {
                                        p.getInventory().addItem(4605, 1);
                                    })
                            );
                        }
                    });
                    create();
                }
            });
        }
    };

	public static ObjectClickHandler handlePyramidBackEntrance = new ObjectClickHandler(new Object[] { 6481 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().setNextWorldTile(new WorldTile(3233, 9310, 0));
		}
	};

	public static ObjectClickHandler handlePyramidSarcophagi = new ObjectClickHandler(new Object[] { 6516 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			if (e.getObject().isAt(3233, 9309))
				e.getPlayer().setNextWorldTile(new WorldTile(3233, 2887, 0));
			else
				e.getPlayer().sendMessage("You search the sarcophagus but find nothing.");
		}
	};

	public static ItemClickHandler handleSplittingGranite = new ItemClickHandler(new Object[] { 6979, 6981, 6983 }, new String[] { "Craft" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!e.getPlayer().getInventory().containsItem(1755, 1, true)) {
				e.getPlayer().sendMessage("You must have a chisel in order to craft granite.");
				return;
			}

			if (e.getPlayer().getInventory().getFreeSlots() < 3) {
				e.getPlayer().sendMessage("You do not have enough room in your inventory to split the granite.");
				return;
			}

			e.getPlayer().lock(2);

			switch(e.getItem().getId()) {
			case 6983: //5kg - splits into 2x 2kg and 2x 500g
				e.getPlayer().getInventory().deleteItem(6983, 1);
				e.getPlayer().getInventory().addItem(6981, 2);
				e.getPlayer().getInventory().addItem(6979, 2);
				e.getPlayer().setNextAnimation(new Animation(11146));
				break;
			case 6981: //2kg - splits into 4x 500g
				e.getPlayer().getInventory().deleteItem(6981, 1);
				e.getPlayer().getInventory().addItem(6979, 4);
				e.getPlayer().setNextAnimation(new Animation(11146));
				break;
			case 6979: //500g
				e.getPlayer().sendMessage("This block of granite is too small to craft into anything.");
				break;
			}
		}
	};

	public static ItemClickHandler handleSplittingSandstone = new ItemClickHandler(new Object[] { 6973, 6975, 6977 }, new String[] { "Craft" }) {
		@Override
		public void handle(ItemClickEvent e) {
			if (!e.getPlayer().getInventory().containsItem(1755, 1, true)) {
				e.getPlayer().sendMessage("You must have a chisel in order to craft sandstone.");
				return;
			}

			if (e.getPlayer().getInventory().getFreeSlots() < (e.getItem().getId() == 6975 ? 2 : 1)) {
				e.getPlayer().sendMessage("You do not have enough room in your inventory to split the sandstone.");
				return;
			}

			e.getPlayer().lock(2);

			switch(e.getItem().getId()) {
			case 6977: //10kg - splits into 2x 5kg
				e.getPlayer().getInventory().deleteItem(6977, 1);
				e.getPlayer().getInventory().addItem(6975, 2);
				e.getPlayer().setNextAnimation(new Animation(11146));
				break;
			case 6975: //5kg - splits into 2x 2kg and 1x 1kg
				e.getPlayer().getInventory().deleteItem(6975, 1);
				e.getPlayer().getInventory().addItem(6973, 2);
				e.getPlayer().getInventory().addItem(6971, 1);
				e.getPlayer().setNextAnimation(new Animation(11146));
				break;
			case 6973: //2kg - splits into 2x 1kg
				e.getPlayer().getInventory().deleteItem(6973, 1);
				e.getPlayer().getInventory().addItem(6971, 2);
				e.getPlayer().setNextAnimation(new Animation(11146));
				break;
			}
		}
	};
}
