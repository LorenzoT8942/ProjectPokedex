package it.lorenzotanzi.pokedex;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
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
        Log.d("MAIN", "initializing recycler view");
        mRecyclerView = findViewById(R.id.rv_pkmn);
        mLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration itemDecorator = (new DividerItemDecoration(MainActivity.this, DividerItemDecoration.VERTICAL));
        itemDecorator.setDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.rv_divider));
        mRecyclerView.addItemDecoration(itemDecorator);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new PokemonRvAdapter(R.layout.cardview_pokemon_detail);
        mRecyclerView.setAdapter(mAdapter);
    }

    private void initListeners(){
    }

    private void initObservers(){
        Log.d("MAIN", "initializing observers");
        mViewModel.getAllPokemons().observe(this, new Observer<List<Pokemon>>() {
            @Override
            public void onChanged(List<Pokemon> pokemons) {
                mAdapter.setPokemonList(pokemons);
            }
        });
    }

    /* --- test per l'icona about in menu --- */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
}
