package scheme.CH.CH_KEF_CZK_2004;

import base.GroupParam.MCL.SingleGroup;
import com.herumi.mcl.Fr;
import com.herumi.mcl.G1;
import com.herumi.mcl.Mcl;
import utils.Hash;

/*
 * Chameleon Hashing without Key Exposure
 * P7. 3.3.1 The scheme
 */

@SuppressWarnings("SuspiciousNameCombination")
public class MCL_G1 {
    public static class PublicParam {
        public SingleGroup.SingleGroupG1 GP = new SingleGroup.SingleGroupG1();
        G1 g = new G1();

        public PublicParam() {
            GP.GetGElement(g);
        }

        public void H(G1 res, String m) {
            Hash.H_MCL_G1_1(res, m);
        }
    }

    public static class PublicKey {
        public G1 y = new G1();
    }

    public static class SecretKey {
        public Fr x = new Fr();
    }

    public static class HashValue {
        public G1 h = new G1();
    }

    public static class Randomness {
        public G1 g_a = new G1(), y_a = new G1();
    }

    private final G1[] G_tmp = new G1[]{new G1()};
    private final Fr[] Fr_tmp = new Fr[]{new Fr()};

    private static void getHashValue(G1 res, Randomness R, PublicParam SP, G1 I, Fr m) {
        Mcl.add(res, SP.g, I);
        Mcl.mul(res, res, m);
        Mcl.add(res, res, R.y_a);
    }

    public void KeyGen(PublicKey pk, SecretKey sk, PublicParam SP) {
        SP.GP.GetZrElement(sk.x);
        Mcl.mul(pk.y, SP.g, sk.x);
    }

    public void Hash(HashValue H, Randomness R, PublicParam SP, PublicKey pk, G1 I, Fr m) {
        SP.GP.GetZrElement(Fr_tmp[0]);
        Mcl.mul(R.g_a, SP.g, Fr_tmp[0]);
        Mcl.mul(R.y_a, pk.y, Fr_tmp[0]);
        getHashValue(H.h, R, SP, I, m);
    }

    public boolean Check(HashValue H, Randomness R, PublicParam SP, G1 I, Fr m) {
        getHashValue(G_tmp[0], R, SP, I, m);
        return H.h.equals(G_tmp[0]);
    }

    public void Adapt(Randomness R_p, Randomness R, PublicParam SP, SecretKey sk, G1 I, Fr m, Fr m_p) {
        Mcl.add(R_p.g_a, SP.g, I);
        Mcl.sub(Fr_tmp[0], m, m_p);
        Mcl.mul(R_p.y_a, R_p.g_a, Fr_tmp[0]);
        Mcl.add(R_p.y_a, R_p.y_a, R.y_a);

        Mcl.div(Fr_tmp[0], Fr_tmp[0], sk.x);
        Mcl.mul(R_p.g_a, R_p.g_a, Fr_tmp[0]);
        Mcl.add(R_p.g_a, R_p.g_a, R.g_a);
    }
}
