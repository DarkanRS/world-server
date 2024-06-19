package com.rs.game.content.items;

import com.rs.lib.game.Animation;
import com.rs.lib.game.SpotAnim;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ItemClickHandler;

@PluginEventHandler
public class AnimalStaffs {

    public static ItemClickHandler BatStaff = new ItemClickHandler(new Object[]{19327}, new String[]{"Bat-dance"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(14298));
        e.getPlayer().setNextSpotAnim(new SpotAnim(101));
    });

    public static ItemClickHandler CatStaff = new ItemClickHandler(new Object[]{19331}, new String[]{"Cat-dance"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(14299));
        e.getPlayer().setNextSpotAnim(new SpotAnim(117));
    });

    public static ItemClickHandler DragonStaff = new ItemClickHandler(new Object[]{19323}, new String[]{"Dragon-dance"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(14300));
        e.getPlayer().setNextSpotAnim(new SpotAnim(118));
    });

    public static ItemClickHandler PenguinStaff = new ItemClickHandler(new Object[]{19325}, new String[]{"Penguin-dance"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(14301));
        e.getPlayer().setNextSpotAnim(new SpotAnim(119));
    });

    public static ItemClickHandler WolfStaff = new ItemClickHandler(new Object[]{19329}, new String[]{"Wolf-dance"}, e -> {
        e.getPlayer().setNextAnimation(new Animation(14302));
        e.getPlayer().setNextSpotAnim(new SpotAnim(120));
    });
}
