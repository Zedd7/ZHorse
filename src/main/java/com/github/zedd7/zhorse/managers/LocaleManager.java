package com.github.zedd7.zhorse.managers;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.enums.LocaleEnum;

public class LocaleManager {

	public static final String[] PROVIDED_LANGUAGES = {"DE", "EN", "ES", "FR", "HU", "NL", "PL"};

	private ZHorse zh;
	private Map<String, FileConfiguration> locales;

	public LocaleManager(ZHorse zh) {
		this.zh = zh;
	}

	public Map<String, FileConfiguration> getLocales() {
		return locales;
	}

	public void setLocales(Map<String, FileConfiguration> locales) {
		this.locales = locales;
	}

    private FileConfiguration getLocale(String language) {
    	if (locales.containsKey(language)) {
    		return locales.get(language);
    	}
    	if (language != null) {
    		zh.getLogger().severe("A player is using an unavailable language : \"" + language + "\" !");
    	}
    	return locales.get(zh.getCM().getDefaultLanguage());
    }

	public String getMessage(String index, String language, boolean hidePrefix) {
        String message = getLocale(language).getString(index);
        if (message == null) {
        	zh.getLogger().severe("No value found in " + language + " locale at index \"" + index + "\" !");
        	if (!language.equals(zh.getCM().getDefaultLanguage())) {
        		return getMessage(index, zh.getCM().getDefaultLanguage(), hidePrefix);
        	}
        	Calendar cal = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            return ChatColor.RED + "Please ask an administrator to take a look at the server's logs at " + sdf.format(cal.getTime());
        }
        if (hidePrefix) {
        	return message;
        }
        return getMessage(LocaleEnum.PLUGIN_PREFIX.getIndex(), language, true) + " " + message;
	}

	public boolean checkConformity() {
		return true;
	}

}
