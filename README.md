FileDrop
=======

A web service for file management using distributed file system on backend.
To quickly get started, refer to Quick Start


Build
-----

	$ git clone https://github.com/suen/Filedrop.git
	$ cd Filedrop

The project uses maven to resolve dependency. Build the project with

	$ mvn package


The project has two components, namenode and datanode, that should be run as
separate processes either on same machine or different machines. If running on
same machine, make sure the nodes are running from two different path. This is
to avoid overwritting of data directory.


Datanode:

For any number of datanodes that you wish to run : 

- clone the repository 

	$ git clone https://github.com/suen/Filedrop.git
	$ cd Filedrop
	
- compile the project with maven
	
	$ mvn package

- run the main class of DataNode

	$ mvn exec:java -Dexec.mainClass="com.filedrop.dfs.datanode.DataNodeServer" -Dexec.args="IP PORT"

	replace "IP" and "PORT" with IP of your network interface and an available port. eg. "127.0.0.1 8000"


Namenode

The config/namenode.conf is the configuration file used by namenode. The namenode
uses postgresql to store metadata. Modify the configuration file with your postgresql
credential. You can also modify the parameter REPLICATION_FACTOR, which determines
the number of times files should be replicated. 

The configuration files in config/datanodes/ directory has .conf files that define
datanodes to be taken into account by namenode. For eg, for datanode above, create
or modify a file (let's say "datanode1.conf") with following values

 	$ vim config/datanodes/datanode1.conf

		#content of datanode1.conf

		name = firstdatanode
		ip = 127.0.0.1 
		port = 8000
		space = 10000000000
	
	"name" is the unique name for this datanode. "space" is the disk space in byte
	allocated in this datanode for file storage. (The "space" parameter should have
	been defined at datanode level, we will likely change it in future)

To add a new datanode to the system, simply clone the repository in a new location
and define a .conf file in config/datanodes/ directory for it be recognized by
namenode


To run namenode, simply run
	
	$ mvn exec:java -Dexec.mainClass="com.filedrop.dfs.namenode.NameNodeHTTPServer" 

The namenode is running and the web interface is accessible at http://IP:PORT, where
IP, PORT are the values defined in config/namenode.conf for namenode. It is "localhost:8080"
by default


Quick Start
-----------

Make sure you have python installed in your system, as there is a small python
script used to transfer files between nodes. More specifically, 

Install python and "requests" library

	# For debian-based Linux distro only
	# please adapt the following commands if you are using a different OS

	$ sudo apt-get install python python-pip

	$ sudo pip install requests 

	# You can always modify the script "clientpy.py" to your liking or replace it
	# with something equivalent. Just make sure they take a URL and filepath as 
	# runtime parameters and uploads the given file to the specified URL. (This script
	# will be replaced by a Java class in future, it's just a quick solution to
	# do multi-part file upload.)

select a directory where you would like to clone the repo

	$ cd /tmp/

clone the repository

	$ git clone https://github.com/suen/Filedrop.git

build the project

	$ mvn package

copy paste the repo, 3 times. We will be creating three datanodes and a namenode

	$ cp -r FileDrop datanode1
	$ cp -r FileDrop datanode2
	$ cp -r FileDrop datanode3
	$ mv FileDrop namenode 

For every datanode (i.e. 3 nodes), run a different terminal ( Ctrl+Shift+N if you
use gnome-terminal). Let's choose ports 8000, 8001, 8002 as the ports for our 
three datanodes

For first datanode run:

	$ cd datanode1

	$ mvn exec:java -Dexec.mainClass="com.filedrop.dfs.datanode.DataNodeServer" -Dexec.args="127.0.0.1 8000"

For second datanode run:

	$ cd datanode2

	$ mvn exec:java -Dexec.mainClass="com.filedrop.dfs.datanode.DataNodeServer" -Dexec.args="127.0.0.1 8001"

and do the same for the third

Now, we will configure Namenode. Since we have three datanodes running, we will need
three datanode config files in config/datanodes/ directory for namenode to recognize them.

Start a new terminal for namenode

	$ cd namenode

	$ rm config/datanodes/*.conf # optional step

Create first datanode config file as:

	$ echo "\
	# datanode1 config
	name = datanode1
	ip = 127.0.0.1
	port = 8000
	space = 10000000000 
	" > config/datanode/datanode1.conf
	
Do same for second datanode as :

	$ echo "\
	# datanode2 config
	name = datanode2
	ip = 127.0.0.1
	port = 8001
	space = 10000000000 
	" > config/datanode/datanode2.conf
	
and similarly for the third. 
(You can also copy paste first datanode config file and simply change the value of the parameters)


Edit config/namenode.conf with the login credential for postgresql

Run the name node as:

	$ mvn exec:java -Dexec.mainClass="com.filedrop.dfs.namenode.NameNodeHTTPServer" 

The web interface should be available at http://localhost:8080/

And the admin interface should be available at http://localhost:8080/admin.html

