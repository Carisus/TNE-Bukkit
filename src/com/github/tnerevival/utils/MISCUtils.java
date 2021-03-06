package com.github.tnerevival.utils;

import com.github.tnerevival.TNE;
import com.github.tnerevival.account.IDFinder;
import com.github.tnerevival.serializable.SerializableItemStack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MISCUtils {

  //Minecraft Version Utils
  public static boolean isOneEight() {
    return Bukkit.getVersion().contains("1.8") || isOneNine() || isOneTen() || isOneEleven();
  }

  public static boolean isOneNine() {
    return Bukkit.getVersion().contains("1.9") || isOneTen() || isOneEleven();
  }

  public static boolean isOneTen() {
    return Bukkit.getVersion().contains("1.10") || isOneEleven();
  }

  public static boolean isOneEleven() {
    return Bukkit.getVersion().contains("1.11");
  }

  //True MISC Utils
  public static void debug(String message) {
    if(TNE.debugMode) {
      TNE.instance.getLogger().info("[DEBUG MODE]" + message);
    }
  }

  public static void debug(StackTraceElement[] stack) {
    for(StackTraceElement element : stack) {
      debug(element.toString());
    }
  }

  public static void debug(Exception e) {
    if(TNE.debugMode) {
      e.printStackTrace();
    }
  }
  /**
   * Returns the player's account world(or the world it's meant to share accounts with if configured to do so)
   */
  public static String getWorld(UUID id) {
    if(MISCUtils.multiWorld()) {
      if(MISCUtils.getPlayer(id) != null) {
        String actualWorld = MISCUtils.getPlayer(id).getWorld().getName();
        if(MISCUtils.worldConfigExists("Worlds." + actualWorld + ".ShareAccounts") && TNE.instance.worldConfigurations.getBoolean("Worlds." + actualWorld + ".ShareAccounts")) {
          return TNE.instance.worldConfigurations.getString("Worlds." + actualWorld + ".ShareWorld");
        }
        MISCUtils.debug("WORLD USING: " + actualWorld);
        return actualWorld;
      }
    }
    MISCUtils.debug("WORLD USING: Default");
    return TNE.instance.defaultWorld;
  }

  public static String getWorld(String world) {
    if(MISCUtils.multiWorld()) {
      if(MISCUtils.worldConfigExists("Worlds." + world + ".ShareAccounts") && TNE.instance.worldConfigurations.getBoolean("Worlds." + world + ".ShareAccounts")) {
        return TNE.instance.worldConfigurations.getString("Worlds." + world + ".ShareWorld");
      }
    }
    return world;
  }

  public static String getWorld(Player player) {
    return MISCUtils.getWorld(IDFinder.getID(player));
  }

  public static boolean hasItems(UUID id, List<SerializableItemStack> items) {
    for(SerializableItemStack item : items) {
      ItemStack stack = item.toItemStack();
      if(getItemCount(id, stack.getType()) < stack.getAmount()) {
        return false;
      }
    }
    return true;
  }

  public static void setItems(UUID id, List<SerializableItemStack> items, boolean add) {
    setItems(id, items, add, false);
  }

  public static void setItems(UUID id, List<SerializableItemStack> items, boolean add, boolean set) {
    for(SerializableItemStack item : items) {
      Material type = item.toItemStack().getType();
      if(set) {
        setItemCount(id, type, item.getAmount());
        continue;
      }

      if(add) {
        setItemCount(id, type, getItemCount(id, type) + item.getAmount());
        continue;
      }
      setItemCount(id, type, getItemCount(id, type) - item.getAmount());
    }
  }

  public static Integer getItemCount(UUID id, ItemStack stack) {
    Player p = MISCUtils.getPlayer(id);
    int count = 0;
    for(ItemStack i : p.getInventory().getContents()) {
      if(i != null && i.getType() != null && i.getType().equals(stack.getType()) && i.getDurability() == stack.getDurability()) {
        count += i.getAmount();
      }
    }
    return count;
  }

  public static Integer getItemCount(UUID id, Material item) {
    Player p = MISCUtils.getPlayer(id);
    int count = 0;
    if(item != null) {
      for(ItemStack i : p.getInventory().getContents()) {
        if(i != null && i.getType() != null && i.getType() == item) {
          count += i.getAmount();
        }
      }
    }
    return count;
  }

  public static void setItemCount(UUID id, Material item, Integer amount) {
    Player p = MISCUtils.getPlayer(id);
    Integer count = getItemCount(id, item);
    if(item != null) {
      if(count > amount) {
        Integer remove = count - amount;
        Integer slot = 0;
        for(ItemStack i : p.getInventory().getContents()) {
          if(i != null && i.getType() != null && i.getType() == item) {
            if(remove > i.getAmount()) {
              remove -= i.getAmount();
              i.setAmount(0);
              p.getInventory().setItem(slot, null);
            } else {
              if(i.getAmount() - remove > 0) {
                i.setAmount(i.getAmount() - remove);
                p.getInventory().setItem(slot, i);
                return;
              }
              p.getInventory().setItem(slot, null);
              return;
            }
          }
          slot++;
        }
      } else if(count < amount) {
        Integer add = amount - count;

        while(add > 0) {
          if(add > item.getMaxStackSize()) {
            if(p.getInventory().firstEmpty() != -1) {
              p.getInventory().addItem(new ItemStack(item, item.getMaxStackSize()));
            } else {
              p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(item, item.getMaxStackSize()));
            }
            add -= item.getMaxStackSize();
          } else {
            if(p.getInventory().firstEmpty() != -1) {
              p.getInventory().addItem(new ItemStack(item, add));
            } else {
              p.getWorld().dropItemNaturally(p.getLocation(), new ItemStack(item, add));
            }
            add = 0;
          }
        }
      }
    }
  }

  public static void reloadConfigurations(String type) {
    if(type.equalsIgnoreCase("all")) {
      TNE.instance.reloadConfig();
      reloadConfigsMaterials();
      reloadConfigsMessages();
      reloadConfigsMobs();
      reloadConfigsObjects();
      reloadConfigPlayers();
      reloadConfigsWorlds();
      TNE.instance.manager.currencyManager.loadCurrencies();
    } else if(type.equalsIgnoreCase("config")) {
      TNE.instance.reloadConfig();
      TNE.configurations.load(TNE.instance.getConfig(), "main");
    } else if(type.equalsIgnoreCase("currencies")) {
      TNE.instance.manager.currencyManager.loadCurrencies();
    } else if(type.equalsIgnoreCase("materials")) {
      reloadConfigsMaterials();
    } else if(type.equalsIgnoreCase("messages")) {
      reloadConfigsMessages();
    } else if(type.equalsIgnoreCase("mobs")) {
      reloadConfigsMobs();
    } else if(type.equalsIgnoreCase("objects")) {
      reloadConfigsObjects();
    } else if(type.equalsIgnoreCase("players")) {
      reloadConfigPlayers();
    } else if(type.equalsIgnoreCase("worlds")) {
      reloadConfigsWorlds();
    }
  }

  public static void reloadConfigsMaterials() {
    if(TNE.instance.materials == null) {
      TNE.instance.materials = new File(TNE.instance.getDataFolder(), "materials.yml");
    }
    TNE.instance.materialConfigurations = YamlConfiguration.loadConfiguration(TNE.instance.materials);
    TNE.configurations.load(TNE.instance.materialConfigurations, "materials");
  }

  public static void reloadConfigsMobs() {
    if(TNE.instance.mobs == null) {
      TNE.instance.mobs = new File(TNE.instance.getDataFolder(), "mobs.yml");
    }
    TNE.instance.mobConfigurations = YamlConfiguration.loadConfiguration(TNE.instance.mobs);
    TNE.configurations.load(TNE.instance.mobConfigurations, "mob");
  }

  public static void reloadConfigsMessages() {
    if(TNE.instance.messages == null) {
      TNE.instance.messages = new File(TNE.instance.getDataFolder(), "messages.yml");
    }
    TNE.instance.messageConfigurations = YamlConfiguration.loadConfiguration(TNE.instance.messages);
    TNE.configurations.load(TNE.instance.messageConfigurations, "messages");
  }

  public static void reloadConfigsObjects() {
    if(TNE.instance.objects == null) {
      TNE.instance.objects = new File(TNE.instance.getDataFolder(), "objects.yml");
    }
    TNE.instance.objectConfigurations = YamlConfiguration.loadConfiguration(TNE.instance.objects);
    TNE.configurations.load(TNE.instance.objectConfigurations, "objects");
  }

  public static void reloadConfigPlayers() {
    if(TNE.instance.players == null) {
      TNE.instance.players = new File(TNE.instance.getDataFolder(), "players.yml");
    }
    TNE.instance.playerConfigurations = YamlConfiguration.loadConfiguration(TNE.instance.players);
    TNE.configurations.load(TNE.instance.playerConfigurations, "players");
  }

  public static void reloadConfigsWorlds() {
    if(TNE.instance.worlds == null) {
      TNE.instance.worlds = new File(TNE.instance.getDataFolder(), "worlds.yml");
    }
    TNE.instance.worldConfigurations = YamlConfiguration.loadConfiguration(TNE.instance.worlds);
  }

  public static JSONObject[] sendPostRequest(String url, String payload) {
    StringBuilder builder = new StringBuilder();
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setConnectTimeout(5000);
      connection.setReadTimeout(5000);
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setRequestProperty("Content-Type", "application/json");

      OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
      out.write(payload);
      out.flush();
      out.close();

      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String response;
      while ((response = reader.readLine()) != null) {
        builder.append(response);
      }
      connection.disconnect();
      reader.close();
    } catch(Exception e) {
      MISCUtils.debug(e);
    }

    String toSplit = builder.toString().replace("[", "").replace("]", "");
    MISCUtils.debug(toSplit);
    String[] split = toSplit.split("},");
    List<JSONObject> objects = new ArrayList<>();

    for(String s : split) {
      String sSuffix = (s.endsWith("}"))? s : s + "}";
      objects.add((JSONObject)JSONValue.parse(sSuffix));
    }

    return objects.toArray(new JSONObject[objects.size()]);
  }

  public static String sendSecureGetRequest(String url) {
    StringBuilder builder = new StringBuilder();
    try {
      HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

      String response;
      while ((response = reader.readLine()) != null) {
        builder.append(response);
      }
      reader.close();
    } catch(Exception e) {
      MISCUtils.debug(e);
    }
    return builder.toString();
  }

  public static String sendGetRequest(String URL) {
    StringBuilder builder = new StringBuilder();
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
      connection.setRequestMethod("GET");
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      String response;
      while((response = reader.readLine()) != null) {
        builder.append(response);
      }
      reader.close();
    } catch (Exception e) {
      MISCUtils.debug(e);
    }
    MISCUtils.debug("GET VALUE");
    MISCUtils.debug(builder.toString());
    return builder.toString();
  }

  public static void printMaterials() {
    FileWriter writer = null;
    BufferedWriter buffWriter = null;
    try {
      writer = new FileWriter(new File(TNE.instance.getDataFolder(), "Material.txt"));
      buffWriter = new BufferedWriter(writer);
    } catch(Exception e) {

    }
    for(Material mat : Material.values()) {
      if(buffWriter != null && writer != null) {
        try {
          buffWriter.write("validNames.add(new MaterialNameHelper(Material." + mat.name() + ", new String[0]));" + System.lineSeparator());
        } catch(Exception e) {

        }
      }
    }
    if(buffWriter != null) {
      try {
        buffWriter.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    if(writer != null) {
      try {
        writer.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static UUID distringuishId(String identifier) {
    if(IDFinder.isUUID(identifier)) {
      return UUID.fromString(identifier);
    }
    return IDFinder.getID(identifier);
  }

  @SuppressWarnings("deprecation")
  public static Player getPlayer(String username) {
    return IDFinder.getPlayer(username);
  }

  @SuppressWarnings("deprecation")
  public static Player getPlayer(UUID id) {
    if(!TNE.instance.api.getBoolean("Core.UUID")) {
      return Bukkit.getPlayer(IDFinder.ecoToUsername(id));
    }
    return Bukkit.getPlayer(id);
  }

  @SuppressWarnings("rawtypes")
  public static Object getKey(Map m, Object value) {
    for(Object obj : m.keySet()) {
      if(m.get(obj).equals(value)) {
        return obj;
      }
    }
    return null;
  }

  public static String dashUUID(String undashed) {
    return undashed.replaceAll(TNE.uuidCreator.pattern(), "$1-$2-$3-$4-$5");
  }

  public static Boolean isBoolean(String value) {
    return value.equalsIgnoreCase(String.valueOf(true)) || value.equalsIgnoreCase(String.valueOf(false));
  }

  public static Boolean isDouble(String value, String world) {
    try {
      Double.valueOf(value.replace(TNE.instance.api.getString("Core.Currency.Decimal", world), "."));
      return true;
    } catch(Exception e) {
      return false;
    }
  }

  public static Boolean isInteger(String value) {
    try {
      Integer.valueOf(value);
      return true;
    } catch(Exception e) {
      return false;
    }
  }

  //World Utils
  public static Boolean multiWorld() {
    return TNE.instance.api.getBoolean("Core.Multiworld");
  }

  public static Boolean worldConfigExists(String node) {
    return (TNE.instance.worldConfigurations.get(node) != null);
  }
}