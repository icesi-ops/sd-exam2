sudo yum install -y centos-release-gluster
sudo yum install -y glusterfs-server
sudo service glusterd start

sudo sfdisk /dev/sdb << EOF
;
EOF

mkfs.xfs /dev/sdb1
mkdir -p /gluster/data 
sudo mount /dev/sdb1 /gluster/data/

