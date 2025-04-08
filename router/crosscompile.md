Openwrt Arch

Interpreted files cannot be run as root with setuid, thus a wrapper should be compiled to an executable file that runs the target interpreted file, but inherits the setuid bit

Result of `opkg install file && file /bin/ls`
LF 32-bit LSB executable, MIPS, MIPS32 rel2 version 1 (SYSV), dynamically linked, interpreter /lib/ld-musl-mipsel-sf.so.1, no section header

https://stackoverflow.com/questions/73989368/how-do-i-cross-compile-c-for-raspberry-pi-and-openwrt-from-ubuntu

1. To compile for Open wrt we have to find toolchain to cross compile a c file for MIPS (library) SF MIPSEL (arch) LINUX (os)
2. We use https://github.com/musl-cross/musl-cross/releases
- mips-unknown-linux-muslsf.tar.xz
- mipsel-unknown-linux-muslsf.tar.xz

3. We use mipsel-unknown-linux-muslsf.tar.xz. 
4. On a linux system where there is no problem with disk space and memory:


    wget https://github.com/musl-cross/musl-cross/releases/download/20250206/mipsel-unknown-linux-musl.tar.xz
    sudo mkdir -p /opt/x-tools
    sudo tar -xf mipsel-unknown-linux-musl.tar.xz -C /opt/x-tools
    /opt/x-tools/mipsel-unknown-linux-muslsf/bin/mipsel-unknown-linux-muslsf-gcc -o guard guard-wrapper.c

5. Move to the openwrt the compiled file
    


