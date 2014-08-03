package com.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class SampleListActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sample_list);

        ListView listView = (ListView) findViewById(R.id.listview_samples);
        ArrayAdapter<Item> adapter = new ArrayAdapter<Item>(this, android.R.layout.simple_list_item_1);
        adapter.add(new Item(SampleActivity.class));
        adapter.add(new Item(SampleFragmentActivity.class));
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Item item = (Item) parent.getAdapter().getItem(position);
                item.startActivity(SampleListActivity.this);
            }
        });
    }

    class Item {
        Class<? extends Activity> cls;

        Item(Class<? extends Activity> cls) {
            this.cls = cls;
        }

        @Override
        public String toString() {
            return cls.getSimpleName();
        }

        void startActivity(Activity activity) {
            Intent intent = new Intent(activity, cls);
            activity.startActivity(intent);
        }
    }
}
