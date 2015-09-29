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
	
//	public String getCommandAnswer(String language, String index) {
//		return getCommandAnswer(language, index, false);
//	}
//	
//	public String getCommandAnswer(String language, String index, boolean hidePrefix) {
//		return getLocaleData(language, "Messages." + index, hidePrefix);
//	}
//	
//	public String getCommandDescription(String language, String index) {
//		return getCommandDescription(language, index, true);
//	}
//	
//	public String getCommandDescription(String language, String index, boolean hidePrefix) {
//		return getLocaleData(language, "Command descriptions." + index, hidePrefix);
//	}
//	
//	public String getCommandUsage(String language, String index) {
//		return getCommandUsage(language, index, true);
//	}
//	
//	public String getCommandUsage(String language, String index, boolean hidePrefix) {
//		return getLocaleData(language, "Command usages." + index, hidePrefix);
//	}
//	
//	public String getEconomyAnswer(String language, String index) {
//		return getEconomyAnswer(language, index, false);
//	}
//	
//	public String getEconomyAnswer(String language, String index, boolean hidePrefix) {
//		return getLocaleData(language, "Economy." + index, hidePrefix);
//	}
//	
//	public String getHeaderMessage(String language, String index) {
//		return getHeaderMessage(language, index, true);
//	}
//	
//	public String getHeaderMessage(String language, String index, boolean hidePrefix) {
//		return getLocaleData(language, "Headers." + index, hidePrefix);
//	}
//	
//	public String getInformationMessage(String language, String index) {
//		return getInformationMessage(language, index, true);
//	}
//	
//	public String getInformationMessage(String language, String index, boolean hidePrefix) {
//		return getLocaleData(language, "Horse informations." + index, hidePrefix);
//	}
//	
//	public String getSettingsCommandDescription(String language, String index) {
//		return getSettingsCommandDescription(language, index, true);
//	}
//	
//	public String getSettingsCommandDescription(String language, String index, boolean hidePrefix) {
//		return getLocaleData(language, "Settings command descriptions." + index, hidePrefix);
//	}
	
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
