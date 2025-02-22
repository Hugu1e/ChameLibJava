package scheme.CH.FCR_CH_PreQA_DKS_2020;

import curve.Group;
import it.unisa.dia.gas.jpbc.Element;
import it.unisa.dia.gas.jpbc.Field;
import it.unisa.dia.gas.jpbc.Pairing;
import utils.Func;
import utils.Hash;

/*
 * Fully Collision-Resistant Chameleon-Hashes from Simpler and Post-Quantum Assumptions
 * P15. Construction 2: Concrete instantiation from DLOG
 */

@SuppressWarnings("rawtypes")
public class PBC {
    public static class PublicParam {
        Field Zr, G;
        Element g_1, g_2;

        private Element H(String m) {
            return Hash.H_String_1_PBC_1(Zr, m);
        }

        public Element H(Element m1, Element m2, Element m3, Element m4, Element m5) {
            return H(String.format("(%s|%s|%s)(%s|%s)", m1, m2, m3, m4, m5));
        }

        public Element H_p(Element m) {
            return Hash.H_PBC_1_1(G, m);
        }

        public Element GetGElement() {
            return G.newRandomElement().getImmutable();
        }

        public Element GetZrElement() {
            return Zr.newRandomElement().getImmutable();
        }
    }

    public static class PublicKey {
        public Element y;
    }

    public static class SecretKey {
        public Element x;
    }

    public static class HashValue {
        public Element O;
    }

    public static class Randomness {
        public Element e_1, e_2, s_1_1, s_1_2, s_2;
    }

    public void SetUp(PublicParam pp, curve.PBC curve, Group group) {
        Pairing pairing = Func.PairingGen(curve);
        pp.G = Func.GetPBCField(pairing, group);
        pp.Zr = pairing.getZr();
        pp.g_1 = pp.GetGElement();
        pp.g_2 = pp.H_p(pp.g_1);
    }

    public void KeyGen(PublicKey pk, SecretKey sk, PublicParam pp) {
        sk.x = pp.GetZrElement();
        pk.y = pp.g_1.powZn(sk.x).getImmutable();
    }

    public void Hash(HashValue H, Randomness R, PublicParam pp, PublicKey pk, Element m) {
        Element xi, k_1_1, k_1_2;
        xi = pp.GetZrElement();
        k_1_1 = pp.GetZrElement();
        k_1_2 = pp.GetZrElement();
        R.e_2 = pp.GetZrElement();
        R.s_2 = pp.GetZrElement();

        H.O = pp.g_1.powZn(m).mul(pp.g_2.powZn(xi)).getImmutable();

        R.e_1 = pp.H(
                pk.y, H.O, m,
                pp.g_1.powZn(k_1_1).mul(pp.g_2.powZn(k_1_2)),
                pp.g_1.powZn(R.s_2).mul(pk.y.powZn(R.e_2.negate()))
        ).sub(R.e_2);
        R.s_1_1 = k_1_1.add(R.e_1.mul(m));
        R.s_1_2 = k_1_2.add(R.e_1.mul(xi));
    }

    public boolean Check(HashValue H, Randomness R, PublicParam pp, PublicKey pk, Element m) {
        return R.e_1.add(R.e_2).isEqual(pp.H(
                pk.y, H.O, m,
                pp.g_1.powZn(R.s_1_1).mul(pp.g_2.powZn(R.s_1_2)).mul(H.O.powZn(R.e_1.negate())),
                pp.g_1.powZn(R.s_2).mul(pk.y.powZn(R.e_2.negate()))
        ));
    }

    public void Adapt(Randomness R_p, HashValue H, Randomness R,PublicParam pp, PublicKey pk, SecretKey sk, Element m, Element m_p) {
        if(!Check(H, R, pp, pk, m)) throw new RuntimeException("wrong hash value");
        Element k_2;
        k_2 = pp.GetZrElement();
        R_p.e_1 = pp.GetZrElement();
        R_p.s_1_1 = pp.GetZrElement();
        R_p.s_1_2 = pp.GetZrElement();

        R_p.e_2 = pp.H(
                pk.y, H.O, m_p,
                pp.g_1.powZn(R_p.s_1_1).mul(pp.g_2.powZn(R_p.s_1_2)).mul(H.O.powZn(R_p.e_1.negate())),
                pp.g_1.powZn(k_2)
        ).sub(R_p.e_1).getImmutable();

        R_p.s_2 = k_2.add(R_p.e_2.mul(sk.x)).getImmutable();
    }
}
