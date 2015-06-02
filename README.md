# JavaRMI_RamseySearch

***********
To run space:

ant -Darg0={numLocalThreads} runSpace

To run computer:

ant -Darg0={remote_space_host_ip_address} -Darg1={desiredPrefetchBufferSize} -Darg2={desiredNumThreads} -Darg3={isLongRunning} runComputer

To run Graph Store for Ramsey:

ant runRamseyStore

To run TSP Client:

ant -Darg0={remote_space_host_ip_address} -Darg1={12 or 16 cities} -Darg2={true or false branchAndBound} runTspClient

To run Ramsey Client:

ant -Darg0={remote_space_host_ip_address} -Darg1={graph_store_host_ip_address} runTspClient


***********

1. Download Code

2. Compile it

3. Run the space

4. Run A computer configured to have long longevity ComputerNode [IP_OF_SPACE]-1 -1 true

5. Run A computer configured to have short longevity ComputerNode [IP_OF_SPACE]-1 -1 false (show how we can support different types of clients)

6. Run a tsp client (maybe a smaller size like 13) [IP_OF_SPACE] 13(show fork join still works)

7. Do not shut space down

8. Start Ramsey Store

9. Start Ramsey Client targeting ramsey store and space (show same space can have multiple schedulers without restarting)
Let it run and show what the store is getting and what client is getting (show it all works, and that scheduling is based on specs)

10. Kill one of the computers (show reliability)

11. Kill Client (store will still be recieving data)

12. Kill the everything
Start Space, Store, Computers, RamseyClient (show that checkpointing works)
Profit?

