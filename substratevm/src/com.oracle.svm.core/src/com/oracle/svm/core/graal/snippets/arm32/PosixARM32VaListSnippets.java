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
package com.oracle.svm.core.graal.snippets.arm32;

import java.util.Map;

import org.graalvm.word.LocationIdentity;
import org.graalvm.word.Pointer;

import com.oracle.svm.core.SubstrateTarget;
import com.oracle.svm.core.graal.nodes.VaListInitializationNode;
import com.oracle.svm.core.graal.nodes.VaListNextArgNode;
import com.oracle.svm.core.graal.snippets.NodeLoweringProvider;
import com.oracle.svm.core.graal.snippets.SubstrateTemplates;
import com.oracle.svm.core.graal.stackvalue.StackValueNode;
import com.oracle.svm.core.graal.stackvalue.StackValueNode.StackSlotIdentity;
import com.oracle.svm.shared.util.VMError;

import jdk.graal.compiler.api.replacements.Snippet;
import jdk.graal.compiler.core.common.memory.BarrierType;
import jdk.graal.compiler.core.common.memory.MemoryOrderMode;
import jdk.graal.compiler.graph.Node;
import jdk.graal.compiler.nodes.ConstantNode;
import jdk.graal.compiler.nodes.FixedNode;
import jdk.graal.compiler.nodes.FrameState;
import jdk.graal.compiler.nodes.StructuredGraph;
import jdk.graal.compiler.nodes.memory.WriteNode;
import jdk.graal.compiler.nodes.memory.address.OffsetAddressNode;
import jdk.graal.compiler.nodes.spi.LoweringTool;
import jdk.graal.compiler.options.OptionValues;
import jdk.graal.compiler.phases.util.Providers;
import jdk.graal.compiler.replacements.SnippetTemplate;
import jdk.graal.compiler.replacements.SnippetTemplate.Arguments;
import jdk.graal.compiler.replacements.SnippetTemplate.SnippetInfo;
import jdk.graal.compiler.replacements.Snippets;
import jdk.vm.ci.code.BytecodeFrame;

/**
 * Implementation of C {@code va_list} handling for POSIX systems on ARM32 (Linux EABIHF).
 *
 * On ARM32 EABIHF, {@code va_list} is defined as {@code void*}, pointing to the argument
 * save area. Varargs arguments are passed via GPRs (r0-r3) regardless of type (even
 * float/double), then on the stack. The ABI rules:
 * <ul>
 *   <li>int, float: 4-byte slot, 4-byte aligned</li>
 *   <li>long long, double: 8-byte slot, 8-byte aligned (may skip an odd GPR)</li>
 * </ul>
 *
 * References:<br>
 * ARM IHI0042F - Procedure Call Standard for the ARM Architecture (AAPCS)<br>
 */
final class PosixARM32VaListSnippets extends SubstrateTemplates implements Snippets {

    // ARM32 AAPCS varargs: int/float are 4-byte aligned, long/double are 8-byte aligned
    private static final int STACK_AREA_GP_ALIGNMENT = 4;
    private static final int STACK_AREA_DWORD_ALIGNMENT = 8;

    private static final StackSlotIdentity vaListIdentity = new StackSlotIdentity("PosixARM32VaListSnippets.vaListSlotIdentifier", false);

    @Snippet
    protected static double vaArgDoubleSnippet(Pointer vaListPointer) {
        // ARM32 AAPCS: double vararg occupies 8-byte aligned slot in GPR save area
        Pointer vaList = vaListPointer.readWord(0);
        vaListPointer.writeWord(0, vaList.add(STACK_AREA_DWORD_ALIGNMENT));
        return vaList.readDouble(0);
    }

    @Snippet
    protected static float vaArgFloatSnippet(Pointer vaListPointer) {
        // ARM32 AAPCS: float vararg is passed as 4-byte GPR value, NOT promoted to double
        Pointer vaList = vaListPointer.readWord(0);
        int bits = vaList.readInt(0);
        vaListPointer.writeWord(0, vaList.add(STACK_AREA_GP_ALIGNMENT));
        return Float.intBitsToFloat(bits);
    }

