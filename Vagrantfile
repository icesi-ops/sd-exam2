# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.

Vagrant.configure("2") do |config|

  config.ssh.insert_key = false
  config.ssh.forward_agent = true
  config.vm.provision 'shell', inline: 'echo "vagrant:vagrant" | chpasswd'
  config.vm.provision "shell", inline: <<-SHELL
     sed -i 's/PasswordAuthentication no/PasswordAuthentication yes/g' /etc/ssh/sshd_config    
     systemctl restart sshd.service
  SHELL
  (1..3).each do |i|
   config.vm.define "worker-#{i}" do |web|
     web.vm.box = "centos/7"
     web.vm.hostname = "worker-#{i}"
     web.vm.network "private_network", ip: "192.168.33.1#{i}"
     web.vm.provision "file", source: "scripts", destination: "$HOME"
     web.vm.provider "virtualbox" do |vb|
      vb.customize ["modifyvm", :id, "--memory", "512", "--cpus", "1", "--name", "worker-#{i}"]
      unless File.exist?("./workerDisk#{i}.vdi")
      vb.customize ['createhd', '--filename', "./workerDisk#{i}.vdi", '--variant', 'Fixed', '--size', 256]
      end
      vb.customize ['storageattach', :id,  '--storagectl', 'IDE', '--port', 1, '--device', 0, '--type', 'hdd', '--medium', "./workerDisk#{i}.vdi"]
     end
   end
  end

   config.vm.define "master" do |master|
     master.vm.box = "centos/7"
     master.vm.hostname = "master"
     master.vm.network "private_network", ip: "192.168.33.10"
     master.vm.provider "virtualbox" do |vb|
      vb.customize ["modifyvm", :id, "--memory", "512", "--cpus", "1", "--name", "master"]
      unless File.exist?('./masterDisk.vdi')
      vb.customize ['createhd', '--filename', './masterDisk.vdi', '--variant', 'Fixed', '--size', 256]
      end
      vb.customize ['storageattach', :id,  '--storagectl', 'IDE', '--port', 1, '--device', 0, '--type', 'hdd', '--medium', './masterDisk.vdi']
     end
     # Provision Ansible playbook
     master.vm.provision "file", source: "Ansible", destination: "$HOME"
     master.vm.provision "file", source: "scripts", destination: "$HOME"
     # Install Ansible and configure nodes
     master.vm.provision "shell", path: "scripts/ansible.sh"

   end

 
end
