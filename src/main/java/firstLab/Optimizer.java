package firstLab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Optimizer {
    public Logger log;
    public boolean toLog;

    public ArrayList<ArrayList<Double>> array = new ArrayList<>();

    public Optimizer() {
        this(null);
    }

    public Optimizer(Logger l) {
        log = l;
        toLog = log != null;
    }

    public double optimize(double l, double r, double eps, Function<Double, Double> func) {
        return 0;
    }

    public void forLog(Object... o) {
        if(toLog)
            log.writeln(o);
        for(Object s : o)
            if(!(s instanceof Number))
                return;
        ArrayList<Double> temp = Arrays.stream(o)
                .map(s ->{
                    if(s instanceof Integer)
                        return (Double)(double)(int) s;
                    if(s instanceof Long)
                        return (Double)(double)(long) s;
                    return (Double)s;
                })
                .collect(Collectors.toCollection(ArrayList<Double>::new));
        if (!temp.isEmpty())
            array.add(temp);
    }
}
