package com.example.chatapp.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.chatapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;



public class CustomExpPersonalListAdapter  extends BaseExpandableListAdapter {
    private Context context;
    private List<String> expandableListTitle;
    private HashMap<String, HashMap<String,String>> expandableListDetail;

    public CustomExpPersonalListAdapter(Context context, List<String> expandableListTitle,
                                       HashMap<String, HashMap<String,String>> expandableListDetail) {
        this.context = context;
        this.expandableListTitle = expandableListTitle;
        this.expandableListDetail = expandableListDetail;
    }


    public Object getChild(int listPosition, int expandedListPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition));
    }


    public long getChildId(int listPosition, int expandedListPosition) {
        return expandedListPosition;
    }


    public View getChildView(int listPosition, final int expandedListPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final HashMap<String,String> expandedListText = (HashMap<String, String>) getChild(listPosition, expandedListPosition);
        List<String> personal = new ArrayList<>();
        personal.add("Name");
        personal.add("Email");
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_per_item, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItemTitle);
        expandedListTextView.setText(personal.get(expandedListPosition));
        TextView tvPerContent=(TextView) convertView
                .findViewById(R.id.expandedListItemContent);
        tvPerContent.setText(expandedListText.get(personal.get(expandedListPosition)));
        return convertView;
    }


    public int getChildrenCount(int listPosition) {
        return this.expandableListDetail.get(this.expandableListTitle.get(listPosition))
                .size();
    }


    public Object getGroup(int listPosition) {
        return this.expandableListTitle.get(listPosition);
    }


    public int getGroupCount() {
        return this.expandableListTitle.size();
    }


    public long getGroupId(int listPosition) {
        return listPosition;
    }


    public View getGroupView(int listPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String listTitle = (String) getGroup(listPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }
        TextView listTitleTextView = (TextView) convertView.findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(listTitle);
        return convertView;
    }


    public boolean hasStableIds() {
        return false;
    }

    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
