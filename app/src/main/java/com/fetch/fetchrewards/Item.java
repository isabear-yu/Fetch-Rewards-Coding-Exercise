package com.fetch.fetchrewards;

/**
 * Represents an item with an ID, list ID, and item number.
 */
public class Item {
    private int id;
    private int listId;
    private int itemNum;

    /**
     * Constructs an Item with the specified ID, list ID, and item number.
     *
     * @param id      The unique identifier for the item.
     * @param listId  The identifier for the list to which the item belongs.
     * @param itemNum The item number within the list.
     */
    public Item(int id, int listId, int itemNum) {
        this.id = id;
        this.listId = listId;
        this.itemNum = itemNum;
    }

    /**
     * Returns the unique identifier for the item.
     *
     * @return The item ID.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the unique identifier for the item.
     *
     * @param id The item ID.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Returns the identifier for the list to which the item belongs.
     *
     * @return The list ID.
     */
    public int getListId() {
        return listId;
    }

    /**
     * Sets the identifier for the list to which the item belongs.
     *
     * @param listId The list ID.
     */
    public void setListId(int listId) {
        this.listId = listId;
    }

    /**
     * Returns the item number within the list.
     *
     * @return The item number.
     */
    public int getItemNum() {
        return itemNum;
    }

    /**
     * Sets the item number within the list.
     *
     * @param itemNum The item number.
     */
    public void setItemNum(int itemNum) {
        this.itemNum = itemNum;
    }

    @Override
    public String toString() {
        return "Item {" + "\n" +
                "id: " + id + "\n" +
                "listId: " + listId + "\n" +
                "name: Item " + itemNum + "\n" +
                '}';
    }
}
