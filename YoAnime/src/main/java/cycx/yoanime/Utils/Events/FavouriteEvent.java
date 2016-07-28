package cycx.yoanime.Utils.Events;

import cycx.yoanime.Models.Anime;

public class FavouriteEvent {
    public boolean addToFavourites; // if false: remove from favourites
    public Anime anime;

    public FavouriteEvent (boolean addToFavourites, Anime anime) {
        this.addToFavourites = addToFavourites;
        this.anime = anime;
    }

}
