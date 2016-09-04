package br.com.tiagohs.popmovies.presenter;

import br.com.tiagohs.popmovies.model.movie.MovieDetails;
import br.com.tiagohs.popmovies.server.ResponseListener;
import br.com.tiagohs.popmovies.view.MovieDetailsView;

public interface MovieDetailsPresenter extends BasePresenter<MovieDetailsView>, ResponseListener<MovieDetails> {

    void getMovieDetails(int movieID, String[] appendToResponse);
    void getVideos(int movieID);
}
