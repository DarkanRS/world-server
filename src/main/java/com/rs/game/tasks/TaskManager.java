package com.rs.game.tasks;

import com.rs.lib.util.Logger;
import com.rs.utils.Ticks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class TaskManager {
    private List<TaskInformation> tasks = new ObjectArrayList<>();

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
                    if (task != null)
                        tasks.remove(task);
            } catch(Throwable e) {
                Logger.handle(WorldTasks.class, "processTasks", e);
            }
        }
    }

    public TaskInformation schedule(Task task, int startDelay, int loopDelay) {
        synchronized(tasks) {
            if (task == null || startDelay < 0 || loopDelay < 0)
                return null;
            TaskInformation taskInfo = new TaskInformation(task, startDelay, loopDelay);
            tasks.add(taskInfo);
            return taskInfo;
        }
    }

    public TaskInformation schedule(Task task, int delayCount) {
        synchronized(tasks) {
            if (task == null || delayCount < 0)
                return null;
            TaskInformation taskInfo = new TaskInformation(task, delayCount, -1);
            tasks.add(taskInfo);
            return taskInfo;
        }
    }

    public TaskInformation schedule(Task task) {
        synchronized(tasks) {
            if (task == null)
                return null;
            TaskInformation taskInfo = new TaskInformation(task, 0, -1);
            tasks.add(taskInfo);
            return taskInfo;
        }
    }

    public TaskInformation schedule(int startDelay, int loopDelay, Runnable task) {
        synchronized(tasks) {
            if (task == null || startDelay < 0 || loopDelay < 0)
                return null;
            TaskInformation taskInfo = new TaskInformation(new TaskLambda(task), startDelay, loopDelay);
            tasks.add(taskInfo);
            return taskInfo;
        }
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

    public TaskInformation schedule(int startDelay, Runnable task) {
        synchronized(tasks) {
            if (task == null || startDelay < 0)
                return null;
            TaskInformation taskInfo = new TaskInformation(new TaskLambda(task), startDelay, -1);
            tasks.add(taskInfo);
            return taskInfo;
        }
    }

    public TaskInformation schedule(Runnable task) {
        synchronized(tasks) {
            if (task == null)
                return null;
            TaskInformation taskInfo = new TaskInformation(new TaskLambda(task), 0, -1);
            tasks.add(taskInfo);
            return taskInfo;
        }
    }

    public void scheduleTimer(int startDelay, int loopDelay, Function<Integer, Boolean> task) {
        synchronized(tasks) {
            if (task == null || startDelay < 0 || loopDelay < 0)
                return;
            tasks.add(new TaskInformation(new TaskTimerLambda(task), startDelay, loopDelay));
        }
    }

    public void scheduleTimer(Function<Integer, Boolean> task) {
        synchronized(tasks) {
            if (task == null)
                return;
            tasks.add(new TaskInformation(new TaskTimerLambda(task), 0, 0));
        }
    }

    public void scheduleTimer(int startDelay, Function<Integer, Boolean> task) {
        synchronized(tasks) {
            if (task == null || startDelay < 0)
                return;
            tasks.add(new TaskInformation(new TaskTimerLambda(task), startDelay, 0));
        }
    }

    public void remove(TaskInformation task) {
        synchronized(tasks) {
            if (task == null)
                return;
            tasks.remove(task);
        }
    }

    public void delay(int ticks, Runnable task) {
        schedule(new Task() {
            @Override
            public void run() {
                task.run();
            }
        }, ticks);
    }
}
