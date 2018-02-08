package cs6301.g45;

/**
* Implementation of multidimensional search
* @author 	Lopamudra 
*  			Bhakti Khatri			
* 			Gautam Gunda 			
* 			Sangeeta Kadambala
*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class MDS {

	HashMap<Long, TreeSet<Long>> descriptionHMap; // key - item description value-item id
	HashMap<Long, TreeSet<Long>> itemHMap; // key - item id; value - item description
	TreeMap<Long, Supplier> supplierTMap; // key - supplier id; value - Supplier
	HashMap<Long, HashMap<Long, Integer>> itemSupPriceHMap;

	public MDS() {
		descriptionHMap = new HashMap<>();
		itemHMap = new HashMap<>();
		itemSupPriceHMap = new HashMap<>();
		supplierTMap = new TreeMap<>();
	}

	public static class Item {
		Long id;
		Long[] description;

		public Item(Long id, Long[] description) {
			this.id = id;
			this.description = description;
		}
	}

	public static class Supplier {
		Long supplierId;
		HashMap<Long, Integer> pair;
		float reputation;

		public Supplier(Long id, HashMap<Long, Integer> pair, float r) {
			this.supplierId = id;
			this.pair = pair;
			this.reputation = r;
		}
	}

	public static class Pair {
		long id;
		int price;

		public Pair(long id, int price) {
			this.id = id;
			this.price = price;
		}
	}

	public static class ItemReputation {
		HashMap<Long, Integer> pair;
		Float reputation;

		public ItemReputation(HashMap<Long, Integer> pair, Float reputation) {
			this.pair = pair;
			this.reputation = reputation;
		}

		@Override
		public boolean equals(Object obj) {
			ItemReputation ir = (ItemReputation) obj;
			if (ir.reputation.equals(this.reputation) && ir.pair.size() == this.pair.size()) {
				for (final Long key : this.pair.keySet()) {
					if (ir.pair.containsKey(key) && ir.pair.get(key).equals(this.pair.get(key))) {
						continue;
					} else {
						// System.out.println("key : "+ key + " this value : "+ this.pair.get(key) + "
						// ir value: "+ir.pair.get(key));
						return false;
					}
				}
				return true;
			} else
				return false;
		}

		@Override
		public int hashCode() {
			int hashcode = 1;
			Iterator it = pair.entrySet().iterator();
			for (Map.Entry<Long, Integer> entry : pair.entrySet()) {
				hashcode = hashcode + 7 * entry.getValue(); // As sequence of item and price doesn't matter, this
															// hashcode is coded according to that.
			}
			hashcode = (int) (hashcode + 7 * reputation);
			return hashcode;
		}
	}

	/*
	 * add a new item. If an entry with the same id already exists, the new
	 * description is merged with the existing description of the item. Returns true
	 * if the item is new, and false otherwise.
	 */
	public boolean add(Long id, Long[] description) {
		for (Long desc : description) {
			TreeSet<Long> itemIdSet = new TreeSet<>();
			if (descriptionHMap.get(desc) != null)
				itemIdSet = descriptionHMap.get(desc);
			itemIdSet.add(id);
			descriptionHMap.remove(desc);
			descriptionHMap.put(desc, itemIdSet);
		}
		if (itemHMap.containsKey(id)) { // if item id exists
			TreeSet<Long> descSet = itemHMap.get(id);
			for (int i = 0; i < description.length; i++) {
				if (!descSet.contains(description[i])) {
					descSet.add(description[i]);
				}
			}
			itemHMap.remove(id);
			itemHMap.put(id, descSet);
			return false;
		} else { // if item id does not exists
			List<Long> list = Arrays.asList(description);
			TreeSet<Long> set = new TreeSet<Long>(list);
			itemHMap.put(id, set);
			return true;
		}

	}

	/*
	 * add a new supplier (Long) and their reputation (float in [0.0-5.0], single
	 * decimal place). If the supplier exists, their reputation is replaced by the
	 * new value. Return true if the supplier is new, and false otherwise.
	 */
	public boolean add(Long supplier, float reputation) {
		if (supplierTMap.containsKey(supplier)) { // if supplier exists
			Supplier supp = supplierTMap.get(supplier);
			if (supp != null) {
				supplierTMap.remove(supplier);
				supplierTMap.put(supplier, new Supplier(supplier, supp.pair, reputation));
			}
			return false;
		} else { // if supplier does not exists
			supplierTMap.put(supplier, new Supplier(supplier, null, reputation));
			return true;
		}
	}

	/*
	 * add products and their prices at which the supplier sells the product.If
	 * there is an entry for the price of an id by the same supplier, then the price
	 * is replaced by the new price. Returns the number of new entries created.
	 */
	public int add(Long supplier, Pair[] idPrice) { // incorrect output
		int entryCount = 0;
		Supplier supp = supplierTMap.get(supplier);
		float reputation = supp.reputation;
		HashMap<Long, Integer> pairHMap = new HashMap<>();
		if (supp.pair != null)
			pairHMap = supp.pair;

		for (int i = 0; i < idPrice.length; i++) {
			if (pairHMap != null && pairHMap.containsKey(idPrice[i].id)) {
				pairHMap.put(idPrice[i].id, idPrice[i].price);
			} else {
				pairHMap.put(idPrice[i].id, idPrice[i].price);
				entryCount++;
			}
			HashMap<Long, Integer> supp_price = new HashMap<>();
			if (itemSupPriceHMap.get(idPrice[i].id) != null)
				supp_price = itemSupPriceHMap.get(idPrice[i].id);
			supp_price.put(supplier, idPrice[i].price);
			itemSupPriceHMap.put(idPrice[i].id, supp_price);
		}
		supplierTMap.remove(supplier);
		supplierTMap.put(supplier, new Supplier(supplier, pairHMap, reputation));
		return entryCount;
	}

	/*
	 * return an array with the description of id. Return null if there is no item
	 * with this id.
	 */
	public Long[] description(Long id) {
		if (itemHMap.containsKey(id)) {
			TreeSet<Long> descSet = itemHMap.get(id);
			Long[] descArr = descSet.toArray(new Long[descSet.size()]);
			return descArr;
		} else {
			return null;
		}
	}

	/*
	 * given an array of Longs, return an array of items whose description contains
	 * one or more elements of the array, sorted by the number of elements of the
	 * array that are in the item's description (non-increasing order).
	 */
	public Long[] findItem(Long[] arr) { // sort output array remaining
		HashMap<Long, Integer> resHMap = new HashMap<>();
		List<Long> idList = new ArrayList<>();
		for (Long desc : arr) {
			TreeSet<Long> tSetId = descriptionHMap.get(desc);
			if (tSetId != null) {
				Iterator<Long> itr = tSetId.iterator();
				while (itr.hasNext()) {
					Long id = itr.next();
					int count = 0;
					if (resHMap.containsKey(id)) {
						count = resHMap.get(id);
						resHMap.put(id, ++count);
					} else {
						resHMap.put(id, 0);
					}
				}
			}
		}
		Util.sortByValue(resHMap, idList, false);
		return idList.toArray(new Long[idList.size()]);
	}

	/*
	 * given a Long n, return an array of items whose description contains n, which
	 * have one or more suppliers whose reputation meets or exceeds the given
	 * minimum reputation, that sell that item at a price that falls within the
	 * price range [minPrice, maxPrice] given. Items should be sorted in order of
	 * their minimum price charged by a supplier for that item (non-decreasing
	 * order).
	 */
	public Long[] findItem(Long n, int minPrice, int maxPrice, float minReputation) {
		TreeSet<Long> tSetId = descriptionHMap.get(n);
		HashMap<Long, Integer> result = new HashMap<>();
		List<Long> idList = new ArrayList<>();

		if (tSetId != null) {
			Iterator<Long> itr = tSetId.iterator();
			while (itr.hasNext()) {
				Long itemId = itr.next();
				HashMap<Long, Integer> sp = itemSupPriceHMap.get(itemId);
				if (sp != null) {
					int tempMinPrice = Integer.MAX_VALUE;
					for (Map.Entry<Long, Integer> entry : sp.entrySet()) {
						int price = entry.getValue();
						if (supplierTMap.get(entry.getKey()).reputation >= minReputation
								&& (price >= minPrice && price < maxPrice)) {
							if (tempMinPrice > price)
								tempMinPrice = price;
							result.put(itemId, tempMinPrice);
						}
					}

				}
			}
			Util.sortByValue(result, idList, true);
		}
		return idList.toArray(new Long[idList.size()]);
	}

	/*
	 * given an id, return an array of suppliers who sell that item, ordered by the
	 * price at which they sell the item (non-decreasing order).
	 */
	public Long[] findSupplier(Long id) {
		List<Long> suppIdList = new ArrayList<>();
		HashMap<Long, Integer> sp = itemSupPriceHMap.get(id);
		Util.sortByValue(sp, suppIdList, true);
		return suppIdList.toArray(new Long[suppIdList.size()]);
	}

	/*
	 * given an id and a minimum reputation, return an array of suppliers who sell
	 * that item, whose reputation meets or exceeds the given reputation. The array
	 * should be ordered by the price at which they sell the item (non-decreasing
	 * order).
	 */
	public Long[] findSupplier(Long id, float minReputation) {
		List<Long> suppIdList = new ArrayList<Long>();
		HashMap<Long, Integer> sp = new HashMap<>();
		HashMap<Long, Integer> sp1 = itemSupPriceHMap.get(id);
		Iterator itr = sp1.entrySet().iterator();
		while (itr.hasNext()) {
			Map.Entry<Long, Integer> pair = (Map.Entry) itr.next();
			Supplier suplr = supplierTMap.get(pair.getKey());
			if (suplr.reputation >= minReputation)
				sp.put(pair.getKey(), pair.getValue());
		}
		Util.sortByValue(sp, suppIdList, true);
		return suppIdList.toArray(new Long[suppIdList.size()]);
	}

	/*
	 * find suppliers selling 5 or more products, who have the same identical
	 * profile as another supplier: same reputation, and, sell the same set of
	 * products, at identical prices. This is a rare operation, so do not do
	 * additional work in the other operations so that this operation is fast.
	 * Creative solutions that are elegant and efficient will be awarded excellence
	 * credit. Return array of suppliers satisfying above condition. Make sure that
	 * each supplier appears only once in the returned array.
	 */

	public Long[] identical() {
		Collection c = supplierTMap.values();
		Iterator<Supplier> itr = c.iterator();
		Map<ItemReputation, List<Long>> itemRepHMap = new HashMap<>();
		Set<Long> result = new HashSet<>();

		while (itr.hasNext()) {
			Supplier supp = itr.next();
			Long value = supp.supplierId;
			HashMap<Long, Integer> pairHMap = supp.pair;
			Float rep = supp.reputation;
			if (pairHMap.size() < 5)
				continue;
			// System.out.println("****************************SupplierId: "+value);
			ItemReputation key = new ItemReputation(pairHMap, rep);

			if (itemRepHMap.get(key) == null)
				itemRepHMap.put(key, new ArrayList<Long>());
			itemRepHMap.get(key).add(value);
		}
		for (Entry<ItemReputation, List<Long>> entry : itemRepHMap.entrySet()) {
			if (entry.getValue().size() > 1)
				result.addAll(entry.getValue());
		}
		// System.out.println("Total identical suppliers: "+result.size());
		return result.toArray(new Long[result.size()]);
	}

	/*
	 * given an array of ids, find the total price of those items, if those items
	 * were purchased at the lowest prices, but only from sellers meeting or
	 * exceeding the given minimum reputation. Each item can be purchased from a
	 * different seller.
	 */
	public int invoice(Long[] arr, float minReputation) {
		int sum = 0;
		for (int i = 0; i < arr.length; i++) {
			HashMap<Long, Integer> sp = itemSupPriceHMap.get(arr[i]);
			if (sp != null) {
				int minPrice = Integer.MAX_VALUE;
				for (Map.Entry<Long, Integer> entry : sp.entrySet()) {
					Long suppId = entry.getKey();
					Supplier supp = supplierTMap.get(suppId);
					if (supp.reputation >= minReputation && minPrice > entry.getValue()) {
						minPrice = entry.getValue();
					}
				}
				if (minPrice != Integer.MAX_VALUE)
					sum += minPrice;
			}
		}
		return sum;
	}

	/*
	 * remove all items, all of whose suppliers have a reputation that is equal or
	 * lower than the given maximum reputation. Returns an array with the items
	 * removed.
	 */
	public Long[] purge(float maxReputation) {
		Set<Long> itemIdSet = new HashSet<Long>();
		Set<Long> goodItemId = new HashSet<>();

		for (Long id : supplierTMap.keySet()) {
			Supplier supp = supplierTMap.get(id);
			if (supp.reputation <= maxReputation) {
				itemIdSet.addAll(supp.pair.keySet());
			} else {
				goodItemId.addAll(supp.pair.keySet());
			}
		}
		Set<Long> intersection = new HashSet<Long>(itemIdSet); // use the copy constructor
		intersection.retainAll(goodItemId);
		itemIdSet.removeAll(intersection);

		Iterator<Long> itr = itemIdSet.iterator();
		while (itr.hasNext()) {
			Long itemID = itr.next();
			Set<Long> descSet = itemHMap.get(itemID);
			itemHMap.remove(itemID); // Update itemHMap
			if (descSet != null) {
				Iterator<Long> descItr = descSet.iterator();
				while (descItr.hasNext()) {
					descriptionHMap.get(descItr.next()).remove(itemID); // Update descriptionHMap
				}
			}
			// Update supplierTMap
			for (Long id : supplierTMap.keySet()) {
				Supplier supp = supplierTMap.get(id);
				if (supp.pair.containsKey(itemID))
					supp.pair.remove(itemID);
			}
			// Update itemSupPriceHMap
			itemSupPriceHMap.remove(itemID);
		}

		return itemIdSet.toArray(new Long[itemIdSet.size()]);
	}

	/*
	 * remove item from storage. Returns the sum of the Longs that are in the
	 * description of the item deleted (or 0, if such an id did not exist).
	 */
	public Long remove(Long id) {
		long descSum = 0;
		if (itemHMap.containsKey(id)) {
			TreeSet<Long> descSet = itemHMap.remove(id);
			for (Long desc : descSet) {
				// Remove from descriptionHMap
				Set<Long> itemIds = descriptionHMap.get(desc);
				itemIds.remove(id);
				descSum += desc;
			}
		}
		// Remove from itemSupPriceHMap
		itemSupPriceHMap.remove(id);
		itemHMap.remove(id); // Remove from itemHMap
		// Remove from the SupplierHashMap
		for (Long suppId : supplierTMap.keySet()) {
			Supplier supp = supplierTMap.get(suppId);
			if (supp != null && supp.pair != null && supp.pair.containsKey(id))
				supp.pair.remove(id);
		}
		return descSum;
	}

	/*
	 * remove from the given id's description those elements that are in the given
	 * array. It is possible that some elements of the array are not part of the
	 * item's description. Return the number of elements that were actually removed
	 * from the description.
	 */
	public int remove(Long id, Long[] arr) {
		int countDesc = 0;
		TreeSet<Long> descSet = itemHMap.get(id);
		for (int i = 0; i < arr.length; i++) {
			if (descSet.contains(arr[i])) {
				descSet.remove(arr[i]);
				++countDesc;
				// Remove from descriptionHMap
				descriptionHMap.get(arr[i]).remove(id);
				if (descriptionHMap.get(arr[i]) == null)
					descriptionHMap.remove(arr[i]);
			}
		}
		return countDesc;
	}

	/*
	 * remove the elements of the array from the description of all items. Return
	 * the number of items that lost one or more terms from their descriptions.
	 */
	public int removeAll(Long[] arr) {
		Set<Long> itemIds = new HashSet<>();
		for (int i = 0; i < arr.length; i++) {
			if (descriptionHMap.containsKey(arr[i])) {
				itemIds.addAll(descriptionHMap.get(arr[i]));
				descriptionHMap.remove(arr[i]); // remove the description from descriptionHMap as well
			}
			Iterator<Long> it = itemIds.iterator();
			while (it.hasNext()) {
				itemHMap.get(it.next()).remove(arr[i]);
			}
		}

		return itemIds.size();
	}
}