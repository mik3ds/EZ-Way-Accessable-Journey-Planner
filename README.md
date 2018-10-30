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
