# JavaRMI_RamseySearch

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

