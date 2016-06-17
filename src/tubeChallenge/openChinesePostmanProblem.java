package tubeChallenge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import tubeChallenge.DijkstraAlgrithm.Path;
import tubeChallenge.UndirectedTubeMap.Edges;
import tubeChallenge.UndirectedTubeMap.NodeClass;

public class openChinesePostmanProblem {
	private static final int ADDITIONALCOST = 700;
	static int shortestDistance = 0;
	public static void main(String args[]){
		//input map and create the virtual infinite edge 
		UndirectedTubeMap tubeMap = new UndirectedTubeMap();
//		testcase1(tubeMap);
//		testcase2(tubeMap);
		tubemapBuild(tubeMap);
		
		//minimum distance, subtract the distance caused by virtual nodes
		int minDistance= tubeMap.getMinDistance()-ADDITIONALCOST;
		
		//find odd degree nodes
		ArrayList<NodeClass> oddNodes = findOddnodes(tubeMap);
		
		//apply Dijkstra algorithm and find minimum collection of pairs 
		long start = System.currentTimeMillis();
//		ArrayList<Pair> minMatchPaired = minWeightMatchingGreedyAlgorithm(oddNodes);  //should be 50
		ArrayList<Pair> minMatchPaired = minWeightMatching(oddNodes);  //should be 40
		long end = System.currentTimeMillis();
		System.out.println("Execution time: " + (end - start) + " ms");
		System.out.println("\n###########################################\n");

		System.out.println("Duplicated routes:" + minMatchPaired.size() + " routes");
		for(Pair temp_pair : minMatchPaired){
			System.out.println("from " + temp_pair.node1.id + " to " + temp_pair.node2.id + " length: " + temp_pair.path.distance);
			System.out.printf("with path: ");
			temp_pair.path.printPath();
		}
		System.out.println("\n###########################################\n");
		int duplicatedDistance = getDistance(minMatchPaired);
		shortestDistance -= ADDITIONALCOST;
		
		//print route N.B. need to decide where to start carefully 
		printRoute(tubeMap.findStation("Amersham"),tubeMap.findStation("Epping"),tubeMap,minMatchPaired);
		
		//this method for test without virtual node
//		printRouteTest(tubeMap.findStation("1"), tubeMap, minMatchPaired);

		System.out.println("\n###########################################\n");
		
		System.out.println("Number of edges left: " + tubeMap.getEdges().size());
		System.out.println("Number of duplicated routes left: " + minMatchPaired.size());
		System.out.println("Finish");
		System.out.println("sum of edge length: " + minDistance+ " mins");
		System.out.println("sum of duplicated edge length: " + duplicatedDistance+ " mins");
		System.out.printf("the shortest route cost: " + shortestDistance + " mins  ");
		int hours = shortestDistance/60;
		int mins = (shortestDistance - hours*60) * 60 / 100;
		System.out.println( hours + "hours" + mins + " mins");


		
		System.out.println("\n###########################################\n");

	}
	
