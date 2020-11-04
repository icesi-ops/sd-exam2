# Segundo parcial Sistemas Distribuidos

## Integrantes

**German Carvajal **

**Jhan Carlos Diaz Vidal  A00310560 **

**Mateo Matta Lopez A00310540**

## Descripción general

Para desarrollar lo requerido en el parcial el equipo decidió empezar creando el archivo VagrantFile, el cual se va a encargar de crear todas las máquinas necesarias para el funcionamiento de la aplicaicoón

   
```
Vagrant.configure("2") do |config|

  config.ssh.insert_key = false
  config.ssh.forward_agent = true
  config.vm.provision 'shell', inline: 'echo "vagrant:vagrant" | chpasswd'
  config.vm.provision "shell", inline: <<-SHELL
     sed -i 's/PasswordAuthentication no/PasswordAuthentication yes/g' /etc/ssh/sshd_config    
     systemctl restart sshd.service
end

```
El primer bloque de nuestro VagrantFile se utiliza config para la configuración general de todos los nodos, en este caso config se usa para habilitar el servicio SSH para pasar archivos a las diferentes máquinas, lo cual es usado más adelante.


```
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
  
```

En este punto se crean los nodos workers con sus respectivas direcciones IP y se definen elementos bases como la memoria, almacenamiento y los scripts usados para aprovisionar


```
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
```
  
 
