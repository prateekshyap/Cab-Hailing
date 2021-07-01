#! /bin/sh
# this test case checks whether a customer's request
# gets rejected if only one cab has signed in but it is busy.

# reset RideService and Wallet.
# every test case should begin with these two steps
curl -s http://localhost:8081/reset
curl -s http://localhost:8082/reset

testPassed="yes"

#cab 101 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=101\&initialPos=0)
if [ "$resp" = "true" ];
then
	echo "Cab 101 signed in"
else
	echo "Cab 101 could not sign in"
	testPassed="no"
fi



#customer 201 requests a ride
rideId=$(curl -s http://localhost:8081/requestRide?custId=201\&sourceLoc=2\&destinationLoc=10)
if [ "$rideId" != "-1" ];
then
	echo "Ride by customer 201 started"
else
	echo "Ride to customer 201 denied"
	testPassed="no"
fi

#customer 202 requests a ride
rideId=$(curl -s http://localhost:8081/requestRide?custId=202\&sourceLoc=1\&destinationLoc=11)
if [ "$rideId" != "-1" ];
then
	echo "Ride by customer 202 started"
	testPassed="no"
else
	echo "Ride to customer 202 denied"
fi

#cab 102 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=102\&initialPos=10)
if [ "$resp" = "true" ];
then
	echo "Cab 102 signed in"
else
	echo "Cab 102 could not sign in"
	testPassed="no"
fi

#customer 202 requests a ride
rideId=$(curl -s http://localhost:8081/requestRide?custId=202\&sourceLoc=1\&destinationLoc=11)
if [ "$rideId" != "-1" ];
then
	echo "Ride by customer 202 started"
	
else
	echo "Ride to customer 202 denied"
	testPassed="no"
fi




#customer 203 requests a ride
rideId=$(curl -s http://localhost:8081/requestRide?custId=203\&sourceLoc=0\&destinationLoc=11)
if [ "$rideId" != "-1" ];
then
	echo "Ride by customer 203 started"
	testPassed="no"
else
	echo "Ride to customer 203 denied"
	
	
fi


#customer 2 requests a ride
rideId=$(curl -s http://localhost:8081/requestRide?custId=2\&sourceLoc=0\&destinationLoc=11)
if [ "$rideId" != "-1" ];
then
	echo "Ride by customer 2 started"
	testPassed="no"	
else
	echo "Ride to customer 2 denied (Not a valid customer)"

	
fi







#cab 102 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=102\&initialPos=10)
if [ "$resp" = "true" ];
then
	echo "Cab 102 signed in"
	testPassed="no"
else
	echo "Cab 102 could not sign in"
	
	
fi

#cab 103 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=103\&initialPos=100)
if [ "$resp" = "true" ];
then
	echo "Cab 103 signed in"
else
	echo "Cab 103 could not sign in"
	testPassed="no"
fi


#cab 104 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=104\&initialPos=50)
if [ "$resp" = "true" ];
then
	echo "Cab 104 signed in"
else
	echo "Cab 104 could not sign in"
	testPassed="no"
fi


#customer 203 requests a ride
rideId=$(curl -s http://localhost:8081/requestRide?custId=203\&sourceLoc=10\&destinationLoc=15000)
if [ "$rideId" != "-1" ];
then
	echo "Ride by customer 203 started"
	testPassed="no"
	
else
	echo "Ride to customer 203 denied"
	
	
fi




echo "Test Passing Status: " $testPassed