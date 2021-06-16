package com.rs.game.player.content;

import com.rs.cache.loaders.EnumDefinitions;
import com.rs.cache.loaders.StructDefinitions;
import com.rs.game.player.Player;
import com.rs.game.player.dialogues.MakeOverMage;
import com.rs.game.player.dialogues.SimpleMessage;
import com.rs.game.player.dialogues.SimpleNPCMessage;
import com.rs.lib.game.Animation;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;

@PluginEventHandler
public final class PlayerLook {

	public static void openCharacterCustomizing(Player player) {
		player.getInterfaceManager().setTopInterface(1028, false);
		player.getPackets().setIFRightClickOps(1028, 65, 0, 11, 0);
		player.getPackets().setIFRightClickOps(1028, 128, 0, 50, 0);
		player.getPackets().setIFRightClickOps(1028, 132, 0, 250, 0);
		player.getVars().setVarBit(8093, player.getAppearance().isMale() ? 0 : 1);
	}
	
	public static ButtonClickHandler handleCharacterCustomizingButtons = new ButtonClickHandler(1028) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 138) { // confirm
				e.getPlayer().getInterfaceManager().setDefaultTopInterface();
				e.getPlayer().getTemporaryAttributes().remove("SelectWearDesignD");
				e.getPlayer().getTemporaryAttributes().remove("ViewWearDesign");
				e.getPlayer().getTemporaryAttributes().remove("ViewWearDesignD");
				e.getPlayer().getAppearance().generateAppearanceData();
			} else if (e.getComponentId() >= 68 && e.getComponentId() <= 74) {
				e.getPlayer().getTemporaryAttributes().put("ViewWearDesign", (e.getComponentId() - 68));
				e.getPlayer().getTemporaryAttributes().put("ViewWearDesignD", 0);
				setDesign(e.getPlayer(), e.getComponentId() - 68, 0);
			} else if (e.getComponentId() >= 103 && e.getComponentId() <= 105) {
				Integer index = (Integer) e.getPlayer().getTemporaryAttributes().get("ViewWearDesign");
				if (index == null)
					return;
				e.getPlayer().getTemporaryAttributes().put("ViewWearDesignD", (e.getComponentId() - 103));
				setDesign(e.getPlayer(), index, e.getComponentId() - 103);
			} else if (e.getComponentId() == 62 || e.getComponentId() == 63) {
				setGender(e.getPlayer(), e.getComponentId() == 62);
			} else if (e.getComponentId() == 65) {
				setSkin(e.getPlayer(), e.getSlotId());
			} else if (e.getComponentId() >= 116 && e.getComponentId() <= 121) {
				e.getPlayer().getTemporaryAttributes().put("SelectWearDesignD", (e.getComponentId() - 116));
			} else if (e.getComponentId() == 128) {
				Integer index = (Integer) e.getPlayer().getTemporaryAttributes().get("SelectWearDesignD");
				if (index == null || index == 1) {
					boolean male = e.getPlayer().getAppearance().isMale();
					int map1 = EnumDefinitions.getEnum(male ? 3304 : 3302).getIntValue(e.getSlotId());
					if (map1 == 0)
						return;
					StructDefinitions map = StructDefinitions.getStruct(map1);
					e.getPlayer().getAppearance().setHairStyle(map.getIntValue(788));
					if (!male)
						e.getPlayer().getAppearance().setBeardStyle(e.getPlayer().getAppearance().getHairStyle());
				} else if (index == 2) {
					e.getPlayer().getAppearance().setTopStyle(EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 3287 : 1591).getIntValue(e.getSlotId()));
					e.getPlayer().getAppearance().generateAppearanceData();
				} else if (index == 3)
					e.getPlayer().getAppearance().setLegsStyle(EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 3289 : 1607).getIntValue(e.getSlotId()));
				else if (index == 4)
					e.getPlayer().getAppearance().setBootsStyle(EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 1136 : 1137).getIntValue(e.getSlotId()));
				else if (e.getPlayer().getAppearance().isMale())
					e.getPlayer().getAppearance().setBeardStyle(EnumDefinitions.getEnum(3307).getIntValue(e.getSlotId()));
				e.getPlayer().getAppearance().generateAppearanceData();
			} else if (e.getComponentId() == 132) {
				Integer index = (Integer) e.getPlayer().getTemporaryAttributes().get("SelectWearDesignD");
				if (index == null || index == 0)
					setSkin(e.getPlayer(), e.getSlotId());
				else {
					if (index == 1 || index == 5)
						e.getPlayer().getAppearance().setHairColor(EnumDefinitions.getEnum(2345).getIntValue(e.getSlotId()));
					else if (index == 2)
						e.getPlayer().getAppearance().setTopColor(EnumDefinitions.getEnum(3283).getIntValue(e.getSlotId()));
					else if (index == 3)
						e.getPlayer().getAppearance().setLegsColor(EnumDefinitions.getEnum(3283).getIntValue(e.getSlotId()));
					else
						e.getPlayer().getAppearance().setBootsColor(EnumDefinitions.getEnum(3297).getIntValue(e.getSlotId()));
				}
			}
			e.getPlayer().getAppearance().generateAppearanceData();
		}
	};

	public static void setGender(Player player, boolean male) {
		if (male == player.getAppearance().isMale())
			return;
		if (!male)
			player.getAppearance().female();
		else
			player.getAppearance().male();
		Integer index1 = (Integer) player.getTemporaryAttributes().get("ViewWearDesign");
		Integer index2 = (Integer) player.getTemporaryAttributes().get("ViewWearDesignD");
		setDesign(player, index1 != null ? index1 : 0, index2 != null ? index2 : 0);
		player.getAppearance().generateAppearanceData();
		player.getVars().setVarBit(8093, male ? 0 : 1);
	}

	public static void setSkin(Player player, int index) {
		player.getAppearance().setSkinColor(EnumDefinitions.getEnum(748).getIntValue(index));
		player.getAppearance().generateAppearanceData();
	}

	public static void setDesign(Player player, int index1, int index2) {
		int map1 = EnumDefinitions.getEnum(3278).getIntValue(index1);
		if (map1 == 0)
			return;
		boolean male = player.getAppearance().isMale();
		int map2Id = StructDefinitions.getStruct(map1).getIntValue((male ? 1169 : 1175) + index2);
		if (map2Id == 0)
			return;
		StructDefinitions map = StructDefinitions.getStruct(map2Id);
		for (int i = 1182; i <= 1186; i++) {
			int value = map.getIntValue(i);
			if (value == -1)
				continue;
			player.getAppearance().setLook(i - 1180, value);
		}
		for (int i = 1187; i <= 1190; i++) {
			int value = map.getIntValue(i);
			if (value == -1)
				continue;
			player.getAppearance().setColor(i - 1186, value);
		}
		if (!player.getAppearance().isMale())
			player.getAppearance().setBeardStyle(player.getAppearance().getHairStyle());
		player.getAppearance().generateAppearanceData();
	}
	
	public static ButtonClickHandler handleMageMakeOverButtons = new ButtonClickHandler(900) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 14 || e.getComponentId() == 16 || e.getComponentId() == 15 || e.getComponentId() == 17)
				e.getPlayer().getTemporaryAttributes().put("MageMakeOverGender", e.getComponentId() == 14 || e.getComponentId() == 16);
			else if (e.getComponentId() >= 20 && e.getComponentId() <= 31) {

				int skin;
				if (e.getComponentId() == 31)
					skin = 11;
				else if (e.getComponentId() == 30)
					skin = 10;
				else if (e.getComponentId() == 20)
					skin = 9;
				else if (e.getComponentId() == 21)
					skin = 8;
				else if (e.getComponentId() == 22)
					skin = 7;
				else if (e.getComponentId() == 29)
					skin = 6;
				else if (e.getComponentId() == 28)
					skin = 5;
				else if (e.getComponentId() == 27)
					skin = 4;
				else if (e.getComponentId() == 26)
					skin = 3;
				else if (e.getComponentId() == 25)
					skin = 2;
				else if (e.getComponentId() == 24)
					skin = 1;
				else
					skin = 0;
				e.getPlayer().getTemporaryAttributes().put("MageMakeOverSkin", skin);
			} else if (e.getComponentId() == 33) {
				Boolean male = (Boolean) e.getPlayer().getTemporaryAttributes().remove("MageMakeOverGender");
				Integer skin = (Integer) e.getPlayer().getTemporaryAttributes().remove("MageMakeOverSkin");
				e.getPlayer().closeInterfaces();
				if (male == null || skin == null)
					return;
				if (male == e.getPlayer().getAppearance().isMale() && skin == e.getPlayer().getAppearance().getSkinColor())
					e.getPlayer().getDialogueManager().execute(new MakeOverMage(), 2676, 1);
				else {
					e.getPlayer().getDialogueManager().execute(new MakeOverMage(), 2676, 2);
					if (e.getPlayer().getAppearance().isMale() != male) {
						if (e.getPlayer().getEquipment().wearingArmour()) {
							e.getPlayer().getDialogueManager().execute(new SimpleMessage(), "You cannot have armor on while changing your gender.");
							return;
						}
						if (male)
							e.getPlayer().getAppearance().resetAppearance();
						else
							e.getPlayer().getAppearance().female();
					}
					e.getPlayer().getAppearance().setSkinColor(skin);
					e.getPlayer().getAppearance().generateAppearanceData();
				}
			}
		}
	};
	
	public static ButtonClickHandler handleHairdresserSalonButtons = new ButtonClickHandler(309) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 6)
				e.getPlayer().getTemporaryAttributes().put("hairSaloon", true);
			else if (e.getComponentId() == 7)
				e.getPlayer().getTemporaryAttributes().put("hairSaloon", false);
			else if (e.getComponentId() == 18) {
				e.getPlayer().closeInterfaces();
			} else if (e.getComponentId() == 10) {
				Boolean hairSalon = (Boolean) e.getPlayer().getTemporaryAttributes().get("hairSaloon");
				if (hairSalon != null && hairSalon) {
					int value = (int) EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 2339 : 2342).getKeyForValue(e.getSlotId() / 2);
					if (value == -1)
						return;
					e.getPlayer().getAppearance().setHairStyle(value);
				} else if (e.getPlayer().getAppearance().isMale()) {
					int value = EnumDefinitions.getEnum(703).getIntValue(e.getSlotId() / 2);
					if (value == -1)
						return;
					e.getPlayer().getAppearance().setBeardStyle(value);
				}
			} else if (e.getComponentId() == 16) {
				int value = EnumDefinitions.getEnum(2345).getIntValue(e.getSlotId() / 2);
				if (value == -1)
					return;
				e.getPlayer().getAppearance().setHairColor(value);
			}
		}
	};

	public static void openMageMakeOver(Player player) {
		player.getInterfaceManager().sendInterface(900);
		player.getPackets().setIFText(900, 33, "Confirm");
		player.getVars().setVarBit(6098, player.getAppearance().isMale() ? 0 : 1);
		player.getVars().setVarBit(6099, player.getAppearance().getSkinColor());
		player.getTemporaryAttributes().put("MageMakeOverGender", player.getAppearance().isMale());
		player.getTemporaryAttributes().put("MageMakeOverSkin", player.getAppearance().getSkinColor());
	}
	
	public static ButtonClickHandler handleThessaliasMakeOverButtons = new ButtonClickHandler(729) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId()== 6)
				e.getPlayer().getTemporaryAttributes().put("ThessaliasMakeOver", 0);
			else if (e.getComponentId() == 7) {
				if (EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 690 : 1591).getKeyForValue(e.getPlayer().getAppearance().getTopStyle()) >= 32) {
					e.getPlayer().getTemporaryAttributes().put("ThessaliasMakeOver", 1);
				} else
					e.getPlayer().sendMessage("You can't select different arms to go with that top.");
			} else if (e.getComponentId() == 8) {
				if (EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 690 : 1591).getKeyForValue(e.getPlayer().getAppearance().getTopStyle()) >= 32) {
					e.getPlayer().getTemporaryAttributes().put("ThessaliasMakeOver", 2);
				} else
					e.getPlayer().sendMessage("You can't select different wrists to go with that top.");
			} else if (e.getComponentId() == 9)
				e.getPlayer().getTemporaryAttributes().put("ThessaliasMakeOver", 3);
			else if (e.getComponentId() == 19) { // confirm
				e.getPlayer().closeInterfaces();
			} else if (e.getComponentId() == 12) { // set part
				Integer stage = (Integer) e.getPlayer().getTemporaryAttributes().get("ThessaliasMakeOver");
				if (stage == null || stage == 0)
					e.getPlayer().getAppearance().setTopStyle(EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 690 : 1591).getIntValue(e.getSlotId() / 2));
				else if (stage == 1) // arms
					e.getPlayer().getAppearance().setArmsStyle((int) EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 711 : 693).getIntValue(e.getSlotId() / 2));
				else if (stage == 2) // wrists
					e.getPlayer().getAppearance().setWristsStyle((int) EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 749 : 751).getIntValue(e.getSlotId() / 2));
				else
					e.getPlayer().getAppearance().setLegsStyle((int) EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 1586 : 1607).getIntValue(e.getSlotId() / 2));

			} else if (e.getComponentId() == 17) {// color
				Integer stage = (Integer) e.getPlayer().getTemporaryAttributes().get("ThessaliasMakeOver");
				if (stage == null || stage == 0 || stage == 1)
					e.getPlayer().getAppearance().setTopColor(EnumDefinitions.getEnum(3282).getIntValue(e.getSlotId() / 2));
				else if (stage == 3)
					e.getPlayer().getAppearance().setLegsColor(EnumDefinitions.getEnum(3284).getIntValue(e.getSlotId() / 2));
			}
			e.getPlayer().getAppearance().generateAppearanceData();
		}
	};

	public static void openThessaliasMakeOver(final Player player) {
		if (player.getEquipment().wearingArmour()) {
			player.getDialogueManager().execute(new SimpleNPCMessage(), 548, "You're not able to try on my clothes with all that armour. Take it off and then speak to me again.");
			return;
		}
		player.setNextAnimation(new Animation(11623));
		player.getInterfaceManager().sendInterface(729);
		player.getPackets().setIFText(729, 21, "Free!");
		player.getTemporaryAttributes().put("ThessaliasMakeOver", 0);
		player.getPackets().setIFRightClickOps(729, 12, 0, 100, 0);
		player.getPackets().setIFRightClickOps(729, 17, 0, EnumDefinitions.getEnum(3282).getSize() * 2, 0);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getDialogueManager().execute(new SimpleNPCMessage(), 548, "A marvellous choice. You look splendid!");
				player.setNextAnimation(new Animation(-1));
				player.getAppearance().generateAppearanceData();
				player.getTemporaryAttributes().remove("ThessaliasMakeOver");
			}

		});
	}

	public static void openHairdresserSalon(final Player player) {
		if (player.getEquipment().getHatId() != -1) {
			player.getDialogueManager().execute(new SimpleNPCMessage(), 598, "I'm afraid I can't see your head at the moment. Please remove your headgear first.");
			return;
		}
		if (player.getEquipment().getWeaponId() != -1 || player.getEquipment().getShieldId() != -1) {
			player.getDialogueManager().execute(new SimpleNPCMessage(), 598, "I don't feel comfortable cutting hair when you are wielding something. Please remove what you are holding first.");
			return;
		}
		player.setNextAnimation(new Animation(11623));
		player.getInterfaceManager().sendInterface(309);
		player.getPackets().setIFRightClickOps(309, 10, 0, EnumDefinitions.getEnum(player.getAppearance().isMale() ? 2339 : 2342).getSize() * 2, 0);
		player.getPackets().setIFRightClickOps(309, 16, 0, EnumDefinitions.getEnum(2345).getSize() * 2, 0);
		player.getPackets().setIFText(309, 20, "Free!");
		player.getTemporaryAttributes().put("hairSaloon", true);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getDialogueManager().execute(new SimpleNPCMessage(), 598, "An excellent choice, " + (player.getAppearance().isMale() ? "sir" : "madam") + ".");
				player.setNextAnimation(new Animation(-1));
				player.getAppearance().generateAppearanceData();
				player.getTemporaryAttributes().remove("hairSaloon");
			}

		});
	}

	public static void openYrsaShop(final Player player) {
		if (player.getEquipment().getBootsId() != -1) {
			player.getDialogueManager().execute(new SimpleNPCMessage(), 1301, "I don't feel comfortable helping you try on new boots when you are wearing some already.", "Please remove your boots first.");
			return;
		}
		player.setNextAnimation(new Animation(11623));
		player.getInterfaceManager().sendInterface(728);
		player.getPackets().setIFText(728, 16, "Free");
		player.getTemporaryAttributes().put("YrsaBoot", 0);
		player.getPackets().setIFRightClickOps(728, 12, 0, 500, 0);
		player.getPackets().setIFRightClickOps(728, 7, 0, EnumDefinitions.getEnum(3297).getSize() * 2, 0);
		player.setCloseInterfacesEvent(new Runnable() {

			@Override
			public void run() {
				player.getDialogueManager().execute(new SimpleNPCMessage(), 1301, "Hey, They look great!");
				player.setNextAnimation(new Animation(-1));
				player.getAppearance().generateAppearanceData();
				player.getTemporaryAttributes().remove("YrsaBoot");
			}
		});
	}
	
	public static ButtonClickHandler handleYrsaShoes = new ButtonClickHandler(728) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() == 14)
				e.getPlayer().closeInterfaces();
			else if (e.getComponentId() == 12) {// setting the colors.
				e.getPlayer().getAppearance().setBootsColor(EnumDefinitions.getEnum(3297).getIntValue(e.getSlotId() / 2));
				e.getPlayer().getAppearance().generateAppearanceData();
			} else if (e.getComponentId() == 7) {// /boot style
				e.getPlayer().getAppearance().setBootsStyle((int) EnumDefinitions.getEnum(e.getPlayer().getAppearance().isMale() ? 3290 : 3293).getIntValue(e.getSlotId() / 2));
				e.getPlayer().getAppearance().generateAppearanceData();
			}
		}
	};

	private PlayerLook() {

	}

}
