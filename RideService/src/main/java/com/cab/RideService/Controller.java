package com.cab.RideService;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


@RestController
public class Controller {

    @Autowired
    private RideServiceFunctionality rsf;

    @Autowired
    private CabStatusFunctionality csf;

    final String cabUri = "http://localhost:8080";
    final String walletUri = "http://localhost:8082";
    RestTemplate restTemplate = new RestTemplate();




    @RequestMapping("/")
    public String index(){
        return "Index page";
    }

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
                 CabStatus cab = new CabStatus(cabId, "signed-out", null);
                csf.updateCab(cab);


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


    @RequestMapping("/addCabs")
    public String addCabs(){

        CabStatus cab1 =  new CabStatus(1,"available",0);
        CabStatus cab2 =  new CabStatus(2,"available",0);
        CabStatus cab3 =  new CabStatus(3, "available",0);

        csf.addCab(cab1);
        csf.addCab(cab2);
        csf.addCab(cab3);



        cab1.setCabStatus("not-available");
        csf.updateCab(cab1);

        return "Cabs Added";
    }

    @RequestMapping("/rideEnded")
    public boolean rideEnded(@RequestParam("rideId") int rideId){

        Optional<RideService> ride = rsf.findById(rideId);

        //System.out.println(ride);

        // If rideId not present
        if(!ride.isPresent()){
            return false;
        }

        // If ride already cancelled
        String rideStatus = ride.get().getRideStatus();
        if(!rideStatus.contentEquals("on-going")){
            return false;
        }


        // set ride ended
        ride.get().setRideStatus("ended");
        CabStatus cab = ride.get().getCab();
        cab.setCabStatus("available");
        cab.setCabCurrentLocation(ride.get().getDestLoc());
        csf.updateCab(cab);
        rsf.updateRideService(ride.get());

        return  true;

    }

    @RequestMapping("/cabSignsIn")
    public boolean cabSignsIn(@RequestParam("cabId") int cabId, @RequestParam("initialPos")  int initialPos){

        Optional<CabStatus> cab = csf.findById(cabId);

        // Not a valid cabId
        if(!cab.isPresent()){
            System.out.println("Not a valid cabId");
            return false;

        }

        String cabStatus = cab.get().cabStatus;

        // Already signed In
        if(!cabStatus.contentEquals("signed-out")){
            System.out.println("Already Signed-in ");
            return false;
        }

        cab.get().setCabStatus("available");
        cab.get().setCabCurrentLocation(initialPos);
        csf.updateCab(cab.get());

        return true;
    }

    @RequestMapping("/cabSignsOut")
    public boolean cabSignsOut( @RequestParam("cabId") int cabId){

        // Cab uses this to sign out for the day. Response is true (i.e., the sign-out is accepted)
        // iff cabId is valid and the cab is in available state.
        // set
        Optional<CabStatus> cab = csf.findById(cabId);

        // Not a valid cabId
        if(!cab.isPresent()){
            return false;
        }

        String cabStatus = cab.get().cabStatus;
        if(cabStatus.contentEquals("available")) {
            cab.get().setCabStatus("signed-out");
            csf.updateCab(cab.get());
            return true;
        }

        return false;
    }

