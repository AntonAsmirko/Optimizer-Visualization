package firstLab;

import java.util.Random;
import java.util.function.Function;

public class Parabolas extends Optimizer {

    public Parabolas() {
        super();
    }

    public Parabolas(Logger l) {
        super(l);
    }


    @Override
    public double optimize(double l, double r, double eps, Function<Double, Double> func) {
        double fl = func.apply(l);
        double fr = func.apply(r);
        Random rand = new Random();
        double x = l + (r-l)*rand.nextDouble();
        double fx = func.apply(x);
        while(!(fl >= fx && fx <= fr)) {
            x = l + (r-l)*rand.nextDouble();
            fx = func.apply(x);
        }
        double u = 1.0/0.0;
        int k = 1;
        double prev = 0;
        while ((r - l)/2 > eps) {
            double numerator =Math.pow(x - l, 2) * (fx - fr) -Math.pow(x - r, 2) * (fx - fl);
            double denominator = (2 * ((x - l) * (fx - fr) - (x - r) * (fx - fl)));
            //to protect from NaN
            if(numerator == 0.0 && denominator == 0.0) {
                if( x == u)
                    break;
                u = x;
            } else u = x - numerator / denominator;
            double fu = func.apply(u);
            if (fu > fx) {
                if (u > x) {
                    r = u;
                    fr = fu;
                } else {
                    l = u;
                    fl = fu;
                }
            } else {
                if (x > u) {
                    r = x;
                    fr = fx;
                } else {
                    l = x;
                    fl = fx;
                }
                x = u;
                fx = fu;
            }
            toMass(l, fl, x, fx, u, fu, r, fr);
            prev = Math.abs(l-r);
            k++;
        }
        return (l + r) / 2;
    }
}
