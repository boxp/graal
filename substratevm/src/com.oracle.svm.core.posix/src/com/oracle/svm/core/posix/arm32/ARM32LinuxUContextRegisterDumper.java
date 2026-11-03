/*
 * Copyright (c) 2022, 2022, Oracle and/or its affiliates. All rights reserved.
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
package com.oracle.svm.core.posix.arm32;

import static com.oracle.svm.core.RegisterDumper.dumpReg;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;
import org.graalvm.nativeimage.c.type.WordPointer;
import org.graalvm.word.PointerBase;

import com.oracle.svm.core.RegisterDumper;
import com.oracle.svm.core.posix.UContextRegisterDumper;
import com.oracle.svm.core.posix.headers.Signal;
import com.oracle.svm.core.posix.headers.Signal.ucontext_t;
import com.oracle.svm.guest.staging.log.Log;
import com.oracle.svm.shared.Uninterruptible;
import com.oracle.svm.shared.singletons.AutomaticallyRegisteredImageSingleton;
import com.oracle.svm.shared.singletons.traits.BuiltinTraits.DisallowLayered;
import com.oracle.svm.shared.singletons.traits.BuiltinTraits.RuntimeAccessOnly;
import com.oracle.svm.shared.singletons.traits.BuiltinTraits.SingleLayer;
import com.oracle.svm.shared.singletons.traits.SingletonTraits;

/** The registers r0 to r15 are stored consecutively in {@code struct sigcontext}. */
@AutomaticallyRegisteredImageSingleton(RegisterDumper.class)
@Platforms(Platform.LINUX_ARM32.class)
@SingletonTraits(access = RuntimeAccessOnly.class, layeredCallbacks = SingleLayer.class, other = DisallowLayered.class)
class ARM32LinuxUContextRegisterDumper implements UContextRegisterDumper {
    private static final int HEAP_BASE = 8;
    private static final int THREAD = 9;
    private static final int SP = 13;
    private static final int PC = 15;

    @Override
    public void dumpRegisters(Log log, Signal.ucontext_t uContext, boolean printLocationInfo, boolean allowJavaHeapAccess, boolean allowUnsafeOperations) {
        WordPointer regs = uContext.uc_mcontext_linux_arm32().regs();
        dumpReg(log, "R0  ", regs.read(0).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R1  ", regs.read(1).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R2  ", regs.read(2).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R3  ", regs.read(3).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R4  ", regs.read(4).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R5  ", regs.read(5).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R6  ", regs.read(6).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R7  ", regs.read(7).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R8  ", regs.read(8).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R9  ", regs.read(9).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R10 ", regs.read(10).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R11 ", regs.read(11).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R12 ", regs.read(12).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R13 ", regs.read(13).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R14 ", regs.read(14).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
        dumpReg(log, "R15 ", regs.read(15).rawValue(), printLocationInfo, allowJavaHeapAccess, allowUnsafeOperations);
    }

    @Override
    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    public PointerBase getHeapBase(ucontext_t uContext) {
        return uContext.uc_mcontext_linux_arm32().regs().read(HEAP_BASE);
    }

    @Override
    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    public PointerBase getThreadPointer(ucontext_t uContext) {
        return uContext.uc_mcontext_linux_arm32().regs().read(THREAD);
    }

    @Override
    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    public PointerBase getSP(ucontext_t uContext) {
        return uContext.uc_mcontext_linux_arm32().regs().read(SP);
    }

    @Override
    @Uninterruptible(reason = "Called from uninterruptible code.", mayBeInlined = true)
    public PointerBase getIP(ucontext_t uContext) {
        return uContext.uc_mcontext_linux_arm32().regs().read(PC);
    }
}
