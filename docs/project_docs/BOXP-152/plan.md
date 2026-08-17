# BOXP-152 Plan

Remove every `jdk.vm.ci.arm` reference from the Native Image POSIX suite definition and its Java sources.
Confirm the POSIX sources have no such imports, commit the GraalVM change, and update the pinned GraalVM revision in the armv7 build workflow.
