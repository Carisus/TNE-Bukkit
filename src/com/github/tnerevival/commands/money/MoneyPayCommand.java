package com.github.tnerevival.commands.money;

import com.github.tnerevival.TNE;
import com.github.tnerevival.account.IDFinder;
import com.github.tnerevival.commands.TNECommand;
import com.github.tnerevival.core.Message;
import com.github.tnerevival.core.currency.CurrencyFormatter;
import com.github.tnerevival.core.transaction.TransactionType;
import com.github.tnerevival.utils.AccountUtils;
import com.github.tnerevival.utils.MISCUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyPayCommand extends TNECommand {

  public MoneyPayCommand(TNE plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "pay";
  }

  @Override
  public String[] getAliases() {
    return new String[0];
  }

  @Override
  public String getNode() {
    return "tne.money.pay";
  }

  @Override
  public boolean console() {
    return false;
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {
    Player player = getPlayer(sender);
    if(arguments.length >= 2) {
      Double value = CurrencyFormatter.translateDouble(arguments[1], MISCUtils.getWorld(getPlayer(sender)));
      if(value < 0) {
        new Message("Messages.Money.Negative").translate(MISCUtils.getWorld(player), player);
        return false;
      }
      if(getPlayer(sender, arguments[0]) != null && IDFinder.getID(player).equals(IDFinder.getID(getPlayer(sender, arguments[0])))) {
        new Message("Messages.Money.SelfPay").translate(MISCUtils.getWorld(player), player);
        return false;
      }

      if(AccountUtils.transaction(IDFinder.getID(player).toString(), null, AccountUtils.round(value), TransactionType.MONEY_INQUIRY, MISCUtils.getWorld(player))) {
        MISCUtils.debug("Player has funds");
        if(getPlayer(sender, arguments[0]) != null) {
          MISCUtils.debug("Player not null");
          boolean transaction = AccountUtils.transaction(IDFinder.getID(player).toString(), IDFinder.getID(getPlayer(sender, arguments[0])).toString(), AccountUtils.round(value), TransactionType.MONEY_PAY, MISCUtils.getWorld(player));
          MISCUtils.debug("" + transaction);
          if(transaction) {
            MISCUtils.debug("Paid player");
            Message paid = new Message("Messages.Money.Paid");
            paid.addVariable("$amount", CurrencyFormatter.format(player.getWorld().getName(), AccountUtils.round(value)));
            paid.addVariable("$player", arguments[0]);
            paid.translate(MISCUtils.getWorld(player), player);
            return true;
          }
        }
      } else {
        Message insufficient = new Message("Messages.Money.Insufficient");
        insufficient.addVariable("$amount", CurrencyFormatter.format(player.getWorld().getName(), AccountUtils.round(Double.valueOf(arguments[1]))));
        insufficient.translate(MISCUtils.getWorld(player), player);
        return false;
      }
    }
    help(sender);
    return false;
  }

  @Override
  public String getHelp() {
    return "/money pay <player> <amount> - pay a player money from your balance";
  }

}