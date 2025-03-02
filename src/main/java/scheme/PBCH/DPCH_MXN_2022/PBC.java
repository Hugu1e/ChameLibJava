package scheme.PBCH.DPCH_MXN_2022;

import it.unisa.dia.gas.jpbc.Element;
import utils.Hash;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

/*
 * Redactable Blockchain in Decentralized Setting
 * P12. Fig. 3. An instantiation of DPCH.
 */

public class PBC {
    public static class PublicParam {
        public ABE.MA_ABE.PBC.PublicParam GP_MA_ABE;
        public Signature.BLS.PBC.PublicParam pp_DS;

        public PublicParam(curve.PBC curve, boolean swap_G1G2) {
            GP_MA_ABE = new ABE.MA_ABE.PBC.PublicParam(curve);
            pp_DS = new Signature.BLS.PBC.PublicParam(curve, swap_G1G2);
        }
    }

    public static class MasterPublicKey {
        scheme.CH.CH_ET_BC_CDK_2017.Native.PublicKey pk_CH = new scheme.CH.CH_ET_BC_CDK_2017.Native.PublicKey();
        Signature.BLS.PBC.PublicKey pk_DS = new Signature.BLS.PBC.PublicKey();
    }

    public static class MasterSecretKey {
        scheme.CH.CH_ET_BC_CDK_2017.Native.SecretKey sk_CH = new scheme.CH.CH_ET_BC_CDK_2017.Native.SecretKey();
        Signature.BLS.PBC.SecretKey sk_DS = new Signature.BLS.PBC.SecretKey();
    }

    public static class Authority {
        public ABE.MA_ABE.PBC.Authority MA_ABE_Auth;

        public Authority(String theta) {
            MA_ABE_Auth = new ABE.MA_ABE.PBC.Authority(theta);
        }
    }

    public static class Modifier {
        String gid;
        scheme.CH.CH_ET_BC_CDK_2017.Native.SecretKey sk_gid = new scheme.CH.CH_ET_BC_CDK_2017.Native.SecretKey();
        Signature.BLS.PBC.Signature sigma_gid = new Signature.BLS.PBC.Signature();
        ABE.MA_ABE.PBC.SecretKey sk_gid_A = new ABE.MA_ABE.PBC.SecretKey();

        public Modifier(String gid) {
            this.gid = gid;
        }
    }

    public static class PublicKeyGroup {
        ABE.MA_ABE.PBC.PublicKeyGroup MA_ABE_PKG = new ABE.MA_ABE.PBC.PublicKeyGroup();

        public void AddPK(Authority Auth) {
            MA_ABE_PKG.AddPK(Auth.MA_ABE_Auth);
        }
    }

    public static class SecretKeyGroup {
        ABE.MA_ABE.PBC.SecretKeyGroup MA_ABE_SKG = new ABE.MA_ABE.PBC.SecretKeyGroup();

        public void AddSK(Modifier mod) {
            MA_ABE_SKG.AddSK(mod.sk_gid_A);
        }
    }

    public static class HashValue {
        scheme.CH.CH_ET_BC_CDK_2017.Native.HashValue h = new scheme.CH.CH_ET_BC_CDK_2017.Native.HashValue();
        SE.AES.CipherText c_SE = new SE.AES.CipherText();
        ABE.MA_ABE.PBC.CipherText c_MA_ABE = new ABE.MA_ABE.PBC.CipherText();
    }

    public static class Randomness {
        scheme.CH.CH_ET_BC_CDK_2017.Native.Randomness r = new scheme.CH.CH_ET_BC_CDK_2017.Native.Randomness();
    }

    Random rand = new Random();
    ABE.MA_ABE.PBC MA_ABE = new ABE.MA_ABE.PBC();
    scheme.CH.CH_ET_BC_CDK_2017.Native CH_ET;
    Signature.BLS.PBC DS = new Signature.BLS.PBC();

    public PBC(int lambda) {
        CH_ET = new scheme.CH.CH_ET_BC_CDK_2017.Native(lambda);
    }

    private void genEncMAABE(ABE.MA_ABE.PBC.CipherText c_MA_ABE, ABE.MA_ABE.PBC.PlainText pt_MA_ABE, PublicKeyGroup PKG, base.LSSS.PBC.Matrix MSP, PublicParam pp, byte[] r_t) {
        int l1 = MSP.M.length;
        int l2 = MSP.M[0].length;

        base.LSSS.PBC.Matrix.Vector t_x = new base.LSSS.PBC.Matrix.Vector();
        t_x.v = new Element[l1];
        for(int i = 1;i <= l1;++i) t_x.v[i - 1] = pp.GP_MA_ABE.Ht(String.format("%s%s0%d", Arrays.toString(r_t), MSP.formula, i));

        base.LSSS.PBC.Matrix.Vector v = new base.LSSS.PBC.Matrix.Vector();
        v.v = new Element[l2];
        v.v[0] = pp.GP_MA_ABE.Ht(String.format("%s%s", Arrays.toString(r_t), MSP.formula));
        for(int i = 2;i <= l2;++i) v.v[i - 1] = pp.GP_MA_ABE.Ht(String.format("%s%s1%d", Arrays.toString(r_t), MSP.formula, i));

        base.LSSS.PBC.Matrix.Vector w = new base.LSSS.PBC.Matrix.Vector();
        w.v = new Element[l2];
        w.v[0] = pp.GP_MA_ABE.GP.Zr.newZeroElement().getImmutable();
        for(int i = 2;i <= l2;++i) w.v[i - 1] = pp.GP_MA_ABE.Ht(String.format("%s%s2%d", Arrays.toString(r_t), MSP.formula, i));

        MA_ABE.Encrypt(c_MA_ABE, pp.GP_MA_ABE, PKG.MA_ABE_PKG, MSP, pt_MA_ABE, v, w, t_x);
    }

