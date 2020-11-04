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
  
 Al final se crea y aprovisiona el nodo máster en el cual se le define el volumen Gluster que va a tener (masterDisk.vdi), y se define la manera en la que se va a provisionar, que es a través de Ansible y algunos scripts, y finalmente se instala  Ansible en el nodo a través de un script.
 
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

Cuando termina la definiciòn de las maquinas, en el master se corre el script Ansible.sh el cual inicialmente instala las dependencias necesarias, las cuales son el servicio sshpass, que sirve para poder enviar archivos a través de SSH, python pip. Posteriormente se instala Ansible en el master y se corren los playbooks _Install_, _Gluster-init_, _master_ y _join_.

## Tareas de integración

Para la integración de cada una de las partes del proyecto se usaron Ansible, Docker-Swarm y Docker-Compose

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

***Gluster-init.yml***

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


***master.yml***

```
hosts: head
  become: true
  tasks:
    - name: Obtain IP and Initialize the cluster
      shell: ip=$(hostname -I | awk '{print $2}') && docker swarm init --advertise-addr $ip >> cluster_initialized.txt
      become_user: root
      args:
        creates: cluster_initialized.txt
```

Este PlayBook inicializa el cluster tipo Docker Swarm en el master a través de una instrucción a través del shell


***join.yml***

```
- hosts: head
  become: true
  tasks:
    - name: Get master join command
      shell: docker swarm join-token manager
      register: master_join_command_raw
    - name: Set master join command
      set_fact:
        master_join_command: "{{ master_join_command_raw.stdout_lines[2] }}"
    - name: Get worker join command
      shell: docker swarm join-token worker
      register: worker_join_command_raw
    - name: Set worker join command
      set_fact:
        worker_join_command: "{{ worker_join_command_raw.stdout_lines[2] }}"
- hosts: workers
  become: true
  tasks:
    - name: Workers join cluster
      shell: "{{ hostvars['master'].worker_join_command }} >> node_joined.txt"
      args:
        chdir: $HOME
        creates: node_joined.txt
- hosts: head
  become: true
  tasks:
    - name: Get image from repository
      shell: docker pull german2404/counter:latest
  tasks:
    - name: Run docker-compose with stack
      shell: sudo docker stack deploy -c docker-compose.yml appcounter
```
Este playbook se encarga de configurar la parte final del Docker Swarm, en donde se hace el join a los diferentes Nodos y se corre el Docker-Compose, 

## Aprovisionamiento del Backend

Luego de la instalación de todas las dependencias a través del Ansible, mostrado anteriormente el Docker compose se encarga de aprovisionar el backend, este backend incluye una base de datos y un servidor web, los cuales son replicados.


***docker-compose.yml

```
services:
   redis:
     image: redis
     ports:
       - "6379:6379"
     command: ["redis-server", "--appendonly", "yes"]
     volumes:
      - source: /swarm/volumes
        target: /data
        type: bind
        bind:
          propagation: shared
     deploy:
       mode: replicated
       replicas: 4
       resources:
         limits:
           cpus: '0.10'
           memory: 20M
   web:
     image: german2404/counter
     ports:
       - "8000:8000"
     deploy:
       mode: replicated
       replicas: 4
       resources:
         limits:
           cpus: '0.10'
           memory: 20M
```

Este docker-compose se encarga de desplegar la base de datos tipo Redis usando los volumenes Gluster, posteriormente se agregan 4 replicas web y 4 replicas de redis, adicionalmente se configuran los puertos, adicionalmente se configura el limite de memoria 20Mb y el limite de 10% de CPU.

## Problemas encontrados

Durante el desarrollo del parcial tuvimos errores en todas las etapas, los cuales retrasaron el desarrollo del parcial, el primero de estos fue la configuración de los playbooks relacionados con la configuración de Docker, el principal problema fue realizar correctamente el join.yml el cual tenia errores en algunos comandos y en su sintaxis

Luego de solucionar el problema ocasionado por Ansible tuvimos dificultad con Gluster, en donde logramos de sincronizar los volumenes pero luego de montar la base de datos sobre los volumenes, la base de datos no lograba hacer persistente los datos guardados en ella. Para solucionar esto se tuvo que cambiar el tipo de configuración en el Gluster y el punto de montaje.Este error estaba ocurriendo debido a que el Yml interpreta la palabra "volume" como un volumen de Docker, el tipo de montaje que se implementó se llama bind mount, para permitir la sincronización, la propagación de la información y la persistencia de los datos guardados ahí.
