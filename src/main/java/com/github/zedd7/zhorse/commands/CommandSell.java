package com.github.zedd7.zhorse.commands;

import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.AbstractHorse;

import com.github.zedd7.zhorse.ZHorse;
import com.github.zedd7.zhorse.database.SaleRecord;
import com.github.zedd7.zhorse.enums.LocaleEnum;
import com.github.zedd7.zhorse.utils.MessageConfig;

public class CommandSell extends AbstractCommand {

	public CommandSell(ZHorse zh, CommandSender s, String[] a) {
		super(zh, s, a);
		if (isPlayer() && zh.getEM().canAffordCommand(p, command) && parseArguments() && hasPermission() && isCooldownElapsed() && isWorldEnabled()) {
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
		if (isOwner()) {
			if (!zh.getDM().isHorseForSale(horse.getUniqueId())) {
				try {
					if (args.size() != 1) {
						throw new NumberFormatException();
					}
					int price = Integer.parseInt(args.get(0));
					if (price <= 0) {
						throw new NumberFormatException();
					}
					SaleRecord saleRecord = new SaleRecord(horse.getUniqueId().toString(), price);
					if (zh.getDM().registerSale(saleRecord)) {
						applyHorsePrice(price);
						String sellerCurrencySymbol = zh.getMM().getMessage(s, new MessageConfig(LocaleEnum.CURRENCY_SYMBOL), true);
						zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_PUT_UP_FOR_SALE) {{ setAmount(price); setCurrencySymbol(sellerCurrencySymbol); setHorseName(horseName); }});
						zh.getCmdM().updateCommandHistory(s, command);
						zh.getEM().payCommand(p, command);
					}
				} catch (NumberFormatException e) {
					sendCommandUsage();
				}
			}
			else {
				if (!args.isEmpty()) {
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_ALREADY_FOR_SALE) {{ setHorseName(horseName); }});
				}
				else {
					zh.getDM().removeSale(horse.getUniqueId());
					horseName = zh.getDM().getHorseName(horse.getUniqueId());
					applyHorseName(targetUUID);
					zh.getMM().sendMessage(s, new MessageConfig(LocaleEnum.HORSE_WITHDRAWN_FROM_SALE) {{ setHorseName(horseName); }});
					zh.getCmdM().updateCommandHistory(s, command);
					zh.getEM().payCommand(p, command);
				}
			}
		}
	}

}