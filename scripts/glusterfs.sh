yum install -y centos-release-gluster
yum install -y glusterfs-server
service glusterd start

sfdisk /dev/sdb << EOF
;
EOF

mkfs.xfs /dev/sdb1
mkdir -p /gluster/data 
mount /dev/sdb1 /gluster/data/

