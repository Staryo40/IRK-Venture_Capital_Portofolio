package irk.staryo.utils;

import java.util.ArrayList;
import java.util.List;

import irk.staryo.model.DiscretePMF;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class ConvolutionCalculator {
    private static final FastFourierTransformer FFT =
            new FastFourierTransformer(DftNormalization.STANDARD);

    private static int nextPow2(int x) {
        int n = 1;
        while (n < x) n <<= 1;
        return n;
    }

    public static void fft(Complex[] a, boolean inverse) {
        int n = a.length;

        // Bit-reversal permutation
        for (int i = 1, j = 0; i < n; i++) {
            int bit = n >> 1;
            for (; j >= bit; bit >>= 1) j -= bit;
            j += bit;
            if (i < j) {
                Complex tmp = a[i];
                a[i] = a[j];
                a[j] = tmp;
            }
        }

        // Cooley–Tukey
        for (int len = 2; len <= n; len <<= 1) {
            double ang = 2 * Math.PI / len * (inverse ? -1 : 1);
            Complex wlen = new Complex(Math.cos(ang), Math.sin(ang));
            for (int i = 0; i < n; i += len) {
                Complex w = new Complex(1.0, 0.0);
                for (int j = 0; j < len / 2; j++) {
                    Complex u = a[i + j];
                    Complex v = a[i + j + len / 2].multiply(w);
                    a[i + j] = u.add(v);
                    a[i + j + len / 2] = u.subtract(v);
                    w = w.multiply(wlen);
                }
            }
        }

        // Scale if inverse
        if (inverse) {
            for (int i = 0; i < n; i++) {
                a[i] = a[i].divide(n);
            }
        }
    }

    /**
     * Result represents (aMin+bMin)..(aMax+bMax)
     * FFT OVERHEAD:
     * 1. Zero-padding both arrays.
     * 2. Two forward FFTs.
     * 3. Pointwise multiplication.
     * 4. One inverse FFT.
     * 5. Normalization.
     */
    public static DiscretePMF convolvePMFsFFT(DiscretePMF aPMF, DiscretePMF bPMF) {
        int na = aPMF.p.size();
        int nb = bPMF.p.size();
        if (na == 0 || nb == 0) {
            return new DiscretePMF(aPMF.min + bPMF.min, List.of(0.0));
        }

        int outLen = na + nb - 1; // length of linear convolution
        int n = nextPow2(outLen);

        Complex[] A = new Complex[n];
        Complex[] B = new Complex[n];
        for (int i = 0; i < n; i++) {
            A[i] = new Complex(i < na ? aPMF.p.get(i) : 0.0, 0.0);
            B[i] = new Complex(i < nb ? bPMF.p.get(i) : 0.0, 0.0);
        }

        // Forward FFT
        fft(A, false);
        fft(B, false);

        // Pointwise multiply
        Complex[] FC = new Complex[n];
        for (int i = 0; i < n; i++) FC[i] = A[i].multiply(B[i]);

        // Inverse FFT
        fft(FC, true);

        // Collect result
        List<Double> out = new ArrayList<>(outLen);
        for (int i = 0; i < outLen; i++) {
            double val = FC[i].getReal();
            if (val < 0 && val > -1e-15) val = 0.0;
            out.add(val);
        }

        // Normalize to sum = 1
        double sum = 0.0;
        for (double v : out) sum += v;
        if (sum > 0) {
            for (int i = 0; i < out.size(); i++) {
                out.set(i, out.get(i) / sum);
            }
        }

        int combinedMin = aPMF.min + bPMF.min;
        return new DiscretePMF(combinedMin, out);
    }

    // O(n*m) version
    public static DiscretePMF convolveNaive(DiscretePMF aPMF, DiscretePMF bPMF) {
        int na = aPMF.p.size();
        int nb = bPMF.p.size();
        int outLen = na + nb - 1;
        double[] out = new double[outLen];
        for (int i = 0; i < na; i++) {
            double ai = aPMF.p.get(i);
            if (ai == 0) continue;
            for (int j = 0; j < nb; j++) {
                out[i + j] += ai * bPMF.p.get(j);
            }
        }

        double s = 0.0; for (double v : out) s += v;
        List<Double> result = new ArrayList<>(outLen);
        for (double v : out) result.add(s > 0 ? v / s : 0.0);

        return new DiscretePMF(aPMF.min + bPMF.min, result);
    }
}
