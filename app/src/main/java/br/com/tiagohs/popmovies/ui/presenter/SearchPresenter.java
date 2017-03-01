package br.com.tiagohs.popmovies.ui.presenter;

import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.tiagohs.popmovies.ui.contracts.SearchContract;
import br.com.tiagohs.popmovies.model.db.MovieDB;
import br.com.tiagohs.popmovies.model.movie.Movie;
import br.com.tiagohs.popmovies.model.movie.MovieDetails;
import br.com.tiagohs.popmovies.model.person.PersonFind;
import br.com.tiagohs.popmovies.model.response.GenericListResponse;
import br.com.tiagohs.popmovies.util.MovieUtils;
import br.com.tiagohs.popmovies.util.enumerations.SearchType;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;

public class SearchPresenter implements SearchContract.SearchPresenter {

    private SearchContract.SearchMoviesView mSearchMoviesView;
    private SearchContract.SearchPersonsView mSearchPersonsView;
    private SearchContract.SearchInterceptor mInterceptor;

    private int mMovieCurrentPage;
    private int mMovieTotalPages;
    private int mPersonCurrentPage;
    private int mPersonTotalPages;

    private CompositeDisposable mSubscribers;

    private boolean mIsNewMovieSearch;
    private boolean mIsNewPersonSearch;

    private long mProfileID;

    private boolean mButtonStage;

    public SearchPresenter(SearchContract.SearchInterceptor searchMoviesInterceptor, CompositeDisposable subscribers) {
        mInterceptor = searchMoviesInterceptor;
        mSubscribers = subscribers;

        mMovieCurrentPage = mPersonCurrentPage = 1;
    }

    public void setProfileID(long profileID) {
        mProfileID = profileID;
    }

    @Override
    public void onBindView(Object view) {

    }

    @Override
    public void onUnbindView() {
        mSubscribers.clear();
    }

    public void setMovieView(SearchContract.SearchMoviesView searchMoviesView) {
        mSearchMoviesView = searchMoviesView;
    }

    public void setPersonView(SearchContract.SearchPersonsView searchPersonsView) {
        mSearchPersonsView = searchPersonsView;
    }

