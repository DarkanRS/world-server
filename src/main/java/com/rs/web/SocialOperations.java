package com.rs.web;

import com.rs.lib.web.Route;

import io.undertow.server.RoutingHandler;

public class SocialOperations implements Route {

	@Override
	public void build(RoutingHandler route) {
//		route.get("/social/lastcommit", ex -> {
//			APIUtil.sendResponse(ex, StatusCodes.OK, Settings.COMMIT_HISTORY);
//		});	
	}
}
