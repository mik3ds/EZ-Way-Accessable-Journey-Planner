package com.example.user.testnav2;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mark on 10/7/2018.
 */

public class stepByStep3 extends Activity{
    private List<StepInfo> infolist = new ArrayList<StepInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview);
        stepAdapter adapter = new stepAdapter(stepByStep3.this, R.layout.stepinfo, infolist);
        initstepinfo();
        ListView listview = (ListView) findViewById(R.id.list_view);
        listview.setAdapter(adapter);
    }

    private void initstepinfo(){
        //Sample
        StepInfo firststep = new StepInfo("firststep", R.drawable.ic_arrow_back_white);
        infolist.add(firststep);
    }
}
