# user setup

mkdir /home/diagnostics

# or verify that the /etc/passwd contains /bin/ash as the last column
useradd -r -s /bin/ash -d /home/diagnostics diagnostics
echo -e "abc\nabc" | passwd "diagnostics"

chmod u-s /bin/busybox # DISABLE setuid BIT. IF diagnostics runs /bin/busybox, then everything is done via effective euid(0)==root

# shell and all tools like ls cat etc, are actually run effectively by root with this bit
# hopefully it won't crash any other service

# or verify thath the /etc/group contains
cat /etc/passwd
cat /etc/group
cat /etc/shadow