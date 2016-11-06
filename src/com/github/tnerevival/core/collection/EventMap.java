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
package com.github.tnerevival.core.collection;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by creatorfromhell on 11/2/2016.
 **/
public class EventMap<K, V> extends HashMap<K, V> {

  public MapListener<K, V> listener;
  public HashMap<K, V> map;

  public V get(Object key) {
    return map.get(key);
  }

  public V put(K key, V value) {
    listener.add(key, value);
    return map.put(key, value);
  }

  public V remove(Object key) {
    listener.preRemove(key, get(key));
    V removed = map.remove(key);
    listener.remove(key);
    return removed;
  }

  public EventMapIterator<Map.Entry<K, V>> getIterator() {
    return new EventMapIterator<>(map.entrySet().iterator(), listener);
  }

  public void setListener(MapListener<K, V> listener) {
    this.listener = listener;
  }
}