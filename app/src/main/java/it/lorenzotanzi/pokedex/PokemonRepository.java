package it.lorenzotanzi.pokedex;

import android.app.Application;
import android.os.AsyncTask;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.lang.reflect.Method;
import java.util.IdentityHashMap;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

class PokemonRepository implements Response.Listener, Response.ErrorListener {

    private PokemonRoomDatabase db;
    private PokemonDao pokemonDao;
    private RequestQueue requestQueue;
    private LiveData<List<Pokemon>> allPokemons;
    private MutableLiveData<List<Pokemon>> searchResults = new MutableLiveData<>();


    //CONSTRUCTOR
    PokemonRepository(Application application) {
        db = PokemonRoomDatabase.getDatabase(application);
        pokemonDao = db.pokemonDao();
        allPokemons = pokemonDao.getAllPokemons();
        if (allPokemons == null){
            //fetch data from API and insert in DB
        }

        requestQueue = Volley.newRequestQueue(application);
    }


    //METHODS TO BE CALLED BY VIEW-MODEL
    LiveData<List<Pokemon>> getAllPokemons(){
        return allPokemons;
    }

    MutableLiveData<List<Pokemon>> getSearchResults(){
        return searchResults;
    }

    void insert(Pokemon pokemon) {
        new InsertPokemonAsyncTask(pokemonDao).execute(pokemon);
    }

//ASYNC TASKS RESPONSIBLE FOR QUERIES

    private void asyncFinished(List<Pokemon> pokemons) {
        searchResults.setValue(pokemons);
    }

    @Override
    public void onErrorResponse(VolleyError error) {

    }

    @Override
    public void onResponse(Object response) {

    }

    //QUERIES THE DB AND STORES THE RESULT IN searchResults
    private static class FindPokemonsAsyncTask extends AsyncTask<String, Void, List<Pokemon>>{

        private PokemonDao asyncTaskDao;
        private PokemonRepository delegate;

        public FindPokemonsAsyncTask(PokemonDao asyncTaskDao){
            this.asyncTaskDao = asyncTaskDao;
        }

        @Override
        protected List<Pokemon> doInBackground(String... strings) {
            return asyncTaskDao.findPokemon(strings[0]);
        }

        @Override
        protected void onPostExecute(List<Pokemon> result) {
            delegate.asyncFinished(result);
        }
    }


            //INSERT A POKEMON RECORD IN THE DB
    private static class InsertPokemonAsyncTask extends AsyncTask<Pokemon, Void, Void> {

        private PokemonDao asyncTaskDao;
        private PokemonRepository delegate = null;

        InsertPokemonAsyncTask(PokemonDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Pokemon... params) {
            asyncTaskDao.insertPokemon(params[0]);
            return null;
        }
    }

    private void apiCall (String url){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, this, this);
        requestQueue.add(stringRequest);
    }

    private void getPokemonById (String id){
        String url = "https://pokeapi.co/api/v2/pokemon/%s/";
        String.format(url, id);
        apiCall(url);
    }

    private void searchAllPokemons(){


    }

}
