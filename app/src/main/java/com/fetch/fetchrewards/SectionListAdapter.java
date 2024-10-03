package com.fetch.fetchrewards;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * SectionListAdapter is a custom adapter for displaying items and headers in a ListView.
 * It supports two types of views: item views and header views.
 */
public class SectionListAdapter extends BaseAdapter {
    private static final int ITEM = 0;
    private static final int HEADER = 1;
    private final ArrayList<Object> list;
    private final LayoutInflater inflater;

    /**
     * Constructor for SectionListAdapter.
     *
     * @param context The context in which the adapter is running.
     * @param list    The list of items and headers to be displayed.
     */
    public SectionListAdapter(Context context, ArrayList<Object> list) {
        this.list = list;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        return (list.get(position) instanceof Item) ? ITEM : HEADER;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflateViewBasedOnType(position);
        }

        bindViewBasedOnType(position, convertView);
        return convertView;
    }

    /**
     * Inflates the appropriate view based on the item type.
     *
     * @param position The position of the item in the list.
     * @return The inflated view.
     */
    private View inflateViewBasedOnType(int position) {
        switch (getItemViewType(position)) {
            case ITEM:
                return inflater.inflate(R.layout.list_item, null);
            case HEADER:
                return inflater.inflate(R.layout.list_group, null);
            default:
                throw new IllegalStateException("Unexpected view type: " + getItemViewType(position));
        }
    }

    /**
     * Binds the data to the view based on the item type.
     *
     * @param position    The position of the item in the list.
     * @param convertView The view to bind data to.
     */
    private void bindViewBasedOnType(int position, View convertView) {
        switch (getItemViewType(position)) {
            case ITEM:
                TextView itemTextView = convertView.findViewById(R.id.textView1);
                itemTextView.setText(list.get(position).toString());
                break;
            case HEADER:
                TextView headerTextView = convertView.findViewById(R.id.list_groupId);
                headerTextView.setText((String) list.get(position));
                break;
        }
    }
}
