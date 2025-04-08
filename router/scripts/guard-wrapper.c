#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

int main(int argc, char** argv) {
    printf("Running as %d", geteuid());
    system( "/etc/judge/guard.lua" );
    return 0;
}