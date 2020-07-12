package it.lorenzotanzi.pokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initListeners();
        initObservers();
        initRecyclerView();
    }



    private void initRecyclerView(){
        mRecyclerView = findViewById(R.id.rv_pkmn);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PokemonRvAdapter(R.layout.cardview_pokemon_detail);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListeners(){

    }

    private void initObservers(){

    }
}
