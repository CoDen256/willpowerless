#include <stdio.h>
#include <unistd.h>

// LF 32-bit LSB executable, MIPS, MIPS32 rel2 version 1 (SYSV), dynamically linked, interpreter /lib/ld-musl-mipsel-sf.so.1, no section header

// mips-unknown-linux-muslsf.tar.xz
// mipsel-unknown-linux-muslsf.tar.xz


int main(int argc, char** argv) {
    printf("%d", geteuid());
    return 0;
}