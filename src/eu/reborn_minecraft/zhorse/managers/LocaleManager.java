package eu.reborn_minecraft.zhorse.managers;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import eu.reborn_minecraft.zhorse.ZHorse;
import eu.reborn_minecraft.zhorse.enums.LocaleEnum;
import eu.reborn_minecraft.zhorse.utils.Utf8YamlConfiguration;

public class LocaleManager {
	
	private static final String LOCALE_PATH = "locale_%s.yml";
	private static final String[] PROVIDED_LANGUAGES = {"EN", "FR", "NL"};
	
	private Map<String, FileConfiguration> locales;
	
	private ZHorse zh;
	
	public LocaleManager(ZHorse zh) {
		this.zh = zh;
		
		for (String language : PROVIDED_LANGUAGES) {
			String localePath = String.format(LOCALE_PATH, language);
			File localeFile = new File(zh.getDataFolder(), localePath);
			if (!localeFile.exists()) {
				zh.getLogger().info(localePath + " is missing... Creating it.");
				zh.saveResource(localePath, false);
			}
		}
		
		locales = new HashMap<String, FileConfiguration>();
		for (String language : zh.getCM().getAvailableLanguages()) {
			String localePath = String.format(LOCALE_PATH, language);
			File localeFile = new File(zh.getDataFolder(), localePath);
			FileConfiguration locale;
			if (localeFile.exists()) {
				locale = Utf8YamlConfiguration.loadConfiguration(localeFile);
			}
			else {
				zh.getLogger().info(localePath + " is missing... Creating it.");
				locale = saveLocale(language);
			}
			locales.put(language, locale);
		}
	}
	
	private FileConfiguration saveLocale(String language) {
		String defaultLocalePath = String.format(LOCALE_PATH, zh.getCM().getDefaultLanguage());
		File defaultLocaleFile = new File(zh.getDataFolder(), defaultLocalePath);
		FileConfiguration locale = Utf8YamlConfiguration.loadConfiguration(defaultLocaleFile);
		String localePath = String.format(LOCALE_PATH, language);
		File localeFile = new File(zh.getDataFolder(), localePath);
        try {
			locale.save(localeFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
        return locale;
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
        	zh.getLogger().severe("No value found in \""+ String.format(LOCALE_PATH, language) +"\" at index \"" + index + "\" !");
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
        return getMessage(LocaleEnum.pluginPrefix.getIndex(), language, true) + " " + message;
	}
	
	public boolean checkConformity() {
		return true;
	}

}
