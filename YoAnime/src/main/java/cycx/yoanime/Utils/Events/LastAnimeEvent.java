package cycx.yoanime.Utils.Events;

import cycx.yoanime.Models.Anime;

public class LastAnimeEvent {
    public final Anime anime;

    public LastAnimeEvent (Anime anime) {
        this.anime = anime;
    }

}
