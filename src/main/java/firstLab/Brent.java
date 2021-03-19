package firstLab;

import java.util.function.Function;

public class Brent extends Optimizer {

    public Brent() {
        super();
    }

    public Brent(Logger l) {
        super(l);
    }

    @Override
    public double optimize(double l, double r, double eps, Function<Double, Double> func) {
        double K = (3 - Math.sqrt(5)) / 2;
        double a = l, c = r;
        double x = a + K * (c - a), w = x, v = x;
        double fX = func.apply(x), fW = fX, fV = fX;
        double d = c - a, e = d;
        double u = 0, fU;
        boolean parabolaU;
        forLog("iter N", "x", "w", "v", "u", "fX", "fW", "fV", "fU", "a", "c");
        int k = 1;
        while (d > eps) {
            parabolaU = false;
            double g = e;
            e = d;
            double tol = eps * Math.abs(x) + eps / 10;
            if (Math.abs(x - (a + c) / 2) + (c - a) / 2 - 2 * tol <= 0) {
                break;
            }
            if (!(x == w || x == v || w == v || fX == fW || fX == fV || fV == fW)) {
                u = (x + w - (fW - fX) / (w - x) / ((fV - fX) / (v - x) - (fW - fX) / (w - x)) / (v - w)) / 2;
                if (u - a >= 0 && c - u >= 0 && Math.abs(u - x) - g / 2 < 0) {
                    parabolaU = true;
                    if (u - a - 2 * tol < 0 || c - u - 2 * tol < 0) {
                        u = x - Math.signum(x - (a + c) / 2) * tol;
                    }
                }
            }
            if (!parabolaU) {
                if (x - (a + c) / 2 < 0) {
                    u = x + K * (c - x);
                    e = c - x;
                } else {
                    u = x - K * (x - a);
                    e = x - a;
                }
            }
            if (Math.abs(u - x) - tol < 0) {
                u = x + Math.signum(u - x) * tol;
            }
            d = Math.abs(u - x);
            fU = func.apply(u);
            if (fU - fX <= 0) {
                if (u - x >= 0) {
                    a = x;
                } else {
                    c = x;
                }
                v = w;
                w = x;
                x = u;
                fV = fW;
                fW = fX;
                fX = fU;
            } else {
                if (u - x >= 0) {
                    c = u;
                } else {
                    a = u;
                }
                if (fU - fW <= 0 || w == x) {
                    v = w;
                    w = u;
                    fV = fW;
                    fW = fU;
                } else if (fU - fV <= 0 || v == x || v == w) {
                    v = u;
                    fV = fU;
                }
            }
            forLog(k, x, w, v, u, fX, fW, fV, fU, a, c);
            k++;
        }
        return x;
    }
}