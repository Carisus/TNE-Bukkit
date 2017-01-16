/*
 * The New Economy Minecraft Server Plugin
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.

 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.tnerevival.core.version.impl;

import com.github.tnerevival.TNE;
import com.github.tnerevival.account.Account;
import com.github.tnerevival.account.Bank;
import com.github.tnerevival.core.auction.Auction;
import com.github.tnerevival.core.auction.Claim;
import com.github.tnerevival.core.db.FlatFile;
import com.github.tnerevival.core.db.H2;
import com.github.tnerevival.core.db.MySQL;
import com.github.tnerevival.core.db.SQLDatabase;
import com.github.tnerevival.core.db.flat.Article;
import com.github.tnerevival.core.db.flat.Entry;
import com.github.tnerevival.core.db.flat.FlatFileConnection;
import com.github.tnerevival.core.db.flat.Section;
import com.github.tnerevival.core.shops.Shop;
import com.github.tnerevival.core.signs.TNESign;
import com.github.tnerevival.core.transaction.Record;
import com.github.tnerevival.core.transaction.TransactionCost;
import com.github.tnerevival.core.transaction.TransactionHistory;
import com.github.tnerevival.core.transaction.TransactionType;
import com.github.tnerevival.core.version.Version;
import com.github.tnerevival.serializable.SerializableItemStack;
import com.github.tnerevival.serializable.SerializableLocation;
import com.github.tnerevival.utils.MISCUtils;
import com.github.tnerevival.utils.SignUtils;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by creatorfromhell on 11/15/2016.
 **/
public class Alpha5_0 extends Version {
  @Override
  public double versionNumber() {
    return 5.1;
  }

  @Override
  public void update(double version, String type) {
    if(version < 4.0 || version == 5.0) return;

    String table = prefix + "_INFO";
    if(type.equalsIgnoreCase("mysql")) {
      mysql().executeUpdate("ALTER TABLE `" + table + "` ADD UNIQUE(id)");
      mysql().executeUpdate("ALTER TABLE `" + table + "` ADD COLUMN `server_name` VARCHAR(250) AFTER `version`");

      table = prefix + "_ECOIDS";
      mysql().executeUpdate("ALTER TABLE `" + table + "` MODIFY `username` VARCHAR(56)");
      mysql().close();
    } else if(type.equalsIgnoreCase("h2")) {
      h2().executeUpdate("ALTER TABLE `" + table + "` ADD UNIQUE(id)");
      h2().executeUpdate("ALTER TABLE `" + table + "` ADD COLUMN `server_name` VARCHAR(250) AFTER `version`");

      table = prefix + "_ECOIDS";
      h2().executeUpdate("ALTER TABLE `" + table + "` MODIFY `username` VARCHAR(56)");
      h2().close();
    }
  }

