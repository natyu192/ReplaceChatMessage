package com.gmail.mckokumin.rcm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{
	public String prefix = "§1[§9RCM§1]§9";
	public boolean enable = true;

	public void onEnable(){
		saveDefaultConfig();
		saveConfig();
		getServer().getPluginManager().registerEvents(this, this);
	}

	public void onDisable() {

	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch(cmd.getName()){
		case "rcm":
			if (args.length == 1){
				if (args[0].equalsIgnoreCase("reload")){
					reloadConfig();
					sender.sendMessage(prefix + "configをリロードしました");
					return true;
				}
				if (args[0].equalsIgnoreCase("list")){
					sender.sendMessage("§1§l====================");
					List<Map<?, ?>> map = getConfig().getMapList("replace");
					for (Map<?, ?> s : map){
						sender.sendMessage("§9" + s.keySet() + " : " + s.values());
					}
					return true;
				}
				if (args[0].equalsIgnoreCase("enable")){
					enable = true;
					sender.sendMessage(prefix + "有効化しました");
					return true;
				}
				if (args[0].equalsIgnoreCase("disable")){
					enable = false;
					sender.sendMessage(prefix + "無効化しました");
					return true;
				}
			}
			if (args.length == 3){
				if (args[0].equalsIgnoreCase("add")){
					String rep1 = args[1];
					String rep2 = args[2];
					Map<String, String> hash = new HashMap<String, String>();
					hash.put(rep1, rep2);
					List<Map<?, ?>> hashes = getConfig().getMapList("replace");
					hashes.add(hash);
					getConfig().set("replace", hashes);
					saveConfig();
					sender.sendMessage(prefix + "メッセージが追加されました。これからは " + rep1 + " が " + rep2 + " に置き換えられます");
					return true;
				}
			}
			if (args.length == 2){
				if (args[0].equalsIgnoreCase("remove")){
					String rep = args[1];
					if (getConfig().getMapList("replace").size() < 1){
						sender.sendMessage(prefix + "§cメッセージが登録されていません");
						return true;
					} else {
						List<Map<?, ?>> map = getConfig().getMapList("replace");
						for (Map<?, ?> m : map){
							if (m.containsKey(rep)){
								map.remove(m);
								getConfig().set("replace", map);
								saveConfig();
								sender.sendMessage(prefix + "メッセージが削除されました: " + rep);
								return true;
							}
						}
					}
					return true;
				}
			}
			sender.sendMessage("§1§l========== §b§lReplaceChatMessage §1§l==========");
			sender.sendMessage("§9/rcm reload - Configをリロードします");
			sender.sendMessage("§9/rcm add <置き換え前> <置き換え後> - 置き換えするメッセージを追加します");
			sender.sendMessage("§9/rcm remove <置き換え前> - 置き換えするメッセージを削除します");
			sender.sendMessage("§9/rcm list - メッセージのリストを表示します");
			sender.sendMessage("§9/rcm enable - プラグインを有効にします");
			sender.sendMessage("§9/rcm disable - プラグインを無効にします");
			break;
		default:
			break;
		}
		return true;
	}

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		Player p = event.getPlayer();
		if (!p.hasPermission("natyu.rcm.bypass") && enable){
			String msg = event.getMessage();
			for (Map<?, ?> map : getConfig().getMapList("replace")){
				String key = Arrays.asList(map.keySet()).get(0).toString();
				String value = Arrays.asList(map.values()).get(0).toString();
				key = key.substring(1, key.length() - 1);
				value = value.substring(1, value.length() - 1);
				msg = msg.replaceAll(key, value);
			}
			event.setMessage(msg);
		}
	}

}
