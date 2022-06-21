# Cab-Hailing
A cab hailing system developed using Springboot and Docker. Microservices concept is used.

# Found any issues? Want to do some improvements?
Fork this repository, make changes and create a pull request.

# Steps to run
1. Compile and Build all three services.
2. For Cab: ````docker run --publish 8080:8080 <image_name>````
3. For RideServices: ````docker run --publish 8081:8081 <image_name>````
4. For Wallet: ````docker run --publish 8082:8082 <image_name>````