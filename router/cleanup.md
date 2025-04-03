if run out ouf space during installing a package and can't do anything  

    cd /overlay/upper/usr/lib
    rm <package_name> # .so, folders etc
    cd /overlay/upper/opkg/info
    rm <package_name>  # .list, .control files etc
    