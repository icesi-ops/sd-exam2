# -*- mode: ruby -*-
# vi: set ft=ruby :

# All Vagrant configuration is done below. The "2" in Vagrant.configure
# configures the configuration version (we support older styles for
# backwards compatibility). Please don't change it unless you know what
# you're doing.

firstDisk = './firstDisk.vdi'
secondDisk = './secondDisk.vdi'
thirdDisk = './thirdDisk.vdi'
fourthDisk = './fourthDisk.vdi'
Vagrant.configure("2") do |config|
  config.ssh.insert_key = false

  config.vm.define "master" do |master|
    master.vm.box = "centos/7"
    master.vm.hostname = "master"
    master.vm.network "private_network", ip: "192.168.33.100"
    master.vm.provider "virtualbox" do |vb|
     vb.customize ["modifyvm", :id, "--memory", "512", "--cpus", "1", "--name", "master"]
     unless File.exist?(firstDisk)
      vb.customize ['createhd', '--filename', firstDisk, '--variant', 'Fixed', '--size', 5 * 1024]
    end
    vb.customize ['storageattach', :id,  '--storagectl', 'IDE', '--port', 1, '--device', 0, '--type', 'hdd', '--medium', firstDisk]
    end 
    master.vm.provision "ansible" do |ansible|
      ansible.playbook = "playbooks/confMaster/master.yml"
    end
  end

   config.vm.define "worker-1" do |node1|
     node1.vm.box = "centos/7"
     node1.vm.hostname = "worker-1"
     node1.vm.network "private_network", ip: "192.168.33.11"
     node1.vm.provider "virtualbox" do |vb|
      vb.customize ["modifyvm", :id, "--memory", "512", "--cpus", "1", "--name", "worker-1"]
     unless File.exist?(secondDisk)
        vb.customize ['createhd', '--filename', secondDisk, '--variant', 'Fixed', '--size', 5 * 1024]
      end
     vb.customize ['storageattach', :id,  '--storagectl', 'IDE', '--port', 1, '--device', 0, '--type', 'hdd', '--medium', secondDisk]
     end
     node1.vm.provision "ansible" do |ansible|
      ansible.playbook = "playbooks/confWorkers/worker.yml"
     ansible.groups = {
       "workers" => ["worker-1"]
     }
     end
   end

   config.vm.define "worker-2" do |node2|
     node2.vm.box = "centos/7"
     node2.vm.hostname = "worker-2"
     node2.vm.network "private_network", ip: "192.168.33.12"
     node2.vm.provider "virtualbox" do |vb|
      vb.customize ["modifyvm", :id, "--memory", "512", "--cpus", "1", "--name", "worker-2"]
     unless File.exist?(thirdDisk)
        vb.customize ['createhd', '--filename', thirdDisk, '--variant', 'Fixed', '--size', 5 * 1024]
      end
     vb.customize ['storageattach', :id,  '--storagectl', 'IDE', '--port', 1, '--device', 0, '--type', 'hdd', '--medium', thirdDisk]
     end
     node2.vm.provision "ansible" do |ansible|
      ansible.playbook = "playbooks/confWorkers/worker.yml"
     ansible.groups = {
       "workers" => ["worker-2"]
     }
     end
   end

config.vm.define "worker-3" do |node3|
     node3.vm.box = "centos/7"
     node3.vm.hostname = "worker-3"
     node3.vm.network "private_network", ip: "192.168.33.13"
     node3.vm.provider "virtualbox" do |vb|
      vb.customize ["modifyvm", :id, "--memory", "512", "--cpus", "1", "--name", "worker-3"]
     unless File.exist?(fourthDisk)
        vb.customize ['createhd', '--filename', fourthDisk, '--variant', 'Fixed', '--size', 5 * 1024]
      end
     vb.customize ['storageattach', :id,  '--storagectl', 'IDE', '--port', 1, '--device', 0, '--type', 'hdd', '--medium', fourthDisk]
     end
     node3.vm.provision "ansible" do |ansible|
      ansible.playbook = "playbooks/confWorkers/worker.yml"
     ansible.groups = {
       "workers" => ["worker-3"]
     }
     end
   end


end