	/****************************************Utility functions************************************************/
	static ArrayList<NodeClass> findOddnodes( UndirectedTubeMap tubeMap){
		ArrayList<NodeClass> oddNodes = new ArrayList<NodeClass>();
		for(NodeClass temp_node : tubeMap.getNodes()){
			int neighborNodeNum = 0;
			for(Map.Entry<NodeClass, ArrayList<Edges>> temp :  temp_node.neighborNodes.entrySet()){
				neighborNodeNum += temp.getValue().size();
			}
			if(neighborNodeNum % 2 != 0){
				oddNodes.add(temp_node);
			}	
		}
				
		//apply Dijkstra Algorithm for all oddNodes
		//only pair the later part in the list
		for(int i=0; i< oddNodes.size(); i++){
			//traverse the rest nodes
			for(int j=i+1; j< oddNodes.size(); j++){
				Path shortestPath = DijkstraAlgrithm.dijkstraRoute(oddNodes.get(i),oddNodes.get(j),tubeMap);
				oddNodes.get(i).otherOddNodeDistance.put(oddNodes.get(j), shortestPath);					
			}
		}
		return oddNodes;
	}
	
	
	//complexity N^2, start from the pair with minimum distance
	static ArrayList<Pair> minWeightMatchingGreedyAlgorithm(ArrayList<NodeClass> oddNodes){
		NodeClass nodeToPair = null;
		NodeClass nodePaired= null;
//		Path path_pair= null;
		ArrayList<Pair> pairList = new ArrayList<Pair>();
		
		//construct a list with all possible pairs
		for(NodeClass temp_node : oddNodes){
			for(Map.Entry<NodeClass, Path> entry_node_distance : temp_node.otherOddNodeDistance.entrySet()){
				pairList.add(new Pair(temp_node,entry_node_distance.getKey(),entry_node_distance.getValue()));
			}
		}

		//sort the pairList from short distance to long
		Collections.sort(pairList, new Comparator<Pair>(
				) {
					@Override
					public int compare(Pair p1, Pair p2) {
						return (p1.path.distance < p2.path.distance) ? -1 : (p1.path.distance > p2.path.distance) ? 1 : 0 ;
					}
		});
		
		
		ArrayList<Pair> min_pairList = new ArrayList<Pair>();
		int minPairDistanceTotal = Integer.MAX_VALUE;
		
		//let any nodes have a chance to be included in the first pair chosen
		for(NodeClass temp_node : oddNodes){
			Pair pairToAdd = null;
			ArrayList<Pair> pairListCopy = new ArrayList<Pair>(pairList);
			for(Pair temp_pair : pairListCopy){
				if(temp_pair.contains(temp_node)){
					pairToAdd = temp_pair; break;
				}
			}
			ArrayList<NodeClass> oddNodesCopy = new ArrayList<NodeClass>(oddNodes);
			ArrayList<Pair> min_pairListCopy = new ArrayList<Pair>();
			ArrayList<Pair> pairListToRemove = new ArrayList<Pair>();
			int minPairDistanceTotal_temp = 0;
			while(oddNodesCopy.size()!=0){
				if(oddNodesCopy.size() != oddNodes.size()){
					pairToAdd = pairListCopy.get(0);
				}
				min_pairListCopy.add(pairToAdd);
				minPairDistanceTotal_temp+= pairToAdd.path.distance;
				nodeToPair = pairToAdd.node1;
				nodePaired = pairToAdd.node2;
				for(Pair temp_pair : pairListCopy){
					if(temp_pair.contains(nodeToPair) || temp_pair.contains(nodePaired)){
						pairListToRemove.add(temp_pair);
					}
				}
				pairListCopy.removeAll(pairListToRemove);
				oddNodesCopy.remove(nodePaired);
				oddNodesCopy.remove(nodeToPair);
			}		
			if(minPairDistanceTotal_temp<minPairDistanceTotal){
				minPairDistanceTotal = minPairDistanceTotal_temp;
				min_pairList.clear();
				min_pairList = min_pairListCopy;
			}
		}
		return min_pairList;
	}
	
