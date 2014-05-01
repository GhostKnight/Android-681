package com.gmu.stratego.client;

public class URLS {
	final public static String SITE = "https://play-stratego.herokuapp.com";
	final public static String LOGIN = SITE + "/api/login";
	final public static String SIGNUP = SITE + "/api/signup";
	final public static String LOGOUT = SITE + "/api/logout";
	final public static String GET_GAMES = SITE + "/api/games";
	final public static String GET_TEST_GAME_STATE = SITE + "/api/games/1234";
	final public static String GET_TEST_GAME_ACTIONS = SITE + "/api/games/1234/actions";
	final public static String GET_GAME_STATE = SITE + "/api/games/:id";
	final public static String GET_GAME_ACTIONS = SITE + "/api/games/:id/actions";
	final public static String POST_ACTION = SITE + "/api/games/:id/action";
	final public static String POST_JOIN_GAME = SITE + "/api/games/:id/join";
	final public static String POST_CREATE_GAME = SITE + "/api/games/create";
}
