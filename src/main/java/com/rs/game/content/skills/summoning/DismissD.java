package com.rs.game.content.skills.summoning;

import com.rs.engine.dialogue.Conversation;
import com.rs.engine.dialogue.Dialogue;
import com.rs.engine.dialogue.HeadE;
import com.rs.engine.dialogue.Options;
import com.rs.game.model.entity.player.Player;
import com.rs.game.model.entity.player.managers.InterfaceManager;
import com.rs.plugin.annotations.PluginEventHandler;

@PluginEventHandler
public class DismissD extends Conversation {
	public DismissD(Player p) {
		super(p);
		addOptions(p.getPet() != null ? "Free pet?" : "Dismiss Familiar?", new Options() {
			@Override
			public void create() {
				if(p.getFamiliar() != null) 
					option("Yes.", new Dialogue()
							.addNext(()->{p.getFamiliar().dismiss();})
					);
				else if(p.getPet() != null)
					option("Yes.", new Dialogue()
							.addPlayer(HeadE.HAPPY_TALKING, "Run along; I'm setting you free.")
							.addNext(()->{
								p.getPetManager().setNpcId(-1);
								p.getPetManager().setItemId(-1);
								p.getPetManager().removeDetails(p.getPet().getItemId());
								p.getPackets().sendRunScript(2471);
								p.getInterfaceManager().removeSub(InterfaceManager.Sub.TAB_FOLLOWER);
								p.getPet().finish();
								p.setPet(null);
								p.sendMessage("Your pet runs off until it's out of sight.");
							})
					);
				option("No.", new Dialogue()
						.addPlayer(HeadE.HAPPY_TALKING, "")
				);
			}
		});
	}
}
