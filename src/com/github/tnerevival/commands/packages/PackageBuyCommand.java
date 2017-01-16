package com.github.tnerevival.commands.packages;

import com.github.tnerevival.TNE;
import com.github.tnerevival.account.IDFinder;
import com.github.tnerevival.commands.TNECommand;
import com.github.tnerevival.core.Message;
import com.github.tnerevival.core.currency.CurrencyFormatter;
import com.github.tnerevival.core.objects.TNEAccessPackage;
import com.github.tnerevival.core.transaction.TransactionType;
import com.github.tnerevival.utils.AccountUtils;
import com.github.tnerevival.utils.MISCUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class PackageBuyCommand extends TNECommand {

  public PackageBuyCommand(TNE plugin) {
    super(plugin);
  }

  @Override
  public String getName() {
    return "buy";
  }

  @Override
  public String[] getAliases() {
    return new String[0];
  }

  @Override
  public String getNode() {
    return "tne.package.buy";
  }

  @Override
  public boolean console() {
    return false;
  }

  @Override
  public boolean execute(CommandSender sender, String command, String[] arguments) {
    Player player = (Player)sender;

    if(!AccountUtils.getAccount(IDFinder.getID(player)).getStatus().getBalance()) {
      Message locked = new Message("Messages.Account.Locked");
      locked.addVariable("$player", player.getDisplayName());
      locked.translate(MISCUtils.getWorld(player), player);
      return false;
    }

    if(arguments.length == 2) {
      List<TNEAccessPackage> packages = TNE.configurations.getObjectConfiguration().getInventoryPackages(arguments[0], MISCUtils.getWorld(player), IDFinder.getID(player).toString());
      if(packages.size() > 0) {
        for(TNEAccessPackage p : packages) {
          if(p.getName().equalsIgnoreCase(arguments[1])) {
            if(AccountUtils.transaction(IDFinder.getID(player).toString(), null, p.getCost(), TransactionType.MONEY_INQUIRY, MISCUtils.getWorld(player))) {
              AccountUtils.transaction(IDFinder.getID(player).toString(), null, p.getCost(), TransactionType.MONEY_REMOVE, MISCUtils.getWorld(player));
              AccountUtils.getAccount(IDFinder.getID(player)).addTime(MISCUtils.getWorld(player), arguments[0], p.getTime());
              Message bought = new Message("Messages.Package.Bought");
              bought.addVariable("$amount",  CurrencyFormatter.format(MISCUtils.getWorld(player), p.getCost()));
              bought.addVariable("$name",  p.getName());
              bought.addVariable("$type",  arguments[0]);
              bought.translate(MISCUtils.getWorld(player), player);
              return true;
            } else {
              Message insufficient = new Message("Messages.Money.Insufficient");
              insufficient.addVariable("$amount",  CurrencyFormatter.format(MISCUtils.getWorld(player), p.getCost()));
              insufficient.translate(MISCUtils.getWorld(player), player);
              return false;
            }
          }
        }
      }
      Message insufficient = new Message("Messages.Package.None");
      insufficient.addVariable("$name",  arguments[1]);
      insufficient.addVariable("$type",  arguments[0]);
      insufficient.translate(MISCUtils.getWorld(player), player);
      return false;
    }
    help(sender);
    return false;
  }

  @Override
  public String getHelp() {
    return "/package buy <type> <package> - Buy <package> for inventory <type>";
  }
}