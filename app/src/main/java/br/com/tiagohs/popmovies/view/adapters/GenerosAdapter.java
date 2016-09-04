package br.com.tiagohs.popmovies.view.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;

import java.util.List;

import br.com.tiagohs.popmovies.R;
import br.com.tiagohs.popmovies.model.movie.Genre;
import br.com.tiagohs.popmovies.view.fragment.MovieDetailsOverviewFragment;

/**
 * Created by Tiago Henrique on 27/08/2016.
 */
public class GenerosAdapter extends RecyclerView.Adapter<GenerosAdapter.GenerosViewHolder> {

    private Context mContext;
    private List<Genre> mGeneros;
    private MovieDetailsOverviewFragment.Callbacks mCallbacks;

    public GenerosAdapter(Context context, List<Genre> genres, MovieDetailsOverviewFragment.Callbacks callbacks) {
        this.mContext = context;
        this.mGeneros = genres;
        this.mCallbacks = callbacks;
    }

    public void setGeneros(List<Genre> genre) {
        this.mGeneros = genre;
    }

    @Override
    public GenerosViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View view = layoutInflater.inflate(R.layout.item_list_words_default, parent, false);

        return new GenerosViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GenerosViewHolder holder, int position) {
        holder.bindGenero(mGeneros.get(position));
    }

    @Override
    public int getItemCount() {
        return mGeneros.size();
    }

    class GenerosViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private MaterialRippleLayout mRippleView;
        private TextView mGeneroNameTextView;
        private Genre mGenero;


        public GenerosViewHolder(View itemView) {
            super(itemView);

            mRippleView = (MaterialRippleLayout) itemView.findViewById(R.id.movie_deails_item_default_riple);
            mRippleView.setOnClickListener(this);
            mGeneroNameTextView = (TextView) itemView.findViewById(R.id.movie_deails_item_default_text_view);

        }

        public void bindGenero(Genre gene) {
            Log.i("GeneroAdp: ", "Genero!" + gene.getName());
            this.mGenero = gene;

            mGeneroNameTextView.setText(mGenero.getName());
        }

        @Override
        public void onClick(View view) {
            mCallbacks.onGenreSelected(mGenero);
        }
    }
}
