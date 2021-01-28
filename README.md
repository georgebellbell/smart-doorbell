# Quick Solutions Smart Doorbell Project - CSC 2033 - Team 17.

Smart doorbell is a custom project that was allocated to us. It utilizes facial detection through a Raspberry Pi and Python
where images are then processed by a FaceSimilarityEngine by OpenImaj to find a match from the images stored in the database.
Result is then displayed to the user via a notification in the Android App.

Nucode clone: https://nucode.ncl.ac.uk/scomp/stage-2/csc2033-software-engineering-team-project/teams/Team-17/smart-doorbell.git

## Installation

### Server 

#### Overview
The server processes all connections from the Raspberry Pi, Android app, and Admin app. The server runs locally and
contains the features such as connecting to the database, facial recognition, 2FA, sending emails, processing faces from
the database to the Android app. All features are evident throughout the server packages and are all processed within
the protocol package.

#### To install
Open the server folder in a seperate IntelliJ window. Open the project in your desired Java IDE. You will need to 
download the dependencies of the project from Maven.

Project Structure > Modules > Add '+' > Select the pom.xml at root level of 'server' directory.

### Admin App

#### Overview
The admin app is the place where the admin can view analytics of the system and database storage. Admins can also view, 
update, delete accounts and doorbells. The admin can also view every doorbell's images stored in a pop up. The admin app
also provides an email client which allows emails to be sent to a specific user, doorbellid, or all users of the system.

#### To install
Open the admin folder in a seperate IntelliJ window. It makes it easier to set up as we have different Maven files for
the server and the admin app.

To set up Maven:<br>
Project Structure > Modules > Add '+' > Select the pom.xml at root level of 'admin' directory.

### Android App

#### Overview
The android app is how the users will directly interact with their doorbell as well as manage their accounts. From the
app users can do the following:
* Users will be able to create and log into existing accounts.
* Add and remove doorbells to those accounts.
* Manage recognised faces on linked doorbells by editing, removing and adding faces.
* Change personal details like email and password as well as deleting account.

#### To install
Open the AndroidApp folder in Android Studio IDE. 

The first thing you will have to do is Sync Project with Gradle Files. 
This can be done in one of three ways:
* File > Sync Project With Gradle Files
* Ctrl + Shift + A > begin typing Sync Project With Gradle Files
* In the top left press the Sync Project With Gradle Files button. <br>
<img src="https://i.imgur.com/jYCZy9F.png" alt="Sync Project With Gradle Files">

After the gradle files have been synced, you will need to set up a device to run the application. You have two options:
* Run the app from an android phone by enabling USB debugging in Developer Settings
* Create an emulator
 
If using an emulator, use Android Studio's AVD Manager to create the emulator by following these steps:

1. Select No Devices and open **AVD Manager**. <br>
<img src="https://i.imgur.com/Ev1OiRc.png" alt="Step 1">
2. Select **+ Create Virtual Device**.
<img src="https://i.imgur.com/oLchcE2.png" alt="Step 2">
3. Select the model of phone to be emulated, we recommend the **Pixel 2**.
<img src="https://i.imgur.com/76r94h9.png" alt="Step 3">
4. Select the System Image **R**, you may need to install it.
<img src="https://i.imgur.com/zAnJXiX.png" alt="Step 4">
5. Then press **finish**. You can change AVD Name if you wish.
<img src="https://i.imgur.com/d8ivAih.png" alt="Step 5">
6. In **Your Virtual Devices** you will see your phone has been created.
<img src="https://i.imgur.com/0aaMG5u.png" alt="Step 6">
7. You can now run the android app via this emulated phone. <br>
<img src="https://i.imgur.com/udrx4Tp.png" alt="Step 7">

## Usage

### Server

Before running the server you need to change the SSH details which can be found in DatabaseConnection.java:
<br>
```
private static final String USER = "universityusername";
private static final String PASSWORD = "universitypassword";
```

