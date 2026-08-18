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

import java.util.EnumSet;

import org.graalvm.nativeimage.Platform;
import org.graalvm.nativeimage.Platforms;

import com.oracle.svm.core.CPUFeatureAccess;
import com.oracle.svm.shared.Uninterruptible;
import com.oracle.svm.shared.singletons.traits.BuiltinTraits.AllAccess;
import com.oracle.svm.shared.singletons.traits.BuiltinTraits.DisallowLayered;
import com.oracle.svm.shared.singletons.traits.BuiltinTraits.NoLayeredCallbacks;
import com.oracle.svm.shared.singletons.traits.SingletonTraits;

import jdk.graal.compiler.nodes.spi.LoweringProvider;
import jdk.vm.ci.code.Architecture;

/**
 * ARM32 CPUFeatureAccess: ARM32 with LLVM backend has no runtime CPU feature detection.
 */
@Platforms(Platform.ARM32.class)
@SingletonTraits(access = AllAccess.class, layeredCallbacks = NoLayeredCallbacks.class, other = DisallowLayered.class)
public class ARM32CPUFeatureAccess implements CPUFeatureAccess {

    private enum EmptyFeature {
    }

    @Override
    @Uninterruptible(reason = "Thread state not set up yet.")
    public int verifyHostSupportsArchitectureEarly() {
        // ARM32 LLVM backend requires no specific CPU features
        return 0;
    }

    @Override
    @Uninterruptible(reason = "Thread state not set up yet.")
    public void verifyHostSupportsArchitectureEarlyOrExit() {
        // ARM32 LLVM backend requires no specific CPU features
    }

    @Override
    public void enableFeatures(Architecture architecture, LoweringProvider runtimeLowerer) {
        // ARM32 LLVM backend: no runtime CPU features to enable
    }

    @Override
    public EnumSet<?> determineHostCPUFeatures() {
        return EnumSet.noneOf(EmptyFeature.class);
    }

    @Override
    public EnumSet<?> buildtimeCPUFeatures() {
        return EnumSet.noneOf(EmptyFeature.class);
    }
}
