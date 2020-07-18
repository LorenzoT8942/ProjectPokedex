package it.lorenzotanzi.pokedex;

import android.app.Application;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.IdentityHashMap;
import java.util.List;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static it.lorenzotanzi.pokedex.R.*;
import static it.lorenzotanzi.pokedex.R.integer.*;

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
            searchAllPokemons();
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

    private void imgCall(String url){
        //TO DO
    }

    private void searchPokemonById (String id){
        String url = "https://pokeapi.co/api/v2/pokemon/%s/";
        String.format(url, id);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Gson gson = new Gson();
                        String pokemonJson;
                        try {
                            JSONObject jsonObject =  new JSONObject(response);
                            pokemonJson = jsonObject.toString();
                            String  newPkmnId = (String) jsonObject.getString("id");
                            String newPkmnName = (String) jsonObject.get("name").toString();
                            JSONArray newPkmnTypes = jsonObject.getJSONArray("types");
                            String Type1 = newPkmnTypes.getString(0);
                            String Type2;
                            if (newPkmnTypes.isNull(1)){
                                Type2 = null;
                            } else {
                                Type2 = newPkmnTypes.getString(1);
                            }
                            Pokemon pokemon = new Pokemon(newPkmnId, newPkmnName, Type1, Type2);
                            pokemonDao.insertPokemon(pokemon);
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
    }

    private void searchAllPokemons(){
        for (int i = 1; max_pokemons >= i; i++) searchPokemonById(Integer.toString(i));
    }

}
