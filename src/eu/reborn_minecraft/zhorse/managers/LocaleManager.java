package eu.reborn_minecraft.zhorse.managers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bukkit.ChatColor;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;

public class LocaleManager {
	private ZHorse zh;
	
	public LocaleManager(ZHorse zh) {
		this.zh = zh;
	}
	
	public String getMessage(String index, String language, boolean hidePrefix) {
        String message = zh.getLocale(language).getString(index);
        if (message == null) {
        	zh.getLogger().severe("No value found in \"locale_" + language + ".yml\" at index \"" + index + "\" !");
        	if (!language.equals(zh.getCM().getDefaultLanguage())) {
        		return getMessage(index, zh.getCM().getDefaultLanguage(), hidePrefix);
        	}
        	Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return(ChatColor.RED + "Please ask an administrator to take a look at the server's logs at " + sdf.format(cal.getTime()));
        }
        if (hidePrefix) {
        	return message;
        }
        else {
        	return getMessage(LocaleEnum.pluginPrefix.getIndex(), language, true) + " " + message;
        }
	}
	
	public boolean checkConformity() {
		return true;
	}

}
