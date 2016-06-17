package tubeChallenge;

import java.util.ArrayList;
import java.util.HashMap;
import tubeChallenge.DijkstraAlgrithm.Path;

public class UndirectedTubeMap {
	
	private ArrayList<NodeClass> nodes;
	private ArrayList<Edges> edges;
	private int minDistance;
	
	public UndirectedTubeMap(UndirectedTubeMap map) {
		this.nodes = new ArrayList<NodeClass>(map.nodes);
		this.edges = new ArrayList<Edges>(map.edges);
		this.minDistance = map.minDistance;
	}
	
	public UndirectedTubeMap(){
		this.nodes = new ArrayList<NodeClass>();
		this.edges = new ArrayList<Edges>();
		minDistance = 0;
	}

	public int getMinDistance(){
		return minDistance;
	}
	public ArrayList<NodeClass> getNodes() {
		return nodes;
	}
	
	public ArrayList<Edges> getEdges(){
		return edges;
	}

	public void addNode(NodeClass nodes) {
		this.nodes.add(nodes);
	}
	
	public NodeClass findStation(String id){
		for(NodeClass node:nodes){
			if(id == node.id) return node;
		}
		return null;
	}
	
	//add edge without name
	public void addEdge(String station1, String station2, int distance){
		Edges temp_edge = new Edges(station1,station2,distance);
		edges.add(temp_edge);
		findStation(station1).addEdge(findStation(station2),temp_edge);
		findStation(station2).addEdge(findStation(station1),temp_edge);	
		this.minDistance += distance;
	}
	
	//edeg with tubeline name
	public void addEdge(String station1, String station2, int distance, String tubeLine1, String tubeline2){
		//tubeline1 close to station 1
		//tubeline2 close to station 2
		Edges temp_edge = new Edges(station1,station2,distance,tubeLine1,tubeline2);
		edges.add(temp_edge);
		findStation(station1).addEdge(findStation(station2),temp_edge);
		findStation(station2).addEdge(findStation(station1),temp_edge);	
		this.minDistance += distance;
	}
	
	public void addEdge(String station1, String station2, int distance, String tubeLine){
		//tubeline1 close to station 1
		//tubeline2 close to station 2
		Edges temp_edge = new Edges(station1,station2,distance,tubeLine,tubeLine);
		edges.add(temp_edge);
		findStation(station1).addEdge(findStation(station2),temp_edge);
		findStation(station2).addEdge(findStation(station1),temp_edge);	
		this.minDistance += distance;
	}
	
	//TODO:
	public void addEdge(String station1, String station2, int distance, String tubeLine1, String tubeline2, String information){
		//tubeline1 close to station 1
		//tubeline2 close to station 2
		Edges temp_edge = new Edges(station1,station2,distance,tubeLine1,tubeline2);
		edges.add(temp_edge);
		findStation(station1).addEdge(findStation(station2),temp_edge);
		findStation(station2).addEdge(findStation(station1),temp_edge);	
		this.minDistance += distance;
	}
	
	//Check if there are any edges existing between two stations 
	public Edges existEdge(String station1, String station2){
		for(Edges temp_edge : edges){
			if((temp_edge.station1.equals(station1) && temp_edge.station2.equals(station2))||
					(temp_edge.station1.equals(station2) && temp_edge.station2.equals(station1))){
				return temp_edge;
			}
		}
		return null;
	}
	
//	public void removeEdge(String station1, String station2, String name) {
//		for(Edges temp_edge : edges){
//			if((temp_edge.station1.equals(station1) && temp_edge.station2.equals(station2) && temp_edge.tubeLine == name) 
//					|| (temp_edge.station1.equals(station2) && temp_edge.station2.equals(station1) && temp_edge.tubeLine == name)){
//				edges.remove(temp_edge);
//				return;
//			}
//		}
//	}
	
	public void  removeEdge(Edges edge) {
		edges.remove(edge);
	}
	
	/***********************************Node**********************************/
	
	//Utility Class for NodeClass
	public static class NameAndDistance{
		String viaLine;
		int distance;
		public NameAndDistance(String viaLine, int distance) {
			this.viaLine = viaLine;
			this.distance = distance;
		}
	}
	
	public static class NodeClass{
		 String  id; // node id
		 HashMap<NodeClass, ArrayList<Edges>> neighborNodes; //neighbor stations and distance, will overwrite
		 HashMap<NodeClass,	Path> otherOddNodeDistance;
		 
 public NodeClass(String name){
	 id = name;
	 neighborNodes = new HashMap<NodeClass, ArrayList<Edges>>();
	 otherOddNodeDistance = new HashMap<NodeClass, Path>();
 }	
		 
		 public NodeClass(NodeClass nodeCopy){
			 this.id = nodeCopy.id;
			 neighborNodes = new HashMap<NodeClass, ArrayList<Edges>>(nodeCopy.neighborNodes);
			 otherOddNodeDistance = new HashMap<NodeClass, Path>(nodeCopy.otherOddNodeDistance);
		 }
		 
		 //important!
		 public void addEdge(NodeClass node, Edges edgeSet) {
			 if(neighborNodes.containsKey(node)){
				 neighborNodes.get(node).add(edgeSet);
			 }else{
				 ArrayList<Edges> temp = new ArrayList<Edges>();
				 temp.add(edgeSet);
				 neighborNodes.put(node,temp) ;
			 }
		}
	}

	/***********************************Edge**********************************/
	public static class Edges{
		String station1;
		String station2;
		int distance;
		String tubeLine1;
		String tubeLine2;
	
		//one edge, two stations
		//tubeline1 close to station 1
		//tubeline2 close to station 2
		public Edges (String station1, String station2, int distance){
			this.station1 = station1;
			this.station2 = station2;
 			this.distance = distance;
 			this.tubeLine1 = "";
 			this.tubeLine2 = "";
		}
		
		public Edges (String station1, String station2, int distance,String tubeLine1,String tubeLine2){
			this.station1 = station1;
			this.station2 = station2;
 			this.distance = distance;
 			this.tubeLine1 = tubeLine1;
 			this.tubeLine2 = tubeLine2;
		}
		
		String getOtherNode(String stationId){
			return (stationId==station1 ? station2: station1) ;
		}
		
		String useTubeLine(NodeClass endStation){
			if(endStation.id.equals(station1)){
				return tubeLine2;
			}else{
				return tubeLine1;
			}
		}
		
	} 
}
