/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.atoms.element;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.scene.paint.Color;
import burai.atoms.model.exception.IncorrectAtomNameException;

public final class ElementUtil {

    private static final String DUMMY_NAME = "X";

    private static final Pattern NAME_PATTERN = Pattern.compile("[0-9_-]");

    private static enum Element {
        H(1, 0.32, 1.000000, 1.000000, 1.000000, 2.20, 1.00794, 1),
        He(2, 0.46, 0.850980, 1.000000, 1.000000, 4.16, 4.00260, 2),
        Li(3, 1.33, 0.800000, 0.501961, 1.000000, 0.98, 6.94100, 1),
        Be(4, 1.02, 0.760784, 1.000000, 0.000000, 1.57, 9.01218, 2),
        B(5, 0.85, 1.000000, 0.709804, 0.709804, 2.04, 10.81100, 3),
        C(6, 0.75, 0.564706, 0.564706, 0.564706, 2.55, 12.01070, 4),
        N(7, 0.71, 0.188235, 0.313726, 0.972549, 3.04, 14.00674, 5),
        O(8, 0.63, 1.000000, 0.050980, 0.050980, 3.44, 15.99940, 6),
        F(9, 0.64, 0.564706, 0.878431, 0.313726, 3.98, 18.99840, 7),
        Ne(10, 0.67, 0.701961, 0.890196, 0.960784, 4.79, 20.17970, 8),
        Na(11, 1.55, 0.670588, 0.360784, 0.949020, 0.93, 22.98977, 1),
        Mg(12, 1.39, 0.541177, 1.000000, 0.000000, 1.31, 24.30500, 2),
        Al(13, 1.26, 0.749020, 0.650980, 0.650980, 1.61, 26.98154, 3),
        Si(14, 1.16, 0.941177, 0.784314, 0.627451, 1.90, 28.08550, 4),
        P(15, 1.11, 1.000000, 0.501961, 0.000000, 2.19, 30.97376, 5),
        S(16, 1.03, 1.000000, 1.000000, 0.188235, 2.58, 32.06600, 6),
        Cl(17, 0.99, 0.121569, 0.941177, 0.121569, 3.16, 35.45270, 7),
        Ar(18, 0.96, 0.501961, 0.819608, 0.890196, 3.24, 39.94800, 8),
        K(19, 1.96, 0.560784, 0.250980, 0.831373, 0.82, 39.09830, 1),
        Ca(20, 1.71, 0.239216, 1.000000, 0.000000, 1.00, 40.07800, 2),
        Sc(21, 1.48, 0.901961, 0.901961, 0.901961, 1.36, 44.95591, 3),
        Ti(22, 1.36, 0.749020, 0.760784, 0.780392, 1.54, 47.86700, 4),
        V(23, 1.34, 0.650980, 0.650980, 0.670588, 1.63, 50.94150, 5),
        Cr(24, 1.22, 0.541177, 0.600000, 0.780392, 1.66, 51.99610, 6),
        Mn(25, 1.19, 0.611765, 0.478431, 0.780392, 1.55, 54.93805, 7),
        Fe(26, 1.16, 0.878431, 0.400000, 0.200000, 1.83, 55.84500, 8),
        Co(27, 1.11, 0.941177, 0.564706, 0.627451, 1.88, 58.93320, 9),
        Ni(28, 1.10, 0.313726, 0.815686, 0.313726, 1.91, 58.69340, 10),
        Cu(29, 1.12, 0.784314, 0.501961, 0.200000, 1.90, 63.54600, 11),
        Zn(30, 1.18, 0.490196, 0.501961, 0.690196, 1.65, 65.39000, 2),
        Ga(31, 1.24, 0.760784, 0.560784, 0.560784, 1.81, 69.72300, 3),
        Ge(32, 1.24, 0.400000, 0.560784, 0.560784, 2.01, 72.61000, 4),
        As(33, 1.21, 0.741177, 0.501961, 0.890196, 2.18, 74.92160, 5),
        Se(34, 1.16, 1.000000, 0.631373, 0.000000, 2.55, 78.96000, 6),
        Br(35, 1.14, 0.650980, 0.160784, 0.160784, 2.96, 79.90400, 7),
        Kr(36, 1.17, 0.360784, 0.721569, 0.819608, 3.00, 83.80000, 8),
        Rb(37, 2.10, 0.439216, 0.180392, 0.690196, 0.82, 85.46780, 1),
        Sr(38, 1.85, 0.000000, 1.000000, 0.000000, 0.95, 87.62000, 2),
        Y(39, 1.63, 0.580392, 1.000000, 1.000000, 1.22, 88.90585, 3),
        Zr(40, 1.54, 0.580392, 0.878431, 0.878431, 1.33, 91.22400, 4),
        Nb(41, 1.47, 0.450980, 0.760784, 0.788235, 1.60, 92.90638, 5),
        Mo(42, 1.38, 0.329412, 0.709804, 0.709804, 2.16, 95.94000, 6),
        Tc(43, 1.28, 0.231373, 0.619608, 0.619608, 1.90, 98.00000, 7),
        Ru(44, 1.25, 0.141176, 0.560784, 0.560784, 2.20, 101.07000, 8),
        Rh(45, 1.25, 0.039216, 0.490196, 0.549020, 2.28, 102.90550, 9),
        Pd(46, 1.20, 0.000000, 0.411765, 0.521569, 2.20, 106.42000, 10),
        Ag(47, 1.28, 0.752941, 0.752941, 0.752941, 1.93, 107.86820, 11),
        Cd(48, 1.36, 1.000000, 0.850980, 0.560784, 1.69, 112.41100, 2),
        In(49, 1.42, 0.650980, 0.458824, 0.450980, 1.78, 114.81800, 3),
        Sn(50, 1.40, 0.400000, 0.501961, 0.501961, 1.96, 118.71000, 4),
        Sb(51, 1.40, 0.619608, 0.388235, 0.709804, 2.05, 121.76000, 5),
        Te(52, 1.36, 0.831373, 0.478431, 0.000000, 2.10, 127.60000, 6),
        I(53, 1.33, 0.580392, 0.000000, 0.580392, 2.66, 126.90447, 7),
        Xe(54, 1.31, 0.258824, 0.619608, 0.690196, 2.60, 131.29000, 8),
        Cs(55, 2.32, 0.341176, 0.090196, 0.560784, 0.79, 132.90545, 1),
        Ba(56, 1.96, 0.000000, 0.788235, 0.000000, 0.89, 137.32700, 2),
        La(57, 1.80, 0.439216, 0.831373, 1.000000, 1.10, 138.90550, 3),
        Ce(58, 1.63, 1.000000, 1.000000, 0.780392, 1.12, 140.11600, 4),
        Pr(59, 1.76, 0.850980, 1.000000, 0.780392, 1.13, 140.90765, 5),
        Nd(60, 1.74, 0.780392, 1.000000, 0.780392, 1.14, 144.24000, 6),
        Rm(61, 1.73, 0.639216, 1.000000, 0.780392, 1.16, 145.00000, 7),
        Sm(62, 1.72, 0.560784, 1.000000, 0.780392, 1.17, 150.36000, 8),
        Eu(63, 1.68, 0.380392, 1.000000, 0.780392, 1.19, 151.96400, 9),
        Gd(64, 1.69, 0.270588, 1.000000, 0.780392, 1.20, 157.25000, 10),
        Tb(65, 1.68, 0.188235, 1.000000, 0.780392, 1.21, 158.92534, 11),
        Dy(66, 1.67, 0.121569, 1.000000, 0.780392, 1.22, 162.50000, 12),
        Ho(67, 1.66, 0.000000, 1.000000, 0.611765, 1.23, 164.93032, 13),
        Er(68, 1.65, 0.000000, 0.901961, 0.458824, 1.24, 167.26000, 14),
        Tm(69, 1.64, 0.000000, 0.831373, 0.321569, 1.25, 168.93421, 15),
        Yb(70, 1.70, 0.000000, 0.749020, 0.219608, 1.26, 173.04000, 16),
        Lu(71, 1.62, 0.000000, 0.670588, 0.141176, 1.27, 174.96700, 17),
        Hf(72, 1.52, 0.301961, 0.760784, 1.000000, 1.30, 178.49000, 4),
        Ta(73, 1.46, 0.301961, 0.650980, 1.000000, 1.50, 180.94790, 5),
        W(74, 1.37, 0.129412, 0.580392, 0.839216, 2.36, 183.84000, 6),
        Re(75, 1.31, 0.149020, 0.490196, 0.670588, 1.90, 186.20700, 7),
        Os(76, 1.29, 0.149020, 0.400000, 0.588235, 2.20, 190.23000, 8),
        Ir(77, 1.22, 0.090196, 0.329412, 0.529412, 2.20, 192.21700, 9),
        Pt(78, 1.23, 0.815686, 0.815686, 0.878431, 2.28, 195.07800, 10),
        Au(79, 1.24, 1.000000, 0.819608, 0.137255, 2.54, 196.96655, 11),
        Hg(80, 1.33, 0.721569, 0.721569, 0.815686, 2.00, 200.59000, 2),
        Tl(81, 1.44, 0.650980, 0.329412, 0.301961, 1.62, 204.38330, 3),
        Pb(82, 1.44, 0.341176, 0.349020, 0.380392, 2.33, 207.20000, 4),
        Bi(83, 1.51, 0.619608, 0.309804, 0.709804, 2.02, 208.98038, 5),
        Po(84, 1.45, 0.670588, 0.360784, 0.000000, 2.00, 209.00000, 6),
        At(85, 1.47, 0.458824, 0.309804, 0.270588, 2.20, 210.00000, 7),
        Rn(86, 1.42, 0.258824, 0.509804, 0.588235, 2.60, 222.00000, 8),
        Fr(87, 2.23, 0.258824, 0.000000, 0.400000, 0.70, 223.00000, 1),
        Ra(88, 2.01, 0.000000, 0.490196, 0.000000, 0.90, 226.00000, 2),
        Ac(89, 1.86, 0.439216, 0.670588, 0.980392, 1.10, 227.00000, 3),
        Th(90, 1.75, 0.000000, 0.729412, 1.000000, 1.30, 232.03810, 4),
        Pa(91, 1.69, 0.000000, 0.631373, 1.000000, 1.50, 231.03588, 5),
        U(92, 1.70, 0.000000, 0.560784, 1.000000, 1.38, 238.02890, 6),
        Np(93, 1.71, 0.000000, 0.501961, 1.000000, 1.36, 237.00000, 7),
        Pu(94, 1.72, 0.000000, 0.419608, 1.000000, 1.28, 244.00000, 8);

