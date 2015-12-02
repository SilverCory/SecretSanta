package co.ryred.secretsanta.pojo;

import co.ryred.secretsanta.util.GeneralUtils;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Cory Redmond
 *         Created by acech_000 on 02/12/2015.
 */
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@AllArgsConstructor
public class SackItem implements ConfigurationSerializable
{

	@Getter
	private final ItemStack item;
	@Getter
	private final UUID leaver;

	@Getter
	@Setter
	public boolean open = false;

	public static SackItem deserialize( Map<String, Object> args )
	{

		UUID leaverUUID = UUID.fromString( (String) args.get( "leaver" ) );
		//ItemStack is = ItemStack.deserialize( (Map<String, Object>) args.get( "item" ) );
		ItemStack is = (ItemStack) args.get( "item" );
		boolean open = (boolean) args.get( "open" );

		return new SackItem( is, leaverUUID, open );

	}

	public ItemStack getDisplayItem( boolean admin )
	{

		ItemMeta meta = item.getItemMeta();

		ArrayList<String> lore = new ArrayList<>();

		if ( meta.getLore() != null ) {
			lore.addAll( meta.getLore() );

			lore.add( "" );
			lore.add( "" );

		}

		lore.add( "" );
		lore.add( GeneralUtils.c( "&bGift sender: " ) );
		lore.add( GeneralUtils.c( "&a    " + getLeaverName( admin ) ) );

		meta.setLore( lore );
		item.setItemMeta( meta );

		return item;

	}

	private String getLeaverName( boolean admin )
	{

		OfflinePlayer leaverPlayer = Bukkit.getOfflinePlayer( leaver );
		if ( leaverPlayer == null ) return "Unknown Player. :(";

		return ( isOpen() || admin ) ? leaverPlayer.getName() : Strings.repeat( "*", leaverPlayer.getName().length() );

	}

	@Override
	public Map<String, Object> serialize()
	{

		LinkedHashMap<String, Object> returnMap = new LinkedHashMap<>();

		returnMap.put( "leaver", leaver.toString() );
		returnMap.put( "item", item );
		returnMap.put( "open", open );

		return returnMap;

	}

}
