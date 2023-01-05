import java.util.ArrayList;
import java.util.Random;

public class Vrp1 {

	public static void main(String[] args) {
		int[] demandlist = 
		{
			2, 7, 21, 21, 16,
			15, 16, 6, 17, 11, 17,
			10, 7, 25, 26, 1, 5,
			16, 15, 22, 22, 21, 17,
			1, 16, 25, 21, 11, 25, 7, 22
		};
		
		//Creating the depot
		Customer depot = new Customer();
		depot.x = 50;
		depot.y = 50;
		depot.demand = 0;
		

		int myBirthNumber = 8021994;
		
		Random ran = new Random (myBirthNumber); 
		
		int numberOfCustomers = 31;
		
		//Creating the list with the customers		
		ArrayList <Customer> customers = new ArrayList<>(); // with this code we initialize the new ArrayList, which is called "customers"
		for (int i = 1 ; i <= numberOfCustomers; i++)
		{
			Customer cust = new Customer();
			cust.x = ran.nextInt(100);
			cust.y = ran.nextInt(100); 
			cust.demand = demandlist[i - 1]; 
			customers.add(cust);
		}
	
		//Build the allCustomers array and the corresponding distance matrix
		ArrayList <Customer> allCustomers = new ArrayList<Customer>();


		allCustomers.add(depot);
		for (int i = 0 ; i < customers.size(); i++)
		{
			Customer cust = customers.get(i);
			allCustomers.add(cust);
		}

		for (int i = 0 ; i < allCustomers.size(); i++)
		{
			Customer nd = allCustomers.get(i);
			nd.ID = i;
		}
		for (int x = 0; x < allCustomers.size(); x++){
			System.out.println(allCustomers.get(x).ID);
		}

		
		// This is a 2-D array which will hold the distances between node pairs
		// The [i][j] element of this array is the distance required for moving 
		// from the i-th node of allNodes (node with id : i)
		// to the j-th node of allNodes list (node with id : j)
		double [][] distanceMatrix = new double [allCustomers.size()][allCustomers.size()];
		for (int i = 0 ; i < allCustomers.size(); i++)
		{
			Customer from = allCustomers.get(i);

			for (int j = 0 ; j < allCustomers.size(); j++)
			{
				Customer to = allCustomers.get(j);

				double Delta_x = (from.x - to.x);
				double Delta_y = (from.y - to.y);
				double distance = Math.sqrt((Delta_x * Delta_x) + (Delta_y * Delta_y));

				distance = Math.round(distance);

				distanceMatrix[i][j] = distance;

			}
		}
		
		

		// This is the solution object - It will store the solution as it is iteratively generated
		// The constructor of Solution class will be executed
		Solution s = new Solution();

		int numberOfVehicles = 6;
		int[] vehicleCapacity = 
		{
			67, 99, 97, 89, 92, 97
		};

		ArrayList <Vehicle> vehicles = new ArrayList<Vehicle>();

		for (int id = 0; id < numberOfVehicles; id++){
			Vehicle vehicle = new Vehicle();
			vehicle.ID = id;
			vehicle.capacity = vehicleCapacity[id];
			vehicles.add(vehicle);

		}

		
		//Let rtList be the ArrayList of Vehicles assigned to the solution "s".		
		ArrayList <Route> rtList = s.routes;
		for (int i = 1 ; i <= numberOfVehicles; i++)
		{
			Route routeTemp = new Route();
			routeTemp.load = 0;
			routeTemp.capacity = vehicleCapacity[i-1];
			routeTemp.cost = 0;
			rtList.add(routeTemp);
		}
				
		// indicate that all customers are non-routed
		for (int i = 0 ; i < customers.size(); i++)
		{
			customers.get(i).isRouted = false;
		}
		
		
		int notInserted = numberOfCustomers;

		for (int j = 0; j < numberOfVehicles; j++){
			ArrayList <Customer> nodeSequence = rtList.get(j).customers;
			nodeSequence.add(depot);

			int capacity = rtList.get(j).capacity;
			int load = rtList.get(j).load;

			boolean isFinal = false;

			if (notInserted == 0){
				isFinal = true;
				nodeSequence.add(depot);
			}

			while (isFinal == false)
			{
				int positionOfTheNextOne = -1;

				double bestColorForTheNextOne = Double.MAX_VALUE;
				Customer lastInTheRoute = nodeSequence.get(nodeSequence.size() - 1);

				for (int k = 0; k < customers.size(); k++){
					Customer candidate = customers.get(k);
					if (candidate.isRouted == false){
						double trialCost = distanceMatrix[lastInTheRoute.ID][candidate.ID];
						if (trialCost < bestColorForTheNextOne && candidate.demand <= capacity){
							positionOfTheNextOne = k;
							bestColorForTheNextOne = trialCost;
						}
					}
				}
				if (positionOfTheNextOne != -1)
				{
					Customer insertedNode = customers.get(positionOfTheNextOne);
					nodeSequence.add(insertedNode);

					rtList.get(j).cost = rtList.get(j).cost + bestColorForTheNextOne;
					s.cost = s.cost + bestColorForTheNextOne;
					insertedNode.isRouted = true;
					capacity = capacity - insertedNode.demand;
					rtList.get(j).load = load + insertedNode.demand;
					load = load + insertedNode.demand;
					notInserted = notInserted - 1;
				}
				else
				{
					nodeSequence.add(depot);
					rtList.get(j).cost = rtList.get(j).cost + distanceMatrix[lastInTheRoute.ID][0];
					s.cost = s.cost + distanceMatrix[lastInTheRoute.ID][0];
					isFinal = true;
				}
			}
		}
		for (int j = 0; j < numberOfVehicles; j++) {
			int vehicle_number = j + 1;
			System.out.println("Route for Vehicle #" + vehicle_number);
			for (int k = 0; k < s.routes.get(j).customers.size(); k++) {
				System.out.print(s.routes.get(j).customers.get(k).ID + "  ");
			}
			System.out.println("");
			System.out.println("Route Cost = " + s.routes.get(j).cost);
			System.out.println("Final Load: " + s.routes.get(j).load);
			System.out.println("Final Remaining Capacity = " + (rtList.get(j).capacity - s.routes.get(j).load));
			System.out.println("----------------------------------------");

		}
		System.out.println("Total Solution Cost = " + s.cost);
	}

}

class Customer 
{
	int x;
	int y;
	int ID;
	int demand; // product demand of each customer
	boolean isRouted; // true/false flag indicating if a customer has been inserted in the solution

	Customer() 
	{
	}
}

class Route 
{
	ArrayList <Customer> customers;
	double cost;
	int load; // load of the route (initially = 0)
	int capacity; // capacity variable indicating the capacity of the vehicles
	
	Route() 
	{
		cost = 0;
		load = 0;
		capacity = 50;
		// A new arraylist of nodes is created
		customers = new ArrayList<Customer>();
	}
}

class Vehicle
{
	int ID;
	int capacity;
	ArrayList <Route> vehicleRoute;
	int currentLocation;

	Vehicle(){
		currentLocation = 0;
		vehicleRoute = new ArrayList<Route>();
	}
}


class Solution 
{
	double cost; 
	ArrayList <Route> routes;

	Solution()
	{
		routes = new ArrayList<Route>();
		cost = 0;
	}
}