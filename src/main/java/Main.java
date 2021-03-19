public class Main {
    public static void main(String[] args) {
        new Runner(true)
                .addForRun("Dichotomy", 1.E-14)
                .addForRun("Fibonacci", 1.E-8)
                .addForRun("GoldenSection", 1.E-15)
                .addForRun("Parabolas", 1.E-15)
                .addForRun("Brent", 1.E-15)
                .runTask(new Task("SeventhExpression",
                        x -> Math.pow(Math.log10(x - 2), 2.0) + Math.pow(Math.log10(10 - x), 2.0) - Math.pow(x, 0.2),
                        6., 9.9, 8.72691, -0.846037))
//                .runTask(new Task("NotUnimodal",
//                        x -> x + Math.pow(x, 2) - 6 * Math.pow(x, 3) - 3 * Math.pow(x, 4) + Math.pow(x, 5),
//                        -1., 4., 3.41488, -167.439));
                        .runTask(new Task("NotUnimodal",
                        x -> (x-1.4)*(x-3)*(x-5)*(x-2)*(x-3)*(x-4),
                        1.2, 5.1, 4.69606, -5.40806));
    }
}
