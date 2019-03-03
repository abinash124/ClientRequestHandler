


import java.util.*;

class Solution {



    class ClientRequest {

        private
        HashMap<Integer,HashSet<String>> blackListClients;
        private int limit;
        private HashSet<String> overFlowClients;
        private HashMap<String, Integer> currentMinuteReq;
        private Queue<HashMap<String, Integer>> requestQueue;
        private HashMap<String ,Integer> fiveMinuteReq;
        private LinkedHashMap<String, Integer> clientTotalRequest;
        private HashMap<String,Integer> firstMinRequest;

        public
        ClientRequest(int limit) {
            this.limit = limit;

            this.currentMinuteReq = new HashMap<>();
            this.fiveMinuteReq = new HashMap<>();
            this.clientTotalRequest = new LinkedHashMap<>();
            this.fiveMinuteReq = new HashMap<>();
            this.overFlowClients = new HashSet<>();
            this.blackListClients = new HashMap<Integer, HashSet<String>>();

        }



        public boolean addOneMinuteRequest(String client, int time){

            //if client is already in overflow list don't allow client to add any further request
            if (isClientBlocked(client) || isClientBlackListed(client, time))
                    return false;

            if(this.currentMinuteReq.get(client) < this.limit )
            {
                this.currentMinuteReq.put(client,this.currentMinuteReq.getOrDefault(client,0)+1);
                this.addRequest(client, time);
                return true;
            }
            //else if the request till so far exceeds limit block the client
            this.blockClient(client);
            return false;
        }
        //after end of every minute add it into five minute list
        //remove client from overflowClient


        public void addfiveMinuteRequest(int time){
           //If first window frame then add client to current minute request
           if(time==5)
           {
               this.firstMinRequest = this.requestQueue.poll();
               for( Map.Entry<String, Integer> cReq : firstMinRequest.entrySet()){
                   String client = cReq.getKey();
                   Integer value = cReq.getValue();
                   this.fiveMinuteReq.put(client,fiveMinuteReq.getOrDefault(client,0)-value);
               }

           }

           //Current request should be added in fiveMinuteReq all the time
           for( Map.Entry<String, Integer> cReq : currentMinuteReq.entrySet()){
               String client = cReq.getKey();
               Integer value = cReq.getValue();
               this.fiveMinuteReq.put(client,fiveMinuteReq.getOrDefault(client,0)+value);
           }



        }

        private void addRequest(String client, int time){

            //If client is allowed because he is neither blacklisted nor blocked then on every request add his request on clientTotalRequest
            if (!this.isClientBlackListed(client,time) && !this.isClientBlocked(client)){
                this.clientTotalRequest.put(client,this.clientTotalRequest.getOrDefault(client,0)+1);
            }
        }




        public HashMap<String, Integer> getFiveMinuteRequest(){
            return this.fiveMinuteReq;
        }


        public LinkedHashMap<String, Integer> getTotalRequest(){
            return this.clientTotalRequest;
        }



        public void setTotalRequest(String client, int requests){
            this.clientTotalRequest.put(client,requests);

        }


       //Allowed or blocked clients checking:


        //Check from the current time to previous two minutes time if the client is black listed or not
        public boolean isClientBlackListed(String client, int time){
            //We start black listing after five minutes
            if (time<5)
                return false;
            //After two minutes the black listed clients won't be in blacklist anymore
            //Check client if he was blacklisted in previous two minute time frame
            for (int timeIndex = time-2; timeIndex<=time; timeIndex++) {
                HashSet<String> clients = this.blackListClients.get(timeIndex);
                if (clients == null)
                    continue;
                if(clients.contains(client))
                    return true;

            }
            return false;


        }


        public  void blackListClient(String client, int time){

            HashSet<String> clients = this.blackListClients.get(time);
            if(clients==null){
                clients = new HashSet<>();
                clients.add(client);
            }
            this.blackListClients.put(time, clients);

        }


        //Unblacklist all the clients from blacklist after two minutes they have been blacklisted
        public void unBlackListClient(Integer time){
            blackListClients.remove(time);
        }


        private void blockClient(String client){

            overFlowClients.add(client);
        }



        private void unblockClient(){
            overFlowClients.clear();
        }



        public boolean isClientBlocked(String client){
            return overFlowClients.contains(client);
        }





    }



    public String[] solution(String[] A, int Y) {

       //A contains client name and request time.
        String [] clientTimeArray = A[0].split("\\s+");
        String currentClient = clientTimeArray[0];
        String previousClient = currentClient;

        int currentReqTime = Integer.parseInt(clientTimeArray[1]);
        ClientRequest request = new ClientRequest(Y);
        int prevReqTime = currentReqTime;
        int fiveMinuteFrame = 0 ;
        int time = 0;
        request.addOneMinuteRequest(currentClient, time);




        for (int i=1;i<A.length; i++) {

            clientTimeArray = A[i].split("\\s+");
            currentClient = clientTimeArray[0];
            currentReqTime = Integer.parseInt(clientTimeArray[1]);

            //if the client has not been changed then calculate requests on the same client


                    //Handling current minute clients
                    //if current time window is same as previous time window add it into oneMinuteRequest
                if(currentReqTime/60 == prevReqTime/60)
                    request.addOneMinuteRequest(currentClient,time);

                else{

                    //Every change in time new blocklist will be created
                    request.unblockClient();
                }


                    //Handling previous 5 minutes clients
                    //increment fiveMinuteTimeFrame
                    //Add client into fiveMinuteRequestLists
                    fiveMinuteFrame += 1;
                    time += 1;
                    //If fiveMinuteFrame is completed reset it by decrementing the time by 1.
                    //Check for blacklist condition.
                    request.addfiveMinuteRequest(fiveMinuteFrame);

                    if (fiveMinuteFrame == 5) {

                        fiveMinuteFrame = 0;
                        HashMap<String, Integer> fivMinuteReq = request.getFiveMinuteRequest();


                        int totalRequestfiveMin = 0;
                        for (Map.Entry<String, Integer> entry : fivMinuteReq.entrySet()) {
                            totalRequestfiveMin += entry.getValue();
                        }

                        //Block clients if over the last 5 minutes if:
                        // 1. client n makes total requests more than 50% of total allowed requests.
                        // 2. total allowed request is more than 10
                        if(totalRequestfiveMin >= 10){
                            for (Map.Entry<String, Integer> entry : fivMinuteReq.entrySet()) {
                                String cl = entry.getKey();


                                if(2*entry.getValue()> totalRequestfiveMin){
                                    request.blackListClient(cl,time);
                                    request.setTotalRequest(cl, totalRequestfiveMin/2);

                                }
                            }

                        }




                    }
                    if(time>6){
                        //Start whitelisting previously blacklisted clients of certain time.
                        request.unBlackListClient(time-2);

                }

            }



        LinkedHashMap<String,Integer> totalReq =   request.getTotalRequest();
        ArrayList<String> allowedClientReq = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : totalReq.entrySet()) {
            String cl = entry.getKey();
            Integer value = entry.getValue();
            allowedClientReq.add(cl+" " + value);

            }

        return allowedClientReq.toArray(new String[allowedClientReq.size()]);

    }
}