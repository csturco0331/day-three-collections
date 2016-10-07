package com.cooksys.ftd.assignments.day.three.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cooksys.ftd.assignments.day.three.collections.hierarchy.Hierarchy;
import com.cooksys.ftd.assignments.day.three.collections.model.Capitalist;
import com.cooksys.ftd.assignments.day.three.collections.model.FatCat;

public class MegaCorp implements Hierarchy<Capitalist, FatCat> {

	private Map<FatCat, Set<Capitalist>> bossEmployeeRelations = new HashMap<>();
	private Set<Capitalist> allEmployees = new HashSet<>();

	/**
	 * Adds a given element to the hierarchy.
	 * <p>
	 * If the given element is already present in the hierarchy, do not add it
	 * and return false
	 * <p>
	 * If the given element has a parent and the parent is not part of the
	 * hierarchy, add the parent and then add the given elementSS
	 * <p>
	 * If the given element has no parent but is a Parent itself, add it to the
	 * hierarchy
	 * <p>
	 * If the given element has no parent and is not a Parent itself, do not add
	 * it and return false
	 *
	 * @param capitalist
	 *            the element to add to the hierarchy
	 * @return true if the element was added successfully, false otherwise
	 */
	@Override
	public boolean add(Capitalist capitalist) {
		if (capitalist == null || has(capitalist)) {
			return false;
		}
		if (capitalist.hasParent()) {
			if (!has(capitalist.getParent())) {
				add(capitalist.getParent());
			}
			bossEmployeeRelations.get(capitalist.getParent()).add(capitalist);
			allEmployees.add(capitalist);
		}
		if (capitalist instanceof FatCat) {
			bossEmployeeRelations.put((FatCat) capitalist, new HashSet<Capitalist>());
			allEmployees.add(capitalist);
		}
		return capitalist.hasParent() || capitalist instanceof FatCat;
	}

	/**
	 * @param capitalist
	 *            the element to search for
	 * @return true if the element has been added to the hierarchy, false
	 *         otherwise
	 */
	@Override
	public boolean has(Capitalist capitalist) {
		return allEmployees.contains(capitalist);
	}

	/**
	 * @return all elements in the hierarchy, or an empty set if no elements
	 *         have been added to the hierarchy
	 */
	@Override
	public Set<Capitalist> getElements() {
		return new HashSet<>(allEmployees);
	}

	/**
	 * @return all parent elements in the hierarchy, or an empty set if no
	 *         parents have been added to the hierarchy
	 */
	@Override
	public Set<FatCat> getParents() {
		return new HashSet<>(bossEmployeeRelations.keySet());
	}

	/**
	 * @param fatCat
	 *            the parent whose children need to be returned
	 * @return all elements in the hierarchy that have the given parent as a
	 *         direct parent, or an empty set if the parent is not present in
	 *         the hierarchy or if there are no children for the given parent
	 */
	@Override
	public Set<Capitalist> getChildren(FatCat fatCat) {
		return bossEmployeeRelations.get(fatCat) == null ? new HashSet<>() : new HashSet<>(bossEmployeeRelations.get(fatCat));
	}

	/**
	 * @return a map in which the keys represent the parent elements in the
	 *         hierarchy, and the each value is a set of the direct children of
	 *         the associate parent, or an empty map if the hierarchy is empty.
	 */
	@Override
	public Map<FatCat, Set<Capitalist>> getHierarchy() {
		Map<FatCat, Set<Capitalist>> result = new HashMap<>();
		for (FatCat i : bossEmployeeRelations.keySet()) {
			result.put(i, new HashSet<>(bossEmployeeRelations.get(i)));
		}
		return result;
	}

	/**
	 * @param capitalist
	 * @return the parent chain of the given element, starting with its direct
	 *         parent, then its parent's parent, etc, or an empty list if the
	 *         given element has no parent or if its parent is not in the
	 *         hierarchy
	 */
	@Override
	public List<FatCat> getParentChain(Capitalist capitalist) {
		List<FatCat> result = new ArrayList<>();
		if (capitalist != null && has(capitalist)) {
			FatCat iterator = capitalist.getParent();
			while (iterator != null) {
				result.add(iterator);
				iterator = iterator.getParent();
			}
		}
		return result;
	}
}
