import firstLab.Logger;
import firstLab.Optimizer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

import org.javatuples.Pair;

public class Runner {
    private final ArrayList<Task> tasks = new ArrayList<>();
    private final ArrayList<Pair<String, Double>> forRun = new ArrayList<>();
    private final boolean toLog;

    Runner() {
        this(false);
    }

    Runner(boolean log) {
        toLog = log;
    }

    public void runTasks() {
        for (Task task :
                tasks) {
            runTask(task);
        }
    }

    public Runner addTask(Task task) {
        tasks.add(task);
        return this;
    }

    public Runner addForRun(String nameOfClass, Double minEpsilon) {
        forRun.add(new Pair<>(nameOfClass, minEpsilon));
        return this;
    }

    public Runner runTask(Task task) {
        if (forRun.isEmpty())
            return this;
        Logger logger = null;
        if (toLog) {
            try {
                Files.createDirectory(Path.of("." + File.separator + "Logs" + task.taskName));
            } catch (Exception ignored) {
            }
            logger = new Logger();
        }
        int i = 1;
        boolean flag = true;
        while (flag) {
            flag = false;
            Double eps = Math.pow(10., -i);
            for (Pair<String, Double> pair :
                    forRun) {
                if (eps >= pair.getValue1()) {
                    runOne(task, pair.getValue0(), eps);
                    flag = true;
                }
            }
            if (toLog) {
                try {
                    logger.reset(true);
                } catch (Exception ignored) {
                }
            }
            i++;
        }
        return this;
    }

    private void runOne(Task task, String name, Double eps) {
        Optimizer optimizer = null;
        try {
            optimizer = (Optimizer) Class.forName("firstLab." + name).getDeclaredConstructor(Logger.class).newInstance(toLog ? new Logger() : null);
        } catch (final ClassNotFoundException e) {
            e.printStackTrace();
        } catch (final Exception e) {
            System.err.println("Can't create optimizer");
            e.printStackTrace();
        }


        if (toLog) {
            optimizer.log.addOrGetSheet(name, false);
        }
        double resultX = optimizer.optimize(task.lb, task.rb, eps, task.function);
        double resultY = task.function.apply(resultX);
        if (toLog) {
            optimizer.log.addOrGetSheet(name, false);
            optimizer.forLog();
            optimizer.forLog("", "x", "y");
            optimizer.forLog("Expected:", task.expectedX, task.expectedY);
            optimizer.forLog("Actual:", resultX, resultY);
            optimizer.forLog("Difference:", Math.abs(task.expectedX - resultX), Math.abs(task.expectedY - resultY));
            try {
                optimizer.log.writeInFile("." + File.separator + "Logs" + task.taskName + File.separator + eps + "Log.xls");
            } catch (IOException e) {
                System.err.println("Can't write log");
            }
        }
    }
}
