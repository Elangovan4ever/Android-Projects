package elangotsharva.mylistfragmenttest;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity implements Fragment1.Fragment1Listener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if(savedInstanceState != null)
            return;

        if(findViewById(R.id.fragment_container2) != null) {
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container1, new elangotsharva.mylistfragmenttest.Fragment1()).commit();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container2, new elangotsharva.mylistfragmenttest.Fragment2()).commit();
        }
        else{
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container1, new elangotsharva.mylistfragmenttest.Fragment1()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragment1ListItemClick(int itemPos) {
        elangotsharva.mylistfragmenttest.Fragment2 fragment2 = (Fragment2) getSupportFragmentManager().findFragmentById(R.id.fragment_container2);

        if(fragment2 != null){
            fragment2.showSelectedItem(itemPos);
        }
        else{
            fragment2 = new Fragment2();
            Bundle args = new Bundle();
            args.putInt(Fragment2.ITEM_POSITION, itemPos);
            fragment2.setArguments(args);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container1, fragment2)
                    .addToBackStack(null)
                    .commit();

        }
    }
}
