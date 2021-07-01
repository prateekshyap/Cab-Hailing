package com.pp.Project2;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class Cab {

    @Autowired
    CabManager cabManager;

    String rideServiceUri = "http://localhost:8081";
    String walletUri = "http://localhost:8082";
    RestTemplate restTemplate = new RestTemplate();


    @RequestMapping("/inputfile")
    public String inputFile() throws IOException {
        FileReader file = new FileReader("/home/tarun/inputfile");
        List<String> lines = Files.readAllLines(Paths.get("/home/tarun/inputfile"));
        int i=-1;

        for (String line : lines){
            if(line.contentEquals("****")) {
                i = i + 1;
                continue;
            }

            if(i == 0){
                // add cabs
                int cabId = Integer.parseInt(line);
                CabEntity cab = new CabEntity(cabId, "signed-out","Yes");
                cabManager.cabRepository.save(cab);


            }
            else if(i == 1){
                // add customer
            }
            else{
                // add wallet
            }



        }
        return "Done Reading";


    }

    @RequestMapping("/requestRide")
    public boolean requestRide(@RequestParam("cabId") int cabId, @RequestParam("rideId") int rideId,@RequestParam("sourceLoc") int sourceLoc, @RequestParam("destinationLoc") int destinationLoc)
    {
        //get the cab details
        CabEntity cab = cabManager.getCabById(cabId);

        System.out.println("tarun");
        //if no such cab exist
        if (cab == null)
            return false;
        System.out.println("kumar" + cab);
        //if cab is available and interested
        if (cab.getCurrentState().equalsIgnoreCase("available") && cab.getInterestStatus().equalsIgnoreCase("Yes")) {
            cab.setCurrentState("committed"); //change state
            cab.setRideId(rideId); //save rideId
            cab.setInterestStatus("No"); //alter interest status to reject the next request
            cabManager.cabRepository.save(cab);
            return true; //return true
        }

        //if cab is available but not interested
        if (cab.getCurrentState().equalsIgnoreCase("Available") && cab.getInterestStatus().equalsIgnoreCase("No"))
        {
            cab.setInterestStatus("Yes"); //alter interest status to accept the next request
            cabManager.cabRepository.save(cab);
        }


        return false;
    }

    @RequestMapping("/rideStarted")
    public boolean rideStarted(@RequestParam("cabId") int cabId,@RequestParam("rideId")  int rideId)
    {
        //get the cab details
        CabEntity cab = cabManager.getCabById(cabId);

        //if no such cab exist
        if (cab == null)
            return false;

        //if cab is committed due to rideId
        if (cab.getCurrentState().equalsIgnoreCase("committed") && cab.getRideId() == rideId)
        {
            cab.setCurrentState("giving-ride"); //set state
            cab.setTotalRides(cab.getTotalRides()+1); //increment the total number of rides
            cabManager.cabRepository.save(cab);
            return true;
        }

        return false;
    }

    @RequestMapping("/rideCanceled")
    public boolean rideCanceled(@RequestParam("cabId") int cabId,@RequestParam("rideId")  int rideId)
    {
        //get the cab details
        CabEntity cab = cabManager.getCabById(cabId);

        //if no such cab exist
        if (cab == null)
            return false;

        //if cab is committed due to rideId
        if (cab.getCurrentState().equalsIgnoreCase("committed") && cab.getRideId() == rideId)
        {
            cab.setCurrentState("available");
            cabManager.cabRepository.save(cab);
            return true;
        }

        return false;
    }

    @RequestMapping("/rideEnded")
    public boolean rideEnded(@RequestParam("cabId") int cabId,@RequestParam("rideId")  int rideId)
    {
        //get the cab details
        CabEntity cab = cabManager.getCabById(cabId);

        //if no such cab exist
        if (cab == null)
            return false;

        //if cab is giving ride to rideId
        if (cab.getCurrentState().equalsIgnoreCase("giving-ride") && cab.getRideId() == rideId)
        {

            //request RideService.rideEnded()
            String uri = rideServiceUri + "/rideEnded?rideId=" + rideId;
            Boolean result = restTemplate.getForObject(uri, Boolean.class);
            System.out.println(uri);
            if(!result)
                return false;
            cab.setCurrentState("available"); //set state
            cabManager.cabRepository.save(cab);

            return true;
        }

        return false;
    }

    @RequestMapping("/signIn")
    public boolean signIn(@RequestParam("cabId") int cabId,@RequestParam("initialPos")  int initialPos)
    {
        //get the cab details
        CabEntity cab = cabManager.getCabById(cabId);

        //if no such cab exist
        if (cab == null)
            return false;



        //if cab has been signed out
        if (cab.getCurrentState().equalsIgnoreCase("signed-out"))
        {
            //request to RideService.cabSignsIn
            String uri = rideServiceUri + "/cabSignsIn?cabId=" + cabId + "&" + "initialPos="+initialPos ;
            Boolean result = restTemplate.getForObject(uri, Boolean.class);

            if(!result)
                return false;
            cab.setCurrentState("available"); //set state
            cab.setTotalRides(0); //set total number of rides to 0
            cab.setCurrentPos(initialPos); //set initial position as current position
            cabManager.cabRepository.save(cab);
            return true;
        }

        return false;
    }

    @RequestMapping("/signOut")
    public boolean signOut(@RequestParam("cabId") int cabId)
    {
        //get the cab details
        CabEntity cab = cabManager.getCabById(cabId);

        //if no such cab exist
        if (cab == null)
            return false;

        //if cab has been signed in
        if (cab.getCurrentState().equalsIgnoreCase("available"))
        {
            //request to RideService.cabSignsOut
            String uri = rideServiceUri + "/cabSignsOut?cabId=" + cabId;
            Boolean result = restTemplate.getForObject(uri, Boolean.class);
            if(!result)
                return false;
            cab.setCurrentState("signed-out"); //set state
            cabManager.cabRepository.save(cab);
            return true;
        }
        return false;
    }

    @RequestMapping("/numRides")
    public int numRides(@RequestParam("cabId") int cabId)
    {
        //get the cab details
        CabEntity cab = cabManager.getCabById(cabId);

        //if no such cab exist
        if (cab == null)
            return -1;

        if (cab.getCurrentState().equalsIgnoreCase("available") || cab.getCurrentState().equalsIgnoreCase("committed") || cab.getCurrentState().equalsIgnoreCase("giving-ride"))
        {
            return cab.getTotalRides();
        }
        return -1;
    }
}
