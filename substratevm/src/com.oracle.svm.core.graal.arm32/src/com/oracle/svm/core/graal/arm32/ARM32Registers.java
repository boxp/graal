/*
 * Copyright (c) 2026, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.svm.core.graal.arm32;

import java.util.List;

import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.Register.RegisterCategory;

/** ARM32 register definitions for JDKs without {@code jdk.vm.ci.arm.ARM}. */
public final class ARM32Registers {

    public static final RegisterCategory CPU = new RegisterCategory("CPU");
    public static final RegisterCategory FPU = new RegisterCategory("FPU");

    public static final Register r0 = new Register(0, 0, "r0", CPU);
    public static final Register r1 = new Register(1, 1, "r1", CPU);
    public static final Register r2 = new Register(2, 2, "r2", CPU);
    public static final Register r3 = new Register(3, 3, "r3", CPU);
    public static final Register r4 = new Register(4, 4, "r4", CPU);
    public static final Register r5 = new Register(5, 5, "r5", CPU);
    public static final Register r6 = new Register(6, 6, "r6", CPU);
    public static final Register r7 = new Register(7, 7, "r7", CPU);
    public static final Register r8 = new Register(8, 8, "r8", CPU);
    public static final Register r9 = new Register(9, 9, "r9", CPU);
    public static final Register r10 = new Register(10, 10, "r10", CPU);
    public static final Register r11 = new Register(11, 11, "r11", CPU);
    public static final Register r12 = new Register(12, 12, "r12", CPU);
    public static final Register r13 = new Register(13, 13, "r13", CPU);
    public static final Register r14 = new Register(14, 14, "r14", CPU);
    public static final Register r15 = new Register(15, 15, "r15", CPU);

    public static final Register s0 = new Register(16, 0, "s0", FPU);
    public static final Register s1 = new Register(17, 1, "s1", FPU);
    public static final Register s2 = new Register(18, 2, "s2", FPU);
    public static final Register s3 = new Register(19, 3, "s3", FPU);
    public static final Register s4 = new Register(20, 4, "s4", FPU);
    public static final Register s5 = new Register(21, 5, "s5", FPU);
    public static final Register s6 = new Register(22, 6, "s6", FPU);
    public static final Register s7 = new Register(23, 7, "s7", FPU);
    public static final Register s8 = new Register(24, 8, "s8", FPU);
    public static final Register s9 = new Register(25, 9, "s9", FPU);
    public static final Register s10 = new Register(26, 10, "s10", FPU);
    public static final Register s11 = new Register(27, 11, "s11", FPU);
    public static final Register s12 = new Register(28, 12, "s12", FPU);
    public static final Register s13 = new Register(29, 13, "s13", FPU);
    public static final Register s14 = new Register(30, 14, "s14", FPU);
    public static final Register s15 = new Register(31, 15, "s15", FPU);
    public static final Register s16 = new Register(32, 16, "s16", FPU);
    public static final Register s17 = new Register(33, 17, "s17", FPU);
    public static final Register s18 = new Register(34, 18, "s18", FPU);
    public static final Register s19 = new Register(35, 19, "s19", FPU);
    public static final Register s20 = new Register(36, 20, "s20", FPU);
    public static final Register s21 = new Register(37, 21, "s21", FPU);
    public static final Register s22 = new Register(38, 22, "s22", FPU);
    public static final Register s23 = new Register(39, 23, "s23", FPU);
    public static final Register s24 = new Register(40, 24, "s24", FPU);
    public static final Register s25 = new Register(41, 25, "s25", FPU);
    public static final Register s26 = new Register(42, 26, "s26", FPU);
    public static final Register s27 = new Register(43, 27, "s27", FPU);
    public static final Register s28 = new Register(44, 28, "s28", FPU);
    public static final Register s29 = new Register(45, 29, "s29", FPU);
    public static final Register s30 = new Register(46, 30, "s30", FPU);
    public static final Register s31 = new Register(47, 31, "s31", FPU);

    public static final List<Register> allRegisters = List.of(
                    r0, r1, r2, r3, r4, r5, r6, r7,
                    r8, r9, r10, r11, r12, r13, r14, r15,
                    s0, s1, s2, s3, s4, s5, s6, s7,
                    s8, s9, s10, s11, s12, s13, s14, s15,
                    s16, s17, s18, s19, s20, s21, s22, s23,
                    s24, s25, s26, s27, s28, s29, s30, s31);

    private ARM32Registers() {
    }
}
