package com.rs.game.content.items;

import com.rs.game.content.minigames.pyramidplunder.PPArtefact;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Item;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;
import com.rs.plugin.handlers.ItemOnItemHandler;

@PluginEventHandler
public class PharaohSceptre {
    private static final int[] PHARAOHS_SCEPTRE = new int[] {9050, 9048, 9046, 9044};
    private static final Tile JALSAVRAH = Tile.of(1968,4428,2);
    private static final Tile JALEUSTROPHOS = Tile.of(3340, 2828, 0);
    private static final Tile JALDRAOCHT = Tile.of(3232, 2895, 0);

    public static ItemClickHandler handlePharaohsSceptre = new ItemClickHandler(new Object[] { 9050, 9048, 9046, 9044 }, new String[] { "Teleport" }, e -> {
        if (e.getItem().getId() == PHARAOHS_SCEPTRE[0]){
            e.getPlayer().sendMessage("There are no charges remaining.");
            return;
        }
        else {
            e.getPlayer().sendMessage("Teleporting");
            e.getPlayer().sendOptionDialogue("Where would you like to go?", ops -> {
                ops.add("Jalsavrah", () -> {
                    Magic.sendTeleportSpell(e.getPlayer(), 12441, 12442, 2172, 2173, 0, 0, JALSAVRAH, 3, true, Magic.MAGIC_TELEPORT, null);
                    removeCharge(e.getItem(), e.getPlayer());
                });
                ops.add("Jaleustrophos", () -> {
                    Magic.sendTeleportSpell(e.getPlayer(), 12441, 12442, 2172, 2173, 0, 0, JALEUSTROPHOS, 3, true, Magic.MAGIC_TELEPORT, null);
                    removeCharge(e.getItem(), e.getPlayer());
                });
                ops.add("Jaldraocht", () -> {
                    Magic.sendTeleportSpell(e.getPlayer(), 12441, 12442, 2172, 2173, 0, 0, JALDRAOCHT, 3, true, Magic.MAGIC_TELEPORT, null);
                    removeCharge(e.getItem(), e.getPlayer());
                });
                ops.add("Nowhere.");
            });
        }
    });

    public static ItemOnItemHandler handleGoldenStatuette = new ItemOnItemHandler(PPArtefact.JEWELLED_GOLDEN.getArtefactId(), PHARAOHS_SCEPTRE, e -> {
        if(e.getItem2().getId() == PHARAOHS_SCEPTRE[3] || e.getItem2().getId() == PHARAOHS_SCEPTRE[2] || e.getItem2().getId() == PHARAOHS_SCEPTRE[1]) {
            e.getPlayer().sendMessage("Your scepter is already charged");
        }
        else {
            Item chargedSceptre = new Item(PHARAOHS_SCEPTRE[3]);
            Item GoldenStatuette = new Item(PPArtefact.JEWELLED_GOLDEN.getArtefactId());
            int SceptreSlot = e.getItem2().getSlot();
            e.getPlayer().getInventory().removeItems(GoldenStatuette);
            e.getPlayer().getInventory().getItems().set(SceptreSlot, chargedSceptre);
            e.getPlayer().getInventory().refresh();
            e.getPlayer().sendMessage("You recharge your sceptre with a jewelled golden statuette.");
        }
    });


    public static void removeCharge(Item item, Player player) {
        item.setId(item.getId()+2);
        player.getInventory().refresh();
    }

}
