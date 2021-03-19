import java.util.function.Function;

public class Task {
    Function<Double, Double> function;
    String taskName;
    Double expectedX;
    Double expectedY;
    Double lb;
    Double rb;

    public Task(String taskName, Function<Double, Double> function, Double lb, Double rb, Double expectedX, Double expectedY) {
        this.function = function;
        this.taskName = taskName;
        this.expectedX = expectedX;
        this.expectedY = expectedY;
        this.lb = lb;
        this.rb = rb;
    }
}
