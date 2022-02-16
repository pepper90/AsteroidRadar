IMPORTANT:
To get an API Key from NASA, follow the instructions as listed:

1 - Go to the following URL - https://api.nasa.gov/ and scroll down a little and you are going to see "Generate API Key" title
2 - Fill the required fields, click the Signup button and you will get a API key (the API Key is also going to be sent to your email ).
3 - Copy & paste your API key inside Constants.kt file
4 - Run the app.


# Asteroid Radar

Asteroid Radar is an app to view the asteroids detected by NASA that pass near Earth, you can view all the detected asteroids in a period of time, their data (Size, velocity, distance to Earth) and if they are potentially hazardous.

The app consists of two screens: A Main screen with a list of all the detected asteroids and a Details screen that is going to display the data of that asteroid once it´s selected in the Main screen list. The main screen will also show the NASA image of the day to make the app more striking.

This kind of app is one of the most usual in the real world and it uses the following functionalities:
- fetching data from the internet,
- saving data to a database,
- display the data in a clear, compelling UI.

## Features

The most important dependencies we are using are:
- Retrofit to download the data from the Internet.
- Moshi to convert the JSON data we are downloading to usable data in form of custom classes.
- Glide to download and cache images.
- RecyclerView to display the asteroids in a list.

The components from the Jetpack library:
- ViewModel
- Room
- LiveData
- Data Binding
- Navigation

The current application:
- Includes Main screen with a list of clickable asteroids as seen in the provided design.
- Includes a Details screen that displays the selected asteroid data once it’s clicked in the Main screen.
- Downloads and parses data from the NASA NeoWS (Near Earth Object Web Service) API.
- Saves the selected asteroid data in the database using a button in details screen.
- Once you save an asteroid in the database, you are able to display the list of asteroids from web or the database in the main screen top menu.
- is able to cache the asteroids data by using a worker, so it downloads and saves week asteroids in background when device is charging and wifi is enabled.
- App provides talk back and push button navigation.


## Built With

NASA NeoWS (Near Earth Object Web Service) API, which you can find here.
https://api.nasa.gov/

