package it.lorenzotanzi.pokedex;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import it.lorenzotanzi.pokedex.cache.FromUrlToBitmap;
import it.lorenzotanzi.pokedex.interfaces.SelectMode;

public class PokemonRvAdapter extends RecyclerView.Adapter<PokemonRvAdapter.ViewHolder> implements View.OnClickListener, View.OnLongClickListener, Filterable {

    private Map<String, String> colors = new HashMap<>();
    private int pokemonItemLayout;
    private Context context;
    private List<Pokemon> pokemonList;
    private List<Pokemon> supportPokemonList; // needed for filter research
    private SelectMode mListener;
    private SparseBooleanArray selectedList = new SparseBooleanArray();
    private List<Pokemon> favorPkmnList = new ArrayList<>(); // need for not sure choices
    private List<Pokemon> supportFavorPkmnList; // need for confermed choices - removed redundant initializer
    private PokemonDao pokemonDao; // new add

    //private PokemonRepository pokeRepo;

    //PokemonRvAdapter(){}

    PokemonRvAdapter(int layoutId, Context context){
        pokemonItemLayout = layoutId;
        this.context = context;

        mListener = (SelectMode) context;

        PokemonRoomDatabase db = PokemonRoomDatabase.getDatabase(this.context);
        pokemonDao = db.pokemonDao();

        // problema qui
        //pokeRepo = new PokemonRepository(); /* così chiamo la sua DAO (conviene chiamare un opportuno metodo che fa quel che deve fare)
        //                                     * probabile gestione di sincronizzazione*/

        //supportFavorPkmnList = pokeRepo.getFavorites(true); // probably do not contains favorites
        supportFavorPkmnList = pokemonDao.getAllFavorites(true); // BISOGNA DEMANDARE A POKEMON-REPOSITORY L'INTERAZIONE CON LA DAO

        colors.put("Normal", "#A8A77A");
        colors.put("Fire", "#EE8130");
        colors.put("Water","#6390F0");
        colors.put("Electric", "#F7D02C");
        colors.put("Grass", "#7AC74C");
        colors.put("Ice", "#96D9D6");
        colors.put("Fighting", "#C22E28");
        colors.put("Poison", "#A33EA1");
        colors.put("Ground", "#E2BF65");
        colors.put("Flying", "#A98FF3");
        colors.put("Psychic", "#F95587");
        colors.put("Bug", "#A6B91A");
        colors.put("Rock", "#B6A136");
        colors.put("Ghost", "#735797");
        colors.put("Dragon", "#6F35FC");
        colors.put("Dark", "#705746");
        colors.put("Steel", "#B7B7CE");
        colors.put("Fairy", "#D685AD");

    }

    // PokemonRvAdapter(){} /* forse serve per far eseguire le ultime due azioni della 'fillFavorPkmnList'
    // dal metodo async di pokeRepo*/

    void setPokemonList(List<Pokemon> pokemons){
        pokemonList = pokemons;

        /* new add necessary for filter search */
        supportPokemonList = new ArrayList<>(pokemonList);

        notifyDataSetChanged();
    }


