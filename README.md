# FIT5120 EasyWay Android Application

Android Application using Amazon AWS backend.

## Installation Instructions

### Create an Amazon EC2 Virtual Machine instance, allow all network traffic to that machine (http and ssh only for more security).

### Create an Amazon RDS MySql database, only allowing incoming network traffic from the EC2 IP address.


### ssh into the EC2, where awsmachinekeypair.pem is your Amazon primary key pair present in the current working directory, and hostname is the public IP of the EC2 Virtual Machine:
  ssh -i "awsmachinekeypair.pem" hostname
 


### List dependencies and setup::
  sudo upgrade
  sudo apt-get install apache2
  sudo apt-get install mysql
  
  
### Store Server Scripts in /var/www/html on EC2 machine.


### Start Apache:

  $ sudo /etc/init.d/apache2 start
 
 
### Connect to RDS DB and upload records to DB:

   mysql -u"root" -password --local-infile; //Where root is the MySQL user previously set as the admin account
   
   LOAD DATA LOCAL INFILE 'records.csv' INTO TABLE stations FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'; //Where records.csv is the data required to be stored in the server db.
   
   
### Install .apk on Android compatible hardware(SDK min v17) and run.




## Example Server URLs

//20 Closest Toilets Sorted By Distance
http://13.59.24.178/nearbyToilets.php?lat=145.034677&lon=-37.877848

//20 Closest Stations Sorted By Distance
http://13.59.24.178/nearbyStations.php?lat=145.034677&lon=-37.877848

//Returns All Toilets
http://13.59.24.178/SamplePage.php

//Returns All Stations
http://13.59.24.178/allStations.php

//Parent Tracker test
http://13.59.24.178/test.php?name=boronely
