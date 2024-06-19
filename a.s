    .globl paramtest
paramtest:
    enter $(80), $0
    movq %rdi, -8(%rbp)
    movq %rsi, -16(%rbp)
    movq %rdx, -24(%rbp)
    movq %rcx, -32(%rbp)
    movq %r8, -40(%rbp)
    movq %r9, -48(%rbp)
    /* call Symbol(printInt:func(TypeList(int)):void) ($t0) */
    /* CallInst */
    args size: 1
    movq -8(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t1) */
    /* CallInst */
    args size: 1
    movq -16(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t2) */
    /* CallInst */
    args size: 1
    movq -24(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t3) */
    /* CallInst */
    args size: 1
    movq -32(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t4) */
    /* CallInst */
    args size: 1
    movq -40(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t5) */
    /* CallInst */
    args size: 1
    movq -48(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t6) */
    /* CallInst */
    args size: 1
    movq -56(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t7) */
    /* CallInst */
    args size: 1
    movq -64(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t8) */
    /* CallInst */
    args size: 1
    movq -72(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    args size: 0
    call println
    leave
    ret
    .globl main
main:
    enter $(80), $0
    /* $t0 = 1 */
    /* CopyInst */
    movq $1, -80(%rbp)
    /* $t1 = 2 */
    /* CopyInst */
    movq $2, -88(%rbp)
    /* $t2 = 3 */
    /* CopyInst */
    movq $3, -96(%rbp)
    /* $t3 = 4 */
    /* CopyInst */
    movq $4, -104(%rbp)
    /* $t4 = 5 */
    /* CopyInst */
    movq $5, -112(%rbp)
    /* $t5 = 6 */
    /* CopyInst */
    movq $6, -120(%rbp)
    /* $t6 = 7 */
    /* CopyInst */
    movq $7, -128(%rbp)
    /* $t7 = 8 */
    /* CopyInst */
    movq $8, -136(%rbp)
    /* $t8 = 9 */
    /* CopyInst */
    movq $9, -144(%rbp)
    /* call Symbol(paramtest:func(TypeList(int, int, int, int, int, int, int, int, int)):void) ($t0$t1$t2$t3$t4$t5$t6$t7$t8) */
    /* CallInst */
    args size: 9
    movq -80(%rbp), %rdi
    movq -88(%rbp), %rsi
    movq -96(%rbp), %rdx
    movq -104(%rbp), %rcx
    movq -112(%rbp), %r8
    movq -120(%rbp), %r9
    movq -144(%rbp), %r10
    movq %r10, 16(%rsp)
    movq -136(%rbp), %r10
    movq %r10, 8(%rsp)
    movq -128(%rbp), %r10
    movq %r10, 0(%rsp)
    call paramtest
    leave
    ret
