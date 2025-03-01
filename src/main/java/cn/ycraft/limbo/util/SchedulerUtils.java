package cn.ycraft.limbo.util;

import com.loohp.limbo.plugins.LimboPlugin;
import com.loohp.limbo.scheduler.LimboRunnable;
import com.loohp.limbo.scheduler.LimboScheduler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class SchedulerUtils {

    private final LimboPlugin plugin;

    public SchedulerUtils(LimboPlugin plugin) {
        this.plugin = plugin;
    }

    private LimboPlugin getPlugin() {
        return plugin;
    }

    public LimboScheduler serverScheduler() {
        return getPlugin().getServer().getScheduler();
    }

    public void run(Runnable runnable) {
        serverScheduler().runTask(getPlugin(), runnable::run);
    }

    public void runAsync(Runnable runnable) {
        serverScheduler().runTaskAsync(getPlugin(), runnable::run);
    }

    public void runLater(long delayTicks, Runnable runnable) {
        serverScheduler().runTaskLater(getPlugin(), runnable::run, delayTicks);
    }

    public void runLater(TimeUnit unit, long delayTicks, Runnable runnable) {
        serverScheduler().runTaskLater(getPlugin(), runnable::run, delayTicks, unit);
    }

    public void runLaterAsync(long delayTicks, Runnable runnable) {
        serverScheduler().runTaskLaterAsync(getPlugin(), runnable::run, delayTicks);
    }

    public void runLaterAsync(TimeUnit unit, long delay, Runnable runnable) {
        serverScheduler().runTaskLaterAsync(getPlugin(), runnable::run, delay, unit);
    }

    public void runAtInterval(long intervalTicks, Runnable... tasks) {
        runAtInterval(0L, intervalTicks, tasks);
    }

    public void runAtInterval(TimeUnit unit, long interval, Runnable... tasks) {
        runAtInterval(unit, 0L, interval, tasks);
    }

    public void runAtIntervalAsync(long intervalTicks, Runnable... tasks) {
        runAtIntervalAsync(0L, intervalTicks, tasks);
    }

    public void runAtIntervalAsync(TimeUnit unit, long interval, Runnable... tasks) {
        runAtIntervalAsync(unit, 0L, interval, tasks);
    }

    public void runAtInterval(long delayTicks, long intervalTicks, Runnable... tasks) {
        new LimboRunnable() {
            private int index;

            @Override
            public void run() {
                if (this.index >= tasks.length) {
                    this.cancel();
                    return;
                }

                tasks[index].run();
                index++;
            }
        }.runTaskTimer(getPlugin(), delayTicks, intervalTicks);
    }

    public void runAtInterval(TimeUnit unit, long delay, long interval, Runnable... tasks) {
        new LimboRunnable() {
            private int index;

            @Override
            public void run() {
                if (this.index >= tasks.length) {
                    this.cancel();
                    return;
                }

                tasks[index].run();
                index++;
            }
        }.runTaskTimer(getPlugin(), delay, interval, unit);
    }

    public void runAtIntervalAsync(long delayTicks, long intervalTicks, Runnable... tasks) {
        new LimboRunnable() {
            private int index;

            @Override
            public void run() {
                if (this.index >= tasks.length) {
                    this.cancel();
                    return;
                }

                tasks[index].run();
                index++;
            }
        }.runTaskTimerAsync(getPlugin(), delayTicks, intervalTicks);
    }

    public void runAtIntervalAsync(TimeUnit unit, long delay, long interval, Runnable... tasks) {
        new LimboRunnable() {
            private int index;

            @Override
            public void run() {
                if (this.index >= tasks.length) {
                    this.cancel();
                    return;
                }

                tasks[index].run();
                index++;
            }
        }.runTaskTimerAsync(getPlugin(), delay, interval, unit);
    }

    public void repeat(int repetitions, long intervalTicks, Runnable task, Runnable onComplete) {
        new LimboRunnable() {
            private int index;

            @Override
            public void run() {
                index++;
                if (this.index >= repetitions) {
                    this.cancel();
                    if (onComplete == null) {
                        return;
                    }

                    onComplete.run();
                    return;
                }

                task.run();
            }
        }.runTaskTimer(getPlugin(), 0L, intervalTicks);
    }


    public void repeatAsync(int repetitions, long intervalTicks, Runnable task, Runnable onComplete) {
        new LimboRunnable() {
            private int index;

            @Override
            public void run() {
                index++;
                if (this.index >= repetitions) {
                    this.cancel();
                    if (onComplete == null) {
                        return;
                    }

                    onComplete.run();
                    return;
                }

                task.run();
            }
        }.runTaskTimerAsync(getPlugin(), 0L, intervalTicks);
    }


    public void repeatWhile(long interval, Callable<Boolean> predicate, Runnable task, Runnable onComplete) {
        new LimboRunnable() {
            @Override
            public void run() {
                try {
                    if (!predicate.call()) {
                        this.cancel();
                        if (onComplete == null) {
                            return;
                        }

                        onComplete.run();
                        return;
                    }

                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimer(getPlugin(), 0L, interval);
    }


    public void repeatWhileAsync(long interval, Callable<Boolean> predicate, Runnable task, Runnable onComplete) {
        new LimboRunnable() {
            @Override
            public void run() {
                try {
                    if (!predicate.call()) {
                        this.cancel();
                        if (onComplete == null) {
                            return;
                        }

                        onComplete.run();
                        return;
                    }

                    task.run();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.runTaskTimerAsync(getPlugin(), 0L, interval);
    }

    public interface Task {
        void start(Runnable onComplete);
    }

    public class TaskBuilder {
        private final Queue<Task> taskList;

        public TaskBuilder() {
            this.taskList = new LinkedList<>();
        }

        public TaskBuilder append(TaskBuilder builder) {
            this.taskList.addAll(builder.taskList);
            return this;
        }

        public TaskBuilder appendDelay(long delay) {
            this.taskList.add(onComplete -> SchedulerUtils.this.runLater(delay, onComplete));
            return this;
        }

        public TaskBuilder appendTask(Runnable task) {
            this.taskList.add(onComplete ->
            {
                task.run();
                onComplete.run();
            });

            return this;
        }

        public TaskBuilder appendTask(Task task) {
            this.taskList.add(task);
            return this;
        }

        public TaskBuilder appendDelayedTask(long delay, Runnable task) {
            this.taskList.add(onComplete -> SchedulerUtils.this.runLater(delay, () ->
            {
                task.run();
                onComplete.run();
            }));

            return this;
        }

        public TaskBuilder appendTasks(long delay, long interval, Runnable... tasks) {
            this.taskList.add(onComplete ->
            {
                Runnable[] runnables = Arrays.copyOf(tasks, tasks.length + 1);
                runnables[runnables.length - 1] = onComplete;
                SchedulerUtils.this.runAtInterval(delay, interval, runnables);
            });

            return this;
        }

        public TaskBuilder appendRepeatingTask(int repetitions, long interval, Runnable task) {
            this.taskList.add(onComplete -> SchedulerUtils.this.repeat(repetitions, interval, task, onComplete));
            return this;
        }

        public TaskBuilder appendConditionalRepeatingTask(long interval, Callable<Boolean> predicate, Runnable task) {
            this.taskList.add(onComplete -> SchedulerUtils.this.repeatWhile(interval, predicate, task, onComplete));
            return this;
        }

        public TaskBuilder waitFor(Callable<Boolean> predicate) {
            this.taskList.add(onComplete -> new LimboRunnable() {
                @Override
                public void run() {
                    try {
                        if (!predicate.call()) {
                            return;
                        }

                        this.cancel();
                        onComplete.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }.runTaskTimer(getPlugin(), 0L, 1L));
            return this;
        }

        public void runTasks() {
            this.startNext();
        }

        private void startNext() {
            Task task = this.taskList.poll();
            if (task == null) {
                return;
            }

            task.start(this::startNext);
        }
    }
}
