package com.rs.game.tasks;

import com.rs.lib.util.Logger;
import com.rs.utils.Ticks;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TaskManager {
    private List<TaskInformation> tasks = new ObjectArrayList<>();
    private Map<String, TaskInformation> mappedTasks = new Object2ObjectOpenHashMap<>();

    public void processTasks() {
        synchronized(tasks) {
            try {
                List<TaskInformation> toRemove = new ArrayList<>();
                Iterator<TaskInformation> iter = tasks.iterator();
                while (iter.hasNext()) {
                    TaskInformation task = iter.next();
                    if (task == null)
                        continue;
                    try {
                        if (task.currDelay > 0) {
                            task.currDelay--;
                            continue;
                        }
                        try {
                            task.getTask().run();
                        } catch (Throwable e) {
                            Logger.handle(WorldTasks.class, "processTasksRun:" + (task.getClass().getDeclaringClass() != null ? task.getClass().getDeclaringClass().getSimpleName() : "UnknownSource"), e);
                        }
                        if (task.getTask().needRemove)
                            toRemove.add(task);
                        else
                            task.currDelay = task.getLoopDelay();
                    } catch (Throwable e) {
                        Logger.handle(WorldTasks.class, "processTasks:" + (task.getClass().getDeclaringClass() != null ? task.getClass().getDeclaringClass().getSimpleName() : "UnknownSource"), e);
                    }
                }
                for (TaskInformation task : toRemove)
                    if (task != null) {
                        tasks.remove(task);
                        if (task.mapping != null)
                            mappedTasks.remove(task.mapping);
                    }
            } catch(Throwable e) {
                Logger.handle(WorldTasks.class, "processTasks", e);
            }
        }
    }

    public TaskInformation schedule(String mapping, Task task, int startDelay, int loopDelay) {
        synchronized(tasks) {
            if (task == null || startDelay < 0 || loopDelay < 0)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, task, startDelay, loopDelay));
        }
    }

    public TaskInformation schedule(Task task, int startDelay, int loopDelay) {
        return schedule(null, task, startDelay, loopDelay);
    }

    public TaskInformation schedule(String mapping, Task task, int delayCount) {
        synchronized(tasks) {
            if (task == null || delayCount < 0)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, task, delayCount, -1));
        }
    }

    public TaskInformation schedule(Task task, int delayCount) {
        return schedule(null, task, delayCount);
    }

    public TaskInformation schedule(String mapping, Task task) {
        synchronized(tasks) {
            if (task == null)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, task, 0, -1));
        }
    }

    public TaskInformation schedule(Task task) {
        return schedule(null, task);
    }

    public TaskInformation schedule(String mapping, int startDelay, int loopDelay, Runnable task) {
        synchronized(tasks) {
            if (task == null || startDelay < 0 || loopDelay < 0)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, new TaskLambda(task), startDelay, loopDelay));
        }
    }

    public TaskInformation schedule(int startDelay, int loopDelay, Runnable task) {
        return schedule(null, startDelay, loopDelay, task);
    }

    public TaskInformation scheduleHalfHourly(Runnable task) {
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime nextHalfHour;
        if (now.getMinute() < 30)
            nextHalfHour = now.withMinute(30).withSecond(0).withNano(0);
        else
            nextHalfHour = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);
        int delay = (int) (Duration.between(now, nextHalfHour).toMillis() / 600L);
        return schedule(delay, Ticks.fromMinutes(30), task);
    }

    public TaskInformation scheduleNthHourly(int hour, Runnable task) {
        ZonedDateTime now = ZonedDateTime.now();
        int currentHour = now.getHour();
        int hoursUntilNextMark = hour - (currentHour % hour);
        ZonedDateTime nextThreeHourMark = now.plusHours(hoursUntilNextMark).withMinute(0).withSecond(0).withNano(0);
        int delay = (int) (Duration.between(now, nextThreeHourMark).toMillis() / 600L);
        return schedule(delay, Ticks.fromHours(hour), task);
    }

    public TaskInformation scheduleHourly(Runnable task) {
        return scheduleNthHourly(1, task);
    }

    public TaskInformation schedule(String mapping, int startDelay, Runnable task) {
        synchronized(tasks) {
            if (task == null || startDelay < 0)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, new TaskLambda(task), startDelay, -1));
        }
    }

    public TaskInformation schedule(int startDelay, Runnable task) {
        return schedule(null, startDelay, task);
    }

    public TaskInformation schedule(String mapping, Runnable task) {
        synchronized(tasks) {
            if (task == null)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, new TaskLambda(task), 0, -1));
        }
    }

    public TaskInformation schedule(Runnable task) {
        return schedule(null, task);
    }

    public TaskInformation scheduleTimer(String mapping, int startDelay, int loopDelay, Function<Integer, Boolean> task) {
        synchronized(tasks) {
            if (task == null || startDelay < 0 || loopDelay < 0)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, new TaskTimerLambda(task), startDelay, loopDelay));
        }
    }

    public TaskInformation scheduleTimer(int startDelay, int loopDelay, Function<Integer, Boolean> task) {
        return scheduleTimer(null, startDelay, loopDelay, task);
    }

    public TaskInformation scheduleTimer(String mapping, Function<Integer, Boolean> task) {
        synchronized(tasks) {
            if (task == null)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, new TaskTimerLambda(task), 0, 0));
        }
    }

    public TaskInformation scheduleTimer(Function<Integer, Boolean> task) {
        return scheduleTimer(null, task);
    }

    public TaskInformation scheduleTimer(String mapping, int startDelay, Function<Integer, Boolean> task) {
        synchronized(tasks) {
            if (task == null || startDelay < 0)
                return null;
            return mapTaskInformation(mapping, new TaskInformation(mapping, new TaskTimerLambda(task), startDelay, 0));
        }
    }

    public TaskInformation scheduleTimer(int startDelay, Function<Integer, Boolean> task) {
        return scheduleTimer(null, startDelay, task);
    }

    public void remove(TaskInformation task) {
        synchronized(tasks) {
            if (task == null)
                return;
            tasks.remove(task);
            if (task.mapping != null)
                mappedTasks.remove(task.mapping);
        }
    }

    private TaskInformation mapTaskInformation(String mapping, TaskInformation taskInfo) {
        tasks.add(taskInfo);
        if (mapping != null) {
            TaskInformation existing = mappedTasks.get(mapping);
            if (existing != null)
                tasks.remove(mappedTasks.get(mapping));
            mappedTasks.put(mapping, taskInfo);
        }
        return taskInfo;
    }
}
