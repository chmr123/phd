package tf.autoodc.pivot;

import java.util.Map;

public class Pivot {
	static int window_size = 3;
	ArrayList<String> pivots = new ArrayList<String>();
	Map<String, String[]> alltext;
	public Pivot(Map<String, String[]> alltext){
		this.alltext = alltext;
	}

	public void getPivotFeature(Map<String[], String> keywords_map){
		for(String keys[] : keywords_map.keySet()){
			for(String key : keys){
				pivots.add(key); // add each key word to pivot arraylist as base feature vector
			}
		}
	}
}
