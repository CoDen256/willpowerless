#!/usr/bin/env sh

cp -r /etc/rc.button/ /root/backup/rc.button

for f in /etc/rc.button/*; do printf "#!/bin/sh\nNOW=\$(date '+%%F_%%H:%%M:%%S')\necho 'executing $f' - \$NOW> /root/log/buttons.log" > "$f"; done
# TODO potentially failsafe mode is possible -> disable
# TODO still possible to execute them via diagnostics but ok

