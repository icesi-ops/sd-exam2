#!/bin/bash

# Update package list
sudo yum update

# Install dependencies
sudo yum install -y sshpass
curl "https://bootstrap.pypa.io/get-pip.py" -o "get-pip.py"
sudo python get-pip.py
sudo -H pip install --upgrade pip

# Install Ansible
sudo -H pip install ansible

# Run Ansible playbooks
#echo -e "\nRUNNING ANSIBLE [cluster.yml] **************************************************\n"
ansible-playbook -i ansible_hosts install.yml
#echo -e "\nRUNNING ANSIBLE [master.yml] ***************************************************\n"
#ansible-playbook -i hosts master.yml
#echo -e "\nRUNNING ANSIBLE [join.yml] *****************************************************\n"
#ansible-playbook -i hosts join.yml
