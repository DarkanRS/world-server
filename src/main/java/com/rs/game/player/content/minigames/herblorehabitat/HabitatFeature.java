package com.rs.game.player.content.minigames.herblorehabitat;

import java.util.HashMap;
import java.util.Map;

import com.rs.game.World;
import com.rs.game.player.content.skills.magic.Magic;
import com.rs.lib.Constants;
import com.rs.lib.game.SpotAnim;
import com.rs.lib.game.WorldTile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ButtonClickEvent;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.events.LoginEvent;
import com.rs.plugin.events.ObjectClickEvent;
import com.rs.plugin.handlers.ButtonClickHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.LoginHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public enum HabitatFeature {
	Boneyard(7, 56, 75),
	Abandoned_house(3, 57, 71),
	Thermal_vent(4, 59, 72),
	Tall_grass(2, 62, 70),
	Pond(1, 65, 68),
	Standing_stones(5, 70, 73),
	Dark_pit(6, 80, 74);
	
	private static Map<Integer, HabitatFeature> BUTTON_MAP = new HashMap<>();
	
	static {
		for (HabitatFeature h : HabitatFeature.values())
			BUTTON_MAP.put(h.button, h);
	}
	
	public static HabitatFeature forButton(int buttonId) {
		return BUTTON_MAP.get(buttonId);
	}
	
	public final int val;
	public final int level;
	public final int button;
	
	private HabitatFeature(int val, int level, int button) {
		this.val = val;
		this.level = level;
		this.button = button;
	}
	
	public static LoginHandler updateFeature = new LoginHandler() {
		@Override
		public void handle(LoginEvent e) {
			e.getPlayer().setHabitatFeature(e.getPlayer().getHabitatFeature());
		}
	};
	
	public static ObjectClickHandler handleFeatureBuild = new ObjectClickHandler(new Object[] { 56803 }) {
		@Override
		public void handle(ObjectClickEvent e) {
			e.getPlayer().getInterfaceManager().sendInterface(459);
		}
	};
	
	public static ButtonClickHandler handleFeature = new ButtonClickHandler(459) {
		@Override
		public void handle(ButtonClickEvent e) {
			if (e.getComponentId() >= 68 && e.getComponentId() <= 75) {
				HabitatFeature toBuild = HabitatFeature.forButton(e.getComponentId());
				if (toBuild == null)
					e.getPlayer().setHabitatFeature(null);
				else {
					if (e.getPlayer().getSkills().getLevel(Constants.CONSTRUCTION) < toBuild.level) {
						e.getPlayer().sendMessage("You need a Construction level of " + toBuild.level + " to build a " + toBuild.name() + ".");
						return;
					}
					e.getPlayer().setHabitatFeature(toBuild);
				}
				World.sendSpotAnim(e.getPlayer(), new SpotAnim(1605), new WorldTile(2952, 2908, 0));
				e.getPlayer().closeInterfaces();	
			}
		}
	};
	
	public static ItemClickHandler handleWitchdoctorTele = new ItemClickHandler(new Object[] { 20046 }, new String[] { "Teleport" }) {
		@Override
		public void handle(ItemClickEvent e) {
			Magic.sendTeleportSpell(e.getPlayer(), 7082, 7084, 1229, 1229, 1, 0, new WorldTile(2952, 2933, 0), 4, true, Magic.ITEM_TELEPORT);
		}
	};
}