    public void SetUp(MasterPublicKey pk, MasterSecretKey sk, PublicParam pp) {
        CH_ET.KeyGen(pk.pk_CH, sk.sk_CH);
        DS.KeyGen(pk.pk_DS, sk.sk_DS, pp.pp_DS);
    }

    public void ModSetup(Modifier mod, PublicParam pp, MasterSecretKey sk) {
        DS.Sign(mod.sigma_gid, sk.sk_DS, pp.pp_DS, "1" + mod.gid);
        mod.sk_gid = sk.sk_CH;
    }

    public void AuthSetup(Authority auth, PublicParam pp) {
        MA_ABE.AuthSetup(auth.MA_ABE_Auth, pp.GP_MA_ABE);
    }

    public void ModKeyGen(Modifier mod, PublicParam pp, MasterPublicKey pk, Authority auth, String A) {
        if(!DS.Verify(pp.pp_DS, pk.pk_DS, mod.sigma_gid, "1" + mod.gid)) throw new RuntimeException("illegal signature");
        MA_ABE.KeyGen(auth.MA_ABE_Auth, mod.sk_gid_A, A, pp.GP_MA_ABE, "0" + mod.gid);
    }

    public void Hash(HashValue H, Randomness R, PublicKeyGroup PKG, base.LSSS.PBC.Matrix MSP, PublicParam pp, MasterPublicKey pk, String m) {
        scheme.CH.CH_ET_BC_CDK_2017.Native.ETrapdoor etd = new scheme.CH.CH_ET_BC_CDK_2017.Native.ETrapdoor();
        CH_ET.Hash(H.h, R.r, etd, pk.pk_CH, m);
        byte[] r_t = new byte[16];
        rand.nextBytes(r_t);
        byte[] k = new byte[16];
        rand.nextBytes(k);
        SE.AES.PlainText pt_SE = new SE.AES.PlainText();
        pt_SE.pt = etd.sk_ch_2.d.toByteArray();
        SE.AES.Encrypt(H.c_SE, pt_SE, k);

        Hash.EncText enc = new Hash.EncText();
        Hash.Encode(enc, pp.GP_MA_ABE.GP.GT, new Hash.PlaText(k, r_t));
        ABE.MA_ABE.PBC.PlainText pt_MA_ABE = new ABE.MA_ABE.PBC.PlainText(enc.K);
        genEncMAABE(H.c_MA_ABE, pt_MA_ABE, PKG, MSP, pp, r_t);
    }

    public boolean Check(HashValue H, Randomness R, MasterPublicKey pk, String m) {
        return CH_ET.Check(H.h, R.r, pk.pk_CH, m);
    }

    public void Adapt(Randomness R_p, HashValue H, Randomness R, PublicKeyGroup PKG, SecretKeyGroup SKG, base.LSSS.PBC.Matrix MSP, PublicParam pp, MasterPublicKey pk, MasterSecretKey sk, String m, String m_p) {
        if(m.compareTo(m_p) == 0) {
            R_p.r = R.r;
            return;
        }

        ABE.MA_ABE.PBC.PlainText pt_MA_ABE = new ABE.MA_ABE.PBC.PlainText(pp.GP_MA_ABE.GP.GetGTElement());
        ABE.MA_ABE.PBC.CipherText ct_MA_ABE = new ABE.MA_ABE.PBC.CipherText();
        MA_ABE.Decrypt(pt_MA_ABE, pp.GP_MA_ABE, SKG.MA_ABE_SKG, MSP, H.c_MA_ABE);
        Hash.PlaText pla = new Hash.PlaText();
        Hash.Decode(pla, new Hash.EncText(pt_MA_ABE.m));
        genEncMAABE(ct_MA_ABE, pt_MA_ABE, PKG, MSP, pp, pla.r);
        if(!ct_MA_ABE.isEqual(H.c_MA_ABE)) throw new RuntimeException("illegal decrypt");

        scheme.CH.CH_ET_BC_CDK_2017.Native.ETrapdoor etd = new scheme.CH.CH_ET_BC_CDK_2017.Native.ETrapdoor();

        SE.AES.PlainText pt_SE = new SE.AES.PlainText();
        SE.AES.Decrypt(pt_SE, H.c_SE, pla.k);
        etd.sk_ch_2.d = new BigInteger(pt_SE.pt);
        CH_ET.Adapt(R_p.r, H.h, R.r, etd, pk.pk_CH, sk.sk_CH, m, m_p);
    }
}