    @RequestMapping("/requestRide")
    public int requestRide(@RequestParam("custId") int custId,@RequestParam("sourceLoc") int sourceLoc,@RequestParam("destinationLoc") int destinationLoc){


        RideService ride = new RideService( null, custId, sourceLoc, destinationLoc, "requested");
        System.out.println(ride);
        rsf.addRideService(ride);
        System.out.println("Ok");

        List<CabStatus> cabs = csf.findAllCabs();
        List<CabStatus> orderedCabs = new ArrayList<>(); // Will contains only 3 closest cabs
        List<Integer> orderedDistance = new ArrayList<>();
        for(CabStatus cab: cabs ){

            if(cab.cabStatus.contentEquals("available")){

                Integer distanceBtwCabLocAndSrcLoc= Math.abs(sourceLoc - cab.cabCurrentLocation);
                if(orderedCabs.size() < 3){
                    int i=0;
                    for(; i < orderedDistance.size(); i++){
                        if( distanceBtwCabLocAndSrcLoc < orderedDistance.get(i) ){
                            break;
                        }
                    }
                    orderedDistance.add(i, distanceBtwCabLocAndSrcLoc);
                    orderedCabs.add(i, cab);

                }
                else{

                    int i=0;
                    for(; i < orderedDistance.size(); i++){
                        if( distanceBtwCabLocAndSrcLoc < orderedDistance.get(i) ){
                            break;
                        }
                    }
                    if(i >= 3)
                        continue;
                    orderedCabs.set(i,cab);
                    orderedDistance.set(i, distanceBtwCabLocAndSrcLoc);

                }


            }

        }

        // Now We have atmost 3 cabs for ride request
        for(CabStatus cab : orderedCabs){

            // Send request to cab for accepting
            // Calculate Cost and deduct

            String uri = cabUri + "/requestRide?cabId=" + cab.cabId + "&rideId=" + ride.rideId + "&sourceLoc=" + sourceLoc + "&destinationLoc=" + destinationLoc;
            System.out.println(uri);
            Boolean result = restTemplate.getForObject(uri, Boolean.class);
            //Boolean result=true;
            if(result){

                // After saying yes, Cab is committed
                cab.setCabStatus("committed");
                csf.updateCab(cab);

                // do the deduction and further processing

                int amount = 10 * (Math.abs(sourceLoc - cab.cabCurrentLocation) + Math.abs(sourceLoc - destinationLoc))   ;


                String deductionUri = walletUri + "/deductAmount?custId=" + custId + "&amount=" + amount;
                Boolean flag = restTemplate.getForObject(deductionUri,Boolean.class);
                //boolean flag = true;
                if(flag){
                    uri = cabUri + "/rideStarted?cabId=" + cab.cabId + "&rideId=" +  ride.rideId;
                    Boolean isRideStarted =  restTemplate.getForObject(uri, Boolean.class);
                    //Boolean isRideStarted=true;
                    if(isRideStarted)
                        ride.setRideStatus("on-going");
                        ride.setCab(cab);
                        cab.setRideId(ride.rideId);
                        cab.setCabStatus("giving-ride");
                        rsf.updateRideService(ride);
                        csf.updateCab(cab);
                        return ride.rideId;

                }


            }

        }
        ride.setRideStatus("cancelled");
        rsf.updateRideService(ride);

        return -1;
    }

    @RequestMapping("/getCabStatus")
    public String getCabStatus( @RequestParam("cabId") int cabId){

        // Get all Cabs

        Optional<CabStatus> cab = csf.findById(cabId);
        String tuple;
        if(!cab.isPresent()){
            return "Not a valid Cab Id";
        }
        else if(cab.get().cabStatus.contentEquals("signed-out")){
            return "-1";
        }
        else if(cab.get().cabStatus.contentEquals("giving-ride")){

            Optional<RideService> rideService = rsf.findById(cab.get().rideId);
            RideService ride = rideService.get();
            //return "Cabid" + cab.get().cabId + " CabStatus:" + cab.get().cabStatus + "LastKnownPosition:" + ride.getSourceLoc() +
            // " Customer-ID:" + ride.getCustId() + " Destination:" + ride.getDestLoc();
            return cab.get().cabId + " " + cab.get().cabStatus + " " + ride.getSourceLoc() +
             " " + ride.getCustId() + " " + ride.getDestLoc();
        }
        else{
            return cab.get().cabId + " " + cab.get().cabStatus + " " + cab.get().cabCurrentLocation;
        }
    }

    @RequestMapping("/reset")
    public void reset(){

        // This end-point will be mainly useful during testing.
        // This end-point should send Cab.rideEnded requests to all cabs that are currently in giving-ride state,
        // then send Cab.signOut requests  to all cabs that are currently in sign-in state.
        List<CabStatus> cabs = csf.findAllCabs();

        for(CabStatus cab :  cabs){

            if(cab.cabStatus.contentEquals("giving-ride")){

                String uri = cabUri + "/rideEnded?cabId=" + cab.cabId + "&rideId=" + cab.rideId;
                Boolean result = restTemplate.getForObject(uri, Boolean.class);
                if(!result){
                    throw new RuntimeException("Should not reach here");
                }
                cab.setCabStatus("signed-out");
                csf.updateCab(cab);
            }
            else if(!cab.cabStatus.contentEquals("signed-out")){

                String uri = cabUri + "/signOut?cabId=" + cab.cabId;
                Boolean result = restTemplate.getForObject(uri, Boolean.class);
                if(!result){
                    throw new RuntimeException("Should not reach here");
                }

                // SignOut all cabs in RideService also
                cab.setCabStatus("signed-out");
                csf.updateCab(cab);

            }
        }


    }



}
