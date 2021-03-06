package com.gmu.stratego.util;

import com.gmu.stratego.R;

/**
 * Constants that will be used around the program!
 * @author Tim
 *
 */
public final class StrategoConstants {
	/** List of all of IDs for the views that make up the deployment table grid */
	public static final Integer[] DEP_TABLE_IDS = { R.id.deployment01, R.id.deployment02, R.id.deployment03,
			R.id.deployment04, R.id.deployment05, R.id.deployment06, R.id.deployment07, R.id.deployment08,
			R.id.deployment11, R.id.deployment12, R.id.deployment13, R.id.deployment14 };

	/**
	 * List of all the IDs for the view that make up the red players deployment
	 * zone
	 */
	public static final Integer[] RED_DEP_ZONE = { R.id.tile00, R.id.tile01, R.id.tile02, R.id.tile03, R.id.tile04,
			R.id.tile05, R.id.tile06, R.id.tile07, R.id.tile08, R.id.tile09, R.id.tile10, R.id.tile11, R.id.tile12,
			R.id.tile13, R.id.tile14, R.id.tile15, R.id.tile16, R.id.tile17, R.id.tile18, R.id.tile19, R.id.tile20,
			R.id.tile21, R.id.tile22, R.id.tile23, R.id.tile24, R.id.tile25, R.id.tile26, R.id.tile27, R.id.tile28,
			R.id.tile29, R.id.tile30, R.id.tile31, R.id.tile32, R.id.tile33, R.id.tile34, R.id.tile35, R.id.tile36,
			R.id.tile37, R.id.tile38, R.id.tile39 };

	/**
	 * List of all the IDs for the view that make up the blue players deployment
	 * zone
	 */
	public static final Integer[] BLUE_DEP_ZONE = { R.id.tile90, R.id.tile91, R.id.tile92, R.id.tile93, R.id.tile94,
			R.id.tile95, R.id.tile96, R.id.tile97, R.id.tile98, R.id.tile99, R.id.tile80, R.id.tile81, R.id.tile82,
			R.id.tile83, R.id.tile84, R.id.tile85, R.id.tile86, R.id.tile87, R.id.tile88, R.id.tile89, R.id.tile70,
			R.id.tile71, R.id.tile72, R.id.tile73, R.id.tile74, R.id.tile75, R.id.tile76, R.id.tile77, R.id.tile78,
			R.id.tile79, R.id.tile60, R.id.tile61, R.id.tile62, R.id.tile63, R.id.tile64, R.id.tile65, R.id.tile66,
			R.id.tile67, R.id.tile68, R.id.tile69 };
	
	public static final Integer[][] boardIDs = {
		{R.id.tile00, R.id.tile01, R.id.tile02, R.id.tile03, R.id.tile04, R.id.tile05, R.id.tile06, R.id.tile07, R.id.tile08, R.id.tile09},
		{R.id.tile10, R.id.tile11, R.id.tile12, R.id.tile13, R.id.tile14, R.id.tile15, R.id.tile16, R.id.tile17, R.id.tile18, R.id.tile19},
		{R.id.tile20, R.id.tile21, R.id.tile22, R.id.tile23, R.id.tile24, R.id.tile25, R.id.tile26, R.id.tile27, R.id.tile28, R.id.tile29},
		{R.id.tile30, R.id.tile31, R.id.tile32, R.id.tile33, R.id.tile34, R.id.tile35, R.id.tile36, R.id.tile37, R.id.tile38, R.id.tile39},
		{R.id.tile40, R.id.tile41, R.id.tile42, R.id.tile43, R.id.tile44, R.id.tile45, R.id.tile46, R.id.tile47, R.id.tile48, R.id.tile49},
		{R.id.tile50, R.id.tile51, R.id.tile52, R.id.tile53, R.id.tile54, R.id.tile55, R.id.tile56, R.id.tile57, R.id.tile58, R.id.tile59},
		{R.id.tile60, R.id.tile61, R.id.tile62, R.id.tile63, R.id.tile64, R.id.tile65, R.id.tile66, R.id.tile67, R.id.tile68, R.id.tile69},
		{R.id.tile70, R.id.tile71, R.id.tile72, R.id.tile73, R.id.tile74, R.id.tile75, R.id.tile76, R.id.tile77, R.id.tile78, R.id.tile79},
		{R.id.tile80, R.id.tile81, R.id.tile82, R.id.tile83, R.id.tile84, R.id.tile85, R.id.tile86, R.id.tile87, R.id.tile88, R.id.tile89},
		{R.id.tile90, R.id.tile91, R.id.tile92, R.id.tile93, R.id.tile94, R.id.tile95, R.id.tile96, R.id.tile97, R.id.tile98, R.id.tile99}
	};
	
	/** 
	 * This is a map that holds the allowed moves for each piece.  Indexed by unit power 
	 */
	public static final Integer[] allowedMoves = {0, 1, 10, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0};
	
	/**
	 * Shortcut to red piece IDs index 0 is nothing because there is no unit with power of 0
	 */
	public static final Integer[] RED_PIECES = { 0, R.drawable.r1, R.drawable.r2, R.drawable.r3, R.drawable.r4,
			R.drawable.r5, R.drawable.r6, R.drawable.r7, R.drawable.r8, R.drawable.r9, R.drawable.r10, R.drawable.r11,
			R.drawable.r12, R.drawable.r13 };

	/**
	 * Shortcut to blue piece IDs index 0 is nothing because there is no unit with power of 0
	 */
	public static final Integer[] BLUE_PIECES = { 0, R.drawable.b1, R.drawable.b2, R.drawable.b3, R.drawable.b4,
			R.drawable.b5, R.drawable.b6, R.drawable.b7, R.drawable.b8, R.drawable.b9, R.drawable.b10, R.drawable.b11,
			R.drawable.b12, R.drawable.b13 };
}
