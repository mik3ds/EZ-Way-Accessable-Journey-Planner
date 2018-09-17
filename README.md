# FIT5120 EasyWay Android Application

## Installation Instructions

### Create and set up a Web Server using Ubuntu Linux 14.05.5 and Apache


For the demo we have created an Amazon EC2 Virtual Machine instance. SSH into the machine (  ssh -i "awsmachinekeypair.pem" hostname  ). When connected, run:


  sudo upgrade
  
  sudo apt-get install apache2
  
  sudo apt-get install mysql
  

Only allow incoming traffic to the web server through ssh http connections.
Copy the Web Server PHP scripts to the public web server directory. (Default Apache2 location is /var/www/html)


### Create and set up a remote MySQL Database


For the demo we have created an Amazon RDS MySql database.
Only allow incoming network traffic from the Web Server public IP address.


SSH into the Web Server.
Connect to MySQL database.


   mysql -u"root" -h -password --local-infile; //Where root is the MySQL user previously set as the admin account
   

Run MySQL create table scripts.


Insert Data Sets into 'stations' and 'toilets' databases, where "records.csv" is the Data Set to be uploaded.

   
   LOAD DATA LOCAL INFILE 'records.csv' INTO TABLE stations FIELDS TERMINATED BY ',' LINES TERMINATED BY '\n'; //Where records.csv is the data required to be stored in the server db.


### Start Apache:

  $ sudo /etc/init.d/apache2 start
 
   
### Install .apk on Android compatible hardware(SDK min v17) and run.


## Basic Functions

### Map
Map that shows user location, nearby Public Transport options, and other points of interest like accessable toilets and myki topup machines. 

### Parent Tracking
#### Setup
To enable Parent Tracking on the child device, a user navigates to Profile -> Edit Profile, where they can enter a name and select Enable Tracking. After selecting Enable Tracking and pressing save, the application will give a pairing code.
A user on another device can then navigate to Track Child Settings. Once there, they can enter in the previous user's name and connection code and select save. 
#### Usage
Once Parent Tracking is enabled, the Child device will update it's GPS coordinates to the server every 15 seconds. 
The Parent device can then navigate to the Map Screen, which opens centered on a marker that displays the linked child's current location.

## Web Server PHP Scripts


##### getStationByID.php

-post station name


-returns station details


##### linkParent.php

-post child name, parent device id, pairing code

-returns child record if linking between parent and child is successful



##### stopTracking.php

-post deviceid

-deletes user tracking details



##### updateLocation.php

-post deviceid, current user lat and lon

-script generates date time stamp

-updates database with tracking information

-returns user tracking details



##### getToiletByID.php

-post toiletid

-returns toilet details



##### trackerSignUp.php

-post child name, deviceid, current lat and lon

-script generates date time stamp



##### nearbyStations.php

-post user current lat and lon

-return closest 20 train stations sorted by distance



##### nearbyToilets.php

-post user current lat and lon

-return closest 20 accessable public toilets sorted by distance



##### trackingStatusChild.php

-post childid

-returns user tracking details if they exist



##### getChildLocation.php

-post parentid

-returns child name, lat, lon, date and time of last update if the parentid is linked to a child record.



##### trackingStatusParent.php

-post parentid

-returns all child tracking details if the parentid is linked to a child record



##### allToilets.php

-returns all toilet records



##### allStations.php

-returns all station records