	//return the total distance of paired oddnodes
	static ArrayList<Pair> minWeightMatching(ArrayList<NodeClass> oddNodes){
		int minDistance = Integer.MAX_VALUE;
		NodeClass nodeToPair = oddNodes.get(0);
		NodeClass nodePaired= null;
		NodeClass nodePaired_selected = null;
		Path path_pair= null;
		ArrayList<Pair> temp_pairList = null;
		ArrayList<Pair> min_pairList = null;
		
		//when only last two nodes left
		if(oddNodes.size() == 2){
			nodePaired = oddNodes.get(1);
			ArrayList<Pair> pairList = new ArrayList<Pair>();
			pairList.add(new Pair(nodeToPair, nodePaired, nodeToPair.otherOddNodeDistance.get(nodePaired)));
			return pairList;
		}
			
		//principal: only to pair the later part in the list
		oddNodes.remove(nodeToPair);
		
		for(Map.Entry<NodeClass, Path> entry_node_distance : nodeToPair.otherOddNodeDistance.entrySet()){
			//if the node is paired
			if(!oddNodes.contains(entry_node_distance.getKey())){
				continue;
			}
			nodePaired = entry_node_distance.getKey();
			//remove the nodePaired temporarily 
			ArrayList<NodeClass> temp_nodeList = new ArrayList<NodeClass>(oddNodes);
			temp_nodeList.remove(nodePaired);
			
			//this is where recursion happens
			temp_pairList = minWeightMatching(temp_nodeList);
			int temp_distance = entry_node_distance.getValue().distance + getDistance(temp_pairList);
			
			//recover nodePaired back
			if( temp_distance< minDistance){
				minDistance = temp_distance;
				nodePaired_selected = entry_node_distance.getKey();
				path_pair = entry_node_distance.getValue();
				min_pairList = new ArrayList<Pair>(temp_pairList);
			}
		}

		//add this confirmed pair to the pairlist
		min_pairList.add(new Pair(nodeToPair, nodePaired_selected, path_pair));
		
		//where recurse back, so need to keep the same state as in last level
		oddNodes.add(nodeToPair);
		return min_pairList;
	}
	
	
	//if the input node is the start (or the end) of the duplicated edge
	static Pair duplicatedRouteContains(NodeClass node, ArrayList<Pair> minMatchPaired){
		for(Pair temp_pair :minMatchPaired){
			if (temp_pair.contains(node)) {
				return temp_pair;
			}
		}
		return null;
	}
	
	static void printRoute(NodeClass start,NodeClass finish, UndirectedTubeMap tubeMap, ArrayList<Pair> minMatchPaired){
		NodeClass virtualNode = tubeMap.findStation("virtual");
		NodeClass nextNode = virtualNode;
		NodeClass currentNode = null;
		boolean firstIteration = true;
		String lastTubeline= "";
		String currentTubeline = "";

		//once the edge is used, deleted
		while(tubeMap.getEdges().size()!= 0 || minMatchPaired.size()!=0){
			currentNode = nextNode;
			Edges edgeUsed = null;
			Pair duplicatedRoute = duplicatedRouteContains(currentNode, minMatchPaired);
			if( duplicatedRoute != null ){	
				//there are duplicated routes containing this node
				if(currentNode.id.equals(duplicatedRoute.node1.id)){
					System.out.printf("from " + duplicatedRoute.node1.id + " to " + duplicatedRoute.node2.id 
							+ ", cost: " + duplicatedRoute.path.distance + " via ");
					duplicatedRoute.path.printPath();
				}else{
					System.out.printf("from " + duplicatedRoute.node2.id + " to " + duplicatedRoute.node1.id
							+ ", cost: " + duplicatedRoute.path.distance + " via ");
					//print backwards
					duplicatedRoute.path.printPathBackwards();
				}
				shortestDistance += duplicatedRoute.path.distance;
				minMatchPaired.remove(duplicatedRoute);
				if(currentNode.id.equals(duplicatedRoute.node1.id)){
					nextNode = duplicatedRoute.node2;
				}else {
					nextNode = duplicatedRoute.node1;
				}

			}else{
				//if not, choose a random edge, but not the one leading to end 
				NodeClass nodeLastToGo;
				if(currentNode != virtualNode){
					Path temp_path = DijkstraAlgrithm.dijkstraRoute(currentNode, virtualNode, tubeMap);
					nodeLastToGo = temp_path.previousNode.get(1);
				}else{
					nodeLastToGo = virtualNode;
				}
				nextNode = nodeLastToGo;
				//nextNode will be overwritten
				if(firstIteration){
					edgeUsed = tubeMap.existEdge(currentNode.id, start.id);
					nextNode = start;
					firstIteration = false;	
				}else{
					for(Map.Entry<NodeClass, ArrayList<Edges>> entry_node_distance : currentNode.neighborNodes.entrySet()){
						if(entry_node_distance.getKey().id!=nodeLastToGo.id){ 
							edgeUsed = tubeMap.existEdge(currentNode.id, entry_node_distance.getKey().id);
							//the edge exists
							if(edgeUsed!=null){
								nextNode = entry_node_distance.getKey();
								break;
							}
						}
					}
				}

				//nextNode is nodeLastToGo, which hasn't been changed
				if(edgeUsed == null){
					edgeUsed = tubeMap.existEdge(currentNode.id, nextNode.id);
				}
				tubeMap.removeEdge(edgeUsed);		 
				if(currentNode.id ==nextNode.id){
					break;
				}
				currentTubeline = edgeUsed.useTubeLine(nextNode);
				if(edgeUsed != null && edgeUsed.tubeLine1!=null && edgeUsed.tubeLine2!=null){
					System.out.println("from " + currentNode.id + " to " + nextNode.id  + ", cost: " + edgeUsed.distance +" via "+ currentTubeline);
				}else{
					System.out.println("from " + currentNode.id + " to " + nextNode.id+ ", cost: " + edgeUsed.distance);
				}
				shortestDistance += edgeUsed.distance;
				if(!currentTubeline.equals(lastTubeline)){
					shortestDistance+= 2;
				}
				lastTubeline = currentTubeline;
			}
		}
	}
	
