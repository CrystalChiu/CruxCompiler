    .comm addi, 8, 8
    .globl main
main:
    enter $(8 * 4), $0
    call readInt
    movq %rax, -8(%rbp)
    movq addi@GOTPCREL(%rip), %r11
    movq %r11, -16(%rbp)
    movq -8(%rbp), -16(%rbp)
    movq addi@GOTPCREL(%rip), %r11
    movq %r11, -24(%rbp)
    movq -24(%rbp), %r10
    movq 0(%r10), %r11
    movq %r11, -32(%rbp)
    movq -32(%rbp), %rdi
    call printInt
    leave
    ret