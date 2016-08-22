/*
 * Copyright (C) 2016 Satomichi Nishihara
 *
 * This file is distributed under the terms of the
 * GNU General Public License. See the file `LICENSE'
 * in the root directory of the present distribution,
 * or http://www.gnu.org/copyleft/gpl.txt .
 */

package burai.com.consts;

public interface Constants {

    /*
     * Physical constants, SI (NIST CODATA 2006), Web Version 5.1
     * <http://physics.nist.gov/constants>
     */
    public static final double H_PLANCK_SI = 6.62606896E-34; // J s
    public static final double K_BOLTZMANN_SI = 1.3806504E-23; // J K^-1
    public static final double ELECTRON_SI = 1.602176487E-19; // C
    public static final double ELECTRONVOLT_SI = 1.602176487E-19; // J
    public static final double ELECTRONMASS_SI = 9.10938215E-31; // Kg
    public static final double HARTREE_SI = 4.35974394E-18; // J
    public static final double RYDBERG_SI = HARTREE_SI / 2.0; // J
    public static final double BOHR_RADIUS_SI = 0.52917720859E-10; // m
    public static final double AMU_SI = 1.660538782E-27; // Kg
    public static final double C_SI = 2.99792458E+8; // m sec^-1
    public static final double MUNOUGHT_SI = 4.0 * Math.PI * 1.0E-7; // N A^-2
    public static final double EPSNOUGHT_SI = 1.0 / (MUNOUGHT_SI * C_SI * C_SI); // F m^-1

    /*
     *
     * Physical constants, atomic units:
     * AU for "Hartree" atomic units (e = m = hbar = 1)
     * RY for "Rydberg" atomic units (e^2=2, m=1/2, hbar=1)
     */
    public static final double K_BOLTZMANN_AU = K_BOLTZMANN_SI / HARTREE_SI;
    public static final double K_BOLTZMANN_RY = K_BOLTZMANN_SI / RYDBERG_SI;

    /*
     * Unit conversion factors: energy and masses
     */
    public static final double AUTOEV = HARTREE_SI / ELECTRONVOLT_SI;
    public static final double RYTOEV = AUTOEV / 2.0;
    public static final double AMU_AU = AMU_SI / ELECTRONMASS_SI;
    public static final double AMU_RY = AMU_AU / 2.0;

    /*
     * Unit conversion factors: atomic unit of time, in s and ps
     */
    public static final double AU_SEC = H_PLANCK_SI / (2.0 * Math.PI) / HARTREE_SI;
    public static final double AU_PS = AU_SEC * 1.0E+12;

    /*
     * Unit conversion factors: pressure (1 Pa = 1 J/m^3, 1GPa = 10 Kbar )
     */
    public static final double AU_GPA = HARTREE_SI / BOHR_RADIUS_SI / BOHR_RADIUS_SI / BOHR_RADIUS_SI / 1.0E+9;
    public static final double RY_KBAR = 10.0 * AU_GPA / 2.0;

    /*
     * Unit conversion factors: 1 debye = 10^-18 esu*cm
     *                                  = 3.3356409519*10^-30 C*m
     *                                  = 0.208194346 e*A
     * ( 1 esu = (0.1/c) Am, c=299792458 m/s)
     */
    public static final double DEBYE_SI = 3.3356409519 * 1.0E-30; // C*m
    public static final double AU_DEBYE = ELECTRON_SI * BOHR_RADIUS_SI / DEBYE_SI;

    public static final double eV_to_kelvin = ELECTRONVOLT_SI / K_BOLTZMANN_SI;
    public static final double ry_to_kelvin = RYDBERG_SI / K_BOLTZMANN_SI;

    /*
     * Unit conversion factors: Energy to wavelength
     */
    public static final double EVTONM = 1.0E+9 * H_PLANCK_SI * C_SI / ELECTRONVOLT_SI;
    public static final double RYTONM = 1.0E+9 * H_PLANCK_SI * C_SI / RYDBERG_SI;

    /*
     * Speed of light in atomic units
     */
    public static final double C_AU = C_SI / BOHR_RADIUS_SI * AU_SEC;

    /*
     * Temperature
     */
    public static final double CENTIGRADE_ZERO = 273.15;

    /*
     * COMPATIBIILITY
     */
    public static final double BOHR_RADIUS_CM = BOHR_RADIUS_SI * 100.0;
    public static final double BOHR_RADIUS_ANGS = BOHR_RADIUS_CM * 1.0E8;
    public static final double ANGSTROM_AU = 1.0 / BOHR_RADIUS_ANGS;
    public static final double DIP_DEBYE = AU_DEBYE;
    public static final double AU_TERAHERTZ = AU_PS;
    public static final double AU_TO_OHMCMM1 = 46000.0; // (ohm cm)^-1
    public static final double RY_TO_THZ = 1.0 / AU_TERAHERTZ / (4.0 * Math.PI);
    public static final double RY_TO_GHZ = RY_TO_THZ * 1000.0;
    public static final double RY_TO_CMM1 = 1.E+10 * RY_TO_THZ / C_SI;
}
