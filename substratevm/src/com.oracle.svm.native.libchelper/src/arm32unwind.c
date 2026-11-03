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
 */

/*
 * ARM EHABI support for the LLVM backend of Native Image. The EHABI unwinder calls a personality
 * routine with an _Unwind_State and leaves virtually unwinding the frame to it, and provides
 * several functions used by the generic personality in LLVMExceptionUnwind.java only as macros.
 */
#if defined(__arm__)

#include <stdint.h>
#include <string.h>
#include <unwind.h>

#define SVM_UA_SEARCH_PHASE 1
#define SVM_UA_CLEANUP_PHASE 2
/* The EHABI requires personality routines to keep the UCB address in this virtual register. */
#define UNWIND_POINTER_REG 12

extern int svm_arm32_java_personality(int version, int action, void *thread, _Unwind_Control_Block *ucbp, _Unwind_Context *context);

extern _Unwind_Reason_Code __gnu_unwind_frame(_Unwind_Control_Block *, _Unwind_Context *);

_Unwind_Reason_Code svm_arm32_personality(_Unwind_State state, _Unwind_Control_Block *ucbp, _Unwind_Context *context) {
    void *thread;
    int action;
    int result;

    _Unwind_SetGR(context, UNWIND_POINTER_REG, (_Unwind_Word) (uintptr_t) ucbp);
    memcpy(&thread, ucbp->exception_class, sizeof(thread));

    switch (state & _US_ACTION_MASK) {
        case _US_VIRTUAL_UNWIND_FRAME:
            action = SVM_UA_SEARCH_PHASE;
            break;
        case _US_UNWIND_FRAME_STARTING:
            action = SVM_UA_CLEANUP_PHASE;
            break;
        case _US_UNWIND_FRAME_RESUME:
            /* Resuming after a cleanup landing pad: nothing to do in this frame. */
            action = 0;
            break;
        default:
            return _URC_FAILURE;
    }

    result = action == 0 ? _URC_CONTINUE_UNWIND : svm_arm32_java_personality(1, action, thread, ucbp, context);
    if (result != _URC_CONTINUE_UNWIND) {
        return (_Unwind_Reason_Code) result;
    }
    if (__gnu_unwind_frame(ucbp, context) != _URC_OK) {
        return _URC_FAILURE;
    }
    return _URC_CONTINUE_UNWIND;
}

#undef _Unwind_GetIP
#undef _Unwind_GetIPInfo
#undef _Unwind_SetIP

uintptr_t _Unwind_GetIPInfo(_Unwind_Context *context, int *ip_before_insn) {
    *ip_before_insn = 0;
    return _Unwind_GetGR(context, 15) & ~(_Unwind_Word) 1;
}

uintptr_t _Unwind_GetIP(_Unwind_Context *context) {
    return _Unwind_GetGR(context, 15) & ~(_Unwind_Word) 1;
}

void _Unwind_SetIP(_Unwind_Context *context, uintptr_t ip) {
    _Unwind_SetGR(context, 15, (_Unwind_Word) ip | (_Unwind_GetGR(context, 15) & 1));
}

#endif