    public void searchMovies(String query, Boolean includeAdult, Integer searchYear,
                             String tag, Integer primaryReleaseYear, boolean isNewSearch) {
        mIsNewMovieSearch = isNewSearch;

        if (mSearchMoviesView.isInternetConnected()) {
            mInterceptor.searchMovies(query, includeAdult, searchYear, primaryReleaseYear,
                                     mIsNewMovieSearch ? mMovieCurrentPage : ++mMovieCurrentPage)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onObserverMovies());
        } else
            noConnection();

    }

    private Observer<GenericListResponse<Movie>> onObserverMovies() {
        return new Observer<GenericListResponse<Movie>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mSubscribers.add(d);
            }

            @Override
            public void onNext(GenericListResponse<Movie> response) {
                onSearchMoviesRequestSucess(response);
            }

            @Override
            public void onError(Throwable e) {
                mSearchMoviesView.setNenhumFilmeEncontradoVisibility(View.VISIBLE);
                mSearchMoviesView.setProgressVisibility(View.GONE);
            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void onSearchMoviesRequestSucess(GenericListResponse<Movie> response) {

        mMovieCurrentPage = response.getPage();
        mMovieTotalPages = response.getTotalPage();
        mSearchMoviesView.setNenhumFilmeEncontradoVisibility(View.GONE);

        if (mIsNewMovieSearch) {
            if (response.getResults().isEmpty()) {
                mSearchMoviesView.setNenhumFilmeEncontradoVisibility(View.VISIBLE);
                setupResponseMovies(new ArrayList<Movie>());
            } else {
                setupResponseMovies(response.getResults());
            }
        } else {
            mSearchMoviesView.addAllMovies(response.getResults(), hasMoreMoviePages());
            mSearchMoviesView.updateAdapter();
        }

        mSearchMoviesView.setProgressVisibility(View.GONE);

    }


    private void noConnection() {
        if (mSearchMoviesView.isAdded())
            mSearchMoviesView.onErrorNoConnection();

        mSearchMoviesView.setProgressVisibility(View.GONE);
    }

    public void searchPersons(String query, Boolean includeAdult, String tag,
                              SearchType searchType, boolean isNewSearch) {
        mIsNewPersonSearch = isNewSearch;

        if (mSearchPersonsView.isInternetConnected()) {
            mSearchPersonsView.setProgressVisibility(View.VISIBLE);

            mInterceptor.searchPerson(query, includeAdult, mIsNewPersonSearch ? mMovieCurrentPage : ++mMovieCurrentPage)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onObserverPerson());

        } else
            noConnection();

    }

    private Observer<GenericListResponse<PersonFind>> onObserverPerson() {
        return new Observer<GenericListResponse<PersonFind>>() {
            @Override
            public void onSubscribe(Disposable d) {
                mSubscribers.add(d);
            }

            @Override
            public void onNext(GenericListResponse<PersonFind> response) {
                onSearchPersonRequestSucess(response);
            }

            @Override
            public void onError(Throwable e) {
                mSearchPersonsView.setNenhumaPessoaEncontradoVisibility(View.VISIBLE);
                mSearchPersonsView.setProgressVisibility(View.GONE);
            }

            @Override
            public void onComplete() {

            }
        };
    }

    private void onSearchPersonRequestSucess(GenericListResponse<PersonFind> personFind) {

        mPersonCurrentPage = personFind.getPage();
        mPersonTotalPages = personFind.getTotalPage();
        mSearchPersonsView.setNenhumaPessoaEncontradoVisibility(View.GONE);

        if (mIsNewPersonSearch) {
            if (personFind.getResults().isEmpty()) {
                mSearchPersonsView.setNenhumaPessoaEncontradoVisibility(View.VISIBLE);
                setupResponsePerson(new ArrayList<PersonFind>());
            } else {
                setupResponsePerson(personFind.getResults());
            }
        } else {
            mSearchPersonsView.addAllPersons(personFind.getResults(), hasMorePersonPages());
            mSearchPersonsView.updateAdapter();
        }

        mSearchPersonsView.setProgressVisibility(View.GONE);

    }

    public void getMovieDetails(int movieID, boolean buttonStage, String tag) {
        this.mButtonStage = buttonStage;

        if (mSearchMoviesView.isInternetConnected()) {
            mInterceptor.getMovieDetails(movieID, new String[]{})
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(onMoviesDetailsObserver());

        } else {
            noConnection();
        }
    }

    private Observer<MovieDetails> onMoviesDetailsObserver() {
        return new Observer<MovieDetails>() {
            @Override
            public void onSubscribe(Disposable d) {
                mSubscribers.add(d);
            }

            @Override
            public void onNext(MovieDetails movie) {
                onResponse(movie);
            }

            @Override
            public void onError(Throwable e) {
                if (mSearchMoviesView != null)
                    mSearchMoviesView.onErrorInServer();
                else if (mSearchPersonsView != null) {
                    mSearchPersonsView.onErrorInServer();
                }
            }

            @Override
            public void onComplete() {

            }
        };
    }

    public void onResponse(MovieDetails movie) {

        if (mSearchMoviesView.isAdded()) {
            if (mButtonStage) {
                mSubscribers.add(mInterceptor.saveMovie(new MovieDB(movie.getId(), MovieDB.STATUS_WATCHED, movie.getRuntime(), movie.getPosterPath(),
                                                        movie.getTitle(), movie.isFavorite(), movie.getVoteCount(), mProfileID,
                                                        Calendar.getInstance(), MovieUtils.formateStringToCalendar(movie.getReleaseDate()),
                                                        MovieUtils.getYearByDate(movie.getReleaseDate()), MovieUtils.genreToGenreDB(movie.getGenres())))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe());
            } else {
                mSubscribers.add(mInterceptor.deleteMovieByServerID(movie.getId(), mProfileID)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe());
            }
        }

    }

    public Observable<Boolean> isJaAssistido(int movieID) {
        return mInterceptor.findMovieByServerID(movieID, mProfileID)
                            .map(new Function<Movie, Boolean>() {
                                @Override
                                public Boolean apply(Movie movie) throws Exception {
                                    return movie != null;
                                }
                            });
    }

    private void setupResponseMovies(List<Movie> movies) {
        mSearchMoviesView.setListMovies(movies, hasMoreMoviePages());
        mSearchMoviesView.setupRecyclerView();
    }

    private boolean hasMoreMoviePages() {
        return mMovieCurrentPage < mMovieTotalPages;
    }

    private boolean hasMorePersonPages() {
        return mPersonCurrentPage < mPersonTotalPages;
    }

    private void setupResponsePerson(List<PersonFind> movies) {
        mSearchPersonsView.setListPersons(movies, hasMorePersonPages());
        mSearchPersonsView.setupRecyclerView();
    }

}
