# FIT5120 EasyWay Android Application

Android Application using Amazon AWS backend.

# Installation Instructions

Create an Amazon EC2 Virtual Machine instance, allow all network traffic to that machine (http and ssh only for more security).
Create an Amazon RDS MySql database, only allowing incoming network traffic from the EC2 IP address.

ssh into the EC2, where awsmachinekeypair.pem is your Amazon primary key pair present in the current working directory, and hostname is the public IP of the EC2 Virtual Machine:
  ssh -i "awsmachinekeypair.pem" hostname
 

List dependencies and setup::
  sudo upgrade
  sudo apt-get install apache2
  sudo apt-get install mysql
  
Store Server Scripts in /var/www/html on EC2 machine.

Start Apache:
  $ sudo /etc/init.d/apache2 start
 
Connect to RDS DB and upload records to DB:
   mysql -u"root" -password --local-infile; //Where root is the MySQL user previously set as the admin account
   LOAD DATA LOCAL INFILE 'records.csv' INTO TABLE stations FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'; //Where records.csv is the data required to be stored in the server db.
   
Install .apk on Android compatible hardware(SDK min v17) and run.

