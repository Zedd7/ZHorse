package com.gmail.xibalbazedd.zhorse.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.SaleRecord;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;

import net.md_5.bungee.api.ChatColor;

public class CommandSell extends AbstractCommand {

	public CommandSell(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		playerOnly = true;
		needTarget = false;
		if (isPlayer() && analyseArguments() && hasPermission() && isWorldEnabled()) {
			if (!idMode) {
				if (!targetMode) {
					boolean ownsHorse = ownsHorse(targetUUID, true);
					if (isOnHorse(ownsHorse)) {
						horse = (AbstractHorse) p.getVehicle();
						if (isRegistered(horse)) {
							execute();
						}
					}
					else if (ownsHorse) {
						horseID = zh.getDM().getPlayerFavoriteHorseID(p.getUniqueId()).toString();
						if (isRegistered(p.getUniqueId(), horseID)) {
							horse = zh.getHM().getFavoriteHorse(p.getUniqueId());
							if (isHorseLoaded(true)) {
								execute();
							}
						}
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				if (isRegistered(targetUUID, horseID)) {
					horse = zh.getHM().getHorse(targetUUID, Integer.parseInt(horseID));
					if (isHorseLoaded(true)) {
						execute();
					}
				}
			}
		}
	}

	private void execute() {
		if (isOwner() && zh.getEM().canAffordCommand(p, command)) {
			if (!zh.getDM().isHorseForSale(horse.getUniqueId())) {
				try {
					if (argument.isEmpty()  || argument.split(" ").length != 1) {
						throw new NumberFormatException();
					}
					int price = Integer.parseInt(argument);
					if (price <= 0) {
						throw new NumberFormatException();
					}
					SaleRecord saleRecord = new SaleRecord(horse.getUniqueId().toString(), price);
					if (zh.getDM().registerSale(saleRecord)) {
						String defaultLanguage = zh.getCM().getDefaultLanguage();
						String defaultCurrencySymbol = zh.getLM().getMessage(LocaleEnum.CURRENCY_SYMBOL.getIndex(), defaultLanguage, true);
						String horsePrice = zh.getMM().getMessageAmountCurrency(s, LocaleEnum.HORSE_PRICE, price, defaultCurrencySymbol, true);
						horse.setCustomName(horse.getCustomName() + ChatColor.RESET + horsePrice);
						if (displayConsole) {
							String sellerCurrencySymbol = zh.getMM().getMessage(s, LocaleEnum.CURRENCY_SYMBOL, true);
							zh.getMM().sendMessageAmountCurrencyHorse(s, LocaleEnum.HORSE_PUT_UP_FOR_SALE, price, sellerCurrencySymbol, horseName);
						}
						zh.getEM().payCommand(p, command);
					}
				} catch (NumberFormatException e) {
					sendCommandUsage();
				}
			}
			else {
				if (!argument.isEmpty() && displayConsole) {
					zh.getMM().sendMessageHorse(s, LocaleEnum.HORSE_ALREADY_ON_SALE, horseName);
				}
				else {
					zh.getDM().removeSale(horse.getUniqueId());
					horseName = zh.getDM().getHorseName(horse.getUniqueId());
					applyHorseName(targetUUID);
					if (displayConsole) {
						zh.getMM().sendMessageHorse(s, LocaleEnum.HORSE_WITHDRAWN_FROM_SALE, horseName);
					}
					zh.getEM().payCommand(p, command);
				}
			}
		}
	}

}