    @Snippet
    protected static long vaArgLongSnippet(Pointer vaListPointer) {
        // ARM32 AAPCS: long long vararg occupies 8-byte aligned slot in GPR save area
        Pointer vaList = vaListPointer.readWord(0);
        vaListPointer.writeWord(0, vaList.add(STACK_AREA_DWORD_ALIGNMENT));
        return vaList.readLong(0);
    }

    @Snippet
    protected static int vaArgIntSnippet(Pointer vaListPointer) {
        // ARM32: int vararg is 4-byte, covers all types narrower than int (promoted to int)
        Pointer vaList = vaListPointer.readWord(0);
        int result = vaList.readInt(0);
        vaListPointer.writeWord(0, vaList.add(STACK_AREA_GP_ALIGNMENT));
        return result;
    }

    @SuppressWarnings("unused")
    public static void registerLowerings(OptionValues options, Providers providers, Map<Class<? extends Node>, NodeLoweringProvider<?>> lowerings) {
        new PosixARM32VaListSnippets(options, providers, lowerings);
    }

    private final SnippetInfo vaArgDouble;
    private final SnippetInfo vaArgFloat;
    private final SnippetInfo vaArgLong;
    private final SnippetInfo vaArgInt;

    private PosixARM32VaListSnippets(OptionValues options, Providers providers, Map<Class<? extends Node>, NodeLoweringProvider<?>> lowerings) {
        super(options, providers);

        this.vaArgDouble = snippet(providers, PosixARM32VaListSnippets.class, "vaArgDoubleSnippet");
        this.vaArgFloat = snippet(providers, PosixARM32VaListSnippets.class, "vaArgFloatSnippet");
        this.vaArgLong = snippet(providers, PosixARM32VaListSnippets.class, "vaArgLongSnippet");
        this.vaArgInt = snippet(providers, PosixARM32VaListSnippets.class, "vaArgIntSnippet");

        lowerings.put(VaListInitializationNode.class, new VaListInitializationSnippetsLowering());
        lowerings.put(VaListNextArgNode.class, new VaListSnippetsLowering());
    }

    protected class VaListInitializationSnippetsLowering implements NodeLoweringProvider<VaListInitializationNode> {
        @Override
        public void lower(VaListInitializationNode node, LoweringTool tool) {
            StructuredGraph graph = node.graph();

            StackValueNode stackValueNode = graph.add(StackValueNode.create(SubstrateTarget.getWordSize(), vaListIdentity, true));
            FrameState frameState = new FrameState(BytecodeFrame.UNKNOWN_BCI);
            frameState.invalidateForDeoptimization();
            stackValueNode.setStateAfter(graph.add(frameState));

            OffsetAddressNode address = graph.unique(new OffsetAddressNode(stackValueNode, graph.unique(ConstantNode.forLong(0))));
            WriteNode writeNode = graph.add(new WriteNode(address, LocationIdentity.any(), node.getVaList(), BarrierType.NONE, MemoryOrderMode.PLAIN));

            FixedNode successor = node.next();
            node.replaceAndDelete(stackValueNode);
            stackValueNode.setNext(successor);

            graph.addAfterFixed(stackValueNode, writeNode);
            stackValueNode.lower(tool);
        }
    }

    protected class VaListSnippetsLowering implements NodeLoweringProvider<VaListNextArgNode> {
        @Override
        public void lower(VaListNextArgNode node, LoweringTool tool) {
            SnippetInfo snippet;
            switch (node.getStackKind()) {
                case Double:
                    snippet = vaArgDouble;
                    break;
                case Float:
                    snippet = vaArgFloat;
                    break;
                case Long:
                    snippet = vaArgLong;
                    break;
                case Int:
                    // everything narrower than int is promoted to int when passed in varargs
                    snippet = vaArgInt;
                    break;
                default:
                    // getStackKind() should be at least int
                    throw VMError.shouldNotReachHereUnexpectedInput(node.getStackKind()); // ExcludeFromJacocoGeneratedReport
            }
            Arguments args = new Arguments(snippet, node.graph(), tool.getLoweringStage());
            args.add("vaListPointer", node.getVaList());
            template(tool, node, args).instantiate(tool.getMetaAccess(), node, SnippetTemplate.DEFAULT_REPLACER, args);
        }
    }
}
