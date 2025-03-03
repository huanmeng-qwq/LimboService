/*
  ~ This file is part of Limbo.
  ~
  ~ Copyright (C) 2024. YourCraftMC <admin@ycraft.cn>
  ~ Copyright (C) 2022. LoohpJames <jamesloohp@gmail.com>
  ~ Copyright (C) 2022. Contributors
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
 */

package com.loohp.limbo.permissions;

import com.loohp.limbo.Console;
import com.loohp.limbo.commands.CommandSender;
import com.loohp.limbo.file.FileConfiguration;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.utils.DeclareCommands;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PermissionsManager {

    private final Map<String, List<String>> userGroups;
    private final Map<String, Set<String>> userPermissions;
    private final Map<String, List<String>> groupPermissions;

    private final Map<String, Set<String>> userRemovePermissions;

    public PermissionsManager() {
        userGroups = new HashMap<>();
        userPermissions = new HashMap<>();
        userRemovePermissions = new HashMap<>();
        groupPermissions = new HashMap<>();
    }

    @SuppressWarnings("unchecked")
    public void loadDefaultPermissionFile(File file) throws IOException {
        FileConfiguration config = new FileConfiguration(file);
        groupPermissions.put("default", new ArrayList<>());
        try {
            for (Object obj : config.get("groups", Map.class).keySet()) {
                String key = (String) obj;
                List<String> nodes = new ArrayList<>();
                nodes.addAll(config.get("groups." + key, List.class));
                groupPermissions.put(key, nodes);
            }
        } catch (Exception e) {
        }
        try {
            for (Object obj : config.get("players", Map.class).keySet()) {
                String key = (String) obj;
                List<String> groups = new ArrayList<>();
                groups.addAll(config.get("players." + key, List.class));
                userGroups.put(key, groups);
            }
        } catch (Exception e) {
        }
    }

    public boolean hasPermission(CommandSender sender, String checkPermission) {
        if (sender instanceof Console) {
            return true;
        } else if (sender instanceof Player player) {
            Set<String> perms = calcPermissions(player);
            for (String perm : perms) {
                if (isMatch(perm, checkPermission)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Set<String> calcPermissions(Player player) {
        Set<String> perms = new HashSet<>();
        if (!userGroups.containsKey(player.getName())) {
            perms.addAll(groupPermissions.getOrDefault("default", new ArrayList<>()));
        } else {
            for (String group : userGroups.get(player.getName())) {
                if (groupPermissions.containsKey(group)) {
                    perms.addAll(groupPermissions.get(group));
                }
            }
        }
        perms.addAll(userPermissions.getOrDefault(player.getName(), Collections.emptySet()));
        perms.removeAll(userRemovePermissions.getOrDefault(player.getName(), Collections.emptySet()));
        return perms;
    }

    public Map<String, List<String>> getUserGroups() {
        return userGroups;
    }

    public Map<String, List<String>> getGroupPermissions() {
        return groupPermissions;
    }


    public void addPermission(Player player, String permission) {
        String name = player.getName();
        if (!userPermissions.containsKey(name)) {
            userPermissions.put(name, new HashSet<>());
        }
        userPermissions.get(name).add(permission);
        player.clientConnection.sendPacket(DeclareCommands.getDeclareCommandsPacket(player));
    }

    public void removePermission(Player player, String permission) {
        String name = player.getName();
        if (!userRemovePermissions.containsKey(name)) {
            userRemovePermissions.put(name, new HashSet<>());
        }
        userRemovePermissions.get(name).add(permission);
        player.clientConnection.sendPacket(DeclareCommands.getDeclareCommandsPacket(player));
    }

    private static boolean isMatch(String exist, String target) {
        if (exist.equals("*")) {
            return true;
        }

        // Split pattern and target into segments
        String[] patternSegments = exist.split("\\.");
        String[] targetSegments = target.split("\\.");

        if (exist.endsWith("*")) {
            // Extract prefix (excluding the trailing *)
            String prefix = exist.substring(0, exist.length() - 1);
            String[] prefixSegments = prefix.split("\\.");

            // Check if target starts with the prefix and has more segments
            if (targetSegments.length > prefixSegments.length) {
                int prefixLength = prefixSegments.length;
                for (int i = 0; i < prefixLength; i++) {
                    if (!prefixSegments[i].equals(targetSegments[i])) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else {
            // Exact match without wildcard
            return exist.equals(target);
        }
    }
}
