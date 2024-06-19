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
    movq -8(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t1) */
    /* CallInst */
    movq -16(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t2) */
    /* CallInst */
    movq -24(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t3) */
    /* CallInst */
    movq -32(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t4) */
    /* CallInst */
    movq -40(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t5) */
    /* CallInst */
    movq -48(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t6) */
    /* CallInst */
    movq -56(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t7) */
    /* CallInst */
    movq -64(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* call Symbol(printInt:func(TypeList(int)):void) ($t8) */
    /* CallInst */
    movq -72(%rbp), %rdi
    call printInt
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    leave
    ret
    .globl main
main:
    enter $(80), $0
    /* $t0 = 1 */
    /* CopyInst */
    movq $1, -8(%rbp)
    /* $t1 = 2 */
    /* CopyInst */
    movq $2, -16(%rbp)
    /* $t2 = 3 */
    /* CopyInst */
    movq $3, -24(%rbp)
    /* $t3 = 4 */
    /* CopyInst */
    movq $4, -32(%rbp)
    /* $t4 = 5 */
    /* CopyInst */
    movq $5, -40(%rbp)
    /* $t5 = 6 */
    /* CopyInst */
    movq $6, -48(%rbp)
    /* $t6 = 7 */
    /* CopyInst */
    movq $7, -56(%rbp)
    /* $t7 = 8 */
    /* CopyInst */
    movq $8, -64(%rbp)
    /* $t8 = 9 */
    /* CopyInst */
    movq $9, -72(%rbp)
    /* call Symbol(paramtest:func(TypeList(int, int, int, int, int, int, int, int, int)):void) ($t0$t1$t2$t3$t4$t5$t6$t7$t8) */
    /* CallInst */
    movq -8(%rbp), %rdi
    movq -16(%rbp), %rsi
    movq -24(%rbp), %rdx
    movq -32(%rbp), %rcx
    movq -40(%rbp), %r8
    movq -48(%rbp), %r9
    movq -72(%rbp), %r10
    pushq %r10
    movq -64(%rbp), %r10
    pushq %r10
    movq -56(%rbp), %r10
    pushq %r10
    call paramtest
    leave
    ret