	static void printRouteTest(NodeClass start,UndirectedTubeMap tubeMap, ArrayList<Pair> minMatchPaired){
		NodeClass nextNode = start;
		NodeClass currentNode = null;
		//once the edge is used, deleted
		while(tubeMap.getEdges().size()!= 0 || minMatchPaired.size()!=0){
			currentNode = nextNode;
			Edges edgeUsed = null;
			Pair duplicatedRoute = duplicatedRouteContains(currentNode, minMatchPaired);
			if( duplicatedRoute != null ){	
				//there are duplicated routes containing this node
				if(currentNode.id.equals(duplicatedRoute.node1.id)){
					System.out.printf("from node " + duplicatedRoute.node1.id + " to node " + duplicatedRoute.node2.id + " (duplicated route) via ");
					duplicatedRoute.path.printPath();
				}else{
					System.out.printf("from node " + duplicatedRoute.node2.id + " to node " + duplicatedRoute.node1.id+ " (duplicated route) via ");
					//print backwards
					duplicatedRoute.path.printPathBackwards();
				}
				minMatchPaired.remove(duplicatedRoute);
				if(currentNode.id.equals(duplicatedRoute.node1.id)){
					nextNode = duplicatedRoute.node2;
				}else {
					nextNode = duplicatedRoute.node1;
				}
			}else{
				//if not, choose a random edge, but not the one leading to start 
				NodeClass nodeLastToGo;
				if(currentNode != start){
					Path temp_path = DijkstraAlgrithm.dijkstraRoute(currentNode, start, tubeMap);
					nodeLastToGo = temp_path.previousNode.get(1);
				}else{
					nodeLastToGo = start;
				}
				nextNode = nodeLastToGo;
				//nextNode will be overwritten
				for(Map.Entry<NodeClass, ArrayList<Edges>> entry_node_distance : currentNode.neighborNodes.entrySet()){
					if(entry_node_distance.getKey().id!=nodeLastToGo.id){ 
						edgeUsed = tubeMap.existEdge(currentNode.id, entry_node_distance.getKey().id);
						//the edge exists
						if(edgeUsed!=null){
							nextNode = entry_node_distance.getKey();
							break;
						}
					}
				}
				//nextNode is nodeLastToGo, which hasnt been changed
				if(edgeUsed == null){
					edgeUsed = tubeMap.existEdge(currentNode.id, nextNode.id);
				}
				tubeMap.removeEdge(edgeUsed);		//TODO:what if edge not exist 
				if(currentNode.id ==nextNode.id){
					break;
				}
				if(edgeUsed != null && edgeUsed.tubeLine1!=null&& edgeUsed.tubeLine2!=null){
				
						System.out.println("*from node " + currentNode.id + " to node " + nextNode.id +" "+ edgeUsed.tubeLine2 );
					
				}else{
					System.out.println("*from node " + currentNode.id + " to node " + nextNode.id);
				}
			}
		}
	}
	
	
	/**************************************Utility Class******************************************/

	static int getDistance(ArrayList<Pair> pairList){
		int distance = 0;
		for(Pair temp_pair : pairList){
			distance += temp_pair.path.distance;
		}
		return distance;
	}
	
