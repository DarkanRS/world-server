package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.events.ItemClickEvent;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class PVPRewards {

	public static ItemClickHandler handleBrags = new ItemClickHandler(new Object[] { 20795, 20796, 20797, 20798, 20799, 20800, 20801, 20802, 20803, 20804, 20805, 20806, }, new String[] { "Brag" }) {
		@Override
		public void handle(ItemClickEvent e) {
			switch (e.getItem().getId()) {
			case 20795 -> {
				e.getPlayer().setNextAnimation(new Animation(512));
				e.getPlayer().setNextSpotAnim(new SpotAnim(91));
			}
			case 20796 -> {
				e.getPlayer().setNextAnimation(new Animation(513));
				e.getPlayer().setNextSpotAnim(new SpotAnim(91));
			}
			case 20797 -> {
				e.getPlayer().setNextAnimation(new Animation(530));
				e.getPlayer().setNextSpotAnim(new SpotAnim(91));
			}
			case 20798 -> {
				e.getPlayer().setNextAnimation(new Animation(531));
				e.getPlayer().setNextSpotAnim(new SpotAnim(92));
			}
			case 20799 -> {
				e.getPlayer().setNextAnimation(new Animation(532));
				e.getPlayer().setNextSpotAnim(new SpotAnim(92));
			}
			case 20800 -> {
				e.getPlayer().setNextAnimation(new Animation(533));
				e.getPlayer().setNextSpotAnim(new SpotAnim(92));
			}
			
			case 20801 -> {
				e.getPlayer().setNextAnimation(new Animation(534));
				e.getPlayer().setNextSpotAnim(new SpotAnim(121));
			}
			case 20802 -> {
				e.getPlayer().setNextAnimation(new Animation(534));
				e.getPlayer().setNextSpotAnim(new SpotAnim(121));
			}
			case 20803 -> {
				e.getPlayer().setNextAnimation(new Animation(412));
				e.getPlayer().setNextSpotAnim(new SpotAnim(121));
			}
			case 20804 -> {
				e.getPlayer().setNextAnimation(new Animation(412));
				e.getPlayer().setNextSpotAnim(new SpotAnim(121));
			}
			case 20805 -> {
				e.getPlayer().setNextAnimation(new Animation(361));
				e.getPlayer().setNextSpotAnim(new SpotAnim(122));
			}
			case 20806 -> {
				e.getPlayer().setNextAnimation(new Animation(361));
				e.getPlayer().setNextSpotAnim(new SpotAnim(122));
			}
			}
		}
	};

}
