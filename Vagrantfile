Vagrant.configure("2") do |config|
    ##MASTER
      config.vm.define "master" do |master|
        master.vm.box = "centos/7"
        master.vm.hostname = "master"
        master.vm.network "private_network", ip: "192.168.33.100"
        master.vm.provider "virtualbox" do |vb|
         vb.customize ["modifyvm", :id, "--memory", "1024", "--cpus", "2", "--name", "master"]
         disco = "./disk.vdi"
         unless File.exist?(disco)
          vb.customize ['createhd', '--filename', disco, '--size', 512]
         end
         vb.customize ['storageattach', :id, '--storagectl', 'IDE', '--port', 1, '--device', 0, '--type', 'hdd', '--medium', disco]
        end
      ##### APROVISIONAMIENTO DEL MASTER##### 
        master.vm.provision "ansible" do |ansible|
         ansible.playbook = "./configuration_managment/ansible/ansible.yml"
         ansible.inventory_path = 'ansible_hosts'  
        end
    end
    ##WORKERS
     config.ssh.insert_key = false
     (1..3).each do |i|
      config.vm.define "worker-#{i}" do |worker|
        worker.vm.box = "centos/7"
        worker.vm.hostname = "worker-#{i}"
        worker.vm.network "private_network", ip: "192.168.33.1#{i}"
        worker.vm.provider "virtualbox" do |vb|
         vb.customize ["modifyvm", :id, "--memory", "512", "--cpus", "2", "--name", "worker-#{i}"]
         disco = "./disk#{i}.vdi"
         unless File.exist?(disco)
          vb.customize ['createhd', '--filename', disco, '--size', 512]
         end
         vb.customize ['storageattach', :id, '--storagectl', 'IDE', '--port', 1, '--device', 0, '--type', 'hdd', '--medium', disco]
        end
       ##### APROVISIONAMIENTO DE LOS SERVIDORES ##### 
        worker.vm.provision "ansible" do |ansible|
         ansible.playbook = "./configuration_managment/ansible/ansible.yml"
         ansible.inventory_path = 'ansible_hosts'  
        end
     end
    end
  end  