  @Override
  public void saveTransaction(Record record) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      String table = prefix + "_TRANSACTIONS";
      sql().executePreparedUpdate("INSERT INTO `" + table + "` (trans_id, trans_initiator, trans_player, trans_world, trans_type, trans_cost, trans_oldBalance, trans_balance, trans_time) " +
              "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE trans_player = ?, trans_world = ?",
          new Object[]{
              record.getId(),
              record.getInitiator(),
              record.getPlayer(),
              record.getWorld(),
              record.getType(),
              record.getCost(),
              record.getOldBalance(),
              record.getBalance(),
              record.getTime(),
              record.getPlayer(),
              record.getWorld()
          }
      );
      sql().close();
    }
  }

  @Override
  public void deleteTransaction(UUID id) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      sql().executePreparedUpdate("DELETE FROM " + prefix + "_TRANSACTIONS WHERE trans_id = ? ", new Object[] { id.toString() });
      sql().close();
    }
  }

  @Override
  public void saveAccount(Account acc) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      String table = prefix + "_USERS";
      sql().executePreparedUpdate("INSERT INTO `" + table + "` (uuid, balances, acc_pin, inventory_credits, command_credits, joinedDate, accountnumber, accountstatus) VALUES(?, ?, ?, ?, ?, ?, ?, ?)" +
              " ON DUPLICATE KEY UPDATE balances = ?, acc_pin = ?, inventory_credits = ?, command_credits = ?, joinedDate = ?, accountnumber = ?, accountstatus = ?",
          new Object[] {
              acc.getUid().toString(),
              acc.balancesToString(),
              acc.getPin(),
              acc.creditsToString(),
              acc.commandsToString(),
              acc.getJoined(),
              acc.getAccountNumber(),
              acc.getStatus().getName(),
              acc.balancesToString(),
              acc.getPin(),
              acc.creditsToString(),
              acc.commandsToString(),
              acc.getJoined(),
              acc.getAccountNumber(),
              acc.getStatus().getName()
          }
      );

      table = prefix + "_BANKS";
      for (Map.Entry<String, Bank> entry : acc.getBanks().entrySet()) {
        sql().executePreparedUpdate("INSERT INTO `" + table + "` (uuid, world, bank) VALUES(?, ?, ?) ON DUPLICATE KEY UPDATE bank = ?",
            new Object[] {
                acc.getUid().toString(),
                entry.getKey(),
                entry.getValue().toString(),
                entry.getValue().toString()
            }
        );
      }
      sql().close();
    }
  }

  @Override
  public void deleteAccount(UUID id) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      sql().executePreparedUpdate("DELETE FROM " + prefix + "_USERS WHERE uuid = ? ", new Object[] { id.toString() });
      sql().close();
    }
  }

  @Override
  public void saveShop(Shop shop) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      String table = prefix + "_SHOPS";
      sql().executePreparedUpdate("INSERT INTO `" + table + "` (shop_name, shop_world, shop_owner, shop_hidden, shop_admin, shop_items, shop_blacklist, shop_whitelist, shop_shares) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)" +
              " ON DUPLICATE KEY UPDATE shop_owner = ?, shop_hidden = ?, shop_admin = ?, shop_items = ?, shop_blacklist = ?, shop_whitelist = ?, shop_shares = ?",
          new Object[]{
              shop.getName(),
              shop.getWorld(),
              shop.getOwner().toString(),
              SQLDatabase.boolToDB(shop.isHidden()),
              SQLDatabase.boolToDB(shop.isAdmin()),
              shop.itemsToString(),
              shop.listToString(true),
              shop.listToString(false),
              shop.sharesToString(),
              shop.getOwner().toString(),
              SQLDatabase.boolToDB(shop.isHidden()),
              SQLDatabase.boolToDB(shop.isAdmin()),
              shop.itemsToString(),
              shop.listToString(true),
              shop.listToString(false),
              shop.sharesToString()
          }
      );
      sql().close();
    }
  }

  @Override
  public void deleteShop(Shop shop) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      sql().executePreparedUpdate("DELETE FROM " + prefix + "_SHOPS WHERE shop_name = ? AND shop_world = ?", new Object[] { shop.getName(), shop.getWorld() });
      sql().close();
    }
  }

  @Override
  public void saveSign(TNESign sign) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      String table = prefix + "_SIGNS";
      sql().executePreparedUpdate("INSERT INTO `" + table + "` (sign_owner, sign_type, sign_location, sign_meta) VALUES(?, ?, ?, ?)" +
              " ON DUPLICATE KEY UPDATE sign_owner = ?, sign_type = ?, sign_meta = ?",
          new Object[] {
              sign.getOwner().toString(),
              sign.getType().getName(),
              sign.getLocation().toString(),
              sign.getMeta(),
              sign.getOwner().toString(),
              sign.getType().getName(),
              sign.getMeta()
          }
      );
      sql().close();
    }
  }

  @Override
  public void deleteSign(TNESign sign) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      sql().executePreparedUpdate("DELETE FROM " + prefix + "_SIGNS WHERE sign_location = ?", new Object[] { sign.getLocation().toString() });
      sql().close();
    }
  }

  @Override
  public void saveAuction(Auction auction) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      String table = prefix + "_AUCTIONS";
      sql().executePreparedUpdate("INSERT INTO `" + table + "` (auction_lot, auction_added, auction_start, auction_owner, auction_world, auction_silent, auction_item, auction_cost, auction_increment, auction_global, auction_time, auction_node) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)" +
              " ON DUPLICATE KEY UPDATE auction_added = ?, auction_start = ?, auction_owner = ?, auction_world = ?, auction_silent = ?, auction_item = ?, auction_cost = ?, auction_increment = ?, auction_global = ?, auction_time = ?, auction_node = ?",
          new Object[] {
              auction.getLotNumber(),
              auction.getAdded(),
              auction.getStartTime(),
              auction.getPlayer().toString(),
              auction.getWorld(),
              SQLDatabase.boolToDB(auction.getSilent()),
              auction.getItem().toString(),
              auction.getCost().getAmount(),
              auction.getIncrement(),
              SQLDatabase.boolToDB(auction.getGlobal()),
              auction.getTime(),
              auction.getNode(),
              auction.getAdded(),
              auction.getStartTime(),
              auction.getPlayer().toString(),
              auction.getWorld(),
              SQLDatabase.boolToDB(auction.getSilent()),
              auction.getItem().toString(),
              auction.getCost().getAmount(),
              auction.getIncrement(),
              SQLDatabase.boolToDB(auction.getGlobal()),
              auction.getTime(),
              auction.getNode()
          }
      );
      sql().close();
    }
  }

  @Override
  public void deleteAuction(Auction auction) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      sql().executePreparedUpdate("DELETE FROM " + prefix + "_AUCTIONS WHERE auction_lot = ? ", new Object[] { auction.getLotNumber() });
      sql().close();
    }
  }

  @Override
  public void loadClaim(UUID owner, Integer lot) {

  }

  @Override
  public void saveClaim(Claim claim) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      String table = prefix + "_CLAIMS";
      sql().executePreparedUpdate("INSERT INTO `" + table + "` (claim_player, claim_lot, claim_item, claim_paid, claim_cost) VALUES(?, ?, ?, ?, ?)" +
              " ON DUPLICATE KEY UPDATE claim_item = ?, claim_paid = ?, claim_cost = ?",
          new Object[] {
              claim.getPlayer().toString(),
              claim.getLot(),
              claim.getItem().toString(),
              SQLDatabase.boolToDB(claim.isPaid()),
              claim.getCost().getAmount(),
              claim.getItem().toString(),
              SQLDatabase.boolToDB(claim.isPaid()),
              claim.getCost().getAmount()
          });
      sql().close();
    }
  }

  @Override
  public void deleteClaim(Claim claim) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      sql().executePreparedUpdate("DELETE FROM " + prefix + "_CLAIMS WHERE claim_player = ? AND claim_lot = ?", new Object[] { claim.getLot(), claim.getPlayer().toString() });
      sql().close();
    }
  }

  @Override
  public void loadID(String username) {

  }

  @Override
  public void saveID(String username, UUID id) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      String table = prefix + "_ECOIDS";
      sql().executePreparedUpdate("INSERT INTO `" + table + "` (username, uuid) VALUES (?, ?) ON DUPLICATE KEY UPDATE username = ?",
          new Object[] {
              username,
              id.toString(),
              username
          });
      sql().close();
    }
  }

  @Override
  public void removeID(String username) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      sql().executePreparedUpdate("DELETE FROM " + prefix + "_ECOIDS WHERE username = ?", new Object[] { username });
      sql().close();
    }
  }

  @Override
  public void removeID(UUID id) {
    if(!TNE.instance.saveManager.type.equalsIgnoreCase("flatfile")) {
      sql().executePreparedUpdate("DELETE FROM " + prefix + "_ECOIDS WHERE uuid = ?", new Object[] { id.toString() });
      sql().close();
    }
  }

  @Override
  public void loadFlat(File file) {
    db = new FlatFile(TNE.instance.getDataFolder() + File.separator + TNE.configurations.getString("Core.Database.FlatFile.File"));
    FlatFileConnection connection = (FlatFileConnection)db.connection();
    Section accounts = null;
    Section ids = null;
    Section shops = null;
    Section auctions = null;
    Section claims = null;
    Section signs = null;
    Section transactions = null;
    try {
      connection.getOIS().readDouble();
      accounts = (Section) connection.getOIS().readObject();
      ids = (Section) connection.getOIS().readObject();
      shops = (Section) connection.getOIS().readObject();
      auctions = (Section) connection.getOIS().readObject();
      claims = (Section) connection.getOIS().readObject();
      signs = (Section) connection.getOIS().readObject();
      transactions = (Section) connection.getOIS().readObject();
      connection.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    Iterator<Map.Entry<String, Article>> it = accounts.getArticle().entrySet().iterator();

    while(it.hasNext()) {
      Map.Entry<String, Article> entry = it.next();
      UUID uid = UUID.fromString(entry.getKey());
      Entry info = entry.getValue().getEntry("info");
      Entry balances = entry.getValue().getEntry("balances");
      Entry banks = entry.getValue().getEntry("banks");

      Account account = new Account(uid, (Integer) info.getData("accountnumber"));
      Map<String, Double> balanceMap = new HashMap<>();
      Map<String, Bank> bankMap = new HashMap<>();

      account.setAccountNumber((Integer) info.getData("accountnumber"));
      account.setStatus((String) info.getData("status"));
      account.setPin((String) info.getData("pin"));
      account.creditsFromString((String)info.getData("inventory_credits"));
      account.commandsFromString((String)info.getData("command_credits"));

      Iterator<Map.Entry<String, Object>> balanceIterator = balances.getData().entrySet().iterator();

      while(balanceIterator.hasNext()) {
        java.util.Map.Entry<String, Object> balanceEntry = balanceIterator.next();

        balanceMap.put(balanceEntry.getKey(), (Double)balanceEntry.getValue());
      }
      account.setBalances(balanceMap);

      Iterator<java.util.Map.Entry<String, Object>> bankIterator = banks.getData().entrySet().iterator();

      while(bankIterator.hasNext()) {
        java.util.Map.Entry<String, Object> bankEntry = bankIterator.next();

        bankMap.put(bankEntry.getKey(), Bank.fromString((String)bankEntry.getValue()));
      }
      account.setBanks(bankMap);

      TNE.instance.manager.accounts.put(uid, account);
    }

    Iterator<Map.Entry<String, Article>> idsIterator = ids.getArticle().entrySet().iterator();

    while(idsIterator.hasNext()) {
      Map.Entry<String, Article> idEntry = idsIterator.next();

      Entry info = idEntry.getValue().getEntry("info");

      TNE.instance.manager.ecoIDs.put((String)info.getData("username"), UUID.fromString((String)info.getData("uuid")));
    }

    Iterator<Map.Entry<String, Article>> shopsIterator = shops.getArticle().entrySet().iterator();
    while(shopsIterator.hasNext()) {
      Map.Entry<String, Article> shopEntry = shopsIterator.next();

      Entry info = shopEntry.getValue().getEntry("info");
      Shop s = new Shop(shopEntry.getKey(), (String)info.getData("world"));

      s.setOwner(UUID.fromString((String)info.getData("owner")));
      s.setHidden((boolean)info.getData("hidden"));
      s.setAdmin((boolean)info.getData("admin"));
      s.listFromString((String)info.getData("blacklist"), true);
      s.listFromString((String)info.getData("whitelist"), false);
      s.sharesFromString((String)info.getData("shares"));
      MISCUtils.debug("Items:" + info.getData("items"));

      if(!((String)info.getData("items")).trim().equals("")) {
        s.itemsFromString((String) info.getData("items"));
      }

      TNE.instance.manager.shops.put(shopEntry.getKey() + ":" + s.getWorld(), s);
    }

    for(Article a : auctions.getArticle().values()) {
      Entry info = a.getEntry("info");
      Auction auction = new Auction((int)info.getData("lot"));
      auction.setAdded((int)info.getData("added"));
      auction.setStartTime((int)info.getData("start"));
      auction.setPlayer(UUID.fromString((String)info.getData("player")));
      auction.setWorld((String)info.getData("world"));
      auction.setSilent((boolean)info.getData("silent"));
      auction.setItem(SerializableItemStack.fromString((String)info.getData("item")));
      auction.setCost(new TransactionCost((double)info.getData("cost")));
      auction.setIncrement((double)info.getData("increment"));
      auction.setGlobal((boolean)info.getData("global"));
      auction.setTime((int)info.getData("time"));
      auction.setNode((String)info.getData("node"));

      TNE.instance.manager.auctionManager.add(auction);
    }

    for(Article a : claims.getArticle().values()) {
      Entry info = a.getEntry("info");

      Claim claim = new Claim(
          UUID.fromString((String)info.getData("player")),
          (int)info.getData("lot"),
          SerializableItemStack.fromString((String)info.getData("item")),
          new TransactionCost((double)info.getData("cost"))
      );
      claim.setPaid((boolean)info.getData("paid"));

      TNE.instance.manager.auctionManager.unclaimed.add(claim);
    }

    Iterator<Map.Entry<String, Article>> signsIterator = signs.getArticle().entrySet().iterator();
    while(signsIterator.hasNext()) {
      Map.Entry<String, Article> signEntry = signsIterator.next();
      Entry info = signEntry.getValue().getEntry("info");

      TNESign sign = SignUtils.instance((String)info.getData("type"), UUID.fromString((String)info.getData("owner")));
      sign.setLocation(SerializableLocation.fromString((String)info.getData("location")));
      sign.loadMeta((String)info.getData("meta"));

      TNE.instance.manager.signs.put(sign.getLocation(), sign);
    }

    if(transactions != null) {
      for (Article a : transactions.getArticle().values()) {
        Entry info = a.getEntry("info");

        TNE.instance.manager.transactions.add(
            (String) info.getData("id"),
            (String) info.getData("initiator"),
            (String) info.getData("player"),
            (String) info.getData("world"),
            TransactionType.fromID((String) info.getData("type")),
            new TransactionCost((Double) info.getData("cost")),
            (Double) info.getData("oldBalance"),
            (Double) info.getData("balance"),
            (Long) info.getData("time")
        );
      }
    }
  }

  @Override
  public void saveFlat(File file) {
    Iterator<java.util.Map.Entry<UUID, Account>> accIT = TNE.instance.manager.accounts.entrySet().iterator();

    Section accounts = new Section("accounts");

    while(accIT.hasNext()) {
      java.util.Map.Entry<UUID, Account> entry = accIT.next();

      Account acc = entry.getValue();
      Article account = new Article(entry.getKey().toString());
      //Info
      Entry info = new Entry("info");
      info.addData("accountnumber", acc.getAccountNumber());
      info.addData("uuid", acc.getUid());
      info.addData("status", acc.getStatus().getName());
      info.addData("inventory_credits", acc.creditsToString());
      info.addData("command_credits", acc.commandsToString());
      info.addData("pin", acc.getPin());
      account.addEntry(info);
      //Balances
      Entry balances = new Entry("balances");
      Iterator<java.util.Map.Entry<String, Double>> balIT = acc.getBalances().entrySet().iterator();

      while(balIT.hasNext()) {
        java.util.Map.Entry<String, Double> balanceEntry = balIT.next();
        balances.addData(balanceEntry.getKey(), balanceEntry.getValue());
      }
      account.addEntry(balances);

      Entry banks = new Entry("banks");

      Iterator<java.util.Map.Entry<String, Bank>> bankIT = acc.getBanks().entrySet().iterator();

      while(bankIT.hasNext()) {
        java.util.Map.Entry<String, Bank> bankEntry = bankIT.next();
        banks.addData(bankEntry.getKey(), bankEntry.getValue().toString());
      }
      account.addEntry(banks);

      accounts.addArticle(entry.getKey().toString(), account);
    }

    Iterator<Map.Entry<String, UUID>> idsIT = TNE.instance.manager.ecoIDs.entrySet().iterator();

    Section ids = new Section("IDS");

    while(idsIT.hasNext()) {
      Map.Entry<String, UUID> idEntry = idsIT.next();

      Article a = new Article(idEntry.getKey());
      Entry e = new Entry("info");

      e.addData("username", idEntry.getKey());
      e.addData("uuid", idEntry.getValue().toString());
      a.addEntry(e);

      ids.addArticle(idEntry.getKey(), a);
    }

    Iterator<Map.Entry<String, Shop>> shopIT = TNE.instance.manager.shops.entrySet().iterator();
    Section shops = new Section("SHOPS");

    while(shopIT.hasNext()) {
      Map.Entry<String, Shop> shopEntry = shopIT.next();
      Shop s = shopEntry.getValue();

      Article a = new Article(s.getName());
      Entry info = new Entry("info");

      info.addData("owner", s.getOwner().toString());
      info.addData("world", s.getWorld());
      info.addData("hidden", s.isHidden());
      info.addData("admin", s.isAdmin());
      MISCUtils.debug("Items:" + s.itemsToString());
      info.addData("items", s.itemsToString());
      info.addData("blacklist", s.listToString(true));
      info.addData("whitelist", s.listToString(false));
      info.addData("shares", s.sharesToString());
      a.addEntry(info);

      shops.addArticle(s.getName(), a);
    }

    Section auctions = new Section("AUCTIONS");
    for(Auction auction : TNE.instance.manager.auctionManager.getJoined()) {
      Article a = new Article(auction.getLotNumber() + "");
      Entry info = new Entry("info");
      info.addData("lot", auction.getLotNumber());
      info.addData("added", auction.getAdded());
      info.addData("start", auction.getStartTime());
      info.addData("player", auction.getPlayer().toString());
      info.addData("world", auction.getWorld());
      info.addData("silent", auction.getSilent());
      info.addData("item", auction.getItem().toString());
      info.addData("cost", auction.getCost().getAmount());
      info.addData("increment", auction.getIncrement());
      info.addData("global", auction.getGlobal());
      info.addData("time", auction.getTime());
      info.addData("node", auction.getNode());
      a.addEntry(info);
      auctions.addArticle(auction.getLotNumber() + "", a);
    }

    Section claims = new Section("CLAIMS");
    for(Claim claim : TNE.instance.manager.auctionManager.unclaimed) {
      Article a = new Article(claim.getLot() + "");
      Entry info = new Entry("info");
      info.addData("player", claim.getPlayer().toString());
      info.addData("lot", claim.getLot());
      info.addData("item", claim.getItem().toString());
      info.addData("paid", claim.isPaid());
      info.addData("cost", claim.getCost().getAmount());
      a.addEntry(info);
      claims.addArticle(claim.getLot() + "", a);
    }

    Iterator<Map.Entry<SerializableLocation, TNESign>> signIT = TNE.instance.manager.signs.entrySet().iterator();
    Section signs = new Section("SIGNS");

    while(signIT.hasNext()) {
      Map.Entry<SerializableLocation, TNESign> signEntry = signIT.next();
      TNESign sign = signEntry.getValue();

      Article a = new Article(sign.getLocation().toString());
      Entry info = new Entry("info");

      info.addData("owner", sign.getOwner().toString());
      info.addData("type", sign.getType().getName());
      info.addData("extra", sign.getMeta());
      info.addData("location", sign.getLocation().toString());
      a.addEntry(info);

      signs.addArticle(sign.getLocation().toString(), a);
    }

    Section transactions = new Section("TRANSACTIONS");
    for(Map.Entry<String, TransactionHistory> entry : TNE.instance.manager.transactions.transactionHistory.entrySet()) {
      for(Record r : entry.getValue().getRecords()) {
        Article a = new Article(r.getId());
        Entry info = new Entry("info");
        info.addData("id", r.getId());
        info.addData("initiator", r.getInitiator());
        info.addData("player", r.getPlayer());
        info.addData("world", r.getWorld());
        info.addData("type", r.getType());
        info.addData("cost", r.getCost());
        info.addData("oldBalance", r.getOldBalance());
        info.addData("balance", r.getBalance());
        info.addData("time", r.getTime());
        a.addEntry(info);
        transactions.addArticle(r.getId(), a);
      }
    }

    try {
      db = new FlatFile(TNE.instance.getDataFolder() + File.separator + TNE.configurations.getString("Core.Database.FlatFile.File"));
      FlatFileConnection connection = (FlatFileConnection)db.connection();
      connection.getOOS().writeDouble(versionNumber());
      connection.getOOS().writeObject(accounts);
      connection.getOOS().writeObject(ids);
      connection.getOOS().writeObject(shops);
      connection.getOOS().writeObject(auctions);
      connection.getOOS().writeObject(claims);
      connection.getOOS().writeObject(signs);
      connection.getOOS().writeObject(transactions);
      connection.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void loadMySQL() {

    db = new MySQL(mysqlHost, mysqlPort, mysqlDatabase, mysqlUser, mysqlPassword);

    String table = prefix + "_USERS";

    try {
      int accountIndex = mysql().executeQuery("SELECT * FROM " + table + ";");

      while(mysql().results(accountIndex).next()) {
        Account account = new Account(UUID.fromString(mysql().results(accountIndex).getString("uuid")));
        account.balancesFromString(mysql().results(accountIndex).getString("balances"));
        account.setAccountNumber(mysql().results(accountIndex).getInt("accountnumber"));
        account.setStatus(mysql().results(accountIndex).getString("accountstatus"));
        account.setJoined(mysql().results(accountIndex).getString("joinedDate"));
        account.creditsFromString(mysql().results(accountIndex).getString("inventory_credits"));
        account.commandsFromString(mysql().results(accountIndex).getString("command_credits"));
        account.setPin(mysql().results(accountIndex).getString("acc_pin"));

        String bankTable = prefix + "_BANKS";
        int bankIndex = mysql().executePreparedQuery("SELECT * FROM " + bankTable + " WHERE uuid = ?;", new Object[] { account.getUid().toString() });

        while(mysql().results(bankIndex).next()) {
          account.getBanks().put(mysql().results(bankIndex).getString("world"), Bank.fromString(mysql().results(bankIndex).getString("bank")));
        }
        TNE.instance.manager.accounts.put(account.getUid(), account);
      }

      table = prefix + "_ECOIDS";
      int idIndex = mysql().executeQuery("SELECT * FROM " + table + ";");
      while(mysql().results(idIndex).next()) {
        TNE.instance.manager.ecoIDs.put(mysql().results(idIndex).getString("username"), UUID.fromString(mysql().results(idIndex).getString("uuid")));
      }

      table = prefix + "_SHOPS";
      int shopIndex = mysql().executeQuery("SELECT * FROM `" + table + "`;");
      while(mysql().results(shopIndex).next()) {
        Shop s = new Shop(mysql().results(shopIndex).getString("shop_name"), mysql().results(shopIndex).getString("shop_world"));
        s.setOwner(UUID.fromString(mysql().results(shopIndex).getString("shop_owner")));
        s.setHidden(SQLDatabase.boolFromDB(mysql().results(shopIndex).getInt("shop_hidden")));
        s.setAdmin(SQLDatabase.boolFromDB(mysql().results(shopIndex).getInt("shop_admin")));
        s.itemsFromString(mysql().results(shopIndex).getString("shop_items"));
        s.listFromString(mysql().results(shopIndex).getString("shop_blacklist"), true);
        s.listFromString(mysql().results(shopIndex).getString("shop_whitelist"), false);
        s.sharesFromString(mysql().results(shopIndex).getString("shop_shares"));
        TNE.instance.manager.shops.put(s.getName() + ":" + s.getWorld(), s);
      }

      table = prefix + "_AUCTIONS";
      int auctionIndex = mysql().executeQuery("SELECT * FROM `" + table + "`;");
      while(mysql().results(auctionIndex).next()) {
        Auction auction = new Auction(mysql().results(auctionIndex).getInt("auction_lot"));
        auction.setAdded(mysql().results(auctionIndex).getInt("auction_added"));
        auction.setStartTime(mysql().results(auctionIndex).getInt("auction_start"));
        auction.setPlayer(UUID.fromString(mysql().results(auctionIndex).getString("auction_owner")));
        auction.setWorld(mysql().results(auctionIndex).getString("auction_world"));
        auction.setSilent(SQLDatabase.boolFromDB(mysql().results(auctionIndex).getInt("auction_silent")));
        auction.setItem(SerializableItemStack.fromString(mysql().results(auctionIndex).getString("auction_item")));
        auction.setCost(new TransactionCost(Double.valueOf(mysql().results(auctionIndex).getString("auction_cost"))));
        auction.setIncrement(mysql().results(auctionIndex).getDouble("auction_increment"));
        auction.setGlobal(SQLDatabase.boolFromDB(mysql().results(auctionIndex).getInt("auction_global")));
        auction.setTime(mysql().results(auctionIndex).getInt("auction_time"));
        auction.setNode(mysql().results(auctionIndex).getString("auction_node"));

        TNE.instance.manager.auctionManager.add(auction);
      }

      table = prefix + "_CLAIMS";
      int claimIndex = mysql().executeQuery("SELECT * FROM `" + table + "`;");
      while(mysql().results(claimIndex).next()) {
        Claim claim = new Claim(//uuid, lot, item, cost
            UUID.fromString(mysql().results(claimIndex).getString("claim_player")),
            mysql().results(claimIndex).getInt("claim_lot"),
            SerializableItemStack.fromString(mysql().results(claimIndex).getString("claim_item")),
            new TransactionCost(Double.valueOf(mysql().results(claimIndex).getString("claim_cost")))
        );
        claim.setPaid(SQLDatabase.boolFromDB(mysql().results(claimIndex).getInt("claim_paid")));

        TNE.instance.manager.auctionManager.unclaimed.add(claim);
      }

      table = prefix + "_SIGNS";
      int signIndex = mysql().executeQuery("SELECT * FROM `" + table + "`;");
      while(mysql().results(signIndex).next()) {
        TNESign sign = SignUtils.instance(mysql().results(signIndex).getString("sign_type"), UUID.fromString(mysql().results(signIndex).getString("sign_owner")));
        sign.setLocation(SerializableLocation.fromString(mysql().results(signIndex).getString("sign_location")));
        sign.loadMeta(mysql().results(signIndex).getString("sign_meta"));
        TNE.instance.manager.signs.put(sign.getLocation(), sign);
      }

      table = prefix + "_TRANSACTIONS";
      int transactionIndex = mysql().executeQuery("SELECT * FROM `" + table + "`;");
      while(mysql().results(transactionIndex).next()) {
        TNE.instance.manager.transactions.add(
            mysql().results(transactionIndex).getString("trans_id"),
            mysql().results(transactionIndex).getString("trans_initiator"),
            mysql().results(transactionIndex).getString("trans_player"),
            mysql().results(transactionIndex).getString("trans_world"),
            TransactionType.fromID(mysql().results(transactionIndex).getString("trans_type")),
            new TransactionCost(mysql().results(transactionIndex).getDouble("trans_cost")),
            mysql().results(transactionIndex).getDouble("trans_oldBalance"),
            mysql().results(transactionIndex).getDouble("trans_balance"),
            mysql().results(transactionIndex).getLong("trans_time")
        );
      }
      mysql().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void saveMySQL() {
    createTables("mysql");
    String table = prefix + "_INFO";
    db = new MySQL(mysqlHost, mysqlPort, mysqlDatabase, mysqlUser, mysqlPassword);
    mysql().executePreparedUpdate("Update " + table + " SET version = ?, server_name = ? WHERE id = 1;", new Object[] { String.valueOf(versionNumber()), TNE.instance.getServer().getServerName() });

    for(Account acc : TNE.instance.manager.accounts.values()) {
      saveAccount(acc);
    }

    for(Map.Entry<String, UUID> entry : TNE.instance.manager.ecoIDs.entrySet()) {
      saveID(entry.getKey(), entry.getValue());
    }

    for(Shop s : TNE.instance.manager.shops.values()) {
      saveShop(s);
    }

    for(Auction auction : TNE.instance.manager.auctionManager.getJoined()) {
      saveAuction(auction);
    }

    for(Claim claim : TNE.instance.manager.auctionManager.unclaimed) {
      saveClaim(claim);
    }

    for(TNESign sign : TNE.instance.manager.signs.values()) {
      saveSign(sign);
    }

    for(Map.Entry<String, TransactionHistory> entry : TNE.instance.manager.transactions.transactionHistory.entrySet()) {
      for(Record r : entry.getValue().getRecords()) {
        saveTransaction(r);
      }
    }
    mysql().close();
  }

  @Override
  public void loadSQLite() {
    loadH2();
  }

  @Override
  public void saveSQLite() {
    saveH2();
  }

  @Override
  public void loadH2() {
    db = new H2(h2File, mysqlUser, mysqlPassword);

    String table = prefix + "_USERS";

    try {
      int accountIndex = h2().executeQuery("SELECT * FROM " + table + ";");

      while(h2().results(accountIndex).next()) {
        Account account = new Account(UUID.fromString(h2().results(accountIndex).getString("uuid")));
        account.balancesFromString(h2().results(accountIndex).getString("balances"));
        account.setAccountNumber(h2().results(accountIndex).getInt("accountnumber"));
        account.setStatus(h2().results(accountIndex).getString("accountstatus"));
        account.setJoined(h2().results(accountIndex).getString("joinedDate"));
        account.creditsFromString(h2().results(accountIndex).getString("inventory_credits"));
        account.commandsFromString(h2().results(accountIndex).getString("command_credits"));
        account.setPin(h2().results(accountIndex).getString("acc_pin"));

        String bankTable = prefix + "_BANKS";
        int bankIndex = h2().executePreparedQuery("SELECT * FROM " + bankTable + " WHERE uuid = ?;", new Object[] { account.getUid().toString() });

        while(h2().results(bankIndex).next()) {
          account.getBanks().put(h2().results(bankIndex).getString("world"), Bank.fromString(h2().results(bankIndex).getString("bank")));
        }
        TNE.instance.manager.accounts.put(account.getUid(), account);
      }

      table = prefix + "_ECOIDS";
      int idIndex = h2().executeQuery("SELECT * FROM " + table + ";");
      while(h2().results(idIndex).next()) {
        TNE.instance.manager.ecoIDs.put(h2().results(idIndex).getString("username"), UUID.fromString(h2().results(idIndex).getString("uuid")));
      }

      table = prefix + "_SHOPS";
      int shopIndex = h2().executeQuery("SELECT * FROM `" + table + "`;");
      while(h2().results(shopIndex).next()) {
        Shop s = new Shop(h2().results(shopIndex).getString("shop_name"), h2().results(shopIndex).getString("shop_world"));
        s.setOwner(UUID.fromString(h2().results(shopIndex).getString("shop_owner")));
        s.setHidden(SQLDatabase.boolFromDB(h2().results(shopIndex).getInt("shop_hidden")));
        s.setAdmin(SQLDatabase.boolFromDB(h2().results(shopIndex).getInt("shop_admin")));
        s.itemsFromString(h2().results(shopIndex).getString("shop_items"));
        s.listFromString(h2().results(shopIndex).getString("shop_blacklist"), true);
        s.listFromString(h2().results(shopIndex).getString("shop_whitelist"), false);
        s.sharesFromString(h2().results(shopIndex).getString("shop_shares"));
        TNE.instance.manager.shops.put(s.getName() + ":" + s.getWorld(), s);
      }

      table = prefix + "_AUCTIONS";
      int auctionIndex = h2().executeQuery("SELECT * FROM `" + table + "`;");
      while(h2().results(auctionIndex).next()) {
        Auction auction = new Auction(h2().results(auctionIndex).getInt("auction_lot"));
        auction.setAdded(h2().results(auctionIndex).getInt("auction_added"));
        auction.setStartTime(h2().results(auctionIndex).getInt("auction_start"));
        auction.setPlayer(UUID.fromString(h2().results(auctionIndex).getString("auction_owner")));
        auction.setWorld(h2().results(auctionIndex).getString("auction_world"));
        auction.setSilent(SQLDatabase.boolFromDB(h2().results(auctionIndex).getInt("auction_silent")));
        auction.setItem(SerializableItemStack.fromString(h2().results(auctionIndex).getString("auction_item")));
        auction.setCost(new TransactionCost(Double.valueOf(h2().results(auctionIndex).getString("auction_cost"))));
        auction.setIncrement(h2().results(auctionIndex).getDouble("auction_increment"));
        auction.setGlobal(SQLDatabase.boolFromDB(h2().results(auctionIndex).getInt("auction_global")));
        auction.setTime(h2().results(auctionIndex).getInt("auction_time"));
        auction.setNode(h2().results(auctionIndex).getString("auction_node"));

        TNE.instance.manager.auctionManager.add(auction);
      }

      table = prefix + "_CLAIMS";
      int claimsIndex = h2().executeQuery("SELECT * FROM `" + table + "`;");
      while(h2().results(claimsIndex).next()) {
        Claim claim = new Claim(
            UUID.fromString(h2().results(claimsIndex).getString("claim_player")),
            h2().results(claimsIndex).getInt("claim_lot"),
            SerializableItemStack.fromString(h2().results(claimsIndex).getString("claim_item")),
            new TransactionCost(Double.valueOf(h2().results(claimsIndex).getString("claim_cost")))
        );
        claim.setPaid(SQLDatabase.boolFromDB(h2().results(claimsIndex).getInt("claim_paid")));

        TNE.instance.manager.auctionManager.unclaimed.add(claim);
      }

      table = prefix + "_SIGNS";
      int signIndex = h2().executeQuery("SELECT * FROM `" + table + "`;");
      while(h2().results(signIndex).next()) {
        TNESign sign = SignUtils.instance(h2().results(signIndex).getString("sign_type"), UUID.fromString(h2().results(signIndex).getString("sign_owner")));
        sign.setLocation(SerializableLocation.fromString(h2().results(signIndex).getString("sign_location")));
        sign.loadMeta(h2().results(signIndex).getString("sign_meta"));

        TNE.instance.manager.signs.put(sign.getLocation(), sign);
      }

      table = prefix + "_TRANSACTIONS";
      int transactionIndex = h2().executeQuery("SELECT * FROM `" + table + "`;");
      while(h2().results(transactionIndex).next()) {
        TNE.instance.manager.transactions.add(
            h2().results(transactionIndex).getString("trans_id"),
            h2().results(transactionIndex).getString("trans_initiator"),
            h2().results(transactionIndex).getString("trans_player"),
            h2().results(transactionIndex).getString("trans_world"),
            TransactionType.fromID(h2().results(transactionIndex).getString("trans_type")),
            new TransactionCost(h2().results(transactionIndex).getDouble("trans_cost")),
            h2().results(transactionIndex).getDouble("trans_oldBalance"),
            h2().results(transactionIndex).getDouble("trans_balance"),
            h2().results(transactionIndex).getLong("trans_time")
        );
      }
      h2().close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void saveH2() {
    createTables("h2");
    String table = prefix + "_INFO";
    db = new H2(h2File, mysqlUser, mysqlPassword);

    h2().executePreparedUpdate("Update " + table + " SET version = ? WHERE id = 1;", new Object[] { String.valueOf(versionNumber()) });

    for(Account acc : TNE.instance.manager.accounts.values()) {
      saveAccount(acc);
    }

    for(Map.Entry<String, UUID> entry : TNE.instance.manager.ecoIDs.entrySet()) {
      saveID(entry.getKey(), entry.getValue());
    }

    for(Shop s : TNE.instance.manager.shops.values()) {
      saveShop(s);
    }

    for(Auction auction : TNE.instance.manager.auctionManager.getJoined()) {
      saveAuction(auction);
    }

    for(Claim claim : TNE.instance.manager.auctionManager.unclaimed) {
      saveClaim(claim);
    }

    for(TNESign sign : TNE.instance.manager.signs.values()) {
      saveSign(sign);
    }

    for(Map.Entry<String, TransactionHistory> entry : TNE.instance.manager.transactions.transactionHistory.entrySet()) {
      for(Record r : entry.getValue().getRecords()) {
        saveTransaction(r);
      }
    }

    h2().close();
  }

  @Override
  public void createTables(String type) {
    String table = prefix + "_INFO";

    if(type.equalsIgnoreCase("mysql")) {
      db = new MySQL(mysqlHost, mysqlPort, mysqlDatabase, mysqlUser, mysqlPassword);

      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`id` INTEGER NOT NULL UNIQUE," +
          "`version` VARCHAR(10)," +
          "`server_name` VARCHAR(250)" +
          ");");
      mysql().executePreparedUpdate("INSERT INTO `" + table + "` (id, version, server_name) VALUES(1, ?, ?) ON DUPLICATE KEY UPDATE version = ?, server_name = ?",
          new Object[] {
              versionNumber(),
              TNE.instance.getServer().getServerName(),
              versionNumber(),
              TNE.instance.getServer().getServerName()
          });

      table = prefix + "_ECOIDS";
      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (" +
          "`username` VARCHAR(56)," +
          "`uuid` VARCHAR(36) UNIQUE" +
          ");");

      table = prefix + "_USERS";
      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`uuid` VARCHAR(36) NOT NULL UNIQUE," +
          "`inventory_credits` LONGTEXT," +
          "`command_credits` LONGTEXT," +
          "`acc_pin` VARCHAR(30)," +
          "`balances` LONGTEXT," +
          "`joinedDate` VARCHAR(60)," +
          "`accountnumber` INTEGER," +
          "`accountstatus` VARCHAR(60)" +
          ");");

      table = prefix + "_SHOPS";
      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`shop_owner` VARCHAR(36)," +
          "`shop_world` VARCHAR(50) NOT NULL," +
          "`shop_name` VARCHAR(60) NOT NULL," +
          "`shop_hidden` TINYINT(1)," +
          "`shop_admin` TINYINT(1)," +
          "`shop_items` LONGTEXT," +
          "`shop_blacklist` LONGTEXT," +
          "`shop_whitelist` LONGTEXT," +
          "`shop_shares` LONGTEXT," +
          "PRIMARY KEY(shop_name, shop_world)" +
          ");");

      table = prefix + "_AUCTIONS";
      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`auction_lot` INT(60) NOT NULL," +
          "`auction_added` BIGINT(60) NOT NULL," +
          "`auction_start` BIGINT(60) NOT NULL," +
          "`auction_owner` VARCHAR(36)," +
          "`auction_world` VARCHAR(36)," +
          "`auction_silent` TINYINT(1)," +
          "`auction_item` LONGTEXT," +
          "`auction_cost` LONGTEXT," +
          "`auction_increment` DOUBLE," +
          "`auction_global` TINYINT(1)," +
          "`auction_time` INT(20)," +
          "`auction_node` LONGTEXT," +
          "PRIMARY KEY(auction_lot)" +
          ");");

      table = prefix +"_CLAIMS";
      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`claim_player` VARCHAR(36)," +
          "`claim_lot` INT(60) NOT NULL," +
          "`claim_item` LONGTEXT," +
          "`claim_paid` TINYINT(1)," +
          "`claim_cost` LONGTEXT," +
          "PRIMARY KEY(claim_player, claim_lot)" +
          ");");

      table = prefix + "_BANKS";
      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`uuid` VARCHAR(36) NOT NULL," +
          "`world` VARCHAR(50) NOT NULL," +
          "`bank` LONGTEXT," +
          "PRIMARY KEY(uuid, world)" +
          ");");

      table = prefix + "_SIGNS";
      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`sign_owner` VARCHAR(36)," +
          "`sign_type` VARCHAR(30) NOT NULL," +
          "`sign_location` VARCHAR(230) NOT NULL UNIQUE," +
          "`sign_meta` LONGTEXT" +
          ");");

      table = prefix + "_TRANSACTIONS";
      mysql().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`trans_id` VARCHAR(36)," +
          "`trans_initiator` VARCHAR(36)," +
          "`trans_player` VARCHAR(36)," +
          "`trans_world` VARCHAR(36)," +
          "`trans_type` VARCHAR(36)," +
          "`trans_cost` DOUBLE," +
          "`trans_oldBalance` DOUBLE," +
          "`trans_balance` DOUBLE," +
          "`trans_time` BIGINT(60)," +
          "PRIMARY KEY(trans_id)" +
          ");");
      mysql().close();
    } else {
      File h2DB = new File(h2File);
      if(!h2DB.exists()) {
        try {
          h2DB.createNewFile();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      db = new H2(h2File, mysqlUser, mysqlPassword);

      h2().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`id` INTEGER NOT NULL UNIQUE," +
          "`version` VARCHAR(10)," +
          "`server_name` VARCHAR(250)" +
          ");");
      h2().executePreparedUpdate("INSERT INTO `" + table + "` (id, version, server_name) VALUES(1, ?, ?) ON DUPLICATE KEY UPDATE version = ?, server_name = ?",
          new Object[] {
              versionNumber(),
              TNE.instance.getServer().getServerName(),
              versionNumber(),
              TNE.instance.getServer().getServerName()
          });

      table = prefix + "_ECOIDS";
      h2().executeUpdate("CREATE TABLE IF NOT EXISTS " + table + " (" +
          "`username` VARCHAR(56)," +
          "`uuid` VARCHAR(36) UNIQUE" +
          ");");

      table = prefix + "_USERS";
      h2().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`uuid` VARCHAR(36) NOT NULL UNIQUE," +
          "`inventory_credits` LONGTEXT," +
          "`command_credits` LONGTEXT," +
          "`acc_pin` VARCHAR(30)," +
          "`balances` LONGTEXT," +
          "`joinedDate` VARCHAR(60)," +
          "`accountnumber` INTEGER," +
          "`accountstatus` VARCHAR(60)" +
          ");");

      table = prefix + "_SHOPS";
      h2().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`shop_owner` VARCHAR(36)," +
          "`shop_name` VARCHAR(60) NOT NULL PRIMARY KEY," +
          "`shop_world` VARCHAR(50) NOT NULL PRIMARY KEY," +
          "`shop_hidden` TINYINT(1)," +
          "`shop_admin` TINYINT(1)," +
          "`shop_items` LONGTEXT," +
          "`shop_blacklist` LONGTEXT," +
          "`shop_whitelist` LONGTEXT," +
          "`shop_shares` LONGTEXT" +
          ");");

      table = prefix + "_AUCTIONS";
      h2().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`auction_lot` INT(60) NOT NULL," +
          "`auction_added` BIGINT(60) NOT NULL," +
          "`auction_start` BIGINT(60) NOT NULL," +
          "`auction_owner` VARCHAR(36)," +
          "`auction_world` VARCHAR(36)," +
          "`auction_silent` TINYINT(1)," +
          "`auction_item` LONGTEXT," +
          "`auction_cost` LONGTEXT," +
          "`auction_increment` DOUBLE," +
          "`auction_global` TINYINT(1)," +
          "`auction_time` INT(20)," +
          "`auction_node` LONGTEXT," +
          "PRIMARY KEY(auction_lot)" +
          ");");

      table = prefix +"_CLAIMS";
      h2().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`claim_player` VARCHAR(36)," +
          "`claim_lot` INT(60) NOT NULL," +
          "`claim_item` LONGTEXT," +
          "`claim_paid` TINYINT(1)," +
          "`claim_cost` LONGTEXT," +
          "PRIMARY KEY(claim_player, claim_lot)" +
          ");");

      table = prefix + "_BANKS";
      h2().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`uuid` VARCHAR(36) NOT NULL PRIMARY KEY," +
          "`world` VARCHAR(50) NOT NULL PRIMARY KEY," +
          "`bank` LONGTEXT" +
          ");");

      table = prefix + "_SIGNS";
      h2().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`sign_owner` VARCHAR(36)," +
          "`sign_type` VARCHAR(30) NOT NULL," +
          "`sign_location` VARCHAR(230) NOT NULL UNIQUE," +
          "`sign_meta` LONGTEXT" +
          ");");

      table = prefix + "_TRANSACTIONS";
      h2().executeUpdate("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
          "`trans_id` VARCHAR(36)," +
          "`trans_initiator` VARCHAR(36)," +
          "`trans_player` VARCHAR(36)," +
          "`trans_world` VARCHAR(36)," +
          "`trans_type` VARCHAR(36)," +
          "`trans_cost` DOUBLE," +
          "`trans_oldBalance` DOUBLE," +
          "`trans_balance` DOUBLE," +
          "`trans_time` BIGINT(60)," +
          "PRIMARY KEY(trans_id)" +
          ");");
      h2().close();
    }
  }
}