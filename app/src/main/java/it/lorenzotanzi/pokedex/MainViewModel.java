package it.lorenzotanzi.pokedex;

import android.app.Application;

import java.lang.invoke.MutableCallSite;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class MainViewModel extends AndroidViewModel {

    private PokemonRepository repository;
    private LiveData<List<Pokemon>> allPokemons;
    private MutableLiveData<List<Pokemon>>  searchResults;


    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = new PokemonRepository(application);
        allPokemons = repository.getAllPokemons();
        searchResults = repository.getSearchResults();
    }

    MutableLiveData<List<Pokemon>> getSearchResults(){
        return repository.getSearchResults();
    }

    LiveData<List<Pokemon>> getAllPokemons(){
        return repository.getAllPokemons();
    }

    void insertPokemon (Pokemon pokemon){
        repository.insert(pokemon);
    }
}
