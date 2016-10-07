package com.cooksys.ftd.assignments.day.three.collections.properties;

import com.cooksys.ftd.assignments.day.three.collections.MegaCorp;
import com.cooksys.ftd.assignments.day.three.collections.generators.Cap;
import com.cooksys.ftd.assignments.day.three.collections.generators.Cat;
import com.cooksys.ftd.assignments.day.three.collections.generators.Corp;
import com.cooksys.ftd.assignments.day.three.collections.generators.Slave;
import com.cooksys.ftd.assignments.day.three.collections.model.Capitalist;
import com.cooksys.ftd.assignments.day.three.collections.model.FatCat;
import com.cooksys.ftd.assignments.day.three.collections.model.WageSlave;
import com.pholser.junit.quickcheck.Property;
import com.pholser.junit.quickcheck.runner.JUnitQuickcheck;
import org.junit.runner.RunWith;

import java.util.*;

import static org.junit.Assert.*;

@RunWith(JUnitQuickcheck.class)
public class MegaCorpProperties {


    @Property
    public void addNull(@Corp MegaCorp corp) {
        assertFalse("#add() returned true when called with null", corp.add(null));
    }

    @Property
    public void addHasParentlessWageSlave(@Corp MegaCorp corp, @Slave WageSlave slave) {
        assertFalse("#add() returned true when called with a parent-less WageSlave", corp.add(slave));
        assertFalse("#has() returned true when called with a parent-less WageSlave that failed to be added", corp.has(slave));
    }

    @Property
    public void addHasParentlessFatCat(@Corp MegaCorp corp, @Cat FatCat cat) {
        assertTrue("#add() returned false when called with a parent-less FatCat", corp.add(cat));
        assertTrue("#has() returned false when called with the parent-less FatCat that was just added", corp.has(cat));
    }

    @Property
    public void addHasCapitalistWithParent(@Corp MegaCorp corp, @Cap(depth = 1) Capitalist cap) {
        assertTrue("#add() returned false when called with a Capitalist", corp.add(cap));
        assertTrue("#has() returned false when called with the parent of the Capitalist that was just added", corp.has(cap));
        assertTrue("#has() returned false when called with the Capitalist that was just added", corp.has(cap));
    }

    @Property
    public void addHasMultipleArbitraryCapitalists(@Corp MegaCorp corp, Set<@Cap(depth = -1) Capitalist> capitalists) {
        for (Capitalist capitalist : capitalists) {
            assertTrue("#add() returned false when called with an Capitalist", corp.add(capitalist));
            assertTrue("#has() returned false when called with the Capitalist that was just added", corp.has(capitalist));
            while (capitalist.hasParent()) {
                capitalist = capitalist.getParent();
                assertTrue("#has() returned false when called with a parent of the Capitalist that was just added", corp.has(capitalist));
            }
        }
    }

    @Property
    public void addHasMultipleArbitraryCapitalistNoDuplicates(@Corp MegaCorp corp, Set<@Cap(depth = -1) Capitalist> capitalists) {
        for (Capitalist capitalist : capitalists) {
            assertTrue("#add() returned false when called with an arbitrary Capitalist", corp.add(capitalist));
            assertTrue("#has() returned false when called with the arbitrary Capitalist that was just added", corp.has(capitalist));
            assertFalse("#add() returned true when called with the arbitrary Capitalist that was just added", corp.add(capitalist));
        }

        for (Capitalist capitalist : capitalists) {
            assertTrue("#has() returned false when called with a previously-added Capitalist", corp.has(capitalist));
            assertFalse("#add() returned true when called with a previously-added Capitalist after adding multiple Capitalists", corp.add(capitalist));
        }
    }

    @Property
    public void getElementsEmpty(@Corp MegaCorp corp) {
        Set<Capitalist> elements = corp.getElements();
        assertNotNull("#getElements() returned a null value when called on an empty MegaCorp", elements);
        assertTrue("#getElements() returned a non-empty set when called on an empty MegaCorp", elements.isEmpty());
    }

    @Property
    public void getElementsDefensiveCopy(@Corp MegaCorp corp, @Cap(depth = -1) Capitalist capitalist) {
        Set<Capitalist> elements = corp.getElements();
        elements.add(capitalist);
        assertFalse("#getElements() returned a live set of elements that allowed external changes to the MegaCorp", corp.has(capitalist));
        elements = corp.getElements();
        assertFalse("#getElements() returned a live set of elements that allowed external changes to the MegaCorp", elements.contains(capitalist));
    }

    @Property
    public void getElementsMultipleArbitraryCapitalists(@Corp MegaCorp corp, Set<@Cap(depth = -1) Capitalist> capitalists) {
        Set<Capitalist> expected = new HashSet<>(capitalists);
        for (Capitalist capitalist : capitalists) {
            corp.add(capitalist);
            while (capitalist != null) {
                expected.add(capitalist);
                capitalist = capitalist.getParent();
            }
        }
        Set<Capitalist> elements = corp.getElements();
        assertEquals("#getElements() returned a set that did not equal the set of previously-added Capitalists and their parents", expected, elements);
    }

