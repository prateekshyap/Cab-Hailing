#! /bin/sh
# this test case mainly tests whether the ride gets cancelled or not, when the amount couldn't be deducted. ALso tests for proper assignment of cabs

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

#cab 102 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=102\&initialPos=0)
if [ "$resp" = "true" ];
then
	echo "Cab 102 signed in"
else
	echo "Cab 102 could not sign in"
	testPassed="no"
fi

#cab 103 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=103\&initialPos=0)
if [ "$resp" = "true" ];
then
	echo "Cab 103 signed in"
else
	echo "Cab 103 could not sign in"
	testPassed="no"
fi

#customer 201 requests a ride
rideId=$(curl -s http://localhost:8081/requestRide?custId=201\&sourceLoc=1\&destinationLoc=550)
if [ "$rideId" != "-1" ];
then
	echo "Ride by customer 201 started"
else
	echo "Ride to customer 201 denied"
	testPassed="no"
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

#customer 201 requests a ride again
rideId=$(curl -s http://localhost:8081/requestRide?custId=201\&sourceLoc=550\&destinationLoc=1)
if [ "$rideId" != "-1" ];
then
	echo "Ride by customer 201 started"
	testPassed="no"
else
	echo "Ride to customer 201 denied"
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

#customer 202 ride ended
resp=$(curl -s http://localhost:8080/rideEnded?cabId=103\&rideId="$rideId")
if [ "$resp" = true ]
then
	echo "Ride by customer 202 ended"
else
	echo "Ride by customer 202 couldn't end"
	testPassed="no"
fi

#cab 103 signs out
resp=$(curl -s http://localhost:8080/signOut?cabId=103)
if [ "$resp" = "true" ];
then
	echo "Cab 103 signed out"
else
	echo "Cab 103 could not sign out"
	testPassed="no"
fi

#cab 103 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=103\&initialPos=0)
if [ "$resp" = "true" ];
then
	echo "Cab 103 signed in"
else
	echo "Cab 103 could not sign in"
	testPassed="no"
fi

echo "Test Passing Status: " $testPassed