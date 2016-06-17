package tubeChallenge;

import java.util.ArrayList;
import java.util.Map;
import tubeChallenge.UndirectedTubeMap.Edges;
import tubeChallenge.UndirectedTubeMap.NodeClass;
public class DijkstraAlgrithm {
	public static void main(String args[]){
		UndirectedTubeMap tubeMap = new UndirectedTubeMap();
		
		//Initialise the tube maps, 
		//Create nodes then add their edge
//tubeMap.addNode(new NodeClass("1"));
//tubeMap.addNode(new NodeClass("2"));
//tubeMap.addNode(new NodeClass("3"));
//tubeMap.addNode(new NodeClass("4"));
//tubeMap.addNode(new NodeClass("5"));
//tubeMap.addNode(new NodeClass("6"));
//tubeMap.addNode(new NodeClass("7"));
//tubeMap.addNode(new NodeClass("8"));
//
//tubeMap.addEdge("1", "2",9);
//tubeMap.addEdge("2", "3",24);
//tubeMap.addEdge("1", "6",14);
//tubeMap.addEdge("1", "7",15);
//tubeMap.addEdge("6", "3",18);
//tubeMap.addEdge("6", "5",30);
//tubeMap.addEdge("6", "7",5);
//tubeMap.addEdge("7", "5",20);
//tubeMap.addEdge("7", "8",44);
//tubeMap.addEdge("5", "8",16);
//tubeMap.addEdge("5", "4",11);
//tubeMap.addEdge("5", "3",2);
//tubeMap.addEdge("4", "3",6);
//tubeMap.addEdge("4", "8",6);
//tubeMap.addEdge("3", "8",19);
		
		Path shortestPath = dijkstraRoute(tubeMap.findStation("1"),tubeMap.findStation("8"),tubeMap);
		shortestPath.printPath();
	}
	
public static Path dijkstraRoute(NodeClass start, NodeClass finish, UndirectedTubeMap tubeMap1){
	UndirectedTubeMap tubeMap = new UndirectedTubeMap(tubeMap1);	//copy tubemap
	
	//not confirmed nodes
	ArrayList<NodeClass> unvistedNodes = tubeMap.getNodes();
	//being passed path
	ArrayList<Path> exploredPath = new ArrayList<Path>();
	ArrayList<Path> confirmedPath = new ArrayList<Path>();
	//create a new path
	Path shortestPath = new Path();
	shortestPath.previousNode.add(start);
	shortestPath.distance = 0;
	exploredPath.add(shortestPath);
	Path oldPath;
	
	while(unvistedNodes.contains(finish)){
		Path current_path = findMinPath(exploredPath);
		//the node of the shortest path to be confirmed
		NodeClass currentNode = current_path.previousNode.get(current_path.previousNode.size()-1);
		
		//update the neighbor nodes
		for(Map.Entry<NodeClass, ArrayList<Edges>> entry_node_distance : currentNode.neighborNodes.entrySet()){
			//check if the node is not confirmed
			if( unvistedNodes.contains(entry_node_distance.getKey()) ){
				//if the node is passed but not confirmed, update this node
				//get the min edge in the edge list
				int min = Integer.MAX_VALUE;
				for(Edges temp : entry_node_distance.getValue()){
					if(temp.distance < min){
						min = temp.distance;
					}
				}
				if( (oldPath = PathlistContains(entry_node_distance.getKey(), exploredPath)) !=null ){
					if(current_path.distance+ min <= oldPath.distance){						
						exploredPath.remove(oldPath);
					}else{//dont update this neighbor node's path
						continue;
					}
				}
				Path newPath = new Path(current_path);
				newPath.previousNode.add(entry_node_distance.getKey());
				newPath.distance += min;
				exploredPath.add(newPath);					
			}				
	    }		
		confirmedPath.add(current_path);
		exploredPath.remove(current_path);
		unvistedNodes.remove(currentNode); //remove last element in the path			
	}
//	//for testing
//	for(Path temp: confirmedPath){
//		System.out.printf("cost: "+ temp.distance  +", path: ");
//		temp.printPath();
//	}
	return PathlistContains(finish, confirmedPath);
}

	static Path findMinPath(ArrayList<Path> pathArray){
		Path minPath = pathArray.get(0);
		for(Path temp_path: pathArray){
			if(minPath.distance>temp_path.distance) {
				minPath = temp_path;	
			}	
		}
		return minPath;
	}
	
	static Path PathlistContains(NodeClass node, ArrayList<Path> exploredPath){
		for(Path entry_path : exploredPath){
			if(node.id == (entry_path.previousNode.get(entry_path.previousNode.size()-1).id)) return entry_path;
		}
		return null;
	}
	
	static class Path{
		ArrayList<NodeClass> previousNode;
		int distance;
		
		public Path() {
			previousNode = new ArrayList<NodeClass>();
			distance = Integer.MAX_VALUE;
		}
		
		//copy constructor
		public Path(Path oldPath){
			this.previousNode =  new ArrayList<NodeClass>(oldPath.previousNode); 
			this.distance = new Integer(oldPath.distance);
		}
		
		public void printPath(){
			for(NodeClass node: previousNode){
				System.out.printf(node.id + " ");
			}
			System.out.println();
		}
		
		public void printPathBackwards(){
			for(int i = previousNode.size()-1; i>=0; i--){
				System.out.printf(previousNode.get(i).id + " " );
			}
			System.out.println();
		}
	}
}
