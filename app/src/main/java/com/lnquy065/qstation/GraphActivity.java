package com.lnquy065.qstation;

import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.lnquy065.qstation.adapter.NodeInfoAdapter;
import com.lnquy065.qstation.chart.DateTimeValueFormatter;
import com.lnquy065.qstation.events.SingleValueEvent;
import com.lnquy065.qstation.pojos.NodeDataChild;

import java.util.ArrayList;
import java.util.List;

public class GraphActivity extends AppCompatActivity {

    public static final int GRAPH_TYPE_CO2 = 0;
    public static final int GRAPH_TYPE_DUST = 1;
    public static final int GRAPH_TYPE_UV = 2;
    public String currentNode;

    private FirebaseDatabase database;
    private DatabaseReference nodeDataRef;
    private DatabaseReference nodeInfoRef;
    private List<Entry> entryList;

    private LineChart lineChart;
    private LineDataSet dataSet;
    private NodeInfoAdapter nodeInfoAdapter;

    private ListView lvNodeList;
    private RadioButton radCo2;
    private RadioButton radUv;
    private RadioButton radDust;
    private TextView txtNodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        initControls();
        initGraphData();
        initFirebase();
        initEvents();
    }

    private void initEvents() {
        radCo2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) refeshGraph(GRAPH_TYPE_CO2, currentNode);
            }
        });

        radUv.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) refeshGraph(GRAPH_TYPE_UV, currentNode);
            }
        });


        radDust.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) refeshGraph(GRAPH_TYPE_DUST, currentNode);
            }
        });

        radCo2.performClick();
    }

    private void initFirebase() {
        database = FirebaseDatabase.getInstance();
        nodeDataRef = database.getReference("nodeData");
        nodeInfoRef = database.getReference("nodeInfo");
    }

    private void initGraphData() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setValueFormatter( new DateTimeValueFormatter());
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(true);

        YAxis yAxis = lineChart.getAxis(YAxis.AxisDependency.LEFT);
        yAxis.setZeroLineWidth(2f);
        yAxis.setGridDashedLine(new DashPathEffect(new float[]{5f, 2f}, 3f));

        lineChart.getLegend().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
    }

    private void initControls() {
        txtNodeId = findViewById(R.id.txtNodeId);
        lvNodeList = findViewById(R.id.lvNodeList);
        radCo2 = findViewById(R.id.radCo2);
        radDust = findViewById(R.id.radDust);
        radUv = findViewById(R.id.radUv);
        lineChart = findViewById(R.id.graph);

        nodeInfoAdapter = new NodeInfoAdapter(this, R.layout.nodelist_items, NodeStaticList.nodeList);
        lvNodeList.setAdapter(nodeInfoAdapter);
        nodeInfoAdapter.notifyDataSetChanged();

        currentNode = NodeStaticList.nodeList.get(0).getNodeID();
    }


    public void refeshGraph(final int valueType, String nodeID) {
        currentNode = nodeID;
        txtNodeId.setText(nodeID);
        Query maxTimeStamp = nodeDataRef.child(nodeID).orderByChild("timeStamp").limitToLast(10);
        maxTimeStamp.addListenerForSingleValueEvent(new SingleValueEvent() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount()!=0) {
                    NodeDataChild nodeDataChild;
                    entryList = new ArrayList<>();
                    dataSet = new LineDataSet(entryList, "");
                    dataSet.setCircleRadius(5f);
                    dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                    dataSet.setLineWidth(2f);
                    dataSet.setColor( Color.BLUE );
                    dataSet.setCircleColor( Color.BLUE );
                    for (DataSnapshot i: dataSnapshot.getChildren()) {
                        nodeDataChild = i.getValue(NodeDataChild.class);
                        dataSet.addEntry(new Entry( (float) nodeDataChild.getTimeStamp(),
                                (float) nodeDataChild.getVal(valueType) ));
                    }
                    LineData lineData = new LineData(dataSet);
                    lineChart.setData(lineData);
                    lineChart.invalidate();
                }

            }
        });

    }
}
