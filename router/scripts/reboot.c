#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <unistd.h>

int main(int argc, char** argv) {
    setuid( 0 );
    printf("Running as %d\n", geteuid());
    system( "reboot" );
    return 0;
}