package scheme.CH.CH_AMV_2017;

import base.GroupParam.MCL.SingleGroup;
import com.herumi.mcl.Fr;
import com.herumi.mcl.GT;
import com.herumi.mcl.Mcl;

import java.util.HashMap;

/*
 * Redactable Blockchain or Rewriting History in Bitcoin and Friends
 * P25. 4.4.2 Random Oracle Model Instantiation
 */

@SuppressWarnings("SuspiciousNameCombination")
public class MCL_GT {
    public static class PublicParam {
        public SingleGroup.SingleGroupGT GP = new SingleGroup.SingleGroupGT();
        public GT g = new GT();

        public PublicParam() {
            GP.GetGElement(g);
        }

        public AE.PKE_CPA_AMV_2017.MCL_GT.PublicParam PKE_CPA_PP = new AE.PKE_CPA_AMV_2017.MCL_GT.PublicParam();
        public AE.PKE_CCA_AMV_2017.MCL_GT.PublicParam PKE_CCA_PP = new AE.PKE_CCA_AMV_2017.MCL_GT.PublicParam();
        public HashMap<String, GT> Omega = new HashMap<>();
        public HashMap<String, Fr> Omega_inv = new HashMap<>();
    }

    public static class PublicKey {
        public AE.PKE_CPA_AMV_2017.MCL_GT.PublicKey PKE_CPA_PK = new AE.PKE_CPA_AMV_2017.MCL_GT.PublicKey();
        public AE.PKE_CCA_AMV_2017.MCL_GT.PublicKey PKE_CCA_PK = new AE.PKE_CCA_AMV_2017.MCL_GT.PublicKey();
        public GT y = new GT();
    }

    public static class SecretKey {
        public AE.PKE_CPA_AMV_2017.MCL_GT.SecretKey PKE_CPA_SK = new AE.PKE_CPA_AMV_2017.MCL_GT.SecretKey();
        public AE.PKE_CCA_AMV_2017.MCL_GT.SecretKey PKE_CCA_SK = new AE.PKE_CCA_AMV_2017.MCL_GT.SecretKey();
        public Fr x = new Fr();
    }

    public static class HashValue {
        public GT h = new GT();
    }

    public static class Randomness {
        public Fr r = new Fr();
    }

    public static class EncRandomness {
        public base.NIZK.MCL_GT.DL_Proof pi_1, pi_2;
        public base.NIZK.MCL_GT.EQUAL_DL_Proof pi_3;
        public base.NIZK.MCL_GT.REPRESENT_Proof pi_4, pi_5;
        AE.PKE_CPA_AMV_2017.MCL_GT.CipherText PKE_CPA_CT = new AE.PKE_CPA_AMV_2017.MCL_GT.CipherText();
        AE.PKE_CCA_AMV_2017.MCL_GT.CipherText PKE_CCA_CT = new AE.PKE_CCA_AMV_2017.MCL_GT.CipherText();
    }

    AE.PKE_CPA_AMV_2017.MCL_GT PKE_CPA = new AE.PKE_CPA_AMV_2017.MCL_GT();
    AE.PKE_CCA_AMV_2017.MCL_GT PKE_CCA = new AE.PKE_CCA_AMV_2017.MCL_GT();

    private final GT[] G_tmp = new GT[]{new GT(), new GT(), new GT(), new GT()};
    private final Fr[] Fr_tmp = new Fr[]{new Fr(), new Fr(), new Fr(), new Fr(), new Fr()};

    private void genR(HashValue H, EncRandomness ER, Randomness R, PublicParam pp, PublicKey pk, Fr m) {
        Mcl.pow(G_tmp[0], pk.y, m);
        Mcl.mul(G_tmp[0], H.h, G_tmp[0]);
        ER.pi_1 = new base.NIZK.MCL_GT.DL_Proof(R.r, pp.g, G_tmp[0], Fr_tmp);

        pp.GP.GetZrElement(Fr_tmp[2]);
        AE.PKE_CPA_AMV_2017.MCL_GT.PlainText PKE_CPA_PT = new AE.PKE_CPA_AMV_2017.MCL_GT.PlainText();
        PKE_CPA_PT.m = R.r;
        PKE_CPA.Encrypt(ER.PKE_CPA_CT, pp.PKE_CPA_PP, pk.PKE_CPA_PK, PKE_CPA_PT, Fr_tmp[2]);
        ER.pi_2 = new base.NIZK.MCL_GT.DL_Proof(Fr_tmp[2], pp.PKE_CPA_PP.g, ER.PKE_CPA_CT.c_1, Fr_tmp);

        AE.PKE_CCA_AMV_2017.MCL_GT.PlainText PKE_CCA_PT = new AE.PKE_CCA_AMV_2017.MCL_GT.PlainText();
        PKE_CCA_PT.m = R.r;
        PKE_CCA.Encrypt(ER.PKE_CCA_CT, pp.PKE_CCA_PP, pk.PKE_CCA_PK, PKE_CCA_PT, Fr_tmp[3]);
        ER.pi_3 = new base.NIZK.MCL_GT.EQUAL_DL_Proof(Fr_tmp[3], pp.PKE_CCA_PP.g_1, ER.PKE_CCA_CT.c_1, pp.PKE_CCA_PP.g_2, ER.PKE_CCA_CT.c_2, Fr_tmp);
        pp.PKE_CCA_PP.H(Fr_tmp[4], ER.PKE_CCA_CT.c_1, ER.PKE_CCA_CT.c_2, ER.PKE_CCA_CT.c_3);
        Mcl.mul(Fr_tmp[4], Fr_tmp[3], Fr_tmp[4]);
        ER.pi_4 = new base.NIZK.MCL_GT.REPRESENT_Proof(ER.PKE_CCA_CT.c_4, pk.PKE_CCA_PK.y_1, Fr_tmp[3], pk.PKE_CCA_PK.y_2, Fr_tmp[4], G_tmp, Fr_tmp);
        Mcl.inv(G_tmp[1], ER.PKE_CCA_CT.c_3);
        Mcl.mul(G_tmp[1], ER.PKE_CPA_CT.c_2, G_tmp[1]);
        Mcl.inv(G_tmp[2], pk.PKE_CCA_PK.y_3);
        ER.pi_5 = new base.NIZK.MCL_GT.REPRESENT_Proof(G_tmp[1], pk.PKE_CPA_PK.y, Fr_tmp[2], G_tmp[2], Fr_tmp[3], G_tmp, Fr_tmp);
    }

