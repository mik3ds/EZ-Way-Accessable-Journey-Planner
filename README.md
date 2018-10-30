# FIT5120 EasyWay Android Application

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

