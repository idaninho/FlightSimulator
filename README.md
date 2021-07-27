# FlightSimulator

Java Project in which we needed to build a GUI application for a flight simulator.

The application needs to get data from the user: 
CSV file for a normal flight, CSV file for the flight you want to check-Anomaly Flight, if there are any anomalies in this flight, and a settings file that tells the application how to treat any data from the CSV files uploaded.
the application can also work only with one CSV file and a settings file, but then it will only show the user the data of the flight and transmit the data to the flight simulator.

after all the data has been uploaded to the application, the user can see the flight in the flight simulator- the data is transmitted to the flight simulator.
and also the user can run on the flight a few algorithms, like Z-Score, creating a line from all the data of a specific feature and detect an anomaly, if there is a data that is too far from the line.

all of the data for the algorithms is calculated by the application once the user uploads both the regular flight data and his anomaly flight data, then the user chooses an Algorithm and the application calculates from the regular flight data what the regular elements should be, and will detect in the anomaly flight data if there is an anomaly.


<img width="691" alt="111" src="https://user-images.githubusercontent.com/76865620/127180075-a619d1ab-ddbd-4aa5-8a8f-76085e59beab.png">
<img width="932" alt="222" src="https://user-images.githubusercontent.com/76865620/127180092-4453e051-d6aa-4c32-8c64-c41ca1f61993.png">
