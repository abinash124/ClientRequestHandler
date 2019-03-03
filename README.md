# ClientRequestHandler
This is simple approach to solve DDoS.
DDoS is Distributed Denial of Service attack where the hacker tries to make the resources unavailable to the intended users by
making excessive unnecessary request to the server.

Java function that gets client name, request and request time.
If client tries to make excessive request (more than the limit) then client is blocked for that time frame from further making 
more requests/
If client makes more than 50% service request and the request amount in continuos five minute is higher that 10 then client is added
in the black list.
Returns an array of valid request each client can make
