package co.ryred.secretsanta.util;

import org.bukkit.ChatColor;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 02/12/2015.
 */
public class GeneralUtils
{

	public static String[] c( String... strings )
	{

		String[] returnArray = new String[ strings.length ];

		for ( int i = 0; i < strings.length; i++ ) {
			returnArray[ i ] = c( strings[ i ] );
		}

		return returnArray;

	}

	public static String c( String string )
	{
		return ChatColor.translateAlternateColorCodes( '&', string );
	}

}
