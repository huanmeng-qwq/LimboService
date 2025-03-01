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

package com.loohp.limbo.scheduler;

import cn.ycraft.limbo.config.ServerConfig;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.plugins.LimboPlugin;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class LimboScheduler {

    private final AtomicInteger idProvider = new AtomicInteger(0);
    private final Map<Long, List<LimboSchedulerTask>> registeredTasks = new HashMap<>();
    private final Map<Integer, LimboSchedulerTask> tasksById = new HashMap<>();
    private final Set<Integer> cancelledTasks = new HashSet<>();

    public LimboScheduler() {

    }

    protected int nextId() {
        return idProvider.getAndUpdate(id -> id == Integer.MAX_VALUE ? 0 : id + 1);
    }

    protected long calculateTicks(long time, TimeUnit unit) {
        return (unit.toMillis(time) * ServerConfig.SERVER.TPS.resolve()) / 1000;
    }

    public void cancelTask(int taskId) {
        if (tasksById.containsKey(taskId)) {
            cancelledTasks.add(taskId);
        }
    }

    public void cancelTask(LimboPlugin plugin) {
        for (LimboSchedulerTask task : tasksById.values()) {
            if (task.getPlugin().getName().equals(plugin.getName())) {
                cancelledTasks.add(task.getTaskId());
            }
        }
    }

    protected int runTask(int taskId, LimboPlugin plugin, LimboTask task) {
        return runTaskLater(taskId, plugin, task, 0);
    }

    public int runTask(LimboPlugin plugin, LimboTask task) {
        return runTaskLater(plugin, task, 0);
    }

    protected int runTaskLater(int taskId, LimboPlugin plugin, LimboTask task, long delay) {
        LimboSchedulerTask st = new LimboSchedulerTask(plugin, task, taskId, LimboSchedulerTaskType.SYNC, 0);
        if (delay <= 0) {
            delay = 1;
        }
        long tick = Limbo.getInstance().getHeartBeat().getCurrentTick() + delay;
        tasksById.put(taskId, st);
        List<LimboSchedulerTask> list = registeredTasks.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            registeredTasks.put(tick, list);
        }
        list.add(st);
        return taskId;
    }

    public int runTaskLater(LimboPlugin plugin, LimboTask task, long delay) {
        return runTaskLater(nextId(), plugin, task, delay);
    }

    public int runTaskLater(LimboPlugin plugin, LimboTask task, long delay, TimeUnit unit) {
        return runTaskLater(plugin, task, calculateTicks(delay, unit));
    }

    protected int runTaskAsync(int taskId, LimboPlugin plugin, LimboTask task) {
        return runTaskLaterAsync(taskId, plugin, task, 0);
    }

    public int runTaskAsync(LimboPlugin plugin, LimboTask task) {
        return runTaskLaterAsync(plugin, task, 0);
    }

    protected int runTaskLaterAsync(int taskId, LimboPlugin plugin, LimboTask task, long delay) {
        LimboSchedulerTask st = new LimboSchedulerTask(plugin, task, taskId, LimboSchedulerTaskType.ASYNC, 0);
        if (delay <= 0) {
            delay = 1;
        }
        long tick = Limbo.getInstance().getHeartBeat().getCurrentTick() + delay;
        tasksById.put(taskId, st);
        List<LimboSchedulerTask> list = registeredTasks.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            registeredTasks.put(tick, list);
        }
        list.add(st);
        return taskId;
    }

    public int runTaskLaterAsync(LimboPlugin plugin, LimboTask task, long delay, TimeUnit unit) {
        return runTaskLaterAsync(plugin, task, calculateTicks(delay, unit));
    }

    public int runTaskLaterAsync(LimboPlugin plugin, LimboTask task, long delay) {
        return runTaskLaterAsync(nextId(), plugin, task, delay);
    }

    protected int runTaskTimer(int taskId, LimboPlugin plugin, LimboTask task, long delay, long period) {
        LimboSchedulerTask st = new LimboSchedulerTask(plugin, task, taskId, LimboSchedulerTaskType.TIMER_SYNC, period);
        if (delay <= 0) {
            delay = 1;
        }
        if (period <= 0) {
            period = 1;
        }
        long tick = Limbo.getInstance().getHeartBeat().getCurrentTick() + delay;
        tasksById.put(taskId, st);
        List<LimboSchedulerTask> list = registeredTasks.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            registeredTasks.put(tick, list);
        }
        list.add(st);
        return taskId;
    }

    public int runTaskTimer(LimboPlugin plugin, LimboTask task, long delay, long period, TimeUnit unit) {
        return runTaskTimer(plugin, task, calculateTicks(delay, unit), calculateTicks(period, unit));
    }

    public int runTaskTimer(LimboPlugin plugin, LimboTask task, long delay, long period) {
        return runTaskTimer(nextId(), plugin, task, delay, period);
    }

    protected int runTaskTimerAsync(int taskId, LimboPlugin plugin, LimboTask task, long delay, long period) {
        LimboSchedulerTask st = new LimboSchedulerTask(plugin, task, taskId, LimboSchedulerTaskType.TIMER_ASYNC, period);
        if (delay <= 0) {
            delay = 1;
        }
        if (period <= 0) {
            period = 1;
        }
        long tick = Limbo.getInstance().getHeartBeat().getCurrentTick() + delay;
        tasksById.put(taskId, st);
        List<LimboSchedulerTask> list = registeredTasks.get(tick);
        if (list == null) {
            list = new ArrayList<>();
            registeredTasks.put(tick, list);
        }
        list.add(st);
        return taskId;
    }

    public int runTaskTimerAsync(LimboPlugin plugin, LimboTask task, long delay, long period, TimeUnit unit) {
        return runTaskTimerAsync(plugin, task, calculateTicks(delay, unit), calculateTicks(period, unit));
    }

    public int runTaskTimerAsync(LimboPlugin plugin, LimboTask task, long delay, long period) {
        return runTaskTimerAsync(nextId(), plugin, task, delay, period);
    }

    protected CurrentSchedulerTask collectTasks(long currentTick) {
        List<LimboSchedulerTask> tasks = registeredTasks.remove(currentTick);
        if (tasks == null) {
            return null;
        }

        List<LimboSchedulerTask> asyncTasks = new LinkedList<>();
        List<LimboSchedulerTask> syncedTasks = new LinkedList<>();

        for (LimboSchedulerTask task : tasks) {
            int taskId = task.getTaskId();
            if (cancelledTasks.contains(taskId)) {
                cancelledTasks.remove(taskId);
                continue;
            }

            switch (task.getType()) {
                case ASYNC:
                    asyncTasks.add(task);
                    break;
                case SYNC:
                    syncedTasks.add(task);
                    break;
                case TIMER_ASYNC:
                    asyncTasks.add(task);
                    runTaskTimerAsync(task.getTaskId(), task.getPlugin(), task.getTask(), task.getPeriod(), task.getPeriod());
                    break;
                case TIMER_SYNC:
                    syncedTasks.add(task);
                    runTaskTimer(task.getTaskId(), task.getPlugin(), task.getTask(), task.getPeriod(), task.getPeriod());
                    break;
            }
        }

        return new CurrentSchedulerTask(syncedTasks, asyncTasks);
    }

    public static class CurrentSchedulerTask {

        private final List<LimboSchedulerTask> asyncTasks;
        private final List<LimboSchedulerTask> syncedTasks;

        public CurrentSchedulerTask(List<LimboSchedulerTask> syncedTasks, List<LimboSchedulerTask> asyncTasks) {
            this.asyncTasks = asyncTasks;
            this.syncedTasks = syncedTasks;
        }

        public List<LimboSchedulerTask> getAsyncTasks() {
            return asyncTasks;
        }

        public List<LimboSchedulerTask> getSyncedTasks() {
            return syncedTasks;
        }

    }

    public static class LimboSchedulerTask {

        private final int taskId;
        private final LimboPlugin plugin;
        private final LimboTask task;
        private final LimboSchedulerTaskType type;
        private final long period;

        private LimboSchedulerTask(LimboPlugin plugin, LimboTask task, int taskId, LimboSchedulerTaskType type, long period) {
            this.plugin = plugin;
            this.task = task;
            this.taskId = taskId;
            this.type = type;
            this.period = period;
        }

        public LimboPlugin getPlugin() {
            return plugin;
        }

        public LimboTask getTask() {
            return task;
        }

        public int getTaskId() {
            return taskId;
        }

        public LimboSchedulerTaskType getType() {
            return type;
        }

        public long getPeriod() {
            return period;
        }

    }

    public enum LimboSchedulerTaskType {

        SYNC,
        ASYNC,
        TIMER_SYNC,
        TIMER_ASYNC

    }

}
