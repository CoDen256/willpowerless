#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

int main(int argc, char** argv) {
    setuid( 0 ); // must have!!!
    printf("Running as %d\n", geteuid());
    system( "/etc/judge/guard.lua" );
    return 0;
}