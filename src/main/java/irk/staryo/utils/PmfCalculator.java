package irk.staryo.utils;

public class PmfCalculator {
    public static double alpha(int pessimistic, int realistic, int optimistic){
        double num = realistic - pessimistic;
        double denum = optimistic - pessimistic;
        double value = 4 * (num / denum);
        return 1 + value;
    }

    public static double beta(int pessimistic, int realistic, int optimistic){
        double num = optimistic - realistic;
        double denum = optimistic - pessimistic;
        double value = 4 * (num / denum);
        return 1 + value;
    }

    // Gamma function (Lanczos approximation)
    private static double gamma(double z) {
        // Lanczos coefficients (g=7, n=9)
        double[] p = {
                0.99999999999980993,
                676.5203681218851,
                -1259.1392167224028,
                771.32342877765313,
                -176.61502916214059,
                12.507343278686905,
                -0.13857109526572012,
                9.9843695780195716e-6,
                1.5056327351493116e-7
        };
        if (z < 0.5) {
            return Math.PI / (Math.sin(Math.PI * z) * gamma(1 - z));
        } else {
            z -= 1;
            double x = p[0];
            for (int i = 1; i < p.length; i++) x += p[i] / (z + i);
            double t = z + p.length - 0.5;
            return Math.sqrt(2 * Math.PI) * Math.pow(t, z + 0.5) * Math.exp(-t) * x;
        }
    }

    private static double betaFunction(double a, double b) {
        return gamma(a) * gamma(b) / gamma(a + b);
    }

    private static double betaPdf(double y, double alpha, double beta) {
        if (y <= 0.0 || y >= 1.0) return 0.0;
        double B = betaFunction(alpha, beta);
        return Math.pow(y, alpha - 1.0) * Math.pow(1.0 - y, beta - 1.0) / B;
    }

    private static double pertPdf(double t, int a, int m, int b, double alpha, double beta) {
        if (t < a || t > b) return 0.0;
        double width = b - a;
        if (width <= 0.0) return 0.0;
        double y = (t - a) / width;
        double pdfY = betaPdf(y, alpha, beta);
        return pdfY / width;
    }

    private static double integratePert(double left, double right, int a, int m, int b, double alpha, double beta) {
        if (right <= left) return 0.0;
        int n = (int)Math.ceil((right - left) * 200); // 200 steps per unit length
        if (n % 2 == 1) n++;
        if (n < 2) n = 2;
        double h = (right - left) / n;
        double sum = pertPdf(left, a, m, b, alpha, beta) + pertPdf(right, a, m, b, alpha, beta);
        for (int i = 1; i < n; i++) {
            double x = left + i * h;
            double fx = pertPdf(x, a, m, b, alpha, beta);
            sum += (i % 2 == 0) ? 2.0 * fx : 4.0 * fx;
        }
        return sum * h / 3.0;
    }

    public static double[] pmfFromPert(int pessimistic, int realistic, int optimistic) {
        if (optimistic <= pessimistic) throw new IllegalArgumentException("optimistic must be greater than pessimistic");
        double alpha = alpha(pessimistic, realistic, optimistic);
        double beta = beta(pessimistic, realistic, optimistic);
        int len = optimistic - pessimistic + 1;
        double[] p = new double[len];
        double total = 0.0;
        for (int k = pessimistic; k <= optimistic; k++) {
            double left = Math.max(pessimistic, k - 0.5);
            double right = Math.min(pessimistic, k + 0.5);
            double mass = integratePert(left, right, pessimistic, realistic, optimistic, alpha, beta);
            p[k - pessimistic] = mass;
            total += mass;
        }
        if (total <= 0.0) {
            for (int i = 0; i < len; i++) p[i] = 0.0;
            if (realistic >= pessimistic && realistic <= optimistic) p[realistic - pessimistic] = 1.0; else p[0] = 1.0;
            return p;
        }
        for (int i = 0; i < len; i++) p[i] /= total;
        return p;
    }

}
