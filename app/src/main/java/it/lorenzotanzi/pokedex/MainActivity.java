package it.lorenzotanzi.pokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mViewModel;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private PokemonRvAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MAIN", "onCreate");
        MyViewModelFactory myViewModelFactory = new MyViewModelFactory(this.getApplication());
        mViewModel = new ViewModelProvider(this, myViewModelFactory).get(MainViewModel.class);
        initListeners();
        initObservers();
        initRecyclerView();
    }



    private void initRecyclerView(){
        Log.d("MAIN", "initializing recyler view");
        mRecyclerView = findViewById(R.id.rv_pkmn);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PokemonRvAdapter(R.layout.cardview_pokemon_detail);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListeners(){
    }

    private void initObservers(){
        Log.d("MAIN", "initializing obervers");
        mViewModel.getAllPokemons().observe(this, new Observer<List<Pokemon>>() {
            @Override
            public void onChanged(List<Pokemon> pokemons) {
                mAdapter.setPokemonList(pokemons);
            }
        });
    }
}
