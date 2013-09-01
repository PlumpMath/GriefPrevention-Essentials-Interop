package au.com.mineauz.GPA;

import java.util.ArrayList;
import java.util.List;

import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

import org.apache.commons.lang.Validate;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Listener
{
	private List<String> mAllValid;
	
	@Override
	public void onEnable()
	{
		getServer().getPluginManager().registerEvents(this, this);
		Command setHome = getServer().getPluginCommand("sethome");
	
		Validate.notNull(setHome, "Could not locate sethome command. Something is not installed correctly.");
		
		mAllValid = new ArrayList<String>();
		mAllValid.add("/" + setHome.getLabel());
		
		if(setHome.getAliases() != null)
		{
			for(String alias : setHome.getAliases())
				mAllValid.add("/" + alias);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	private void onSetHome(PlayerCommandPreprocessEvent event)
	{
		String commandPart = event.getMessage().split(" ")[0];
		
		boolean matched = false;
		
		for(String name : mAllValid)
		{
			if(commandPart.equals(name))
			{
				matched = true;
				break;
			}
		}
		
		if(!matched)
			return;
		
		if(event.getPlayer().hasPermission("trustedhomes.bypass"))
			return;
		
		PlayerData pdata = GriefPrevention.instance.dataStore.getPlayerData(event.getPlayer().getName());
		Claim claim = GriefPrevention.instance.dataStore.getClaimAt(event.getPlayer().getLocation(), true, pdata.lastClaim);
		
		if(claim != null)
		{
			String reason = claim.allowAccess(event.getPlayer());
			
			if(reason != null)
			{
				event.setCancelled(true);
				event.getPlayer().sendMessage(ChatColor.RED + reason);
			}
		}
	}
}
