package scheme.IBCH.IB_CH_ZSS_S1_2003;

import com.herumi.mcl.*;
import utils.Func;
import utils.Hash;

/*
 * ID-Based Chameleon Hashes from Bilinear Pairings
 * P4. 4.1 Scheme 1
 */

public class MCL {
    public static class PublicParam {
        G2 P = new G2(), P_pub = new G2();

        public void H0(G1 res, String m) {
            Hash.H_MCL_G1_1(res, m);
        }

        public void H1(Fr res, String m) {
            Hash.H_MCL_Zr_1(res, m);
        }
    }

    public static class MasterSecretKey {
        public Fr s = new Fr();
    }

    public static class SecretKey {
        public G1 S_ID = new G1();
    }

    public static class HashValue {
        public GT h = new GT();
    }

    public static class Randomness {
        public G1 R = new G1();
    }

    private static final G1[] G1_tmp = new G1[]{new G1()};
    private static final GT[] GT_tmp = new GT[]{new GT(), new GT()};
    private static final Fr[] Fr_tmp = new Fr[]{new Fr(), new Fr()};

    private static void getHashValue(GT h, Randomness R, PublicParam SP, String ID, String m) {
        Mcl.pairing(h, R.R, SP.P);
        SP.H0(G1_tmp[0], ID);
        SP.H1(Fr_tmp[0], m);
        Mcl.mul(G1_tmp[0], G1_tmp[0], Fr_tmp[0]);
        Mcl.pairing(GT_tmp[0], G1_tmp[0], SP.P_pub);
        Mcl.mul(h, h, GT_tmp[0]);
    }

    public void SetUp(PublicParam SP, MasterSecretKey msk) {
        Func.GetMCLG2RandomElement(SP.P);
        Func.GetMCLZrRandomElement(msk.s);
        Mcl.mul(SP.P_pub, SP.P, msk.s);
    }

    public void KeyGen(SecretKey sk, PublicParam SP, MasterSecretKey msk, String ID) {
        SP.H0(sk.S_ID, ID);
        Mcl.mul(sk.S_ID, sk.S_ID, msk.s);
    }

    public void Hash(HashValue H, Randomness R, PublicParam SP, String ID, String m) {
        Func.GetMCLG1RandomElement(R.R);
        getHashValue(H.h, R, SP, ID, m);
    }

    public boolean Check(HashValue H, Randomness R, PublicParam SP, String ID, String m) {
        getHashValue(GT_tmp[1], R, SP, ID, m);
        return H.h.equals(GT_tmp[1]);
    }

    public void Adapt(Randomness R_p, Randomness R, PublicParam SP, SecretKey sk, String m, String m_p) {
        SP.H1(Fr_tmp[0], m);
        SP.H1(Fr_tmp[1], m_p);
        Mcl.sub(Fr_tmp[0], Fr_tmp[0], Fr_tmp[1]);
        Mcl.mul(R_p.R, sk.S_ID, Fr_tmp[0]);
        Mcl.add(R_p.R, R.R, R_p.R);
    }
}