    public void SetUp(PublicParam pp) {
        pp.GP.GetGElement(pp.g);
        PKE_CPA.SetUp(pp.PKE_CPA_PP);
        PKE_CCA.SetUp(pp.PKE_CCA_PP);
        pp.PKE_CPA_PP.Omega = pp.Omega;
        pp.PKE_CCA_PP.Omega = pp.Omega;
        pp.PKE_CPA_PP.Omega_inv = pp.Omega_inv;
        pp.PKE_CCA_PP.Omega_inv = pp.Omega_inv;
    }

    public void KeyGen(PublicKey pk, SecretKey sk, PublicParam pp) {
        PKE_CPA.KeyGen(pk.PKE_CPA_PK, sk.PKE_CPA_SK, pp.PKE_CPA_PP);
        PKE_CCA.KeyGen(pk.PKE_CCA_PK, sk.PKE_CCA_SK, pp.PKE_CCA_PP);
        pp.GP.GetZrElement(sk.x);
        Mcl.pow(pk.y, pp.g, sk.x);
    }

    public void Hash(HashValue H, EncRandomness ER, Randomness R, PublicParam pp, PublicKey pk, Fr m) {
        pp.GP.GetZrElement(R.r);

        Mcl.pow(H.h, pp.g, R.r);
        Mcl.pow(G_tmp[0], pk.y, m);
        Mcl.inv(G_tmp[0], G_tmp[0]);
        Mcl.mul(H.h, H.h, G_tmp[0]);
        genR(H, ER, R, pp, pk, m);
    }

    public boolean Check(HashValue H, EncRandomness ER, PublicParam pp, PublicKey pk, Fr m) {
        Mcl.pow(G_tmp[2], pk.y, m);
        Mcl.mul(G_tmp[2], H.h, G_tmp[2]);
        boolean res = ER.pi_1.Check(pp.g, G_tmp[2], G_tmp, Fr_tmp) &&
                ER.pi_2.Check(pp.PKE_CPA_PP.g, ER.PKE_CPA_CT.c_1, G_tmp, Fr_tmp) &&
                ER.pi_3.Check(pp.PKE_CCA_PP.g_1, ER.PKE_CCA_CT.c_1, pp.PKE_CCA_PP.g_2, ER.PKE_CCA_CT.c_2, G_tmp, Fr_tmp) &&
                ER.pi_4.Check(ER.PKE_CCA_CT.c_4, pk.PKE_CCA_PK.y_1, pk.PKE_CCA_PK.y_2, G_tmp, Fr_tmp);

        Mcl.inv(G_tmp[2], ER.PKE_CCA_CT.c_3);
        Mcl.mul(G_tmp[2], ER.PKE_CPA_CT.c_2, G_tmp[2]);
        Mcl.inv(G_tmp[3], pk.PKE_CCA_PK.y_3);
        return res && ER.pi_5.Check(G_tmp[2], pk.PKE_CPA_PK.y, G_tmp[3], G_tmp, Fr_tmp);
    }

    public void Adapt(EncRandomness ER_p, Randomness R_p, HashValue H, EncRandomness ER, PublicParam pp, PublicKey pk, SecretKey sk, Fr m, Fr m_p) {
        if(!Check(H, ER, pp, pk, m)) throw new RuntimeException("wrong hash value");
        AE.PKE_CPA_AMV_2017.MCL_GT.PlainText CPA_PT = new AE.PKE_CPA_AMV_2017.MCL_GT.PlainText();
        PKE_CPA.Decrypt(CPA_PT, pp.PKE_CPA_PP, sk.PKE_CPA_SK, ER.PKE_CPA_CT);
        Mcl.sub(R_p.r, m, m_p);
        Mcl.mul(R_p.r, sk.x, R_p.r);
        Mcl.sub(R_p.r, CPA_PT.m, R_p.r);
        genR(H, ER_p, R_p, pp, pk, m_p);
    }
}