	static class Pair{
		NodeClass node1;
		NodeClass node2;	
		Path path;
		
		public Pair(NodeClass node1 ,NodeClass node2, Path path) {
			this.node1 = node1;
			this.node2 = node2;
			this.path = path;	
		}
		
		boolean contains(NodeClass node){
			if(node.id.equals(this.node1.id) || node.id.equals(this.node2.id)) {
				return true;
			}else{
				return false;
			}
		}
	}
	
	
	/**************************************Data input******************************************/
	static void testcase1(UndirectedTubeMap tubeMap){
		//input tubeMap
		tubeMap.addNode(new NodeClass("1"));
		tubeMap.addNode(new NodeClass("2"));
		tubeMap.addNode(new NodeClass("3"));
		tubeMap.addNode(new NodeClass("4"));
		tubeMap.addNode(new NodeClass("5"));
		tubeMap.addNode(new NodeClass("6"));
		tubeMap.addNode(new NodeClass("7"));
		tubeMap.addNode(new NodeClass("8"));
		
		tubeMap.addEdge("1","2",10);
		tubeMap.addEdge("1","5",5);
		tubeMap.addEdge("1","4",5);
		tubeMap.addEdge("5","4",15);
		tubeMap.addEdge("2","4",15);
		tubeMap.addEdge("2","3",20);
		tubeMap.addEdge("5","6",20);
		tubeMap.addEdge("3","4",30);
		tubeMap.addEdge("4","6",30);
		tubeMap.addEdge("3","8",20);
		tubeMap.addEdge("4","8",20);
		tubeMap.addEdge("6","7",15);
		tubeMap.addEdge("7","8",10);
				
//		//start from 1 and end at 9
//		tubeMap.addNode(new NodeClass("virtual"));
//		tubeMap.addEdge("1","virtual",500);
//		tubeMap.addEdge("8","virtual",200);
	}
	
