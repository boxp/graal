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
 * Please contact Oracle, 500 Oracle Parkway, Redwood, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package com.oracle.svm.core.graal.arm32;

import java.nio.ByteOrder;

import jdk.vm.ci.code.Architecture;
import jdk.vm.ci.code.Register;
import jdk.vm.ci.meta.JavaKind;
import jdk.vm.ci.meta.PlatformKind;

/**
 * ARM32 (ARMv7/EABIHF) architecture metadata for SubstrateVM.
 *
 * This intentionally avoids {@code jdk.vm.ci.arm.ARM}, which is unavailable in the JDK 25
 * labs-openjdk used to build the ARM32 LLVM backend.
 */
public final class ARM32Architecture extends Architecture {

    public static final ARM32Architecture INSTANCE = new ARM32Architecture();

    private static final PlatformKind WORD_KIND = ARM32Kind.DWORD;

    /** ARM32 has no runtime-detected CPU features while it uses the LLVM backend. */
    public enum CPUFeature {
    }

    public ARM32Architecture() {
        super("ARM32", WORD_KIND, ByteOrder.LITTLE_ENDIAN, false,
                        ARM32Registers.allRegisters,
                        0, 4, 4);
    }

    @Override
    public PlatformKind getPlatformKind(JavaKind javaKind) {
        // LLVM performs the concrete type mapping for the ARM32 backend.
        return WORD_KIND;
    }

    /** Minimal 32-bit JVMCI kind used until the JDK exposes an ARM32-specific kind. */
    private enum ARM32Kind implements PlatformKind {
        DWORD;

        private final Key key = new EnumKey<>(this);

        @Override
        public Key getKey() {
            return key;
        }

        @Override
        public int getSizeInBytes() {
            return Integer.BYTES;
        }

        @Override
        public int getVectorLength() {
            return 1;
        }

        @Override
        public char getTypeChar() {
            return 'i';
        }
    }
}