    @Property
    public void getParentsEmpty(@Corp MegaCorp corp) {
        Set<FatCat> parents = corp.getParents();
        assertNotNull("#getParents() returned a null value when called on an empty MegaCorp", parents);
        assertTrue("#getParents() returned a non-empty set when called on an empty MegaCorp", parents.isEmpty());
    }

    @Property
    public void getParentsDefensiveCopy(@Corp MegaCorp corp, @Cat FatCat cat) {
        Set<FatCat> parents = corp.getParents();
        parents.add(cat);
        assertFalse("#getParents() returned a live set of parents that allowed external changes to the MegaCorp", corp.has(cat));
        parents = corp.getParents();
        assertFalse("#getParents() returned a live set of parents that allowed external changes to the MegaCorp", parents.contains(cat));
    }

    @Property
    public void getParentsFatCat(@Corp MegaCorp corp, @Cat FatCat cat) {
        corp.add(cat);
        Set<FatCat> parents = corp.getParents();
        assertFalse("#getParents() returned an empty set after adding a FatCat to the MegaCorp", parents.isEmpty());
        assertEquals("#getParents() returned a set with size > 1 after adding a single FatCat to the MegaCorp", 1, parents.size());
    }

    @Property
    public void getParentsWageSlaveWithParent(@Corp MegaCorp corp, @Slave(depth = 1) WageSlave slave) {
        corp.add(slave);
        FatCat parent = slave.getParent();
        Set<FatCat> parents = corp.getParents();
        assertFalse("#getParents() returned an empty set after adding a WageSlave with a Parent to the MegaCorp", parents.isEmpty());
        assertEquals("#getParents() returned a set with size > 1 after adding a single WageSlave with a single Parent to the MegaCorp", 1, parents.size());
        assertTrue("#getParents() returned a set that did not contain the parent of the WageSlave added to the MegaCorp", parents.contains(parent));
    }

    @Property
    public void getParentsMultipleArbitraryCapitalists(@Corp MegaCorp corp, Set<@Cap(depth = -1) Capitalist> capitalists) {
        Set<FatCat> expected = new HashSet<>();
        for (Capitalist capitalist : capitalists) {
            corp.add(capitalist);
            FatCat parent = capitalist instanceof FatCat ? (FatCat) capitalist : capitalist.getParent();
            while (parent != null) {
                expected.add(parent);
                parent = parent.getParent();
            }
        }
        Set<FatCat> parents = corp.getParents();
        assertEquals("#getParents() returned a set that did not equal the set of all parents of the added Capitalists", expected, parents);
    }

    @Property
    public void getChildrenEmpty(@Corp MegaCorp corp, @Cat FatCat cat) {
        Set<Capitalist> children = corp.getChildren(cat);
        assertNotNull("#getChildren() returned a null value when called on an empty MegaCorp", children);
        assertTrue("#getChildren() returned a non-empty set when called on an empty MegaCorp", children.isEmpty());
    }

    @Property
    public void getChildrenDefensiveCopy(@Corp MegaCorp corp, @Cat FatCat cat, @Cap Capitalist capitalist) {
        corp.add(cat);
        Set<Capitalist> children = corp.getChildren(cat);
        assertTrue("#getChildren() returned a non-empty set after adding a parentless FatCat to the MegaCorp", children.isEmpty());
        children.add(capitalist);
        assertFalse("#getChildren() returned a live set that allowed external changes to the MegaCorp", corp.has(capitalist));
        children = corp.getChildren(cat);
        assertFalse("#getChildren() returned a live set that allowed external changes to the MegaCorp", children.contains(capitalist));
    }

    @Property
    public void getChildrenFatCatWithParent(@Corp MegaCorp corp, @Cat(depth = 1) FatCat cat) {
        corp.add(cat);
        Set<Capitalist> children = corp.getChildren(cat);
        assertTrue("#getChildren() returned a non-empty set when called with a previously-added FatCat that has a single parent", children.isEmpty());
        children = corp.getChildren(cat.getParent());
        assertTrue("#getChildren() returned a set that does not contain the previously-added FatCat when called with its parent", children.contains(cat));
    }

    @Property
    public void getChildrenMultipleCapitalistsWithSharedParent(@Corp MegaCorp corp, @Cat FatCat parent, Set<@Cap Capitalist> children) {
        corp.add(parent);
        Set<Capitalist> expected = new HashSet<>();
        for (Capitalist parentless : children) {
            Capitalist withParent =
                    parentless instanceof FatCat
                            ? new FatCat(parentless.getName(), parentless.getSalary(), parent)
                            : new WageSlave(parentless.getName(), parentless.getSalary(), parent);
            corp.add(withParent);
            expected.add(withParent);
        }
        assertEquals("@getChildren() returned a set that did not equal the set of children of the previously-added FatCat", expected, corp.getChildren(parent));
    }