        private int atomicNumber;
        private double covalentRadius;
        private Color color;
        private double electronegativity;
        private double mass;
        private int valence;

        private Element(int atomicNumber, double covalentRadius,
                double red, double green, double blue, double electronegativity, double mass, int valence) {
            this.atomicNumber = atomicNumber;
            this.covalentRadius = covalentRadius;
            this.color = Color.color(red, green, blue);
            this.electronegativity = electronegativity;
            this.mass = mass;
            this.valence = valence;
        }

        public int getAtomicNumber() {
            return this.atomicNumber;
        }

        public double getCovalentRadius() {
            return this.covalentRadius;
        }

        public Color getColor() {
            return this.color;
        }

        public double getElectronegativity() {
            return this.electronegativity;
        }

        public double getMass() {
            return this.mass;
        }

        public int getValence() {
            return this.valence;
        }
    }

    private ElementUtil() {
        // NOP
    }

    public static String[] listAllElements() {
        Element[] elements = Element.values();
        String[] names = new String[elements.length];

        for (int i = 0; i < elements.length; i++) {
            names[i] = elements[i].name();
        }

        return names;
    }

    public static String toAvailableName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return DUMMY_NAME;
        }

        String name2 = ElementUtil.toElementName(name);
        if (name2 == null || name2.trim().isEmpty()) {
            return DUMMY_NAME;
        }

        return name;
    }

    public static String toElementName(String name) {
        if (name == null) {
            return null;
        }

        String name2 = name.trim();
        if (name2 == null || name2.isEmpty()) {
            return name2;
        }

        Matcher nameMatcher = ElementUtil.NAME_PATTERN.matcher(name2);
        if (nameMatcher.find()) {
            try {
                name2 = name2.substring(0, nameMatcher.start());
            } catch (IllegalStateException e) {
                name2 = name;
            }
        }

        int lenName = name2.length();
        if (lenName < 1) {
            return name2;
        } else if (lenName < 2) {
            name2 = name2.toUpperCase();
        } else {
            char head = name2.charAt(0);
            name2 = Character.toUpperCase(head) + name2.substring(1, lenName).toLowerCase();
        }

        return name2;
    }

    public static String toElementName(int atomicNumber) {
        Element[] elements = Element.values();
        for (Element element : elements) {
            if (element.getAtomicNumber() == atomicNumber) {
                return element.name();
            }
        }

        return null;
    }

    public static double obtainCovalentRadius(String name) throws IncorrectAtomNameException {
        Element element = toElement(name);
        if (element == null) {
            throw new IncorrectAtomNameException(name);
        }

        return element.getCovalentRadius();
    }

    public static double getCovalentRadius(String name) {
        Element element = toElement(name);
        if (element == null) {
            return 1.0;
        }

        return element.getCovalentRadius();
    }

    public static double obtainElectronegativity(String name) throws IncorrectAtomNameException {
        Element element = toElement(name);
        if (element == null) {
            throw new IncorrectAtomNameException(name);
        }

        return element.getElectronegativity();
    }

    public static double getElectronegativity(String name) {
        Element element = toElement(name);
        if (element == null) {
            return 0.0;
        }

        return element.getElectronegativity();
    }

    public static double obtainMass(String name) throws IncorrectAtomNameException {
        Element element = toElement(name);
        if (element == null) {
            throw new IncorrectAtomNameException(name);
        }

        return element.getMass();
    }

    public static double getMass(String name) {
        Element element = toElement(name);
        if (element == null) {
            return -1.0;
        }

        return element.getMass();
    }

    public static int obtainValence(String name) throws IncorrectAtomNameException {
        Element element = toElement(name);
        if (element == null) {
            throw new IncorrectAtomNameException(name);
        }

        return element.getValence();
    }

    public static int getValence(String name) {
        Element element = toElement(name);
        if (element == null) {
            return 0;
        }

        return element.getValence();
    }

    public static Color obtainColor(String name) throws IncorrectAtomNameException {
        Element element = toElement(name);
        if (element == null) {
            throw new IncorrectAtomNameException(name);
        }

        return element.getColor();
    }

    public static Color getColor(String name) {
        Element element = toElement(name);
        if (element == null) {
            return Color.BLACK;
        }

        return element.getColor();
    }

    public static int obtainAtomicNumber(String name) throws IncorrectAtomNameException {
        Element element = toElement(name);
        if (element == null) {
            throw new IncorrectAtomNameException(name);
        }

        return element.getAtomicNumber();
    }

    public static int getAtomicNumber(String name) {
        Element element = toElement(name);
        if (element == null) {
            return -1;
        }

        return element.getAtomicNumber();
    }

    private static Element toElement(String name) {
        if (name == null) {
            return null;
        }

        String name2 = toElementName(name);
        if (name2 == null || name2.isEmpty()) {
            return null;
        }

        Element element = null;

        try {
            element = Element.valueOf(name2);
        } catch (Exception e) {
            return null;
        }

        return element;
    }

    public static boolean isTransitionMetal(String name) {
        int num = getAtomicNumber(name);

        if (21 <= num && num <= 29) { // Sc ~ Cu
            return true;
        } else if (39 <= num && num <= 47) { // Y ~ Ag
            return true;
        } else if (57 <= num && num <= 79) { // La ~ Au
            return true;
        } else if (89 <= num) { // Ac ~
            return true;
        }

        return false;
    }

    public static boolean isLanthanoid(String name) {
        int num = getAtomicNumber(name);

        if (57 <= num && num <= 71) { // La ~ Lu
            return true;
        }

        return false;
    }

    public static boolean isActinoid(String name) {
        int num = getAtomicNumber(name);

        if (89 <= num && num <= 103) { // Ac ~ Lr
            return true;
        }

        return false;
    }
}
