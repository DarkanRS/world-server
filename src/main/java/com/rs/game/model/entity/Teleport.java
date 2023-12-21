package com.rs.game.model.entity;

import com.rs.game.content.bosses.godwars.GodwarsController;
import com.rs.game.content.skills.dungeoneering.DamonheimController;
import com.rs.game.content.skills.magic.Magic;
import com.rs.game.content.skills.magic.TeleType;
import com.rs.game.content.world.areas.wilderness.WildernessController;
import com.rs.game.model.entity.player.Player;
import com.rs.lib.game.Tile;

import java.util.function.Supplier;

public record Teleport(Tile start, Tile destination, TeleType type, Supplier<Boolean> meetsRequirements, Runnable begin, Runnable end, boolean clearDamage) {
    public static void execute(Player player, Teleport teleport, int delay) {
        if (player.isLocked() || (teleport.meetsRequirements != null && !teleport.meetsRequirements.get()))
            return;
        if (!player.getControllerManager().processTeleport(teleport))
            return;
        player.stopAll();
        player.lock();
        if (teleport.begin != null)
            teleport.begin.run();
        player.getTasks().schedule(delay, () -> {
            player.getControllerManager().onTeleported(teleport.type);
            player.tele(teleport.destination);
            if (teleport.end != null)
                teleport.end.run();
            if (player.getControllerManager().getController() == null)
                checkDestinationControllers(player, teleport.destination);
            if (teleport.clearDamage) {
                player.resetReceivedDamage();
                player.resetReceivedHits();
            }
            player.unlock();
        });
    }

    public static void checkDestinationControllers(Player player, Tile teleTile) {
        if (DamonheimController.isAtKalaboss(teleTile))
            player.getControllerManager().startController(new DamonheimController());
        else if (GodwarsController.isAtGodwars(teleTile))
            player.getControllerManager().startController(new GodwarsController());
        else if (WildernessController.isAtWild(teleTile))
            player.getControllerManager().startController(new WildernessController());
    }
}