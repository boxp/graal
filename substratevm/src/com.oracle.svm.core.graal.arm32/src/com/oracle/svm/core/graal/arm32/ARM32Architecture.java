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
import java.util.Set;

import jdk.vm.ci.code.Architecture;
import jdk.vm.ci.code.CPUFeatureName;
import jdk.vm.ci.code.Register;
import jdk.vm.ci.code.Register.RegisterCategory;
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

    /** ARM32 has no runtime-detected CPU features while it uses the LLVM backend. */
    public enum CPUFeature implements CPUFeatureName {
    }

    public ARM32Architecture() {
        super("ARM32", ARM32Kind.DWORD, ByteOrder.LITTLE_ENDIAN, false,
                        ARM32Registers.allRegisters,
                        0, 4, 4);
    }

    @Override
    public PlatformKind getPlatformKind(JavaKind javaKind) {
        return switch (javaKind) {
            case Boolean, Byte -> ARM32Kind.BYTE;
            case Short, Char -> ARM32Kind.WORD;
            case Int, Float -> ARM32Kind.DWORD;
            case Long, Double -> ARM32Kind.QWORD;
            case Object -> ARM32Kind.DWORD;   // 32-bit pointer on ARM32
            default -> ARM32Kind.DWORD;
        };
    }

    @Override
    public Set<CPUFeature> getFeatures() {
        return Set.of();
    }

    @Override
    public boolean canStoreValue(RegisterCategory category, PlatformKind kind) {
        return kind instanceof ARM32Kind;
    }

    @Override
    public PlatformKind getLargestStorableKind(RegisterCategory category) {
        return ARM32Kind.QWORD;
    }

    /**
     * ARM32 platform kinds covering all primitive Java types.
     * These are used by the C type size verifier to match Java types against C struct fields.
     */
    public enum ARM32Kind implements PlatformKind {
        BYTE(1, 'b'),
        WORD(2, 's'),
        DWORD(4, 'i'),
        QWORD(8, 'l');

        private final int sizeInBytes;
        private final char typeChar;
        private final Key key = new EnumKey<>(this);

        ARM32Kind(int sizeInBytes, char typeChar) {
            this.sizeInBytes = sizeInBytes;
            this.typeChar = typeChar;
        }

        @Override
        public Key getKey() {
            return key;
        }

        @Override
        public int getSizeInBytes() {
            return sizeInBytes;
        }

        @Override
        public int getVectorLength() {
            return 1;
        }

        @Override
        public char getTypeChar() {
            return typeChar;
        }
    }
}