	static void testcase2(UndirectedTubeMap tubeMap){
		tubeMap.addNode(new NodeClass("Amersham"));
		tubeMap.addNode(new NodeClass("Rayner"));
		tubeMap.addNode(new NodeClass("HarrowOnTheHill"));
		tubeMap.addNode(new NodeClass("NorthActon"));
		tubeMap.addNode(new NodeClass("ActonTown"));
		tubeMap.addNode(new NodeClass("Heathrow"));
		tubeMap.addNode(new NodeClass("Paddington"));
		tubeMap.addNode(new NodeClass("NorthwickPark"));
		tubeMap.addNode(new NodeClass("Hammarsmith"));
		tubeMap.addNode(new NodeClass("WhiteCity"));
//		tubeMap.addNode(new NodeClass("WoodLane"));
		tubeMap.addNode(new NodeClass("WembleyPark"));
		tubeMap.addNode(new NodeClass("BakerStreet"));
		tubeMap.addNode(new NodeClass("RegentPark")); //NB
		tubeMap.addNode(new NodeClass("NottingHillGate"));
		tubeMap.addNode(new NodeClass("EarlsCourt"));
		tubeMap.addNode(new NodeClass("Kensington(Olympia)"));	//NB
		tubeMap.addNode(new NodeClass("Victoria"));
		tubeMap.addNode(new NodeClass("Stockwell"));
		tubeMap.addNode(new NodeClass("HighBarnet"));
		tubeMap.addNode(new NodeClass("CamdenTown"));
//		tubeMap.addNode(new NodeClass("Euston"));
		tubeMap.addNode(new NodeClass("Enbankment"));
		tubeMap.addNode(new NodeClass("ElephantCastle"));
		tubeMap.addNode(new NodeClass("KingsCross"));
		tubeMap.addNode(new NodeClass("Holborn"));
		tubeMap.addNode(new NodeClass("Finsbury"));
		tubeMap.addNode(new NodeClass("Moorgate"));
		tubeMap.addNode(new NodeClass("Monument"));
		tubeMap.addNode(new NodeClass("LondonBridge"));
		tubeMap.addNode(new NodeClass("Epping"));
		tubeMap.addNode(new NodeClass("SouthWoodford")); //or Leytonstone 
		tubeMap.addNode(new NodeClass("WestHam"));
		tubeMap.addNode(new NodeClass("Upminster"));
		tubeMap.addNode(new NodeClass("AldgateEast"));
		
		tubeMap.addEdge("Amersham","HarrowOnTheHill",60+4*2, "m", "m" );
		tubeMap.addEdge("Rayner","HarrowOnTheHill",5 ,"m", "m");
		tubeMap.addEdge("HarrowOnTheHill","NorthwickPark",3 );
		tubeMap.addEdge("Rayner","ActonTown",21 );
		tubeMap.addEdge("ActonTown","Heathrow",33+2 );
		tubeMap.addEdge("Heathrow", "Paddington", 15+5, "@ via Heathrow express", "@ via Heathrow express" );	//Heathrow express
		tubeMap.addEdge("Rayner","NorthActon",33+4+2*2 );
		tubeMap.addEdge("ActonTown","Hammarsmith",27+2*2 );
		tubeMap.addEdge("NorthActon","WhiteCity",5 );
		tubeMap.addEdge("NorthActon","ActonTown",12+1*2 );
		tubeMap.addEdge("Paddington","NorthwickPark",25+2 );
		tubeMap.addEdge("Paddington","WhiteCity",13 );
		tubeMap.addEdge("WhiteCity","Hammarsmith",8);
		tubeMap.addEdge("NorthwickPark","WembleyPark",6 );
		tubeMap.addEdge("WembleyPark","BakerStreet",20 );
		tubeMap.addEdge("Paddington","BakerStreet",6+1*2 );
		tubeMap.addEdge("BakerStreet","RegentPark",2 );
		tubeMap.addEdge("WhiteCity","NottingHillGate",6);
		tubeMap.addEdge("NottingHillGate","EarlsCourt",7 );
		tubeMap.addEdge("WembleyPark","CamdenTown",36+6+1*2 );
//		tubeMap.addEdge("CamdenTown","Euston",);
//		tubeMap.addEdge("Enbankment","Euston",);
		tubeMap.addEdge("CamdenTown","Enbankment",12 );
		tubeMap.addEdge("NottingHillGate","Holborn", 14);
		tubeMap.addEdge("EarlsCourt","Victoria",8);
		tubeMap.addEdge("EarlsCourt","Holborn",16);
		tubeMap.addEdge("EarlsCourt","Stockwell",39+10+1*2);
		tubeMap.addEdge("EarlsCourt","Hammarsmith",7);
		tubeMap.addEdge("EarlsCourt","Kensington(Olympia)",3);
		tubeMap.addEdge("Stockwell","Victoria",7);
		tubeMap.addEdge("Victoria","Enbankment",5);
		tubeMap.addEdge("Stockwell","ElephantCastle",7);
		tubeMap.addEdge("ElephantCastle","Enbankment",11+3*2 );	//NB
		tubeMap.addEdge("Enbankment","Monument",8);
		tubeMap.addEdge("Holborn","Monument",5+2);
		tubeMap.addEdge("KingsCross","BakerStreet",6);
		tubeMap.addEdge("Holborn","KingsCross",4);
		tubeMap.addEdge("KingsCross","Finsbury",9);
		tubeMap.addEdge("Finsbury","HighBarnet",22+12+1*2);
		tubeMap.addEdge("HighBarnet","CamdenTown",25);
		tubeMap.addEdge("KingsCross","Moorgate",7);	//NB
		tubeMap.addEdge("KingsCross","Moorgate",8, " @ via Northern Line");		//
		tubeMap.addEdge("Moorgate","Monument",2);	//NB
		tubeMap.addEdge("ElephantCastle","LondonBridge",4);
		tubeMap.addEdge("LondonBridge","WestHam",16);
		tubeMap.addEdge("WestHam","AldgateEast",17+2*2);
		tubeMap.addEdge("AldgateEast","Monument",5);
		tubeMap.addEdge("AldgateEast","Moorgate",5);
		tubeMap.addEdge("WestHam","Upminster",29);
		tubeMap.addEdge("WestHam","SouthWoodford",13+1*2);
		tubeMap.addEdge("SouthWoodford","Finsbury",17+12+2*2);
		tubeMap.addEdge("SouthWoodford","Epping",46+2*2);
		//virtual node
		//set distance as 500 for the edge attached to starting node
		//and 200 for the end node
		tubeMap.addNode(new NodeClass("virtual"));
		tubeMap.addEdge("virtual","Epping",200);
		tubeMap.addEdge("virtual","Amersham",500);
	}
	
	

