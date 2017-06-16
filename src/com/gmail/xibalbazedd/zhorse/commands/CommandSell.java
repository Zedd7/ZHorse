package com.gmail.xibalbazedd.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.gmail.xibalbazedd.zhorse.ZHorse;
import com.gmail.xibalbazedd.zhorse.database.SaleRecord;
import com.gmail.xibalbazedd.zhorse.enums.LocaleEnum;
import com.gmail.xibalbazedd.zhorse.utils.MessageConfig;

public class CommandSell extends AbstractCommand {

	public CommandSell(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
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
						execute(p.getUniqueId(), horseID);
					}
				}
				else {
					sendCommandUsage();
				}
			}
			else {
				execute(targetUUID, horseID);
			}
		}
	}
	
	private void execute(UUID ownerUUID, String horseID) {
		if (isRegistered(ownerUUID, horseID)) {
			horse = zh.getHM().getHorse(ownerUUID, Integer.parseInt(horseID));
			if (isHorseLoaded(true)) {
				execute();
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
						applyHorsePrice(price);
						String sellerCurrencySymbol = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.CURRENCY_SYMBOL), true);
						zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_PUT_UP_FOR_SALE) {{ setAmount(price); setCurrencySymbol(sellerCurrencySymbol); setHorseName(horseName); }});
						zh.getEM().payCommand(p, command);
					}
				} catch (NumberFormatException e) {
					sendCommandUsage();
				}
			}
			else {
				if (!argument.isEmpty()) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_ALREADY_ON_SALE) {{ setHorseName(horseName); }});
				}
				else {
					zh.getDM().removeSale(horse.getUniqueId());
					horseName = zh.getDM().getHorseName(horse.getUniqueId());
					applyHorseName(targetUUID);
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_WITHDRAWN_FROM_SALE) {{ setHorseName(horseName); }});
					zh.getEM().payCommand(p, command);
				}
			}
		}
	}

}