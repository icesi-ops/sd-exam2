# Segundo parcial Sistemas Distribuidos

## Integrantes

**German Carvajal A00134280**

**Jhan Carlos Diaz Vidal  A00310560**

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

En este punto se crean los nodos workers con sus respectivas direcciones IP y se definen elementos bases como la memoria, almacenamiento, predefiniendo la condición de los volumenes que se van a crear mas adelante con Gluster, y los scripts usados para aprovisionar


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
  
 Al final se crea y aprovisiona el nodo máster en el cual se le define el volumen Gluster que va a tener y el aprovisionamiento a través de Ansible, adicionalmente la instalación de Ansible en el a través de un script
 
 ```
# Install dependencies
sudo yum install -y sshpass
sudo curl "https://bootstrap.pypa.io/get-pip.py" -o "get-pip.py"
sudo python get-pip.py
sudo -H pip install --upgrade pip

# Install Ansible
sudo -H pip install ansible

# Run Ansible playbooks
echo -e "\nRUNNING ANSIBLE [install.yml] **************************************************\n"
ansible-playbook -i hosts install.yml
echo -e "\nRUNNING ANSIBLE [gluster-init.yml] ***************************************************\n"
ansible-playbook -i hosts gluster-init.yml
echo -e "\nRUNNING ANSIBLE [master.yml] ***************************************************\n"
ansible-playbook -i hosts master.yml
echo -e "\nRUNNING ANSIBLE [join.yml] *****************************************************\n"
ansible-playbook -i hosts join.yml
```

Cuando termina la definiciòn de la maquinas, en el master se corre el script Ansible.sh el cual inicialmente instala las dependencias necesarias, las cuales son el servicio sshpass para poder enviar archivos a través de SSH, python pip. Posteriormente se instala Ansible en el master y se corren los playbooks _Install_, _Gluster-init_, _master_ y _join_.

***Install.yml***

```
tasks:
    - name: Run Gluster Install Commands
      shell: bash glusterfs.sh
      become_user: root

    - name: Run Gluster Hosts Commands
      shell: bash glusterconfig.sh
      become_user: root
   
    - name: Install yum utils
      yum:
        name: yum-utils
        state: latest
    - name: Install device-mapper-persistent-data
      yum:
        name: device-mapper-persistent-data
        state: latest
    - name: Install lvm2
            ....
    - name: Add Docker repo
            ....
    - name: Enable Docker Edge repo
            ....
    - name: Enable Docker Test repo
            ....
    - name: Install Docker
            ....
    - name: Start Docker service
            ....
    - name: Add user vagrant to docker group
            ....
```

En este archivo yml, ansible instala a través de scripts Gluster y su configuración, posteriormente instala dependencias necesarias para el manejo de volumenes y el manejo de docker, como _utils_, _lvm2_ y _Docker_

***Gluster-init***

```
hosts: head
  gather_facts: No
  become: true
  tasks:
    - name: Peer probe worker-1
      shell: sudo gluster peer probe worker-1
      become_user: root
    - name: Peer probe worker-2
      shell: sudo gluster peer probe worker-2
      become_user: root
    - name: Peer probe worker-3
      shell: sudo gluster peer probe worker-3
      become_user: root
    - name: Create gluster volume
      shell: sudo gluster volume create swarm-vols replica 4 master:/gluster/data worker-1:/gluster/data worker-2:/gluster/data worker-3:/gluster/data force
      become_user: root
    - name: Allow gluster volume auth
      shell: sudo gluster volume set swarm-vols auth.allow 127.0.0.1
      become_user: root
    - name: Start gluster volume
      shell: sudo gluster volume start swarm-vols
      become_user: root
- hosts: all
  gather_facts: No
  become: true
  tasks:
    - name: Mount gluster volume
      shell: sudo mount.glusterfs localhost:/swarm-vols /swarm/volumes
      become_user: root
  tasks:
    - name: Create swarm-vols directory
      shell: sudo mkdir /swarm/volumes/swarm-vols
      become_user: root
```

Inicialmente en los host tipo head, correspondiente al master, se prueba la conexión entre los demas nodos y posteriormente se crean los volumenes nombrados _swarm-vols_ y finalmente se inicializan a través de la instrucción _start_. Luego en todos los host (Workers y Master) se montan los volumenes gluster.