	static void tubemapBuild(UndirectedTubeMap tubeMap){
		tubeMap.addNode(new NodeClass("Amersham"));
		tubeMap.addNode(new NodeClass("Rayner"));
		tubeMap.addNode(new NodeClass("HarrowOnTheHill"));
		tubeMap.addNode(new NodeClass("NorthActon"));
		tubeMap.addNode(new NodeClass("ActonTown"));
		tubeMap.addNode(new NodeClass("Heathrow"));
		tubeMap.addNode(new NodeClass("Paddington"));
		tubeMap.addNode(new NodeClass("NorthwickPark"));
		tubeMap.addNode(new NodeClass("Hammarsmith"));
		// tubeMap.addNode(new NodeClass("WhiteCity"));
//		tubeMap.addNode(new NodeClass("WoodLane"));
		tubeMap.addNode(new NodeClass("WembleyPark"));
		tubeMap.addNode(new NodeClass("BakerStreet"));
		tubeMap.addNode(new NodeClass("RegentPark")); //NB
		tubeMap.addNode(new NodeClass("NottingHillGate"));
		tubeMap.addNode(new NodeClass("EarlsCourt"));
		tubeMap.addNode(new NodeClass("Kensington(Olympia)"));	//NB
		tubeMap.addNode(new NodeClass("Victoria"));
		tubeMap.addNode(new NodeClass("Stockwell"));
		tubeMap.addNode(new NodeClass("HighBarnet"));
		tubeMap.addNode(new NodeClass("CamdenTown"));
//		tubeMap.addNode(new NodeClass("Euston"));
		tubeMap.addNode(new NodeClass("Enbankment"));
		tubeMap.addNode(new NodeClass("ElephantCastle"));
		tubeMap.addNode(new NodeClass("KingsCross"));
		tubeMap.addNode(new NodeClass("Holborn"));
		tubeMap.addNode(new NodeClass("Finsbury"));
		tubeMap.addNode(new NodeClass("Moorgate"));
		tubeMap.addNode(new NodeClass("Monument"));
		tubeMap.addNode(new NodeClass("LondonBridge"));
		tubeMap.addNode(new NodeClass("Epping"));
		tubeMap.addNode(new NodeClass("SouthWoodford")); //or Leytonstone 
		tubeMap.addNode(new NodeClass("WestHam"));
		tubeMap.addNode(new NodeClass("Upminster"));
		tubeMap.addNode(new NodeClass("AldgateEast"));
		tubeMap.addNode(new NodeClass("TurnhamGreen"));
		tubeMap.addNode(new NodeClass("Richmond"));

		
		tubeMap.addEdge("Amersham","HarrowOnTheHill",60+4*2,"m");
		tubeMap.addEdge("Rayner","HarrowOnTheHill",5,"m");
//		tubeMap.addEdge("HarrowOnTheHill","WembleyPark",9);
//		tubeMap.addEdge("HarrowOnTheHill","Paddington",30 );
		
		tubeMap.addEdge("HarrowOnTheHill","NorthwickPark",3,"m");
		tubeMap.addEdge("Paddington","NorthwickPark",25+2,"b");
		tubeMap.addEdge("NorthwickPark","WembleyPark",6, "m");
		
		tubeMap.addEdge("Rayner","ActonTown",21,"p");
		tubeMap.addEdge("ActonTown","Heathrow",33+2,"p");
		tubeMap.addEdge("Heathrow", "Paddington", 15+2, "@ via Heathrow express");	//Heathrow express
		tubeMap.addEdge("Rayner","NorthActon",33+4+2*2 , "p", "c");
//		tubeMap.addEdge("ActonTown","Hammarsmith",27+2*2 );	//WTF?
		tubeMap.addEdge("ActonTown","TurnhamGreen",4, "d" );
		tubeMap.addEdge("TurnhamGreen","Hammarsmith",5,"d" );
		tubeMap.addEdge("TurnhamGreen","Richmond",9,"d" );

		// tubeMap.addEdge("NorthActon","WhiteCity",5 );
		// tubeMap.addEdge("WhiteCity","NottingHillGate",6);
		tubeMap.addEdge("NorthActon","NottingHillGate",11,"c");

		// tubeMap.addEdge("Paddington","WhiteCity",13 );
		// tubeMap.addEdge("WhiteCity","Hammarsmith",8);
		tubeMap.addEdge("Paddington","Hammarsmith",15,"h");


		tubeMap.addEdge("NorthActon","ActonTown",12+1*2, "c", "d" );

		tubeMap.addEdge("WembleyPark","BakerStreet",20, "m" );
		tubeMap.addEdge("Paddington","BakerStreet",6+1*2, "b" );
		tubeMap.addEdge("BakerStreet","RegentPark",2, "b" );
		tubeMap.addEdge("NottingHillGate","EarlsCourt",7 ,"d");
		tubeMap.addEdge("WembleyPark","CamdenTown",36+6+1*2, "j", "n" );
//		tubeMap.addEdge("CamdenTown","Euston",);
//		tubeMap.addEdge("Enbankment","Euston",);
		tubeMap.addEdge("CamdenTown","Enbankment",12, "n" );
		tubeMap.addEdge("NottingHillGate","Holborn", 14,"c");
		tubeMap.addEdge("EarlsCourt","Victoria",8, "d");
		tubeMap.addEdge("EarlsCourt","Holborn",16,"p");
		tubeMap.addEdge("EarlsCourt","Stockwell",39+10+1*2,"d", "n");
		tubeMap.addEdge("EarlsCourt","Hammarsmith",7, "d");
		tubeMap.addEdge("EarlsCourt","Kensington(Olympia)",3, "d");
		tubeMap.addEdge("Stockwell","Victoria",7,"v");
		tubeMap.addEdge("Victoria","Enbankment",5,"d");
		tubeMap.addEdge("Stockwell","ElephantCastle",7, "n");
		tubeMap.addEdge("ElephantCastle","Enbankment",11+3*2, "b" );	//NB
		tubeMap.addEdge("Enbankment","Monument",8, "d");
		tubeMap.addEdge("Holborn","Monument",5+2, "c");
		tubeMap.addEdge("KingsCross","BakerStreet",6, "m");
		tubeMap.addEdge("Holborn","KingsCross",4, "p");
		tubeMap.addEdge("KingsCross","Finsbury",9, "v");
		tubeMap.addEdge("Finsbury", "CamdenTown",45+12, "p", "n");
		tubeMap.addEdge("KingsCross","Moorgate",7, "m");	//NB
		tubeMap.addEdge("KingsCross","Moorgate",8, "n");		//
		tubeMap.addEdge("Moorgate","Monument",2,"cir");	//NB
		tubeMap.addEdge("ElephantCastle","LondonBridge",4, "n");
		tubeMap.addEdge("LondonBridge","WestHam",16, "j");
		tubeMap.addEdge("WestHam","AldgateEast",17+2*2, "h");
		tubeMap.addEdge("AldgateEast","Monument",5,"d");
		tubeMap.addEdge("AldgateEast","Moorgate",5, "h");
		tubeMap.addEdge("WestHam","Upminster",29, "d");
		tubeMap.addEdge("WestHam","SouthWoodford",13+1*2, "j", "c");
		tubeMap.addEdge("SouthWoodford","Finsbury",17+12+2*2, "v");
		tubeMap.addEdge("SouthWoodford","Epping",46+2*2, "c");
		//virtual node
		tubeMap.addNode(new NodeClass("virtual"));
		tubeMap.addEdge("virtual","Amersham",500);
		tubeMap.addEdge("virtual","Epping",200);
	}
	
	
	
}