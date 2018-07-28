package com.lnquy065.qstation.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.lnquy065.qstation.GraphActivity;
import com.lnquy065.qstation.MainMapActivity;
import com.lnquy065.qstation.R;
import com.lnquy065.qstation.pojos.NodeInfoChild;

import java.util.List;

public class NodeInfoAdapter extends ArrayAdapter<NodeInfoChild> {
    private GraphActivity context;
    private int resource;
    private List<NodeInfoChild> objects;

    public NodeInfoAdapter(@NonNull GraphActivity context, int resource, @NonNull List<NodeInfoChild> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = context.getLayoutInflater().inflate(resource, null);

        TextView txtNodeId = row.findViewById(R.id.txtNodeId);
        TextView txtNodeName = row.findViewById(R.id.txtNodeName);
        ImageButton btnViewChart = row.findViewById(R.id.btnViewChart);
        ImageButton btnViewOnMap = row.findViewById(R.id.btnViewOnMap);

        final NodeInfoChild node = objects.get(position);

        txtNodeId.setText(node.getNodeID());
        txtNodeName.setText(node.getName());

        btnViewChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.refeshGraph(GraphActivity.GRAPH_TYPE_CO2, node.getNodeID());
            }
        });

        btnViewOnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent map = new Intent(context, MainMapActivity.class);
                map.putExtra("Node", node);
                context.startActivity(map);
                context.finish();
            }
        });

        return row;
    }
}
