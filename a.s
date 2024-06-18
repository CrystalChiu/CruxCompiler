    .globl main
main:
    enter $(48), $0
    /* $t0 = true */
    /* CopyInst */
    movq $1, -8(%rbp)
    /* jump $t0 */
    /* JumpInst */
    movq -8(%rbp), %r10
    cmp $1, %r10
    je L1
L2:
    /* $t2 = 0 */
    /* CopyInst */
    movq $0, -16(%rbp)
    /* $t3 = 1 */
    /* CopyInst */
    movq $1, -24(%rbp)
    /* $t4 = $t2 - $t3 */
    /* BinaryOperator */
    movq -24(%rbp), %r10
    subq -16(%rbp), %r10
    movq %r10, -32(%rbp)
    /* call Symbol(printInt:func(TypeList(int)):void) ($t4) */
    /* CallInst */
    movq -32(%rbp), %rdi
    call printInt
    leave
    ret
L1:
    /* $t1 = 1 */
    /* CopyInst */
    movq $1, -40(%rbp)
    /* call Symbol(printInt:func(TypeList(int)):void) ($t1) */
    /* CallInst */
    movq -40(%rbp), %rdi
    call printInt
    jmp L2
    leave
    ret