Run the Server.java class main to start up the local server which will be using port 4444 on localhost.
```java
public static void main(String[] args) {
    Server server = new Server();
    server.run();
}
```

### Admin App

Requirements to run the app:<br>
Run the Server.java class main before running the Login.java main in UI > Login directory.

```java
public static void main(String[] args) {
    new Login();
}
```
You will have a Window appear as such: <br>
<br>
<img src="https://imgur.com/s5UcmZH.png" alt="Login Window">

Please use the admin details below to login to the application:<br>
Username: admin <br>
Password: password

### Android App

When you are ready to run the app, you need to do the following:
* Run the server
* Navigate to the **Client** class in **app > java > com.example.doorbellandroidapp**.
* In the **Client** class set the HOST equal to the IP address of the device running the server.
```java
public abstract class Client extends Thread {
    // Connection details
    private static final String HOST = "192.0.0.0"; /** <--- CHANGE THIS TO YOU IP ADDRESS*/
    private static final int PORT = 4444;
```

After doing this you can run the app and should be greeted by this page: <br>
<img src="https://i.imgur.com/jgXV0QB.png" alt="IP address"> <br>

We have some existing data in the database under Doorbell ID **00000001** which can be used for trialing the app<br>

Adding the doorbell ID to an account and moving to the faces page will show you this

## Raspberry Pi Doorbell
### Install a fresh Raspberry Pi OS
**IMPORTANT - do not skip**  
To set up the raspberry pi doorbell, you will first need to install the latest version of [Raspberry Pi OS](https://www.raspberrypi.org/software/).
(The university raspberry pi's are very out of date and will not meet the minimum software requirements for the project to run. 
Running a system update will not be sufficient)

The provided link will take you to the installations page, pick the one for your local systems os. Install the os using 
the linked tool onto a micro sd card and insert it into the pi. Connect the pi to power and peripherals and finish the setup.
You need to connect the Raspberry Pi to your network, either through ethernet or Wi-Fi.

### Install Project Dependencies
Once you have installed the latest version of raspberry pi os, you will need to install a few things required for the project
to run that are not installed by default.  
Run the following commands from the terminal:
```
pip3 install opencv-utils
pip3 install opencv-python
sudo apt install libatlas-base-dev
```

### Install the Project from nucode
Once this is done, you need to make a new directory to store the project in and `cd` into that directory.  
e.g. 
```
mkdir SmartDoorbellProject
cd SmartDoorbellProject
```

Next, you need to get the project from gitlab.
```
git clone https://nucode.ncl.ac.uk/scomp/stage-2/csc2033-software-engineering-team-project/teams/Team-17/smart-doorbell.git
```
Move into the camera folder (`cd smart-doorbell/Camera`). From here, you can run the program.

### Set the server IP address
**IMPORTANT**  
Before running the program, you need to make sure that the ip address is correct for your setup. Open the "ServerIP.txt"
file and change it to the ip address of the system that your server is running on. If it is not on the same network, ensure
that port-forwarding is set up for the server.

### Change the device id
**OPTIONAL**  
Change the id in the "PiID.txt" file to give your device a new unique id other than the default. Some example faces have 
been left on the default PiID.

### Run the program on startup
**OPTIONAL**  
To set up the program to run when the device turns on, you can execute "runOnBoot.py". This must be run with sudo.
```
sudo python3 runOnBoot.py
```
You can pass the optional argument true or false to enable or disable the run on boot functionality.
Having the program run on boot and then starting it manually through the commandline can sometimes cause issues.

### Run the program
To run the main program and to see the command line output, use:
```
python3 main.py
```
Make sure that the server is running for the Pi to connect to.


## Contributors
Dominykas Makarovas, George Bell, Jack Reed, Dale Quinn, Zach Smith.

## License
This project is created and modified by Team 17 (which consists of the people named above), Copyright, 2021, Newcastle University.
You may copy or use these files as part of our assessment and feedback at Newcastle University.
