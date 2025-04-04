package PBCTest.CHTest;

import PBCTest.BasicParam;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import scheme.CH.CH_KEF_MH_RSA_F_AM_2004.Native;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static utils.Func.InitialLib;

@SuppressWarnings("NewClassNamingConvention")
public class CH_KEF_MH_RSA_F_AM_2004 extends BasicParam {
    double[] time_cost = new double[5];

    @BeforeAll
    static void initTest() {
        InitialLib();
        try {
            File_Writer = new BufferedWriter(new FileWriter("./data/PBC/CH/CH_KEF_MH_RSA_F_AM_2004.txt"));
            File_Writer.write("CH_KEF_MH_RSA_F_AM_2004\t\t\tSetUp, KeyGen, Hash, Check, Adapt\n");
            System.out.println("CH_KEF_MH_RSA_F_AM_2004");
            System.out.println("\t\t\tSetUp, KeyGen, Hash, Check, Adapt");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @DisplayName("test CH_KEF_MH_RSA_F_AM_2004")
    @ParameterizedTest(name = "test k = {0}")
    @ValueSource(ints = {256, 512, 1024})
    void NativeTest(int k) {
        try {
            File_Writer.write(String.format("k:%d: ", k));
            System.out.printf("k:%d: ", k);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Random rand = new Random();
        Native scheme = new Native();
        Native.PublicParam pp = new Native.PublicParam();

        int stage_id = -1;
        {
            long start = System.nanoTime();
            for(int i = 0;i < repeat_cnt;++i) scheme.SetUp(pp, k / 2, k);
            long end = System.nanoTime();
            double duration = (end - start) / 1.0e6;
            time_cost[++stage_id] = duration / repeat_cnt;
        }

        k = k / 4;

        Native.PublicKey[] pk = new Native.PublicKey[repeat_cnt];
        Native.SecretKey[] sk = new Native.SecretKey[repeat_cnt];
        Native.HashValue[] h = new Native.HashValue[repeat_cnt];
        Native.Randomness[] r = new Native.Randomness[repeat_cnt];
        Native.Randomness[] rp = new Native.Randomness[repeat_cnt];
        BigInteger[] m = new BigInteger[repeat_cnt];
        BigInteger[] m2 = new BigInteger[repeat_cnt];
        BigInteger[] L = new BigInteger[repeat_cnt];
        for (int i = 0; i < repeat_cnt; i++) {
            pk[i] = new Native.PublicKey();
            sk[i] = new Native.SecretKey();
            m[i] = new BigInteger(k, rand);
            m2[i] = new BigInteger(k, rand);
            L[i] = new BigInteger(k, rand);
            h[i] = new Native.HashValue();
            r[i] = new Native.Randomness();
            rp[i] = new Native.Randomness();
        }

        {
            long start = System.nanoTime();
            for(int i = 0;i < repeat_cnt;++i) scheme.KeyGen(pk[i], sk[i], pp);
            long end = System.nanoTime();
            double duration = (end - start) / 1.0e6;
            time_cost[++stage_id] = duration / repeat_cnt;
        }

        {
            long start = System.nanoTime();
            for(int i = 0;i < repeat_cnt;++i) scheme.Hash(h[i], r[i], pk[i], L[i], m[i], pp);
            long end = System.nanoTime();
            double duration = (end - start) / 1.0e6;
            time_cost[++stage_id] = duration / repeat_cnt;
        }

        {
            boolean res = true;
            long start = System.nanoTime();
            for(int i = 0;i < repeat_cnt;++i) res &= scheme.Check(h[i], r[i], pk[i], L[i], m[i], pp);
            long end = System.nanoTime();
            double duration = (end - start) / 1.0e6;
            time_cost[++stage_id] = duration / repeat_cnt;
            assertTrue(res, "Hash Check Failed");
        }

        {
            long start = System.nanoTime();
            for(int i = 0;i < repeat_cnt;++i) scheme.Adapt(rp[i], r[i], pk[i], sk[i], L[i], m[i], m2[i], pp);
            long end = System.nanoTime();
            double duration = (end - start) / 1.0e6;
            time_cost[++stage_id] = duration / repeat_cnt;
        }

        {
            boolean res = true;
            for(int i = 0;i < repeat_cnt;++i) res &= scheme.Check(h[i], rp[i], pk[i], L[i], m2[i], pp);
            assertTrue(res, "Adapt Check Failed");
        }

    }

    @AfterEach
    void afterEach() {
        try {
            for (double x : time_cost) File_Writer.write(String.format("%.6f, ", x));
            File_Writer.write("\n");
            for (double x : time_cost) System.out.printf("%.6f, ", x);
            System.out.println();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    static void afterAll() {
        try {
            File_Writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
