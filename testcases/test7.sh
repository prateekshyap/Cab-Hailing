#! /bin/sh
# this test case checks customer balance

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


#get balance
currBalance=$(curl -s http://localhost:8082/getBalance?custId=201)
if [ "$currBalance" != "10000" ];
then
	echo "Invalid balance for customer 201"
	testPassed="no"
else
	echo "Correct balance for customer 201"
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

#get balance
currBalance=$(curl -s http://localhost:8082/getBalance?custId=201)
if [ "$currBalance" != "9900" ];
then
	echo "Invalid balance for customer 201"
	testPassed="no"
else
	echo "Correct balance for customer 201"
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

#get balance
currBalance=$(curl -s http://localhost:8082/getBalance?custId=201)
if [ "$currBalance" != "9900" ];
then
	echo "Invalid balance for customer 201"
	testPassed="no"
else
	echo "Correct balance for customer 201"
fi


echo "Test Passing Status: " $testPassed