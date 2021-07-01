#! /bin/sh
# fourth cab shouldn't be asked

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

#cab 104 signs in
resp=$(curl -s http://localhost:8080/signIn?cabId=104\&initialPos=0)
if [ "$resp" = "true" ];
then
	echo "Cab 104 signed in"
else
	echo "Cab 104 could not sign in"
	testPassed="no"
fi

#customer 201 requests a ride
rideId1=$(curl -s http://localhost:8081/requestRide?custId=201\&sourceLoc=2\&destinationLoc=10)
if [ "$rideId1" != "-1" ];
then
	echo "Ride by customer 201 started"
else
	echo "Ride to customer 201 denied"
	testPassed="no"
fi

#customer 202 requests a ride
rideId2=$(curl -s http://localhost:8081/requestRide?custId=202\&sourceLoc=3\&destinationLoc=20)
if [ "$rideId2" != "-1" ];
then
	echo "Ride by customer 202 started"
else
	echo "Ride to customer 202 denied"
	testPassed="no"
fi

#customer 203 requests a ride
rideId3=$(curl -s http://localhost:8081/requestRide?custId=203\&sourceLoc=1\&destinationLoc=6)
if [ "$rideId3" != "-1" ];
then
	echo "Ride by customer 203 started"
else
	echo "Ride to customer 203 denied"
	testPassed="no"
fi

#customer 203 requests another ride
rideId4=$(curl -s http://localhost:8081/requestRide?custId=203\&sourceLoc=6\&destinationLoc=15)
if [ "$rideId4" != "-1" ];
then
	echo "Ride by customer 203 started"
else
	echo "Ride to customer 203 denied"
	testPassed="no"
fi

#customer 203 previous ride ended
resp=$(curl -s http://localhost:8080/rideEnded?cabId=103\&rideId="$rideId3")
if [ "$resp" = true ]
then
	echo "Ride by customer 203 ended"
else
	echo "Ride by customer 203 couldn't end"
	testPassed="no"
fi

#customer 201 ride ended
resp=$(curl -s http://localhost:8080/rideEnded?cabId=101\&rideId="$rideId1")
if [ "$resp" = true ]
then
	echo "Ride by customer 201 ended"
else
	echo "Ride by customer 201 couldn't end"
	testPassed="no"
fi

#customer 202 ride ended
resp=$(curl -s http://localhost:8080/rideEnded?cabId=102\&rideId="$rideId2")
if [ "$resp" = true ]
then
	echo "Ride by customer 202 ended"
else
	echo "Ride by customer 202 couldn't end"
	testPassed="no"
fi

#customer 203 ride ended
resp=$(curl -s http://localhost:8080/rideEnded?cabId=104\&rideId="$rideId4")
if [ "$resp" = true ]
then
	echo "Ride by customer 203 ended"
else
	echo "Ride by customer 203 couldn't end"
	testPassed="no"
fi

#customer 201 requests for another ride
rideId1=$(curl -s http://localhost:8081/requestRide?custId=201\&sourceLoc=10\&destinationLoc=2)
if [ "$rideId1" != "-1" ];
then
	echo "Ride by customer 201 started"
	testPassed="no"
else
	echo "Ride to customer 201 denied"
fi

#cab 101 signs out
resp=$(curl -s http://localhost:8080/signOut?cabId=101)
if [ "$resp" = "true" ];
then
	echo "Cab 101 signed out"
else
	echo "Cab 101 could not sign out"
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

#cab 104 signs out
resp=$(curl -s http://localhost:8080/signOut?cabId=104)
if [ "$resp" = "true" ];
then
	echo "Cab 104 signed out"
else
	echo "Cab 104 could not sign out"
	testPassed="no"
fi

#customer 201 requests for ride again
rideId1=$(curl -s http://localhost:8081/requestRide?custId=201\&sourceLoc=10\&destinationLoc=2)
if [ "$rideId1" != "-1" ];
then
	echo "Ride by customer 201 started"
	testPassed="no"
else
	echo "Ride to customer 201 denied"
fi

#customer 201 requests for ride again
rideId1=$(curl -s http://localhost:8081/requestRide?custId=201\&sourceLoc=10\&destinationLoc=2)
if [ "$rideId1" != "-1" ];
then
	echo "Ride by customer 201 started"
else
	echo "Ride to customer 201 denied"
	testPassed="no"
fi

echo "Test Passing Status: " $testPassed