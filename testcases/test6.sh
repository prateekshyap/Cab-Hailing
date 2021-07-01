#! /bin/sh
# this test case checks the number of rides by a cab at signed-out, available and giving-ride states

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


#get number of rides
numRides=$(curl -s http://localhost:8080/numRides?cabId=101)
if [ "$numRides" != "0" ];
then
	echo "Invalid number of rides for cab 101"
	testPassed="no"
else
	echo "Correct number of rides for cab 101"
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

#get number of rides
numRides=$(curl -s http://localhost:8080/numRides?cabId=101)
if [ "$numRides" != "1" ];
then
	echo "Invalid number of rides for cab 101"
	testPassed="no"
else
	echo "Correct number of rides for cab 101"
fi

#customer 201 ride ended
resp=$(curl -s http://localhost:8080/rideEnded?cabId=101\&rideId="$rideId")
if [ "$resp" = true ]
then
	echo "Ride by customer 201 ended"
else
	echo "Ride by customer 201 couldn't end"
	testPassed="no"
fi

#get number of rides
numRides=$(curl -s http://localhost:8080/numRides?cabId=101)
if [ "$numRides" != "1" ];
then
	echo "Invalid number of rides for cab 101"
	testPassed="no"
else
	echo "Correct number of rides for cab 101"
fi

#signing out cab 101 
resp=$(curl -s http://localhost:8080/signOut?cabId=101)
if [ "$resp" = "true" ];
then
	echo "Cab 101 signed out"
else
	echo "Cab 101 could not sign out"
	testPassed="no"
fi


#cab 101 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=101\&initialPos=0)
if [ "$resp" = "true" ];
then
	echo "Cab 101 signed in"
else
	echo "Cab 101 could not sign in"
	testPassed="no"
fi


#get number of rides
numRides=$(curl -s http://localhost:8080/numRides?cabId=101)
if [ "$numRides" != "0" ];
then
	echo "Invalid number of rides for cab 101"
	testPassed="no"
else
	echo "Correct number of rides for cab 101"
fi

echo "Test Passing Status: " $testPassed