package com.rs.game.content.world.areas.dungeons;

import com.rs.game.model.entity.player.Player;
import com.rs.game.tasks.WorldTasks;
import com.rs.lib.game.Animation;
import com.rs.lib.game.Tile;
import com.rs.plugin.annotations.PluginEventHandler;
import com.rs.plugin.handlers.ObjectClickHandler;

@PluginEventHandler
public class PolyporeDungeon {
	
	public static ObjectClickHandler vineClimbs = new ObjectClickHandler(new Object[] { 64359, 64360, 64361, 64362 }, e -> {
		int id = e.getObjectId();
		int x = e.getObject().getX(), y = e.getObject().getY();
		if (id == 64360 && x == 4629 && y == 5453)
			useStairs(e.getPlayer(), Tile.of(4629, 5451, 2), true);
		else if (id == 64361 && x == 4629 && y == 5452)
			useStairs(e.getPlayer(), Tile.of(4629, 5454, 3), false);
		else if (id == 64359 && x == 4632 && y == 5443)
			useStairs(e.getPlayer(), Tile.of(4632, 5443, 1), true);
		else if (id == 64361 && x == 4632 && y == 5442)
			useStairs(e.getPlayer(), Tile.of(4632, 5444, 2), false);
		else if (id == 64359 && x == 4632 && y == 5409)
			useStairs(e.getPlayer(), Tile.of(4632, 5409, 2), true);
		else if (id == 64361 && x == 4633 && y == 5409)
			useStairs(e.getPlayer(), Tile.of(4631, 5409, 3), false);
		else if (id == 64359 && x == 4642 && y == 5389)
			useStairs(e.getPlayer(), Tile.of(4642, 5389, 1), true);
		else if (id == 64361 && x == 4643 && y == 5389)
			useStairs(e.getPlayer(), Tile.of(4641, 5389, 2), false);
		else if (id == 64359 && x == 4652 && y == 5388)
			useStairs(e.getPlayer(), Tile.of(4652, 5388, 0), true);
		else if (id == 64362 && x == 4652 && y == 5387)
			useStairs(e.getPlayer(), Tile.of(4652, 5389, 1), false);
		else if (id == 64359 && x == 4691 && y == 5469)
			useStairs(e.getPlayer(), Tile.of(4691, 5469, 2), true);
		else if (id == 64361 && x == 4691 && y == 5468)
			useStairs(e.getPlayer(), Tile.of(4691, 5470, 3), false);
		else if (id == 64359 && x == 4689 && y == 5479)
			useStairs(e.getPlayer(), Tile.of(4689, 5479, 1), true);
		else if (id == 64361 && x == 4689 && y == 5480)
			useStairs(e.getPlayer(), Tile.of(4689, 5478, 2), false);
		else if (id == 64359 && x == 4698 && y == 5459)
			useStairs(e.getPlayer(), Tile.of(4698, 5459, 2), true);
		else if (id == 64361 && x == 4699 && y == 5459)
			useStairs(e.getPlayer(), Tile.of(4697, 5459, 3), false);
		else if (id == 64359 && x == 4705 && y == 5460)
			useStairs(e.getPlayer(), Tile.of(4704, 5461, 1), true);
		else if (id == 64361 && x == 4705 && y == 5461)
			useStairs(e.getPlayer(), Tile.of(4705, 5459, 2), false);
		else if (id == 64359 && x == 4718 && y == 5467)
			useStairs(e.getPlayer(), Tile.of(4718, 5467, 0), true);
		else if (id == 64361 && x == 4718 && y == 5466)
			useStairs(e.getPlayer(), Tile.of(4718, 5468, 1), false);
		else if (id == 64360 && e.getObject().getTile().isAt(4696, 5618))
			useStairs(e.getPlayer(), Tile.of(4696, 5618, 2), true);
		else if (id == 64361 && e.getObject().getTile().isAt(4696, 5617))
			useStairs(e.getPlayer(), Tile.of(4696, 5619, 3), false);
		else if (id == 64359 && e.getObject().getTile().isAt(4684, 5586))
			useStairs(e.getPlayer(), Tile.of(4684, 5588, 2), true);
		else if (id == 64361 && e.getObject().getTile().isAt(4684, 5587))
			useStairs(e.getPlayer(), Tile.of(4684, 5585, 3), false);
		else if (id == 64359 && e.getObject().getTile().isAt(4699, 5617))
			useStairs(e.getPlayer(), Tile.of(4699, 5617, 1), true);
		else if (id == 64361 && e.getObject().getTile().isAt(4698, 5617))
			useStairs(e.getPlayer(), Tile.of(4700, 5617, 2), false);
		else if (id == 64359 && e.getObject().getTile().isAt(4721, 5602))
			useStairs(e.getPlayer(), Tile.of(4720, 5601, 1), true);
		else if (id == 64361 && e.getObject().getTile().isAt(4720, 5602))
			useStairs(e.getPlayer(), Tile.of(4722, 5602, 2), false);
		else if (id == 64359 && e.getObject().getTile().isAt(4702, 5612))
			useStairs(e.getPlayer(), Tile.of(4702, 5610, 0), true);
		else if (id == 64361 && e.getObject().getTile().isAt(4702, 5611))
			useStairs(e.getPlayer(), Tile.of(4702, 5613, 1), false);
	});
	
	public static void useStairs(final Player player, Tile tile, final boolean down) {
		player.useStairs(down ? 15458 : 15456, tile, 2, 3); // TODO find correct emote
		WorldTasks.schedule(1, () -> player.setNextAnimation(new Animation(down ? 15459 : 15457)));
	}
}
