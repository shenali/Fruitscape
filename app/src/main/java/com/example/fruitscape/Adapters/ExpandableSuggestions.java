package com.example.fruitscape.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.fruitscape.R;

import java.util.HashMap;
import java.util.List;

public class ExpandableSuggestions extends BaseExpandableListAdapter {

    private Context context;
    private HashMap<String, List<String>> listHashMap;
    private List<String> headersList;

    public ExpandableSuggestions(Context context, HashMap<String, List<String>> listHashMap, List<String> headersList) {
        this.context = context;
        this.listHashMap = listHashMap;
        this.headersList = headersList;
    }

    @Override
    public int getGroupCount() {
        return headersList.size();
    }

    @Override
    public int getChildrenCount(int position) {
        return listHashMap.get(headersList.get(position)).size();
    }

    @Override
    public Object getGroup(int position) {
        return headersList.get(position);
    }

    @Override
    public Object getChild(int position, int childPosition) {
        return listHashMap.get(headersList.get(position)).get(childPosition);
    }

    @Override
    public long getGroupId(int position) {
        return position;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int position, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(position);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_list_group,null);
        }
        TextView listHeader = (TextView)convertView.findViewById(R.id.list_header);
        listHeader.setText(headerTitle);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String)getChild(groupPosition,childPosition);
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_items,null);
        }

        TextView listChildren = (TextView)convertView.findViewById(R.id.list_item);
        listChildren.setText(childText);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
