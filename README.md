# Quick Solutions Smart Doorbell Project - CSC 2033 - Team 17.

Smart doorbell is a custom project that was allocated to us. It utilizes facial detection through a Raspberry Pi and Python
where images are then processed by a FaceSimilarityEngine by OpenImaj to find a match from the images stored in the database.
Result is then displayed to the user via a notification in the Android App.

## Installation

###Server 

Nucode clone: https://nucode.ncl.ac.uk/scomp/stage-2/csc2033-software-engineering-team-project/teams/Team-17/smart-doorbell.git

Open the project in your desired Java IDE. You will need to download the dependencies of the project from Maven.

Project Structure > Modules > Add '+' > Select the pom.xml at root level of 'server' directory.

Run the 'Server.java' class main to start up the local server.
```java
public static void main(String[] args) {
		Server server = new Server();
		server.run();
	}
```

## Usage

```Java
import foobar

foobar.pluralize('word') # returns 'words'
foobar.pluralize('goose') # returns 'geese'
foobar.singularize('phenomena') # returns 'phenomenon'
```

## Contributors
Dominykas Makarovas, George Bell, Jack Reed, Dale Quinn, Zach Smith.

## License
This project is created and modified by Team 17 (which consists of the people named above), Copyright, 2020, Newcastle University.
You may copy or use these files as part of our assessment and feedback at Newcastle University.
