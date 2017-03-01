package br.com.tiagohs.popmovies.ui.contracts;

import java.util.List;

import br.com.tiagohs.popmovies.model.db.MovieDB;
import br.com.tiagohs.popmovies.model.db.ProfileDB;
import br.com.tiagohs.popmovies.model.response.ImageResponse;
import br.com.tiagohs.popmovies.ui.presenter.BasePresenter;
import br.com.tiagohs.popmovies.ui.view.BaseView;
import io.reactivex.Observable;

/**
 * Created by Tiago on 25/02/2017.
 */

public class PerfilContract {

    public interface PerfilInterceptor {

        Observable<ProfileDB> findProfileByUserUsername(String username);
        Observable<List<MovieDB>> findAllMoviesDB(long profileID);
        Observable<ImageResponse> getMovieImagens(int movieID);
    }

    public interface PerfilPresenter extends BasePresenter<PerfilView> {

        void initUpdates(String username);
    }

    public interface PerfilView extends BaseView {

        void setNamePerfil(String nameUser);
        void setImagePerfil(String imagePath);
        void setLocalImagePerfil();
        void setBackground(String backgroundPath);
        void setupTabs();
        void setProfile(ProfileDB mProfile);

        void onErrorLoadingBackground();
    }
}
