package cs6301.g45;

/**
* Helper Util class for multidimensional search
* @author 	Lopamudra 
*  			Bhakti Khatri			
* 			Gautam Gunda 			
* 			Sangeeta Kadambala
*/
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Util {

	// Util function to sort the hashmap according to the value.
	public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> unsortMap, List<K> result,
			boolean increasing) {

		List<Map.Entry<K, V>> list = new LinkedList<Map.Entry<K, V>>(unsortMap.entrySet());

		Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
			public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
				if (increasing)
					return (o1.getValue()).compareTo(o2.getValue());
				else
					return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		Map<K, V> res = new LinkedHashMap<K, V>();
		for (Map.Entry<K, V> entry : list) {
			res.put(entry.getKey(), entry.getValue());
			result.add(entry.getKey());
		}

		return res;

	}
}