    // alla seconda iterazione quando parte (da preferiti a main) bisogna fillare la favorite list dal db
    @NonNull
    @Override
    public PokemonRvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pokemon_detail, parent, false);

        CardView cv = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pokemon_detail, parent, false);
        cv.setOnClickListener(this);
        cv.setOnLongClickListener(this);

        reordering();

        return new ViewHolder(cv);

        //return new ViewHolder(v);

    }

    @SuppressLint({"CheckResult", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull PokemonRvAdapter.ViewHolder viewHolder, final int position) {

        Log.d("ON BIND VIEW HOLDER","POKEMON RV ADAPTER");

        TextView tv_pkmn_num = viewHolder.tv_pkmn_num;
        TextView tv_pkmn_name = viewHolder.tv_pkmn_name;
        TextView tv_pkmn_type1 = viewHolder.tv_pkmn_type1;
        TextView tv_pkmn_type2 = viewHolder.tv_pkmn_type2;
        ImageView iv_pkmn_icon = viewHolder.iv_pkmn_icon;
        View cardView = viewHolder.cardView;
        ImageView iv_pkmn_status = viewHolder.iv_pkmn_status; // new add


       String idString = pokemonList.get(position).getPkmnNum().toString();
       String pkmnNameString = pokemonList.get(position).getPkmnName();
       String type1str = pokemonList.get(position).getType1();
       String type2str = pokemonList.get(position).getType2();
       String type1col = colors.get(type1str);

       /* aggiungere il fetch della immagine (vedere PokemonRepository) */


        // problema qui all'atto della ricerca dinamica (FILTER SEARCH) poichè viene aggiunta l'immagine
        // in base al numero e non al nome conviene salvare l'immagine in locale e fare poi il fetch da
        // lì in base al nome come fatto per i 'FavoritesPokemonRvAdapter'

        iv_pkmn_icon.setImageResource(R.drawable.pokeball); // new add

        final String imgPkmnUrl = pokemonList.get(position).getImg(); // new add


        /*if(pokemonList.get(position).getImg().equals("")) {
            //REQUEST .PNG OF THE POKEMON SPRITE TO SET
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.pokeball);
            requestOptions.error(R.drawable.pokeball);
            Glide.with(context)
                    .setDefaultRequestOptions(requestOptions)
                    .asBitmap()
                    .load("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + (position + 1) + ".png")
                    .into(iv_pkmn_icon);

        }else{ *//* al posto di questo richiamare il codice di salvataggio dell'immagine in locale
                * (come in FavoritePokemon) magari creando una classe a parte e richiamarla con una new *//*
            iv_pkmn_icon.setImageResource(R.drawable.pokeball);
            Glide.with(context).load(pokemonList.get(position).getImg()).into(iv_pkmn_icon);
        }*/

        File pokeImg = new File(context.getCacheDir() + "/pokemons" + "/" + pokemonList.get(position).getPkmnName() + ".png");
        if(!pokeImg.exists()) {
            new FromUrlToBitmap(iv_pkmn_icon, position, context, pokemonList, 1).execute(imgPkmnUrl);
        }else{
            Glide.with(context).load(pokeImg).into(iv_pkmn_icon);
        }

        //STRING ADJUSTMENTS
        if (Integer.parseInt(idString) < 10) idString = new StringBuilder().append("#00").append(idString).toString();
        else if (Integer.parseInt(idString) < 100) idString = new StringBuilder().append("#0").append(idString).toString();
        else idString = new StringBuilder().append("#").append(idString).toString();
        pkmnNameString = pkmnNameString.substring(0, 1).toUpperCase() + pkmnNameString.substring(1);

        //BINDING OF POKEMON INFO AND CREATION OF GRADIENT BACKGROUND
        tv_pkmn_num.setText(idString);
        tv_pkmn_name.setText(pkmnNameString);
        tv_pkmn_type1.setText(type1str);



        if(!pokemonList.get(position).getFavorite()){
            iv_pkmn_status.setImageResource(R.drawable.ic_pkm_free);
        }else{
            // forse si può levare
            if(!favorPkmnList.contains(pokemonList.get(position))){
                if(!supportFavorPkmnList.contains(pokemonList.get(position))) {}
            }
            iv_pkmn_status.setImageResource(R.drawable.ic_pkm_capt);
        }


        //SE IL POKEMON IN POSIZIONE position HA UN TIPO 2 ALLORA VIENE SETTATO IL TESTO DELLA TEXT VIEW
        //tv_pkmn_type2 E VIENE CREATA UNA NUOVA ISTANZA DI GradientDrawable CHE VERRA' USATA COME
        //BACKGROUND DELLA CARDVIEW -- /* STESSO APPROCCIO PER FavoritePokemonRvAdapter */
        if (type2str != null) {
            String type2col = colors.get(type2str);
            tv_pkmn_type2.setText(type2str);
            viewHolder.tv_pkmn_type2.setVisibility(View.VISIBLE);
            // TEST
            try{
                Log.d("ADAPTER", "COLORS: " + " " + type1str + " " +type1col + " , " + type2str + " " + type2col); // TEST
                int[] gradientColors = {Color.parseColor(type1col), Color.parseColor(type2col)}; // TEST
                GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, gradientColors);
                gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
                gd.setCornerRadius(30);
                cardView.setBackground(gd);


            }catch(NullPointerException ignored){

            }


        } else {
            /*SE INVECE IL POKEMON HA UN SOLO TIPO VIENE SETTATO IL TESTO DELLA TEXT VIEW tv_pkmn_type2 A UNA STRINGA VUOTA
            * E VIENE SETTATO LO SFONDO DELLA CARD VIEW AL COLORE DELL'UNICO TIPO RELATIVO AL POKEMON*/
            tv_pkmn_type2.setText("");
            Log.d("ADAPTER", "COLORS: " + " " + type1str + " " + type1col + " , " + type2str + " null");
            viewHolder.tv_pkmn_type2.setVisibility(View.GONE);
            //TEST
            try{
                int backgroundColor = Color.parseColor(type1col); // PROBLEMA QUI
                cardView.setBackgroundColor(backgroundColor);
            }catch (NullPointerException ignored){

            }
        }
        

        boolean isSelected = selectedList.get(position,false);
        if(isSelected) {
            cardView.setSelected(true);
            cardView.setBackgroundColor(Color.LTGRAY);
        } else {
            cardView.setSelected(false);
        }
    }

    @Override
    public int getItemCount() {
        return pokemonList == null? 0 : pokemonList.size();
    }


    /* ---- FOR FILTER SEARCH ON SEARCH MENU ---- */
    @Override
    public Filter getFilter() {
        return pokemonFilter;
    }

    private Filter pokemonFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<Pokemon> pkmnFiltList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0 || constraint.toString().isEmpty()){

                pkmnFiltList.addAll(supportPokemonList);

            }else{

                String filterPattern = constraint.toString().toLowerCase().trim();

                for(Pokemon poke : supportPokemonList){

                    if(poke.getPkmnName().toLowerCase().contains(filterPattern) && poke.getPkmnName().startsWith(String.valueOf(filterPattern.charAt(0)))){

                        pkmnFiltList.add(poke);

                    }

                }

            }

            FilterResults results = new FilterResults();
            results.values = pkmnFiltList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            pokemonList.clear();
            pokemonList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };


    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView iv_pkmn_icon;
        TextView tv_pkmn_num;
        TextView tv_pkmn_name;
        TextView tv_pkmn_type1;
        TextView tv_pkmn_type2;
        View cardView;
        ImageView iv_pkmn_status; /* new add */



        ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_pkmn_icon = itemView.findViewById(R.id.iv_pkmn_icon);
            tv_pkmn_name = itemView.findViewById(R.id.tv_pkmn_name);
            tv_pkmn_num = itemView.findViewById(R.id.tv_pkmn_num);
            tv_pkmn_type1 = itemView.findViewById(R.id.tv_pkmn_type1);
            tv_pkmn_type2 = itemView.findViewById(R.id.tv_pkmn_type2);
            cardView = itemView.findViewById(R.id.cl_card);
            iv_pkmn_status = itemView.findViewById(R.id.iv_pkmn_status); /* new add */

        }

    }

    @Override
    public void onClick(View v) {
        if(selectedList.size() > 0){
            onLongClick(v);
        }
    }


    @Override
    public boolean onLongClick(View v) {

        // into final for testing with db adn dao
        final int position = ((RecyclerView) v.getParent()).getChildAdapterPosition(v);

        boolean isSel = selectedList.get(position, false);

        if(isSel){

            if(!supportFavorPkmnList.contains(pokemonList.get(position))){

                v.setSelected(false);
                selectedList.delete(position);

                pokemonList.get(position).setFavorites(false);
                favorPkmnList.remove(pokemonList.get(position));

            }

        }else{

            if(!supportFavorPkmnList.contains(pokemonList.get(position))){

                v.setSelected(true);
                selectedList.put(position, true);

                pokemonList.get(position).setFavorites(true);
                favorPkmnList.add(pokemonList.get(position));
            }

        }

        /* thanks to this menu doesn't appear even if client touch on pokemon added to the favorites */
        if(mListener != null && !supportFavorPkmnList.contains(pokemonList.get(position))){
            mListener.onSelect(selectedList.size());
        }
        notifyDataSetChanged();
        return true;
    }


    public void deselectAll() {

        for(int index = 0; index < pokemonList.size(); index++){
            if(favorPkmnList.contains(pokemonList.get(index))){
                pokemonList.get(index).setFavorites(false);
            }
        }

        favorPkmnList.removeAll(favorPkmnList);
        selectedList.clear();
        notifyDataSetChanged();
    }



    public void fillFavorPkmonList(){

        /* thanks to allow main-thread queries it work correctly */

        //pokeRepo.count = favorPkmnList.size(); // need then for correct execution of 'finishUpdates'

        for(int index = 0; index < favorPkmnList.size(); index++){
            supportFavorPkmnList.add(favorPkmnList.get(index));
            pokemonDao.setFavorite(favorPkmnList.get(index)); // ricorda quanto detto su PokemonRepository
            //pokeRepo.updateFavorites(favorPkmnList.get(index));
        }
        //necessario un altro metodo in pokeRepo di tipo async che esegua in backround 'endfillFavorPkmnList'
        //solo dopo che vi siano stati tutti gli aggiornamenti di DAO

        /* forse dopo la onPostExecute del metodo di pokeRepo vanno eseguite queste operazioni */
        //pokeRepo.finishUpdates();
        favorPkmnList.removeAll(favorPkmnList);
        selectedList.clear();

    }


    public List<Pokemon> chosenFavorites(){
        return supportFavorPkmnList;
    }


    /*public void endFillFavorPkmnList(){
        favorPkmnList.removeAll(favorPkmnList);
        selectedList.clear();
    }*/


    /* necessario per eliminare i duplicati */
    public void reordering(){

        for(int position = 0; position < supportFavorPkmnList.size(); position++){
            for(int index = 0; index < pokemonList.size(); index++){
                if(supportFavorPkmnList.get(position).getPkmnName().equals(pokemonList.get(index).getPkmnName())){
                    pokemonList.remove(index);
                    pokemonList.add(index, supportFavorPkmnList.get(position));
                    break;
                }
            }
        }

    }
}
