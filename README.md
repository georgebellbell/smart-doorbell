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

### Admin app

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

## Contributors
Dominykas Makarovas, George Bell, Jack Reed, Dale Quinn, Zach Smith.

## License
This project is created and modified by Team 17 (which consists of the people named above), Copyright, 2021, Newcastle University.
You may copy or use these files as part of our assessment and feedback at Newcastle University.