    @Property
    public void getChildrenMultipleCapitalistsSomeWithSharedParent(@Corp MegaCorp corp, @Cat FatCat parent, Set<@Cap Capitalist> children, Set<@Cap Capitalist> loose) {
        corp.add(parent);
        Set<Capitalist> expected = new HashSet<>();
        for (Capitalist parentless : children) {
            Capitalist withParent =
                    parentless instanceof FatCat
                            ? new FatCat(parentless.getName(), parentless.getSalary(), parent)
                            : new WageSlave(parentless.getName(), parentless.getSalary(), parent);
            corp.add(withParent);
            expected.add(withParent);
        }

        loose.forEach(corp::add);
        assertEquals("#getChildren() returned a set that did not equal the set of children of a previously-added FatCat after adding loose capitalists", expected, corp.getChildren(parent));
    }

    @Property
    public void getHierarchyEmpty(@Corp MegaCorp corp) {
        Map<FatCat, Set<Capitalist>> hierarchy = corp.getHierarchy();
        assertNotNull("#getHierarchy() returned a null value when called on an empty MegaCorp", hierarchy);
        assertTrue("#getHierarchy() returned a non-empty Map when called on an empty MegaCorp", hierarchy.isEmpty());
    }

    @Property
    public void getHierarchyInitializesChildSets(@Corp MegaCorp corp, @Cat FatCat cat) {
        corp.add(cat);
        Set<Capitalist> children = corp.getHierarchy().get(cat);
        assertNotNull("#getHierarchy() returned a map with a null value for a previously-added childless FatCat key", children);
        assertTrue("#getHierarchy() returned a map with a non-empty set for a previously-added childless FatCat key", children.isEmpty());
    }

    @Property
    public void getHierarchyDefensiveCopy(@Corp MegaCorp corp, @Slave(depth = 1) WageSlave slave) {
        Set<Capitalist> children = new HashSet<>();
        children.add(slave);
        Map<FatCat, Set<Capitalist>> hierarchy = corp.getHierarchy();
        hierarchy.put(slave.getParent(), children);
        assertFalse("#getHierarchy() returned a live map that allowed external changes to the MegaCorp", corp.has(slave.getParent()));
        assertFalse("#getHierarchy() returned a live map that allowed external changes to the MegaCorp", corp.has(slave));

        hierarchy = corp.getHierarchy();
        assertNotEquals("#getHierarchy() returned a live map that allowed external changes to the MegaCorp", children, hierarchy.get(slave.getParent()));
        assertFalse("#getHierarchy() returned a live map that allowed external changes to the MegaCorp", hierarchy.containsKey(slave.getParent()));

        corp.add(slave.getParent());
        hierarchy = corp.getHierarchy();
        children = hierarchy.get(slave.getParent());

        children.add(slave);
        assertFalse("#getHierarchy() returned a live map that allowed external changes to the MegaCorp", corp.has(slave));

        hierarchy = corp.getHierarchy();
        assertFalse("#getHierarchy() returned a live map that allowed external changes to the MegaCorp", hierarchy.get(slave.getParent()).contains(slave));
    }

    @Property
    public void getHierarchyConsistencyWithOneLevel(@Corp(depth = 5) MegaCorp corp) {
        Map<FatCat, Set<Capitalist>> hierarchy = corp.getHierarchy();
        Set<FatCat> expectedParents = corp.getParents();
        assertEquals("#getHierarchy() returned a map with a key set that did not match the MegaCorp's parents", expectedParents, hierarchy.keySet());
        Set<Capitalist> actualElements = new HashSet<>();
        for (FatCat parent : expectedParents) {
            actualElements.add(parent);
            Set<Capitalist> expectedChildren = corp.getChildren(parent);
            actualElements.addAll(expectedChildren);
            assertEquals("#getHierarchy() returned a map in which a key's associated set of values did not match the MegaCorp's children for that key", expectedChildren, hierarchy.get(parent));
        }
        assertEquals(corp.getElements(), actualElements);
    }

    @Property
    public void getParentChainEmpty(@Corp MegaCorp corp, @Cap(depth = -1) Capitalist capitalist) {
        List<FatCat> actual = corp.getParentChain(null);
        assertNotNull("#getParentChain() returned a null value when called on an empty corp with null", actual);
        assertTrue("#getParentChain() returned a non-empty list when called on an empty corp with null", actual.isEmpty());

        actual = corp.getParentChain(capitalist);
        assertNotNull("#getParentChain() returned a null value when called on an empty corp with an arbitrary Capitalist", actual);
        assertTrue("#getParentChain() returned a non-empty list when called on an empty corp with an arbitrary Capitalist", actual.isEmpty());
    }

    @Property
    public void getParentChainMatchesInternalStructure(@Corp(depth = 5) MegaCorp corp, @Cap(depth = -1) Capitalist capitalist) {
        corp.add(capitalist);
        FatCat parent = capitalist.getParent();
        List<FatCat> expected = new LinkedList<>();
        while (parent != null) {
            expected.add(parent);
            parent = parent.getParent();
        }
        assertEquals("#getParentChain() returned a list that did not match the calculated structure of the arbitrary Capitalist that was just added to the MegaCorp", expected, corp.getParentChain(capitalist));
    }
}
