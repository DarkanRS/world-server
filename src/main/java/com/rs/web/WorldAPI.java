package com.rs.web;

import com.rs.Settings;
import com.rs.game.World;
import com.rs.game.player.Player;
import com.rs.lib.web.APIUtil;
import com.rs.lib.web.ErrorResponse;
import com.rs.lib.web.WebAPI;
import com.rs.lib.web.dto.PacketEncoderDto;

import io.undertow.util.StatusCodes;

public class WorldAPI extends WebAPI {

	public WorldAPI() {
		super("api", 4041);
	
		this.routes.post("/sendpacket", ex -> {
			ex.dispatch(() -> {
				if (!APIUtil.authenticate(ex, Settings.getConfig().getLobbyApiKey())) {
					APIUtil.sendResponse(ex, StatusCodes.UNAUTHORIZED, new ErrorResponse("Invalid authorization key."));
					return;
				}
				APIUtil.readJSON(ex, PacketEncoderDto.class, request -> {
					Player player = World.getPlayer(request.getUsername());
					if (player == null || player.getSession() == null)
						return;
					player.getSession().writeToQueue(request.getEncoders());
					APIUtil.sendResponse(ex, StatusCodes.OK, true);
				});
			});
		});
		
		addRoute(new Telemetry());
		addRoute(new SocialOperations());
	}

}
