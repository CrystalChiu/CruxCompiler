    .globl myPrintZero
myPrintZero:
    enter $(16), $0
    /* $t0 = 0 */
    /* CopyInst */
    movq $0, -8(%rbp)
    /* call Symbol(printInt:func(TypeList(int)):void) ($t0) */
    /* CallInst */
    movq -8(%rbp), %rdi
    call printInt
    leave
    ret
    .globl myPrintOne
myPrintOne:
    enter $(16), $0
    movq %rdi, -16(%rbp)
    /* call Symbol(printInt:func(TypeList(int)):void) ($t0) */
    /* CallInst */
    movq -16(%rbp), %rdi
    call printInt
    leave
    ret
    .globl myPrintTwo
myPrintTwo:
    enter $(16), $0
    movq %rdi, -8(%rbp)
    movq %rsi, -16(%rbp)
    /* call Symbol(printInt:func(TypeList(int)):void) ($t0) */
    /* CallInst */
    movq -8(%rbp), %rdi
    call printInt
    /* call Symbol(printInt:func(TypeList(int)):void) ($t1) */
    /* CallInst */
    movq -16(%rbp), %rdi
    call printInt
    leave
    ret
    .globl myPrintThree
myPrintThree:
    enter $(32), $0
    movq %rdi, -8(%rbp)
    movq %rsi, -16(%rbp)
    movq %rdx, -24(%rbp)
    /* call Symbol(printInt:func(TypeList(int)):void) ($t0) */
    /* CallInst */
    movq -8(%rbp), %rdi
    call printInt
    /* call Symbol(printInt:func(TypeList(int)):void) ($t1) */
    /* CallInst */
    movq -16(%rbp), %rdi
    call printInt
    /* call Symbol(printInt:func(TypeList(int)):void) ($t2) */
    /* CallInst */
    movq -24(%rbp), %rdi
    call printInt
    leave
    ret
    .globl main
main:
    enter $(48), $0
    /* call Symbol(myPrintZero:func(TypeList()):void) () */
    /* CallInst */
    call myPrintZero
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* $t0 = 1 */
    /* CopyInst */
    movq $1, -8(%rbp)
    /* call Symbol(myPrintOne:func(TypeList(int)):void) ($t0) */
    /* CallInst */
    movq -8(%rbp), %rdi
    call myPrintOne
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* $t1 = 1 */
    /* CopyInst */
    movq $1, -16(%rbp)
    /* $t2 = 2 */
    /* CopyInst */
    movq $2, -24(%rbp)
    /* call Symbol(myPrintTwo:func(TypeList(int, int)):void) ($t1$t2) */
    /* CallInst */
    movq -16(%rbp), %rdi
    movq -24(%rbp), %rsi
    call myPrintTwo
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    /* $t3 = 1 */
    /* CopyInst */
    movq $1, -32(%rbp)
    /* $t4 = 2 */
    /* CopyInst */
    movq $2, -40(%rbp)
    /* $t5 = 3 */
    /* CopyInst */
    movq $3, -48(%rbp)
    /* call Symbol(myPrintThree:func(TypeList(int, int, int)):void) ($t3$t4$t5) */
    /* CallInst */
    movq -32(%rbp), %rdi
    movq -40(%rbp), %rsi
    movq -48(%rbp), %rdx
    call myPrintThree
    /* call Symbol(println:func(TypeList()):void) () */
    /* CallInst */
    call println
    leave
    ret
